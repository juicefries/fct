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
// Data 2026/09/18 22:49
//

package io.github.juicefries.fct.glfw;

import io.github.juicefries.fct.sign.ApiSign;
import io.github.juicefries.fct.util.Lock;
import java.util.Objects;


import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.glfw.GLFW.*;
import static io.github.juicefries.fct.Sys.checkInit;

@ApiSign.InternalApi(since = "0.0.5")
public final class _GLFW_API {

    private final static Lock createLock = Lock.create();



    public static long _create_window(int width, int height, String title, long monitor, long share, Hint... hints) {
        checkInit();
        var w = Math.max(width, 0);
        var h = Math.max(height,0);
        var t = Objects.requireNonNullElse(title, "");

        long window;
        synchronized (createLock) {
            if (hints != null) {
                Hint.reset();
                for (Hint hint : hints) {
                    if (hint == null) continue;
                    hint.apply();
                }
            }
            window = glfwCreateWindow(w, h, t, monitor, share);
        }
        if (window == NULL) {
            throw new IllegalArgumentException("GLFW window creation failed!");
        }
        return window;
    }

    public static long _create_window(int width, int height, String title, long monitor, Hint... hints) {
        return _create_window(width, height, title, monitor,NULL, hints);
    }

    public static long _create_window(int width, int height, String title, Hint... hints) {
        return _create_window(width,height,title,NULL,NULL,hints);
    }






}
