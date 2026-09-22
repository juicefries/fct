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

package io.github.juicefries.fct.callback;

import io.github.juicefries.fct.sign.Copyable;
import io.github.juicefries.fct.sign.Readonly;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface CallbackData extends Copyable,Cloneable, Readonly {

    @Contract(value = " -> new", pure = true)
    static long @NotNull [] toCallbackEventTypes() {
        return new long[]{
                WindowData.WINDOW_FOCUS_EVENT,
                WindowData.WINDOW_FRAME_BUFFER_SIZE_EVENT,
                WindowData.WINDOW_CLOSE_EVENT,
                WindowData.WINDOW_REFRESH_EVENT,
                WindowData.WINDOW_SIZE_EVENT,
                WindowData.WINDOW_POS_EVENT,
                WindowData.WINDOW_CONTENT_SCALE_EVENT,
                WindowData.WINDOW_MAXIMIZE_EVENT,
                WindowData.WINDOW_MINIMIZED_EVENT,
                MouseData.MOUSE_BUTTON_EVENT,
                MouseData.MOUSE_CURSOR_ENTER_EVENT,
                MouseData.MOUSE_DROP_EVENT,
                MouseData.MOUSE_CURSOR_POS_EVENT,
                MouseData.MOUSE_SCROLL_EVENT,
                KeyData.KEY_INPUT_EVENT,
                KeyData.KEY_CHAR_INPUT_EVENT,
                KeyData.KEY_CHAR_MODES_INPUT_EVENT,
        };
    }

    @Override
    CallbackData copy();
    CallbackData clone();

    long getWindow();
    long getType();
    default long window() {
        return getWindow();
    }

    default long type() {
        return getType();
    }
}
