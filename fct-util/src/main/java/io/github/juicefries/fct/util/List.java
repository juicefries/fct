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
// Data 2026/11/07 16:25
//

package io.github.juicefries.fct.util;


import io.github.juicefries.fct.sign.CopyFailedException;
import io.github.juicefries.fct.sign.Copyable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.function.Consumer;

public class List<E> implements Copyable {

    protected final static Object[] NULL_ARRAY = new Object[0];
    protected final Lock lock = Lock.create("UtilListLock");
    protected Object[] elements = new Object[0];

    @Contract(pure = true)
    public List() {

    }

    @Contract(value = " -> new", pure = true)
    public static <E> @NotNull List<E> build() {
        return new List<>();
    }

    // ========================= OTM =========================

    public <T extends E> void add(Class<T> type, T element) {
        if (type == null) {
            throw new NullPointerException("type is null!");
        }
        if (element == null) {
            throw new NullPointerException("element is null!");
        }

        if (!type.isInstance(element)) {
            throw new IllegalArgumentException("element " + element + " is not of type " + type);
        }
        synchronized (lock) {
            if (elements.length == 0) {
                elements = new Object[]{type, element};
                return;
            }
            int i = elements.length;
            Object[] tmp = new Object[i + 2];
            System.arraycopy(elements, 0, tmp, 0, i);

            tmp[i] = type;
            tmp[i + 1] = element;

            elements = tmp;
        }
    }

    public void clear() {
        synchronized (lock) {
            elements = NULL_ARRAY;
        }
    }

    public <T extends E> void remove(Class<T> type, T element) {
        if (type == null) {
            throw new NullPointerException("type is null!");
        }
        if (element == null) {
            throw new NullPointerException("element is null!");
        }

        if (!type.isInstance(element)) {
            throw new IllegalArgumentException("element " + element + " is not of type " + type);
        }

        //noinspection DuplicatedCode
        synchronized (lock) {
            int index = -1;
            for (int i = elements.length - 2; i >= 0; i -= 2) {
                if ((elements[i] == type) && (elements[i + 1].equals(element))) {
                    index = i;
                    break;
                }
            }

            if (index != -1) {
                Object[] tmp = new Object[elements.length - 2];
                System.arraycopy(elements, 0, tmp, 0, index);
                if (index < tmp.length)
                    System.arraycopy(elements, index + 2,
                            tmp, index, tmp.length - index
                    );
                elements = (tmp.length == 0) ? NULL_ARRAY : tmp;
            }
        }
    }

    // ========================= SET =========================

    // ========================= GET =========================

    public <T extends E> T[] getElements(Class<T> t) {
        Object[] lList = elements;
        int n = getElementsCount(lList, t);
        @SuppressWarnings("unchecked")
        T[] result = (T[]) Array.newInstance(t, n);
        int j = 0;
        for (int i = lList.length-2; i>=0; i-=2) {
            if (lList[i] == t) {
                @SuppressWarnings("unchecked")
                T tmp = (T) lList[i+1];
                result[j++] = tmp;
            }
        }
        return result;
    }

    @Contract(pure = true)
    public Object[] getElements() {
        return elements;
    }

    @Contract(pure = true)
    public boolean isEmpty() {
        return elements.length <= 1;
    }

    @Contract(pure = true)
    public int getElementsCount(Class<?> t) {
        Object[] lList = elements;
        return getElementsCount(lList, t);
    }

    @Contract(pure = true)
    public int getElementsCount() {
        return elements.length/2;
    }

    // ========================= UTIL =========================

    @Contract(pure = true)
    private int getElementsCount(Object @NotNull [] list, Class<?> t) {
        int count = 0;
        for (int i = 0; i < list.length; i+=2) {
            if (t == list[i])
                count++;
        }
        return count;
    }

    @FunctionalInterface
    public interface TraverseElements<E> {
        void traverse(Class<E> type,E elements);
    }

    public void forList(TraverseElements<E> traverse) {
        Object[] src;
        synchronized (lock) {
            if (elements.length == 0) {
                return;
            }
            src = elements;
        }

        for (int i = 0; i < src.length; i += 2) {
            //noinspection unchecked
            var type = (Class<E>) src[i];
            //noinspection unchecked
            var element = (E) src[i + 1];

            traverse.traverse(type,element);
        }
    }

    public void forList(Consumer<E> consumer) {
        forList((_, elements) -> {
            consumer.accept(elements);
        });
    }

    @Override
    public List<E> copy() {
        List<E> eList = build();
        forList(eList::add);
        return eList;
    }

    @Override
    @SuppressWarnings("deprecation")
    @Deprecated(since = "1.0.7",forRemoval = true)
    public void copy(Copyable template) throws CopyFailedException {
        if (template == null) {
            throw new CopyFailedException("list is null!");
        }
        try {
            //noinspection rawtypes
            if (!(template instanceof List list)) {
                throw new CopyFailedException("Incorrect template object!");
            }
            list.clear();
            //noinspection unchecked
            forList(list::add);
        } catch (Exception e) {
            throw new CopyFailedException("Copy failed!",e);
        }
    }

    // ========================= EEE =========================

    public String toString() {
        Object[] lList = elements;
        StringBuilder s = new StringBuilder("UtilList: ");
        s.append(lList.length / 2).append(" elements: ");
        for (int i = 0 ; i <= lList.length-2 ; i+=2) {
            s.append(" type ").append(((Class<?>) lList[i]).getCanonicalName());
            s.append(" element ").append(lList[i + 1]);
        }
        return s.toString();
    }
}
