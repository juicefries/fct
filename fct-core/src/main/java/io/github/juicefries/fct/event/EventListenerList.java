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
// Data 2026/09/06 11:04
//

package io.github.juicefries.fct.event;

import io.github.juicefries.fct.util.List;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class EventListenerList extends List<Listener> {

    public EventListenerList() {

    }

    // ========================= OTM =========================

    @Override
    public <T extends Listener> void add(Class<T> t,T l) {
        if (t == null || l == null) return;
        if (!t.isInstance(l)) throw new IllegalArgumentException("Listener " + l + " is not of type " + t);
        super.add(t, l);
    }

    @Override
    public <T extends Listener> void remove(Class<T> t, T l) {
        if (l ==null) return;
        if (!t.isInstance(l)) throw new IllegalArgumentException("Listener " + l + " is not of type " + t);
        super.remove(t, l);
    }

    // ========================= GET =========================

    public <T extends Listener> T[] getListeners(Class<T> t) {
        return getElements(t);
    }

    @Contract(pure = true)
    public Object[] getListenerList() {
        return getElements();
    }

    @Contract(pure = true)
    public int getListenerCount(Class<?> t) {
        return getElementsCount(t);
    }

    @Contract(pure = true)
    public int getListenerCount() {
        return getElementsCount();
    }

    // ========================= EEE =========================

    @Override
    public @NotNull String toString() {
        Object[] lList = elements;
        StringBuilder s = new StringBuilder("EventListenerList: ");
        s.append(lList.length / 2).append(" listeners: ");
        for (int i = 0 ; i <= lList.length-2 ; i+=2) {
            s.append(" type ").append(((Class<?>) lList[i]).getCanonicalName());
            s.append(" listener ").append(lList[i + 1]);
        }
        return s.toString();
    }

}

