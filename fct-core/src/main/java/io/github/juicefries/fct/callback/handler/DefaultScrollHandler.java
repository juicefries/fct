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
// Data 2026/08/29 02:26
//

package io.github.juicefries.fct.callback.handler;

import io.github.juicefries.fct.callback.CallbackData;
import io.github.juicefries.fct.callback.MouseData;
import io.github.juicefries.fct.callback.MouseScrollCallback;
import io.github.juicefries.fct.logging.LoggerFactory;
import io.github.juicefries.fct.util.Array;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.system.Callback;

public final class DefaultScrollHandler extends ScrollHandler {

    private final static Logger logger = LoggerFactory.getLogger(DefaultScrollHandler.class);

    @Override
    public void simulate(CallbackData data) {
        if (data == null) {
            throw new NullPointerException("data is null!");
        }
        if (data.getType() != MouseData.MOUSE_SCROLL_EVENT) return;
        if (!(data instanceof MouseData md)) return;

        MouseData msd = MouseData.check(getUpdateData(),md);
        forEvents(msd.copy());
    }

    @Override
    public void invoke(long window, double xoffset, double yoffset) {
        if (!getUpdateData().getGlfwCallbackList().isEmpty()) {
            MouseData data = MouseData.scroll(window, xoffset, yoffset);
            forEvents(data.copy());
        }

        if (!getUpdateData().isKeep()) return;
        if (getUpdateData().getGlfwCallbacks().isEmpty()) return;

        for (Callback callback : getUpdateData().getGlfwCallbacks()) {
            if (callback == null) continue;
            if (!getUpdateData().isKeep()) continue;
            if (callback == this) continue;

            if (!(callback instanceof GLFWScrollCallback callbackI)) continue;
            try {
                callbackI.invoke(window, xoffset, yoffset);
            } catch (Exception e) {
                logger.error("Send an error when invoking the callback.", e);
            }

        }
    }

    private void forEvents(MouseData data) {
        var list = getUpdateData().getGlfwCallbackList();
        var callbacks = list.getCallbacks(MouseScrollCallback.class);
        Array.forArr(callbacks, callback -> {
            try {
                callback.scroll(data.copy());
            } catch (Exception e) {
                logger.error("An error occurred while handling the callback.", e);
            }
        });
    }

}
