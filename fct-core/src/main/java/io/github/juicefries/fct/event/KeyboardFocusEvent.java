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
// Data 2026/10/07 19:00
//

package io.github.juicefries.fct.event;

import io.github.juicefries.fct.sign.CopyFailedException;
import io.github.juicefries.fct.sign.Copyable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class KeyboardFocusEvent implements EventData {

    public final static long KEY_GAIN_FOCUS_EVENT = _event_data_util._build_event_id("KFED-GainFocus");
    public final static long KEY_LOSE_FOCUS_EVENT = _event_data_util._build_event_id("KFED-LoseFocus");

    long type;

    @Contract(pure = true)
    private KeyboardFocusEvent() {}

    public static @NotNull KeyboardFocusEvent gain() {
        KeyboardFocusEvent e = new KeyboardFocusEvent();
        e.type = KEY_GAIN_FOCUS_EVENT;
        return e;
    }

    public static @NotNull KeyboardFocusEvent lose() {
        KeyboardFocusEvent e = new KeyboardFocusEvent();
        e.type = KEY_LOSE_FOCUS_EVENT;
        return e;
    }


    @Override
    public long getType() {
        return type;
    }

    @Override
    public EventData copy() {
        KeyboardFocusEvent e = new KeyboardFocusEvent();
        e.type = getType();
        return e;
    }

    @Override
    public void copy(Copyable template) throws CopyFailedException {
        if (template == null) {
            throw new CopyFailedException("event is null!");
        }
        if (!(template instanceof KeyboardFocusEvent e)) {
            throw new CopyFailedException("Incorrect template object!");
        }
        e.type = type;
    }

    @Override
    public KeyboardFocusEvent clone() {
        try {
            var e = (KeyboardFocusEvent) super.clone();
            e.type = type;
            return e;
        } catch (Exception e) {
            throw new RuntimeException("clone failed!", e);
        }
    }

}
