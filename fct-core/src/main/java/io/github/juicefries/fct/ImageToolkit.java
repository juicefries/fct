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
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package io.github.juicefries.fct;

import io.github.juicefries.fct.image.Format;
import io.github.juicefries.fct.util.Resources;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public final class ImageToolkit {

    static {
        Toolkit.initialize();
    }

    // ========================= IMAGE =========================

    public static Image getResourcesImage(Class<?> clazz, String name) {
        if (clazz == null || name == null) {
            throw new NullPointerException("clazz or name is null");
        }
        try (InputStream is = Resources.getResourceAsStream(clazz, name)) {
            if (is == null) {
                throw new IOException("Resource not found: " + name);
            }
            return loadImageFromStream(is);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load image: " + name, e);
        }
    }

    public static Image getResourcesImage(String path) {
        return getResourcesImage(ImageToolkit.class, path);
    }

    public static Image getImage(byte[] image) {
        if (image == null) {
            throw new NullPointerException("image byte array is null");
        }
        return loadImageFromBytes(image);
    }

    /**
     * 从 GLFWImage 转换。
     * GLFWImage 的像素数据通过 width(), height(), 和 address() 访问。
     */
    public static Image getImage(GLFWImage image) {
        if (image == null) {
            throw new NullPointerException("GLFWImage is null");
        }
        int w = image.width();
        int h = image.height();
        // GLFWImage 的像素数据是一个连续的字节数组，每个像素 RGBA
        // 通过 MemoryUtil.memByteBuffer 将地址转为 ByteBuffer
        long addr = image.address();
        if (addr == 0L) {
            throw new IllegalArgumentException("GLFWImage has no pixel data (null address)");
        }
        // 每个像素 4 字节 (RGBA)
        int capacity = w * h * 4;
        ByteBuffer pixels = MemoryUtil.memByteBuffer(addr, capacity);
        if (pixels == null) {
            throw new IllegalArgumentException("Failed to map GLFWImage pixel data");
        }
        // 复制一份，避免与 GLFW 生命周期绑定
        ByteBuffer copy = MemoryUtil.memAlloc(capacity);
        copy.put(pixels.duplicate().rewind());
        copy.flip();
        return new DefaultImage(w, h, Format.RGBA, copy);
    }

    @ApiStatus.Experimental
    public static @NotNull BufferedImage toAwtImage(Image image) {
        GLFWImage gl = ImageToolkit.getImage(image);
        int w = gl.width(), h = gl.height();
        ByteBuffer src = gl.pixels(w * h * 4);   // ← 传容量：RGBA，每像素 4 字节

        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        int[] dst = ((DataBufferInt) bi.getRaster().getDataBuffer()).getData();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int si = (y * w + x) * 4;
                int r = src.get(si)     & 0xFF;
                int g = src.get(si + 1) & 0xFF;
                int b = src.get(si + 2) & 0xFF;
                int a = src.get(si + 3) & 0xFF;
                dst[y * w + x] = (a << 24) | (r << 16) | (g << 8) | b;  // ARGB
            }
        }
        return bi;
    }


    public static GLFWImage getImage(Image image) {
        if (image == null) {
            throw new NullPointerException("image is null");
        }
        if (!(image instanceof DefaultImage di)) {
            throw new IllegalArgumentException("Only DefaultImage is supported");
        }
        ByteBuffer data = di.getData();
        Format fmt = di.getFormat();
        ByteBuffer out;
        if (fmt == Format.RGBA) {
            // 直接使用，但需要确保是直接缓冲区
            out = data.duplicate();
        } else if (fmt == Format.RGB) {
            // 转为 RGBA：添加 A=255
            int pixelCount = di.getWidth() * di.getHeight();
            out = MemoryUtil.memAlloc(pixelCount * 4);
            for (int i = 0; i < pixelCount; i++) {
                int r = data.get() & 0xFF;
                int g = data.get() & 0xFF;
                int b = data.get() & 0xFF;
                out.put((byte) r);
                out.put((byte) g);
                out.put((byte) b);
                out.put((byte) 0xFF);
            }
            out.flip();
        } else {
            throw new IllegalArgumentException("Unsupported format for GLFWImage: " + fmt);
        }
        GLFWImage glfwImage = GLFWImage.malloc();
        // 设置宽高和像素数据
        glfwImage.width(di.getWidth());
        glfwImage.height(di.getHeight());
        glfwImage.set(di.getWidth(), di.getHeight(), out);
        return glfwImage;
    }

    public static Image getImage(ImageIcon imageIcon) {
        if (imageIcon == null) {
            throw new NullPointerException("ImageIcon is null");
        }
        return getImage(imageIcon.getImage());
    }

    public static Image getImage(java.awt.Image image) {
        if (image == null) {
            throw new NullPointerException("AWT Image is null");
        }
        BufferedImage bi;
        if (image instanceof BufferedImage) {
            bi = (BufferedImage) image;
        } else {
            bi = toBufferedImage(image);
        }
        return fromBufferedImage(bi);
    }

    private static Image loadImageFromStream(InputStream is) throws IOException {
        byte[] bytes = readAllBytes(is);
        return loadImageFromBytes(bytes);
    }

    private static Image loadImageFromBytes(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("Image data is empty or null");
        }
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer wBuf = stack.mallocInt(1);
            IntBuffer hBuf = stack.mallocInt(1);
            IntBuffer compBuf = stack.mallocInt(1);

            // --- 分配直接缓冲区并拷贝数据 ---
            ByteBuffer buffer = MemoryUtil.memAlloc(bytes.length);
            buffer.put(bytes);
            buffer.flip();

            ByteBuffer data;
            try {
                data = STBImage.stbi_load_from_memory(buffer, wBuf, hBuf, compBuf, 0);
            } finally {
                MemoryUtil.memFree(buffer); // 用完释放临时缓冲区
            }

            if (data == null) {
                throw new RuntimeException("STBImage failed to load: " + STBImage.stbi_failure_reason());
            }

            int width = wBuf.get(0);
            int height = hBuf.get(0);
            int channels = compBuf.get(0);

            // 确定 Format
            Format format = switch (channels) {
                case 1 -> Format.GRAYSCALE;
                case 2 -> Format.ALPHA;
                case 3 -> Format.RGB;
                case 4 -> Format.RGBA;
                default -> throw new IllegalStateException("Unexpected channels: " + channels);
            };


            ByteBuffer copy = MemoryUtil.memAlloc(data.remaining());
            copy.put(data.duplicate().rewind());
            copy.flip();
            STBImage.stbi_image_free(data);

            return new DefaultImage(width, height, format, copy);
        }
    }

    private static byte[] readAllBytes(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int len;
        while ((len = is.read(buf)) != -1) {
            baos.write(buf, 0, len);
        }
        return baos.toByteArray();
    }

    // ========================= AWT 转换辅助 =========================

    private static BufferedImage toBufferedImage(java.awt.Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        BufferedImage bi = new BufferedImage(
                image.getWidth(null),
                image.getHeight(null),
                BufferedImage.TYPE_INT_ARGB
        );
        java.awt.Graphics2D g = bi.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return bi;
    }

    private static Image fromBufferedImage(BufferedImage bi) {
        int w = bi.getWidth();
        int h = bi.getHeight();
        int type = bi.getType();
        ByteBuffer buffer;
        Format format;

        if (type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB) {
            DataBufferInt db = (DataBufferInt) bi.getRaster().getDataBuffer();
            int[] pixels = db.getData();
            boolean hasAlpha = (type == BufferedImage.TYPE_INT_ARGB);
            format = hasAlpha ? Format.ARGB : Format.RGB;
            buffer = MemoryUtil.memAlloc(pixels.length * 4);
            for (int pixel : pixels) {
                int a = (pixel >> 24) & 0xFF;
                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;
                if (hasAlpha) {
                    buffer.put((byte) a);
                    buffer.put((byte) r);
                    buffer.put((byte) g);
                    buffer.put((byte) b);
                } else {
                    buffer.put((byte) r);
                    buffer.put((byte) g);
                    buffer.put((byte) b);
                }
            }
            buffer.flip();
        } else if (type == BufferedImage.TYPE_3BYTE_BGR) {
            DataBufferByte db = (DataBufferByte) bi.getRaster().getDataBuffer();
            byte[] bgr = db.getData();
            format = Format.RGB;
            buffer = MemoryUtil.memAlloc(bgr.length);
            for (int i = 0; i < bgr.length; i += 3) {
                buffer.put(bgr[i + 2]); // R
                buffer.put(bgr[i + 1]); // G
                buffer.put(bgr[i]);     // B
            }
            buffer.flip();
        } else if (type == BufferedImage.TYPE_BYTE_GRAY) {
            DataBufferByte db = (DataBufferByte) bi.getRaster().getDataBuffer();
            byte[] gray = db.getData();
            format = Format.GRAYSCALE;
            buffer = MemoryUtil.memAlloc(gray.length);
            buffer.put(gray);
            buffer.flip();
        } else {
            // 其他类型先转换为 ARGB
            BufferedImage converted = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            converted.getGraphics().drawImage(bi, 0, 0, null);
            return fromBufferedImage(converted);
        }
        return new DefaultImage(w, h, format, buffer);
    }

    private ImageToolkit() {}
}