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

import io.github.juicefries.fct.Sys;
import io.github.juicefries.fct.logging.LoggerFactory;
import io.github.juicefries.fct.util.Lock;
import java.lang.reflect.Array;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class EventListenerList {

    private final static Object[] NULL_ARRAY = new Object[0];
    private final static Lock lock = Lock.create();
    private static final Logger log = LoggerFactory.getLogger(EventListenerList.class);

    Object[] listeners = new Object[0];

    public EventListenerList() {

    }
    // ========================= OTM =========================

    public <T extends Listener> void add(Class<T> t,T l) {
        if (t == null || l == null) return;
        if (!t.isInstance(l)) throw new IllegalArgumentException("Listener " + l + " is not of type " + t);
        synchronized (lock) {
            if (listeners.length == 0) {
                listeners = new Object[]{t, l};
                return;
            }
            int i = listeners.length;
            Object[] tmp = new Object[i + 2];
            System.arraycopy(listeners, 0, tmp, 0, i);

            tmp[i] = t;
            tmp[i + 1] = l;

            listeners = tmp;

            if (t == SystemListener.class) {
                log.warn("Dangerous operation, such listeners should not be passed into add()!");
                Sys.register((SystemListener) l);
            }
        }
    }

    public <T extends Listener> void remove(Class<T> t, T l) {
        if (l ==null) return;
        if (!t.isInstance(l)) throw new IllegalArgumentException("Listener " + l + " is not of type " + t);

        synchronized (lock) {
            int index = -1;
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if ((listeners[i] == t) && (listeners[i + 1].equals(l))) {
                    index = i;
                    break;
                }
            }

            if (index != -1) {
                Object[] tmp = new Object[listeners.length - 2];
                System.arraycopy(listeners, 0, tmp, 0, index);
                if (index < tmp.length)
                    System.arraycopy(listeners, index + 2,
                            tmp, index, tmp.length - index
                    );
                listeners = (tmp.length == 0) ? NULL_ARRAY : tmp;
            }
            if (t == SystemListener.class) {
                log.warn("Dangerous operation, such listeners should not be passed into remove()!");
                Sys.cancel((SystemListener) l);
            }
        }
    }

    // ========================= SET =========================

    // ========================= GET =========================

    public <T extends Listener> T[] getListeners(Class<T> t) {
        Object[] lList = listeners;
        int n = getListenerCount(lList, t);
        @SuppressWarnings("unchecked")
        T[] result = (T[]) Array.newInstance(t, n);
        int j = 0;
        for (int i = lList.length-2; i>=0; i-=2) {
            if (lList[i] == t) {
                @SuppressWarnings("unchecked")
                T tmp = (T)lList[i+1];
                result[j++] = tmp;
            }
        }
        return result;
    }

    public Object[] getListenerList() {
        return listeners;
    }

    public boolean isEmpty() {
        return listeners.length <= 1;
    }

    public int getListenerCount(Class<?> t) {
        Object[] lList = listeners;
        return getListenerCount(lList, t);
    }

    public int getListenerCount() {
        return listeners.length/2;
    }

    // ========================= UTIL =========================

    @Contract(pure = true)
    private int getListenerCount(Object @NotNull [] list, Class<?> t) {
        int count = 0;
        for (int i = 0; i < list.length; i+=2) {
            if (t == list[i])
                count++;
        }
        return count;
    }

    // ========================= EEE =========================

    public String toString() {
        Object[] lList = listeners;
        StringBuilder s = new StringBuilder("EventListenerList: ");
        s.append(lList.length / 2).append(" listeners: ");
        for (int i = 0 ; i <= lList.length-2 ; i+=2) {
            s.append(" type ").append(((Class<?>) lList[i]).getCanonicalName());
            s.append(" listener ").append(lList[i + 1]);
        }
        return s.toString();
    }

}

