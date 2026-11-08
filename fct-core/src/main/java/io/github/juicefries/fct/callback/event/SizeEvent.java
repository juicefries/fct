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
// Data 2026/11/07 21:56
//

package io.github.juicefries.fct.callback.event;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.system.Callback;

public final class SizeEvent extends CallbackEventData {

    public final static String WINDOW_SIZE_EVENT = registerEventType("WindowSize");

    private int width = 0;
    private int height = 0;

    private SizeEvent() {

    }

    public static @NotNull SizeEvent size(long window, int width, int height) {
        var e = new SizeEvent();
        e.window = window;
        e.type = WINDOW_SIZE_EVENT;
        e.width = width;
        e.height = height;
        return e;
    }

    public static @NotNull SizeEvent size(int width, int height) {
        return size(NULL_WINDOW,width,height);
    }

    @Contract(pure = true)
    public int width() {
        return width;
    }

    @Contract(pure = true)
    public int height() {
        return height;
    }

    @Override
    public @NotNull SizeEvent copy() {
        return size(window(),width(),height());
    }

    @Contract(pure = true)
    @Override
    public Class<? extends Callback> getRequiredType() {
        return GLFWWindowSizeCallback.class;
    }

}
