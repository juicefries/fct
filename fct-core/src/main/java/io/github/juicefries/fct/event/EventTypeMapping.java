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
// Data 2026/11/07 02:21
//

package io.github.juicefries.fct.event;

import io.github.juicefries.fct.sign.Readonly;
import io.github.juicefries.fct.util.Util;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 刚写就扔也是没谁了
 */
@Deprecated(since = "1.0.7")
public final class EventTypeMapping implements Readonly {

    public final String eventName;
    public final long eventType;

    @Contract(pure = true)
    private EventTypeMapping(String eventName, long eventType) {
        this.eventName = eventName;
        this.eventType = eventType;
    }


    @Contract("null -> fail")
    public @NotNull EventTypeMapping create(String name) {
        if (name == null) {
            throw new NullPointerException("name is null!");
        }
        if (name.isBlank()) {
            throw new IllegalArgumentException("The event name cannot be empty or a blank character!");
        }
        long val = Util.turn(name);
        return new EventTypeMapping(name,val);
    }


    @Contract(pure = true)
    public long getEventType() {
        return eventType;
    }

    @Contract(pure = true)
    public String getEventName() {
        return eventName;
    }

    @Contract(pure = true)
    public long eventType() {
        return getEventType();
    }

    @Contract(pure = true)
    public String eventName() {
        return getEventName();
    }

    @Override
    @Contract(pure = true)
    public @NotNull String toString() {
        return "EventTypeMapping[eventName='%s', eventType=%d]".formatted(eventName, eventType);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EventTypeMapping that = (EventTypeMapping) o;
        return getEventType() == that.getEventType() && Objects.equals(getEventName(), that.getEventName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEventName(), getEventType());
    }

}
