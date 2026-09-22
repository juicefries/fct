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
// Data 2026/09/10 20:07
//

package io.github.juicefries.fct.event;

import io.github.juicefries.fct.sign.Copyable;
import io.github.juicefries.fct.sign.Readonly;
import io.github.juicefries.fct.util.Util;
import java.util.Objects;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class SysEvent implements EventData, Copyable, Readonly {

    public final static long SYS_INIT_EVENT = Util.turn("SysEvent-Init");
    public final static long SYS_TERMINATE_EVENT = Util.turn("SysEvent-Terminate");
    public final static long SYS_CANCEL_EVENT = Util.turn("SysEvent-Cancel");

    long type;

    SysEvent() {

    }

    public SysEvent(long type) {
        this.type = type;
    }

    @Override
    public long getType() {
        return type;
    }

    @Override
    public @NotNull SysEvent clone() {
        try {
            SysEvent e = (SysEvent) super.clone();
            e.type = type;
            return e;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("clone failed!", e);
        }
    }

    @Override
    public @NotNull SysEvent copy() {
        SysEvent e = new SysEvent();
        e.type = type;
        return e;
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName() + "[type=" + type + "]";
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        SysEvent sysEvent = (SysEvent) object;
        return getType() == sysEvent.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getType());
    }

    @Contract(value = " -> new", pure = true)
    public static @NotNull SysEvent terminate() {
        return new SysEvent(SYS_TERMINATE_EVENT);
    }

    @Contract(value = " -> new", pure = true)
    public static @NotNull SysEvent cancel() {
        return new SysEvent(SYS_CANCEL_EVENT);
    }

    @Contract(value = " -> new", pure = true)
    public static @NotNull SysEvent init() {
        return new SysEvent(SYS_INIT_EVENT);
    }

    public static @NotNull String toType(long type) {
        if (type == SYS_CANCEL_EVENT) {
            return "SysEvent-Cancel";
        } else if (type == SYS_TERMINATE_EVENT) {
            return "SysEvent-Terminate";
        } else if (type == SYS_INIT_EVENT) {
            return "SysEvent-Init";
        } else {
            throw new IllegalArgumentException("Wrong type!");
        }
    }

}
