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
// Data 2026/11/07 23:17
//

package io.github.juicefries.fct.callback.handler;

import io.github.juicefries.fct.callback.Event;
import io.github.juicefries.fct.callback.event.CallbackEventData;
import io.github.juicefries.fct.callback.event.RefreshEvent;
import io.github.juicefries.fct.callback.listener.RefreshListener;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWWindowRefreshCallback;

public final class RefreshHandler extends GLFWWindowRefreshCallback implements Handler {

    public RefreshHandler() {

    }

    @Override
    public void register(long window) {
        check(GLFW.glfwSetWindowRefreshCallback(window,this));
    }

    @Override
    public void simulate(@NotNull CallbackEventData event) {
        invoke(event.window());
    }

    @Override
    public void invoke(long window) {
        var container = Event.get(window);
        if (container == null) return;
        var listeners = container.getListeners(RefreshListener.class);
        var e = RefreshEvent.refresh(window);
        for (var l : listeners) {
            if (l == null) continue;
            l.refresh(e.copy());
        }
    }

}
