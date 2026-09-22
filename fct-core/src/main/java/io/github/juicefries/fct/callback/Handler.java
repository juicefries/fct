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
// Data 2026/08/22 19:38
//

package io.github.juicefries.fct.callback;

import org.lwjgl.system.Callback;

public interface Handler {

    // WINDOW
    String CLOSE = "Close";
    String SIZE = "Size";
    String FRAME_BUFFER_SIZE = "FramebufferSize";
    String FOCUS = "Focus";
    String REFRESH = "Refresh";
    String POS = "Pos";
    String CONTENT_SCALE = "ContentScale";
    String MAXIMIZE = "Maximize";
    String MINIMIZED = "Minimized";

    // KEY
    String KEY_INPUT = "KeyInput";
    String CHAR_INPUT = "CharInput";
    String CHAR_MODES_INPUT = "CharModesInput";

    // MOUSE
    String SCROLL = "Scroll";
    String CURSOR_POS = "CursorPos";
    String DROP = "Drop";
    String CURSOR_ENTER = "CursorEnter";
    String BUTTON = "Button";

    // HT
    String WINDOW_HANDLER = "WindowHandler";
    String MOUSE_HANDLER = "MouseHandler";
    String KEY_HANDLER = "KeyHandler";
    String NONE_HANDLER = "NoneHandler";

    Callback register(long window);
    void simulate(CallbackData data);
    void free();
    default void update(Data data) {
        if (data == null) return;
        this.getUpdateData().clear();
        this.getUpdateData().putAll(data.copy());
    }

    default void freeHandler() {

    }

    default Callback check(Callback callback) {
        if (callback == null) return null;
        if (callback == this) return null;
        return callback;
    }

    String getType();
    default String type() {
        return getType();
    }

    default String getHandlerType() {
        return NONE_HANDLER;
    }
    default String handlerType() {
        return getHandlerType();
    }
    Data getUpdateData();

}
