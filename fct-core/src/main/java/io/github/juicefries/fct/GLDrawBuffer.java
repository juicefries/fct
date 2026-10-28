/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2026 juicefries
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

//
// Created by juicefries
// The project name is fct
// Data 2026/10/28 18:20
//

package io.github.juicefries.fct;

import io.github.juicefries.fct.image.Format;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * 基于 FBO 的离屏画布。
 *
 * @since 1.0.5
 * @author juicefries
 */
@ApiStatus.Experimental
public class GLDrawBuffer extends DrawBuffer {

    private final Graphics2D graphics;
    private final int[] savedViewport = new int[4];
    private final Matrix4f savedMatrix = new Matrix4f();

    private int framebuffer;
    private int colorTexture;
    private int depthBuffer;

    /**
     * 必须在 GL 上下文就绪的线程上创建。
     *
     * @param width    宽度（像素）
     * @param height   高度（像素）
     * @param graphics 用来绘制本缓冲的画笔
     * @throws IllegalStateException FBO 不完整
     */
    public GLDrawBuffer(int width, int height, Graphics2D graphics) {
        super(width, height);
        if (graphics == null) {
            throw new NullPointerException("graphics is null!");
        }
        this.graphics = graphics;
        createFramebuffer();
    }

    private void createFramebuffer() {
        framebuffer = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);

        colorTexture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0,
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, MemoryUtil.NULL);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0,
                GL11.GL_TEXTURE_2D, colorTexture, 0);

        depthBuffer = GL30.glGenRenderbuffers();
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer);
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH_COMPONENT24, width, height);
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
                GL30.GL_RENDERBUFFER, depthBuffer);

        int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        if (status != GL30.GL_FRAMEBUFFER_COMPLETE) {
            throw new IllegalStateException("Framebuffer incomplete: " + status);
        }
    }

    /**
     * 接管绘制目标：记下当前视口和矩阵，切到本缓冲。
     *
     * @throws IllegalStateException 已关闭
     */
    @Override
    public Graphics2D graphics() {
        checkOpen();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer viewport = stack.mallocInt(4);
            GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
            for (int i = 0; i < 4; i++) {
                savedViewport[i] = viewport.get(i);
            }
        }
        savedMatrix.set(graphics.getAttribute().getMatrix4f());

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
        GL11.glViewport(0, 0, width, height);
        graphics.setMatrix4f(new Matrix4f().ortho2D(0, width, height, 0));
        graphics.update();
        return graphics;
    }

    /**
     * 支持 {@link Image}（{@code Format.RGBA}）、{@link BufferedImage}、{@code int[]}（ARGB）。
     *
     * @param type 目标类型
     * @param <T>  目标类型
     * @return 新对象
     * @throws IllegalArgumentException 不支持的类型
     * @throws IllegalStateException    已关闭
     */
    @Override
    public <T> T output(Class<T> type) {
        checkOpen();
        if (type == null) {
            throw new NullPointerException("type is null!");
        }
        ByteBuffer pixels = readPixels();
        release();

        if (type == int[].class) {
            return type.cast(toArgb(pixels));
        }
        if (type == BufferedImage.class) {
            int[] argb = toArgb(pixels);
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            image.setRGB(0, 0, width, height, argb, 0, width);
            return type.cast(image);
        }
        if (type == Image.class) {
            return type.cast(new DefaultImage(width, height, Format.RGBA, pixels));
        }
        throw new IllegalArgumentException("Unsupported output type: " + type.getName());
    }

    /**
     * 交还绘制目标：恢复 framebuffer、视口和矩阵。
     */
    private void release() {
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
        GL11.glViewport(savedViewport[0], savedViewport[1], savedViewport[2], savedViewport[3]);
        graphics.setMatrix4f(savedMatrix);
        graphics.update();
    }

    /** 读回像素并上下翻转（GL 原点在左下，图像在左上）。 */
    private ByteBuffer readPixels() {
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, framebuffer);
        ByteBuffer raw = BufferUtils.createByteBuffer(width * height * 4);
        GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, raw);

        ByteBuffer flipped = BufferUtils.createByteBuffer(width * height * 4);
        int rowBytes = width * 4;
        for (int y = height - 1; y >= 0; y--) {
            int offset = y * rowBytes;
            for (int i = 0; i < rowBytes; i++) {
                flipped.put(raw.get(offset + i));
            }
        }
        flipped.flip();
        return flipped;
    }

    private int[] toArgb(ByteBuffer rgba) {
        int[] argb = new int[width * height];
        for (int i = 0; i < argb.length; i++) {
            int r = rgba.get() & 0xFF;
            int g = rgba.get() & 0xFF;
            int b = rgba.get() & 0xFF;
            int a = rgba.get() & 0xFF;
            argb[i] = (a << 24) | (r << 16) | (g << 8) | b;
        }
        return argb;
    }

    private void checkOpen() {
        if (closed) {
            throw new IllegalStateException("DrawBuffer has been closed!");
        }
    }

    /**
     * 幂等。
     */
    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        GL30.glDeleteRenderbuffers(depthBuffer);
        GL11.glDeleteTextures(colorTexture);
        GL30.glDeleteFramebuffers(framebuffer);
    }
}
