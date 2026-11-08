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
// Data 2026/11/07 22:50
//

package io.github.juicefries.fct.callback.event;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFWWindowPosCallback;
import org.lwjgl.system.Callback;

public final class PosEvent extends CallbackEventData {

    public final static String WINDOW_POS_EVENT = registerEventType("Pos");

    int x = 0;
    int y = 0;

    private PosEvent() {

    }

    public static @NotNull PosEvent pos(long window, int x, int y) {
        var e = new PosEvent();
        e.window = window;
        e.type = WINDOW_POS_EVENT;
        e.x = x;
        e.y = y;
        return e;
    }

    public static @NotNull PosEvent pos(int x, int y) {
        return pos(NULL_WINDOW,x,y);
    }

    @Contract(pure = true)
    public int x() {
        return x;
    }

    @Contract(pure = true)
    public int y() {
        return y;
    }


    @Override
    public @NotNull PosEvent copy() {
        return pos(window(),x(),y());
    }

    @Override
    @Contract(pure = true)
    public Class<? extends Callback> getRequiredType() {
        return GLFWWindowPosCallback.class;
    }

}
