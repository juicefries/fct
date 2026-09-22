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
// Data 2026/08/23 22:09
//

package io.github.juicefries.fct.callback;

import io.github.juicefries.fct.callback.handler.DefaultButtonHandler;
import io.github.juicefries.fct.callback.handler.DefaultCharInputHandler;
import io.github.juicefries.fct.callback.handler.DefaultCharModesInputHandler;
import io.github.juicefries.fct.callback.handler.DefaultCloseHandler;
import io.github.juicefries.fct.callback.handler.DefaultContentScaleHandler;
import io.github.juicefries.fct.callback.handler.DefaultCursorEnterHandler;
import io.github.juicefries.fct.callback.handler.DefaultCursorPosHandler;
import io.github.juicefries.fct.callback.handler.DefaultDropHandler;
import io.github.juicefries.fct.callback.handler.DefaultFocusHandler;
import io.github.juicefries.fct.callback.handler.DefaultFramebufferSizeHandler;
import io.github.juicefries.fct.callback.handler.DefaultKeyInputHandler;
import io.github.juicefries.fct.callback.handler.DefaultMaximizeHandler;
import io.github.juicefries.fct.callback.handler.DefaultMinimizedHandler;
import io.github.juicefries.fct.callback.handler.DefaultPosHandler;
import io.github.juicefries.fct.callback.handler.DefaultRefreshHandler;
import io.github.juicefries.fct.callback.handler.DefaultScrollHandler;
import io.github.juicefries.fct.callback.handler.DefaultSizeHandler;

import java.util.concurrent.ConcurrentHashMap;

public final class Handlers extends ConcurrentHashMap<String,Class<? extends Handler>> {

    private final static Handlers defaults = new Handlers(false);

    static {
        // 窗口
        defaults.put(Handler.CLOSE, DefaultCloseHandler.class);
        defaults.put(Handler.FOCUS, DefaultFocusHandler.class);
        defaults.put(Handler.SIZE, DefaultSizeHandler.class);
        defaults.put(Handler.FRAME_BUFFER_SIZE, DefaultFramebufferSizeHandler.class);
        defaults.put(Handler.POS, DefaultPosHandler.class);
        defaults.put(Handler.REFRESH, DefaultRefreshHandler.class);
        defaults.put(Handler.CONTENT_SCALE, DefaultContentScaleHandler.class);
        defaults.put(Handler.MAXIMIZE, DefaultMaximizeHandler.class);
        defaults.put(Handler.MINIMIZED, DefaultMinimizedHandler.class);

        // 键盘
        defaults.put(Handler.KEY_INPUT, DefaultKeyInputHandler.class);
        defaults.put(Handler.CHAR_INPUT, DefaultCharInputHandler.class);
        defaults.put(Handler.CHAR_MODES_INPUT, DefaultCharModesInputHandler.class);

        // 鼠标
        defaults.put(Handler.SCROLL, DefaultScrollHandler.class);
        defaults.put(Handler.CURSOR_POS, DefaultCursorPosHandler.class);
        defaults.put(Handler.DROP, DefaultDropHandler.class);
        defaults.put(Handler.CURSOR_ENTER, DefaultCursorEnterHandler.class);
        defaults.put(Handler.BUTTON, DefaultButtonHandler.class);
    }


    public Handlers(boolean loadDefaults) {
        if (loadDefaults) {
            putAll(Handlers.defaults);
        }
    }

    public Handlers() {
        this(true);
    }

}
