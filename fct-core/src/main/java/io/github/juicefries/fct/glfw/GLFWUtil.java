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

//
// Created by juicefries
// The project name is fct
// Data 2026/09/04 15:00
//

package io.github.juicefries.fct.glfw;

import io.github.juicefries.fct.Image;
import io.github.juicefries.fct.ImageToolkit;
import io.github.juicefries.fct.Size;
import io.github.juicefries.fct.Sys;
import io.github.juicefries.fct.Toolkit;
import io.github.juicefries.fct.Window;
import io.github.juicefries.fct.sign.ApiSign;
import io.github.juicefries.fct.util.Array;
import io.github.juicefries.fct.util.Lock;
import java.util.Objects;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

public class GLFWUtil {

    public final static Lock lock = Lock.create();

    // ========================= GetWindowAttrib =========================

    public static boolean getWindowAttrib(long window, int attrib) {
        return getWindowAttribI(window, attrib) == GLFW.GLFW_TRUE;
    }

    public static int getWindowAttribI(long window, int attrib) {
        return GLFW.glfwGetWindowAttrib(window, attrib);
    }

    // ========================= GetCurrentContext =========================

    public static long getCurrentContext() {
        return GLFW.glfwGetCurrentContext();
    }

    public static boolean isVisible(long window) {
        return getWindowAttrib(window, GLFW.GLFW_VISIBLE);
    }

    // ========================= Monitor =========================

    public static long @Nullable [] getMonitors() {
        Sys.checkInit(true);
        PointerBuffer monitors = GLFW.glfwGetMonitors();

        if (monitors != null) {
            long[] ms = new long[monitors.limit()];
            for (int i = 0; i < monitors.limit(); i++) {
                long m = monitors.get(i);
                ms[i] = m;
            }
            return ms;
        }
        return null;
    }

    public static GLFWVidMode getVideoMode(long monitor) {
        Sys.checkInit(true);
        GLFWVidMode vidMode;
        if (monitor != MemoryUtil.NULL) {
            vidMode = GLFW.glfwGetVideoMode(monitor);
        } else {
            vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        }
        return vidMode;
    }

    // ========================= CreateWindow =========================

    public static long createWindow(int width,int height,String title,boolean bind) {
        Sys.checkInit();
        if (width < 0) {
            throw new IllegalArgumentException("width is less than or equal to 0!");
        }
        if (height < 0) {
            throw new IllegalArgumentException("height is less than or equal to 0!");
        }
        var _title__ = Objects.requireNonNullElse(title, "");
        long window;
        synchronized (lock) {
            window = GLFW.glfwCreateWindow(width,height,_title__,MemoryUtil.NULL,MemoryUtil.NULL);
        }
        if (window == MemoryUtil.NULL) {
            throw new IllegalArgumentException("GLFW window creation failed!");
        }
        if (bind) {
            GLFW.glfwMakeContextCurrent(window);
            GL.createCapabilities();
        }
        return window;
    }

    public static long createWindow(int width,int height,String title) {
        return createWindow(width,height,title,true);
    }

    public static long createWindow(Vector2i size,String title) {
        if (size == null) {
            throw new NullPointerException("size is null!");
        }
        if (title == null) {
            throw new NullPointerException("title is null!");
        }
        return createWindow(size.x,size.y,title);
    }

    public static long createWindow(int width,int height,boolean bind) {
        return createWindow(width,height,"",bind);
    }

    public static long createWindow(int width,int height) {
        return createWindow(width,height,true);
    }

    // ========================= SetIcon =========================

    public static void setWindowIcon(long window, GLFWImage.Buffer icon, boolean free) {
        if (icon == null) {
            throw new NullPointerException("icon is null!");
        }
        GLFW.glfwSetWindowIcon(window, icon);
        if (free) {
            icon.free();
        }
    }

    public static void setWindowIcon(long window, GLFWImage.Buffer icon) {
        setWindowIcon(window, icon, false);
    }

    public static void setWindowIcon(long window, GLFWImage image, boolean free) {
        if (image == null) {
            throw new NullPointerException("image is null!");
        }

        GLFWImage.Buffer buffer = GLFWImage.malloc(1).put(0, image);

        setWindowIcon(window, buffer);

        if (free) {
            buffer.free();
            image.free();
        }

    }

    public static void setWindowIcon(long window, GLFWImage image) {
        setWindowIcon(window, image, false);
    }

    public static void setWindowIcon(long window, Image icon) {
        if (icon == null) {
            throw new NullPointerException("icon is null!");
        }

        GLFWImage image = ImageToolkit.getImage(icon);
        setWindowIcon(window, image, true);
    }

    public static void setWindowIcon(long window, java.awt.Image icon) {
        if (icon == null) {
            throw new NullPointerException("icon is null!");
        }

        Image image = ImageToolkit.getImage(icon);
        GLFWImage glfwImage = ImageToolkit.getImage(image);
        setWindowIcon(window, glfwImage, true);
    }

    public static void setWindowIcon(Window window, java.awt.Image icon) {
        if (window == null) {
            throw new NullPointerException("window is null!");
        }
        if (icon == null) {
            throw new NullPointerException("icon is null!");
        }
        long _window__ = Toolkit.getWindow(window);
        setWindowIcon(_window__,icon);
    }

    // ========================= center =========================

    @Contract("_, null -> fail")
    public static @NotNull Vector2i center(long monitor, Vector2i size) {
        if (size == null) throw new NullPointerException("size is null!");

        final int width = size.x;
        final int height = size.y;

        if (width < 0) throw new IllegalArgumentException("width is less than or equal to 0!");
        if (height < 0) throw new IllegalArgumentException("height is less than or equal to 0!");


        Sys.checkInit(true);
        final GLFWVidMode vidMode = getVideoMode(monitor);

        final int x = ((vidMode.width() - width) / 2);
        final int y = ((vidMode.height() - height) / 2);

        return new Vector2i(x, y);
    }

    @Contract("null -> fail")
    public static @NotNull Vector2i center(Vector2i size) {
        return center(MemoryUtil.NULL, size);
    }

    public static @NotNull Vector2i center(@NotNull Size size) {
        return center(size.toVector2i());
    }

    @Contract("null -> fail")
    public static @NotNull Vector2i center(Window window) {
        if (window == null) {
            throw new NullPointerException("window is null!");
        }
        if (!window.isInit()) {
            throw new IllegalStateException("The window has not been initialized yet!");
        }
        final long _window__ = Toolkit.getWindow(window);
        final long monitor = GLFW.glfwGetWindowMonitor(_window__);
        return GLFWUtil.center(monitor, window.getSize().toVector2i());
    }

    public static void centerVoid(Window window) {
        if (window == null) {
            throw new NullPointerException("window is null!");
        }
        if (!window.isInit()) {
            throw new IllegalStateException("The window has not been initialized yet!");
        }
        Vector2i vector2i = center(window);
        window.invoke(() -> {
            window.setSize(vector2i.x, vector2i.y);
        });
    }

    // ========================= getSize =========================

    public static @NotNull Vector2i getSize(long window) {
        Sys.checkInit();
        if (window == MemoryUtil.NULL) {
            throw new IllegalArgumentException("Invalid window!");
        }
        Vector2i size = new Vector2i();

        int[] width = Array.createI(1);
        int[] height = Array.createI(1);
        GLFW.glfwGetWindowSize(window, width, height);
        size.x = width[0];
        size.y = height[0];
        return size;
    }

    public static @NotNull Vector2i getFrameBufferSize(long window) {
        Sys.checkInit();
        if (window == MemoryUtil.NULL) {
            throw new IllegalArgumentException("Invalid window!");
        }
        Vector2i size = new Vector2i();

        int[] width = Array.createI(1);
        int[] height = Array.createI(1);
        GLFW.glfwGetFramebufferSize(window, width, height);
        size.x = width[0];
        size.y = height[0];
        return size;
    }

    @ApiSign.NotRecommended(since = "0.0.4")
    public static @NotNull Vector2i getSize() {
        return getSize(getCurrentContext());
    }

    @ApiSign.NotRecommended(since = "0.0.4")
    public static @NotNull Vector2i getFrameBufferSize() {
        return getFrameBufferSize(getCurrentContext());
    }

}
