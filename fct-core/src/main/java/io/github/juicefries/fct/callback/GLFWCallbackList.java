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

package io.github.juicefries.fct.callback;

import io.github.juicefries.fct.event.EventListenerList;
import io.github.juicefries.fct.util.Lock;
import java.lang.reflect.Array;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * 别看文档了,代码直接抄的{@link EventListenerList}
 * @since 0.0.5
 */
public final class GLFWCallbackList {

    private final static Object[] NULL_ARRAY = new Object[0];
    private final static Lock lock = Lock.create();

    Object[] callbacks = new Object[0];

    @Contract(pure = true)
    public GLFWCallbackList() {

    }

    // ========================= OTM =========================

    public <T extends GLFWCallback> void add(Class<T> t, T c) {
        if (t == null || c == null) return;
        if (!t.isInstance(c)) throw new IllegalArgumentException("GLFWCallback " + c + " is not of type " + t);
        synchronized (lock) {
            if (callbacks.length == 0) {
                callbacks = new Object[]{t, c};
                return;
            }
            int i = callbacks.length;
            Object[] tmp = new Object[i + 2];
            System.arraycopy(callbacks, 0, tmp, 0, i);

            tmp[i] = t;
            tmp[i + 1] = c;

            callbacks = tmp;
        }
    }

    public <T extends GLFWCallback> void remove(Class<T> t, T c) {
        if (c ==null) return;
        if (!t.isInstance(c)) throw new IllegalArgumentException("GLFWCallback " + c + " is not of type " + t);

        synchronized (lock) {
            int index = -1;
            for (int i = callbacks.length - 2; i >= 0; i -= 2) {
                if ((callbacks[i] == t) && (callbacks[i + 1].equals(c))) {
                    index = i;
                    break;
                }
            }

            if (index != -1) {
                Object[] tmp = new Object[callbacks.length - 2];
                System.arraycopy(callbacks, 0, tmp, 0, index);
                if (index < tmp.length)
                    System.arraycopy(callbacks, index + 2,
                            tmp, index, tmp.length - index
                    );
                callbacks = (tmp.length == 0) ? NULL_ARRAY : tmp;
            }
        }
    }

    // ========================= SET =========================

    // ========================= GET =========================

    public <T extends GLFWCallback> T[] getCallbacks(Class<T> t) {
        Object[] lList = callbacks;
        int n = getCallbackCount(lList, t);
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

    @Contract(pure = true)
    public Object[] getCallbackList() {
        return callbacks;
    }

    @Contract(pure = true)
    public boolean isEmpty() {
        return callbacks.length <= 1;
    }

    @Contract(pure = true)
    public int getCallbackCount(Class<?> t) {
        Object[] lList = callbacks;
        return getCallbackCount(lList, t);
    }

    @Contract(pure = true)
    public int getCallbackCount() {
        return callbacks.length/2;
    }

    // ========================= UTIL =========================

    @Contract(pure = true)
    private int getCallbackCount(Object @NotNull [] list, Class<?> t) {
        int count = 0;
        for (int i = 0; i < list.length; i+=2) {
            if (t == list[i])
                count++;
        }
        return count;
    }

    // ========================= EEE =========================

    public String toString() {
        Object[] lList = callbacks;
        StringBuilder s = new StringBuilder("GLFWCallbackList: ");
        s.append(lList.length / 2).append(" callbacks: ");
        for (int i = 0 ; i <= lList.length-2 ; i+=2) {
            s.append(" type ").append(((Class<?>) lList[i]).getCanonicalName());
            s.append(" callback ").append(lList[i + 1]);
        }
        return s.toString();
    }

}

