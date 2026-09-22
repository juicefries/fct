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
// Data 2026/08/09 02:35
//

package io.github.juicefries.fct.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;


public class TraverseArrayList<E> implements List<E>, Cloneable {

    private final Class<E> elementType;
    private List<E> buffer;
    private E[] backingArray;
    private int size = 0;

    public TraverseArrayList(Class<E> elementType) {
        this.elementType = elementType;
    }

    public TraverseArrayList(final Class<E> elementType, final int capacity) {
        this.elementType = elementType;
        this.buffer = new ArrayList<>(capacity);
    }

    public TraverseArrayList(final Class<E> elementType, final Collection<? extends E> collection) {
        this.elementType = elementType;
        this.buffer = new ArrayList<>(collection);
        this.size = buffer.size();
    }

    @Deprecated
    @Contract(value = "_ -> new", pure = true)
    public static <E> @NotNull TraverseArrayList<E> create(Class<E> elementType) {
        return new TraverseArrayList<>(elementType);
    }

    @Deprecated
    @Contract(value = "_, _ -> new", pure = true)
    public static <E> @NotNull TraverseArrayList<E> create(final Class<E> elementType, final int capacity) {
        return new TraverseArrayList<>(elementType, capacity);
    }

    @Deprecated
    @Contract("_, _ -> new")
    public static <E> @NotNull TraverseArrayList<E> create(final Class<E> elementType, final Collection<? extends E> collection) {
        return new TraverseArrayList<>(elementType, collection);
    }

    @Override
    @SuppressWarnings("unchecked")
    public TraverseArrayList<E> clone() {
        try {
            TraverseArrayList<E> clone = (TraverseArrayList<E>)super.clone();

            if (backingArray != null) {
                clone.backingArray = backingArray.clone();
            }
            if (buffer != null) {
                clone.buffer = (List<E>)((ArrayList<E>)buffer).clone();
            }

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @SuppressWarnings("unchecked")
    protected final <T> T[] createArray(Class<T> type, int size) {
        return (T[])java.lang.reflect.Array.newInstance(type, size);
    }

    protected final E[] createArray(int size) {
        return createArray(elementType, size);
    }

    public final E[] getArray() {
        if (backingArray != null)
            return backingArray;

        if (buffer == null) {
            backingArray = createArray(0);
        } else {
            backingArray = buffer.toArray(createArray(buffer.size()));
            buffer = null;
        }
        return backingArray;
    }

    protected final List<E> getBuffer() {
        if (buffer != null)
            return buffer;

        if (backingArray == null) {
            buffer = new ArrayList<>();
        } else {
            buffer = new ArrayList<>(Arrays.asList(backingArray));
            backingArray = null;
        }
        return buffer;
    }

    @Override
    public final int size() {
        return size;
    }

    @Override
    public final boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    @Override
    public @NotNull Iterator<E> iterator() {
        return listIterator();
    }

    @Override
    public Object @NotNull [] toArray() {
        return getArray();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T @NotNull [] toArray(T @NotNull [] a) {

        E[] array = getArray();
        if (a.length < array.length) {
            return (T[])Arrays.copyOf(array, array.length, a.getClass());
        }

        System.arraycopy(array, 0, a, 0, array.length);

        if (a.length > array.length) {
            a[array.length] = null;
        }

        return a;
    }

    @Override
    public boolean add(E e) {
        boolean result = getBuffer().add(e);
        size = getBuffer().size();
        return result;
    }

    @Override
    public boolean remove(Object o) {
        boolean result = getBuffer().remove(o);
        size = getBuffer().size();
        return result;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return new HashSet<>(Arrays.asList(getArray())).containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean result = getBuffer().addAll(c);
        size = getBuffer().size();
        return result;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        boolean result = getBuffer().addAll(index, c);
        size = getBuffer().size();
        return result;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean result = getBuffer().removeAll(c);
        size = getBuffer().size();
        return result;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean result = getBuffer().retainAll(c);
        size = getBuffer().size();
        return result;
    }

    @Override
    public void clear() {
        getBuffer().clear();
        size = 0;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        } else if (o instanceof TraverseArrayList) {

            final Object[] targetArray = ((TraverseArrayList) o).getArray();
            final E[] array = getArray();

            return Arrays.equals(targetArray, array);
        } else if (!(o instanceof List)) {//covers null too
            return false;
        }

        List other = (List)o;
        Iterator i1 = iterator();
        Iterator i2 = other.iterator();
        while (i1.hasNext() && i2.hasNext()) {
            Object o1 = i1.next();
            Object o2 = i2.next();
            if (o1 == o2)
                continue;
            if (o1 == null || !o1.equals(o2))
                return false;
        }
        return !(i1.hasNext() || i2.hasNext());
    }

    @Override
    public int hashCode() {
        E[] array = getArray();
        int result = 1;
        for (E e : array) {
            result = 31 * result + (e == null ? 0 : e.hashCode());
        }
        return result;
    }

    @Override
    public final E get(int index) {
        if (backingArray != null)
            return backingArray[index];
        if (buffer != null)
            return buffer.get(index);
        throw new IndexOutOfBoundsException("Index:" + index + ", Size:0");
    }

    @Override
    public E set(int index, E element) {
        return getBuffer().set(index, element);
    }

    @Override
    public void add(int index, E element) {
        getBuffer().add(index, element);
        size = getBuffer().size();
    }

    @Override
    public E remove(int index) {
        E result = getBuffer().remove(index);
        size = getBuffer().size();
        return result;
    }

    @Override
    public int indexOf(Object o) {
        E[] array = getArray();
        for (int i = 0; i < array.length; i++) {
            E element = array[i];
            if (element == o) {
                return i;
            }
            if (element != null && element.equals(o)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        E[] array = getArray();
        for (int i = array.length - 1; i >= 0; i--) {
            E element = array[i];
            if (element == o) {
                return i;
            }
            if (element != null && element.equals(o)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public @NotNull ListIterator<E> listIterator() {
        return new ArrayIterator<>(getArray(), 0);
    }

    @Override
    public @NotNull ListIterator<E> listIterator(int index) {
        return new ArrayIterator<>(getArray(), index);
    }

    @Override
    public @NotNull List<E> subList(int fromIndex, int toIndex) {
        List<E> raw =  Arrays.asList(getArray()).subList(fromIndex, toIndex);
        return Collections.unmodifiableList(raw);
    }

    @Override
    public String toString() {
        E[] array = getArray();
        if (array.length == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < array.length; i++) {
            if (i > 0)
                sb.append(", ");
            E e = array[i];
            sb.append(e == this ? "(this Collection)" : e);
        }
        sb.append(']');
        return sb.toString();
    }

    protected class ArrayIterator<E> implements ListIterator<E> {
        final private E[] array;
        private int next;
        private int lastReturned;

        protected ArrayIterator(E[] array, int index) {
            this.array = array;
            this.next = index;
            this.lastReturned = -1;
        }

        @Override
        public boolean hasNext() {
            return next != array.length;
        }

        @Override
        public E next() {
            if (!hasNext())
                throw new NoSuchElementException();
            lastReturned = next++;
            return array[lastReturned];
        }

        @Override
        public boolean hasPrevious() {
            return next != 0;
        }

        @Override
        public E previous() {
            if (!hasPrevious())
                throw new NoSuchElementException();
            lastReturned = --next;
            return array[lastReturned];
        }

        @Override
        public int nextIndex() {
            return next;
        }

        @Override
        public int previousIndex() {
            return next - 1;
        }

        @Override
        public void remove() {
            TraverseArrayList.this.remove(array[lastReturned]);
        }

        @Override
        public void set(E e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(E e) {
            throw new UnsupportedOperationException();
        }
    }
}
