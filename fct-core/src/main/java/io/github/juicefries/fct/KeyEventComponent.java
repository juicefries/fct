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
// Data 2026/09/20 16:52
//

package io.github.juicefries.fct;

import io.github.juicefries.fct.event.KeyCharInputListener;
import io.github.juicefries.fct.event.KeyInputListener;

public interface KeyEventComponent extends EventComponent {

    // ========================= OTM =========================

    /**
     * 失去焦点
     * <p>
     *     失去键盘焦点，
     * </p>
     * @since 0.0.4
     */
    void loseFocus();

    /**
     * 获取键盘焦点
     * @since 0.0.4
     */
    void getFocus();

    // ========================= ADD =========================

    void addKeyCharInputListener(KeyCharInputListener l);

    void addKeyInputListener(KeyInputListener l);

    // ========================= REMOVE =========================

    void removeKeyInputListener(KeyInputListener l);

    void removeKeyCharInputListener(KeyCharInputListener l);

    // ========================= GET =========================

    boolean isFocus();

    default KeyInputListener[] getKeyInputListeners() {
        return getListeners(KeyInputListener.class);
    }

    default KeyCharInputListener[] getKeyCharInputListeners() {
        return getListeners(KeyCharInputListener.class);
    }

}
