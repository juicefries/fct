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
// Data 2026/08/25 18:52
//

package io.github.juicefries.fct.callback.handler;

import io.github.juicefries.fct.callback.CallbackData;
import io.github.juicefries.fct.callback.WindowData;
import io.github.juicefries.fct.callback.WindowToggleCallback;
import io.github.juicefries.fct.logging.LoggerFactory;
import io.github.juicefries.fct.util.Array;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.lwjgl.glfw.GLFWWindowIconifyCallback;

public final class DefaultMinimizedHandler extends MinimizedHandler implements UnqualifiedWindowHandlerI {
    private final static Logger logger = LoggerFactory.getLogger(DefaultMinimizedHandler.class);

    @Contract("null -> fail")
    @Override
    public void simulate(CallbackData data) {
        if (data == null) {
            throw new NullPointerException("data is null!");
        }
        if (data.getType() != WindowData.WINDOW_MINIMIZED_EVENT) return;
        if (!(data instanceof WindowData wd)) return;

        WindowData wmd = WindowData.check(getUpdateData(),wd);
        forEvents(wmd.copy());
    }

    @Override
    public void invoke(long window, boolean iconified) {
        if (!getUpdateData().getGlfwCallbackList().isEmpty()) {
            WindowData data = WindowData.minimized(window,iconified);
            forEvents(data);
        }
        forGlfwList(GLFWWindowIconifyCallback.class, iconify -> {
            try {
                iconify.invoke(window, iconified);
            } catch (Exception e) {
                logger.error("Send an error when invoking the callback.", e);
            }
        });
    }

    void forEvents(WindowData data) {
        var list = getUpdateData().getGlfwCallbackList();
        var callbacks = list.getCallbacks(WindowToggleCallback.class);
        Array.forArr(callbacks, callback -> {
            try {
                if (data.isFocused()) {
                    callback.restored(data.copy());
                } else {
                    callback.minimized(data.copy());
                }
            } catch (Exception e) {
                logger.error("An error occurred while handling the callback.", e);
            }
        });
    }
}
