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
// Data 2026/09/22 19:20
//

package io.github.juicefries.fct.event;

import io.github.juicefries.fct.util.Util;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class NullEvent implements EventData {

    public final static long NULL_EVENT = Util.turn("Null");

    long type;

    @Contract(pure = true)
    NullEvent() {}

    public static @NotNull NullEvent Null() {
        var e = new NullEvent();
        e.type = NULL_EVENT;
        return e;
    }

    @Contract(pure = true)
    @Override
    public long getType() {
        return type;
    }

    @Override
    public @NotNull EventData copy() {
        var e = new NullEvent();
        e.type = type;
        return e;
    }

    @Override
    public NullEvent clone() {
        return Util.cloneFailed(() -> {
            NullEvent e = (NullEvent) super.clone();
            e.type = type;
            return e;
        });
    }
}
