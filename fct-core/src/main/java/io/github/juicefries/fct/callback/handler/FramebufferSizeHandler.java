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
// Data 2026/08/23 00:13
//

package io.github.juicefries.fct.callback.handler;

import io.github.juicefries.fct.callback.Event;
import io.github.juicefries.fct.callback.event.CallbackEventData;
import io.github.juicefries.fct.callback.event.FrameBufferSizeEvent;
import io.github.juicefries.fct.callback.listener.FrameBufferSizeListener;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;

public final class FramebufferSizeHandler extends GLFWFramebufferSizeCallback implements Handler {

    public FramebufferSizeHandler() {

    }

    @Override
    public void register(long window) {
        check(GLFW.glfwSetFramebufferSizeCallback(window,this));
    }

    @Override
    public void simulate(@NotNull CallbackEventData event) {
        var container = Event.get(event.window());
        if (container == null) return;
        var listeners = container.getListeners(FrameBufferSizeListener.class);
        if (!(event instanceof FrameBufferSizeEvent wfbse)) return;
        for (var l : listeners) {
            if (l == null) continue;
            l.size(wfbse.copy());
        }
    }

    @Override
    public void invoke(long window, int width, int height) {
        var container = Event.get(window);
        if (container == null) return;
        var listeners = container.getListeners(FrameBufferSizeListener.class);
        var e = FrameBufferSizeEvent.size(window,width,height);
        for (var l : listeners) {
            if (l == null) continue;
            l.size(e.copy());
        }
    }

}
