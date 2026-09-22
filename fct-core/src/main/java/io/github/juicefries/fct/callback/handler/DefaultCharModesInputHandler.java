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
// Data 2026/08/25 22:39
//

package io.github.juicefries.fct.callback.handler;

import io.github.juicefries.fct.callback.CallbackData;
import io.github.juicefries.fct.callback.KeyCallback;
import io.github.juicefries.fct.callback.KeyData;
import io.github.juicefries.fct.logging.LoggerFactory;
import io.github.juicefries.fct.util.Array;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFWCharModsCallback;

public final class DefaultCharModesInputHandler extends CharModesInputHandler{

    private final static Logger logger = LoggerFactory.getLogger(DefaultCharModesInputHandler.class);

    @Override
    public void simulate(CallbackData data) {
        if (data == null) {
            throw new NullPointerException("data is null!");
        }
        if (data.getType() != KeyData.KEY_CHAR_MODES_INPUT_EVENT) return;
        if (!(data instanceof KeyData keyData)) return;
        KeyData kcd = KeyData.check(getUpdateData(),keyData);
        forEvents(kcd.copy());
    }

    @Override
    public void invoke(long window, int codepoint,int mods) {
        if (!getUpdateData().getGlfwCallbackList().isEmpty()) {
            KeyData data = KeyData.charModesInput(window, (char) codepoint,mods);
            forEvents(data.copy());
        }
        if (!getUpdateData().isKeep()) return;
        if (getUpdateData().getGlfwCallbacks().isEmpty()) return;
        getUpdateData().getGlfwCallbacks().forEach(callback -> {
            if (callback == null || !getUpdateData().isKeep() || callback == this) return;
            if (!(callback instanceof GLFWCharModsCallback keyCallback)) return;

            try {
                keyCallback.invoke(window,codepoint,mods);
            } catch (Exception e) {
                logger.error("Send an error when invoking the callback.", e);
            }
        });
    }

    void forEvents(@NotNull KeyData data) {
        var list = getUpdateData().getGlfwCallbackList();
        var callbacks = list.getCallbacks(KeyCallback.class);
        Array.forArr(callbacks, callback -> {
            try {
                callback.inputModsKey(data.copy());
            } catch (Exception e) {
                logger.error("An error occurred while handling the callback.", e);
            }
        });
    }

}
