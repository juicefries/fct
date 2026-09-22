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
// Data 2026/09/18 23:04
//

package io.github.juicefries.fct.glfw;

import io.github.juicefries.fct.sign.ApiSign;
import io.github.juicefries.fct.util.Lock;
import io.github.juicefries.fct.util.Util;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwWaitEvents;

@ApiSign.InternalApi(since = "0.0.5")
public class _GL_API {


    private final static Lock stateLock = Lock.create();

    public static void _bind(long window) {
        synchronized (stateLock) {
            GLFW.glfwMakeContextCurrent(window);
            GL.createCapabilities();
        }
    }

    public static void _swap_interval(boolean interval) {
        final int _interval__ = Util.match(interval,GLFW.GLFW_TRUE,GLFW.GLFW_FALSE);
        synchronized (stateLock) {
            glfwSwapInterval(_interval__);
        }

    }

    public static void _poll_events() {
        synchronized (stateLock) {
            glfwPollEvents();
        }
    }

    public static void _glfw_wait_events() {
        synchronized (stateLock) {
            glfwWaitEvents();
        }
    }

    public static void _wait_events_timeout(double timeout) {
        synchronized (stateLock) {
            glfwWaitEventsTimeout(timeout);
        }
    }

}
