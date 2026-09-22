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
// Data 2026/09/16 14:18
//

package io.github.juicefries.fct.glfw;

import io.github.juicefries.fct.Sys;
import io.github.juicefries.fct.sign.ApiSign;
import io.github.juicefries.fct.util.Array;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;


import static org.lwjgl.system.MemoryUtil.NULL;

import static org.lwjgl.glfw.GLFW.*;

@ApiSign.NotRecommended(since = "0.0.4")
public class Mouse {

    public static @NotNull Vector2d getCursorPos(long window) {
        Sys.checkInit();
        var pos = new Vector2d();
        if (window == NULL){
            return pos;
        }
        var x = Array.createD(1);
        var y = Array.createD(1);
        glfwGetCursorPos(window,x,y);
        pos.x = x[0];
        pos.y = y[0];

        return pos;
    }



}
