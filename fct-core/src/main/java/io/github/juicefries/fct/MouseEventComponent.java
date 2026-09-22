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
// Data 2026/09/20 16:59
//

package io.github.juicefries.fct;

import io.github.juicefries.fct.event.MouseButtonListener;
import io.github.juicefries.fct.event.MouseCursorListener;
import io.github.juicefries.fct.event.MouseDropListener;
import io.github.juicefries.fct.event.MouseEntersListener;
import io.github.juicefries.fct.event.MouseWheelListener;

public interface MouseEventComponent extends EventComponent {

    // ========================= ADD =========================

    void addMouseButtonListener(MouseButtonListener l);

    void addMouseWheelListener(MouseWheelListener l);

    void addMouseCursorListener(MouseCursorListener l);

    void addMouseDropListener(MouseDropListener l);

    void addMouseEntersListener(MouseEntersListener l);

    // ========================= REMOVE =========================

    void removeMouseButtonListener(MouseButtonListener l);

    void removeMouseWheelListener(MouseWheelListener l);

    void removeMouseCursorListener(MouseCursorListener l);

    void removeMouseDropListener(MouseDropListener l);

    void removeMouseEntersListener(MouseEntersListener l);

    // ========================= GET =========================

    default MouseButtonListener[] getMouseButtonListeners() {
        return getListeners(MouseButtonListener.class);
    }

    default MouseWheelListener[] getMouseWheelListeners() {
        return getListeners(MouseWheelListener.class);
    }

    default MouseCursorListener[] getMouseCursorListeners() {
        return getListeners(MouseCursorListener.class);
    }

    default MouseDropListener[] getMouseDropListeners() {
        return getListeners(MouseDropListener.class);
    }

    default MouseEntersListener[] getMouseEntersListeners() {
        return getListeners(MouseEntersListener.class);
    }
}
