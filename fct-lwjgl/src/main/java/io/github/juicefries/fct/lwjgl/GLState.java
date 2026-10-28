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

package io.github.juicefries.fct.lwjgl;

import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

@Deprecated(since = "1.0.4",forRemoval = true)
public final class GLState implements AutoCloseable {

    private final boolean blend;
    private final int blendSrcRgb;
    private final int blendDstRgb;
    private final int blendSrcAlpha;
    private final int blendDstAlpha;

    private final boolean depthTest;
    private final int depthFunc;
    private final boolean depthMask;

    private final boolean cullFace;
    private final int cullMode;

    private final int program;
    private final int vertexArray;
    private final int activeTexture;
    private final int texture2D;

    private final int viewportX;
    private final int viewportY;
    private final int viewportWidth;
    private final int viewportHeight;

    private GLState(boolean blend, int blendSrcRgb, int blendDstRgb, int blendSrcAlpha, int blendDstAlpha,
                    boolean depthTest, int depthFunc, boolean depthMask,
                    boolean cullFace, int cullMode,
                    int program, int vertexArray, int activeTexture, int texture2D,
                    int viewportX, int viewportY, int viewportWidth, int viewportHeight) {
        this.blend = blend;
        this.blendSrcRgb = blendSrcRgb;
        this.blendDstRgb = blendDstRgb;
        this.blendSrcAlpha = blendSrcAlpha;
        this.blendDstAlpha = blendDstAlpha;
        this.depthTest = depthTest;
        this.depthFunc = depthFunc;
        this.depthMask = depthMask;
        this.cullFace = cullFace;
        this.cullMode = cullMode;
        this.program = program;
        this.vertexArray = vertexArray;
        this.activeTexture = activeTexture;
        this.texture2D = texture2D;
        this.viewportX = viewportX;
        this.viewportY = viewportY;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
    }

    /** 记录当前状态。 */
    public static GLState capture() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer value = stack.mallocInt(1);
            IntBuffer viewport = stack.mallocInt(4);

            GL33.glGetIntegerv(GL33.GL_BLEND_SRC_RGB, value);
            int srcRgb = value.get(0);
            GL33.glGetIntegerv(GL33.GL_BLEND_DST_RGB, value);
            int dstRgb = value.get(0);
            GL33.glGetIntegerv(GL33.GL_BLEND_SRC_ALPHA, value);
            int srcAlpha = value.get(0);
            GL33.glGetIntegerv(GL33.GL_BLEND_DST_ALPHA, value);
            int dstAlpha = value.get(0);

            GL33.glGetIntegerv(GL33.GL_DEPTH_FUNC, value);
            int depthFunc = value.get(0);
            GL33.glGetIntegerv(GL33.GL_CULL_FACE_MODE, value);
            int cullMode = value.get(0);
            GL33.glGetIntegerv(GL33.GL_CURRENT_PROGRAM, value);
            int program = value.get(0);
            GL33.glGetIntegerv(GL33.GL_VERTEX_ARRAY_BINDING, value);
            int vertexArray = value.get(0);
            GL33.glGetIntegerv(GL33.GL_ACTIVE_TEXTURE, value);
            int activeTexture = value.get(0);
            GL33.glGetIntegerv(GL33.GL_TEXTURE_BINDING_2D, value);
            int texture2D = value.get(0);

            GL33.glGetIntegerv(GL33.GL_VIEWPORT, viewport);

            return new GLState(
                    GL33.glIsEnabled(GL33.GL_BLEND), srcRgb, dstRgb, srcAlpha, dstAlpha,
                    GL33.glIsEnabled(GL33.GL_DEPTH_TEST), depthFunc,
                    GL33.glGetBoolean(GL33.GL_DEPTH_WRITEMASK),
                    GL33.glIsEnabled(GL33.GL_CULL_FACE), cullMode,
                    program, vertexArray, activeTexture, texture2D,
                    viewport.get(0), viewport.get(1), viewport.get(2), viewport.get(3));
        }
    }

    /** 把状态恢复成 capture 时的样子。 */
    public void restore() {
        // 先回到当时的活动纹理单元，再绑2D 纹理，否则会绑错单元
        GL33.glActiveTexture(activeTexture);
        GL33.glBindTexture(GL33.GL_TEXTURE_2D, texture2D);

        GL33.glUseProgram(program);
        GL33.glBindVertexArray(vertexArray);

        if (blend) {
            GL33.glEnable(GL33.GL_BLEND);
        } else {
            GL33.glDisable(GL33.GL_BLEND);
        }
        GL33.glBlendFuncSeparate(blendSrcRgb, blendDstRgb, blendSrcAlpha, blendDstAlpha);

        if (depthTest) {
            GL33.glEnable(GL33.GL_DEPTH_TEST);
        } else {
            GL33.glDisable(GL33.GL_DEPTH_TEST);
        }
        GL33.glDepthFunc(depthFunc);
        GL33.glDepthMask(depthMask);

        if (cullFace) {
            GL33.glEnable(GL33.GL_CULL_FACE);
        } else {
            GL33.glDisable(GL33.GL_CULL_FACE);
        }
        GL33.glCullFace(cullMode);

        GL33.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);
    }

    @Override
    public void close() {
        restore();
    }
}
