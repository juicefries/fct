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

package io.github.juicefries.fct.lwjgl;

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

    // ========================= center =========================

    @Contract("_, null -> fail")
    public static @NotNull Vector2i center(long monitor, Vector2i size) {
        if (size == null) throw new NullPointerException("size is null!");

        final int width = size.x;
        final int height = size.y;

        if (width < 0) throw new IllegalArgumentException("width is less than or equal to 0!");
        if (height < 0) throw new IllegalArgumentException("height is less than or equal to 0!");

        final GLFWVidMode vidMode = getVideoMode(monitor);

        final int x = ((vidMode.width() - width) / 2);
        final int y = ((vidMode.height() - height) / 2);

        return new Vector2i(x, y);
    }

    @Contract("null -> fail")
    public static @NotNull Vector2i center(Vector2i size) {
        return center(MemoryUtil.NULL, size);
    }

    // ========================= getSize =========================

    public static @NotNull Vector2i getSize(long window) {
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
