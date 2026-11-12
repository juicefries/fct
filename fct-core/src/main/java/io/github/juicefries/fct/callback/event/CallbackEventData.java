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
// Data 2026/11/07 02:55
//

package io.github.juicefries.fct.callback.event;

import io.github.juicefries.fct.sign.ApiSign;
import io.github.juicefries.fct.sign.Copyable;
import io.github.juicefries.fct.sign.Readonly;
import io.github.juicefries.fct.util.Lock;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryUtil;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class CallbackEventData implements Copyable, Readonly  {

    private final static Lock ETL = Lock.create("EventTypeLock");
    private final static Set<String> types = ConcurrentHashMap.newKeySet();
    public final static long NULL_WINDOW = MemoryUtil.NULL;

    protected long window = NULL_WINDOW;
    protected String type;

    @Contract(pure = true)
    protected CallbackEventData() {

    }

    public long getWindow() {
        return window;
    }

    public long window() {
        return window;
    }

    public String getType() {
        return type;
    }

    public String type() {
        return type;
    }

    public abstract CallbackEventData copy();
    public abstract Class<? extends Callback> getRequiredType();

    public Class<? extends Callback>[] getRequiredTypes() {
        //noinspection unchecked
        return new Class[]{getRequiredType()};
    }

    @ApiSign.InternalApi
    public void correction(long window) {
        correction(window,this);
    }

    @Contract("_, null -> fail")
    public static void correction(long window, CallbackEventData e) {
        if (e == null) {
            throw new NullPointerException("event is null!");
        }
        if (e.window != window) {
            e.window = window;
        }
    }

    public static String[] getEventTypes() {
        synchronized (ETL) {
            return types.toArray(String[]::new);
        }
    }

    @Contract("null -> fail")
    public static @NotNull String registerEventType(String type) {
        if (type == null) {
            throw new NullPointerException("type is null!");
        }
        if (type.isBlank()) {
            throw new IllegalArgumentException("The event type cannot be empty or a blank character!");
        }
        synchronized (ETL) {
            var val = "CallbackEvent-%s".formatted(type);
            if (types.contains(val)) {
                throw new IllegalArgumentException("This event type already exists! type:%s.".formatted(val));
            }
            types.add(val);
            return val;
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    public static String[] toEventTypes() {
        synchronized (ETL) {
            return types.toArray(String[]::new);
        }
    }

}
