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
// Data 2026/08/09 00:17
//

package io.github.juicefries.fct.util;

import io.github.juicefries.fct.sign.ListTask;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Array {

    // ----- create -----

    @Deprecated(since = "0.0.2",forRemoval = true)
    @Contract(value = "_ -> new", pure = true)
    public static Object @NotNull [] create(int length) {
        if (length < 0) {
            return new Object[0];
        }
        return new Object[length];
    }

    @Contract(value = "_ -> new", pure = true)
    public static String @NotNull [] createS(int length) {
        if (length < 0) {
            return new String[0];
        }
        return new String[length];
    }

    @Contract(value = "_ -> new", pure = true)
    public static boolean @NotNull [] createB(int length) {
        if (length < 0) {
            return new boolean[0];
        }
        return new boolean[length];
    }

    @Contract(value = "_ -> new", pure = true)
    public static int @NotNull [] createI(int length) {
        if (length < 0) {
            return new int[0];
        }
        return new int[length];
    }

    @Contract(value = "_ -> new", pure = true)
    public static long @NotNull [] createL(int length) {
        if (length < 0) {
            return new long[0];
        }
        return new long[length];
    }

    @Contract(value = "_ -> new", pure = true)
    public static double @NotNull [] createD(int length) {
        if (length < 0) {
            return new double[0];
        }
        return new double[length];
    }

    @Contract(value = "_ -> new", pure = true)
    public static float @NotNull [] createF(int length) {
        if (length < 0) {
            return new float[0];
        }
        return new float[length];
    }

    @Contract(value = "_ -> new", pure = true)
    public static Float @NotNull [] createFO(int length) {
        if (length < 0) {
            return new Float[0];
        }
        return new Float[length];
    }

    @Contract("null -> fail")
    public static Float @NotNull [] createFO(float[] val) {
        if (val == null) {
            throw new NullPointerException("val is null!");
        }
        Float[] arr = new Float[val.length];
        for (int i = 0; i < val.length; i++) {
            arr[i] = val[i];
        }
        return arr;
    }

    @SafeVarargs
    public static <T> boolean contains(T value, T... values) {
        if (values == null) {
            throw new NullPointerException("values is null!");
        }
        if (values.length < 1) {
            throw new IllegalArgumentException("The length of values cannot be less than 1!");
        }
        for (T t : values) {
            if (Objects.equals(value, t)) {
                return true;
            }
        }
        return false;
    }

    @SafeVarargs
    public static <T> boolean notIncluded(T value, T... values) {
        return !contains(value, values);
    }

    public static void isNotEmpty(List<?> list, Runnable runnable) {
        if (list == null) {
            throw new NullPointerException("list is null!");
        }
        if (runnable == null) {
            throw new NullPointerException("runnable is null!");
        }
        if (!list.isEmpty()) {
            runnable.run();
        }
    }

    public static <E> void forList(List<E> list, ListTask<E> task) {
        if (list == null) {
            throw new NullPointerException("list is null!");
        }
        if (task == null) {
            throw new NullPointerException("task is null!");
        }
        if (list.isEmpty()) return;

        for (int index = 0; index < list.size(); index++) {
            task.accept(list.get(index),index);
        }
    }

    public static <E> void forArr(E[] arr,ListTask<E> task) {
        if (arr == null) {
            throw new NullPointerException("arr is null!");
        }
        if (task == null) {
            throw new NullPointerException("task is null!");
        }
        if (arr.length == 0) return;

        for (int i = 0; i < arr.length; i++) {
            task.accept(arr[i],i);
        }
    }

    public static <E> void forArr(E[] arr,Consumer<? super E> action) {
        if (arr == null) {
            throw new NullPointerException("arr is null!");
        }
        for (E e : arr) {
            if (e == null) continue;
            action.accept(e);
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean allMatch(List<?> list, Class<?> clazz) {
        if (list == null) {
            throw new NullPointerException("list is null!");
        }
        if (clazz == null) {
            throw new NullPointerException("clazz is null!");
        }
        return list.stream().allMatch((Predicate<Object>) clazz::isInstance);
    }

    // ----- copy ----

    @Contract("null, _ -> fail")
    public static Object @NotNull [] copyArr(Object[] arr, int length) {
        if (arr == null) {
            throw new NullPointerException("arr is null!");
        }
        if (length <= 0) {
            return create(0);
        }
        if (arr.length == 0) {
            return create(0);
        }
        Object[] objects = create(length);

        System.arraycopy(arr, 0, objects, 0, length);

        return objects;
    }

    public static float @NotNull [] copyArrF(float[] arr, int length) {
        if (arr == null) {
            throw new NullPointerException("arr is null!");
        }
        if (length <= 0) {
            return createF(0);
        }
        if (arr.length == 0) {
            return createF(0);
        }
        float[] floats = createF(length);

        System.arraycopy(arr, 0, floats, 0, length);

        return floats;
    }

    // ----- requireNotLessThan -----

    /**
     * 要求数组的长度不小于值
     *
     * <p>
     *     传入数组并检查其长度是否符合对应的长度要求，
     *     <br>
     *     若小于指定的长度则抛出对应的{@link IllegalArgumentException}异常，
     *     <br>
     *     可传入{@code message}参数定义要抛出的异常消息。
     * </p>
     *
     * @param arr 数组
     * @param length 长度
     * @param message 消息
     * @param <T> 类型
     * @since 0.0.1
     * @throws NullPointerException 数组不能为null
     * @throws NullPointerException 消息不能为null
     * @throws IllegalArgumentException 传入的指定的长度至少要大于等于0
     */
    public static <T> void requireNotLessThan(T[] arr,int length,String message) {
        if (arr == null) {
            throw new NullPointerException("arr is null!");
        }
        if (message == null) {
            throw new NullPointerException("message is null!");
        }
        if (length < 0) {
            throw new IllegalArgumentException(MessageFormat.format("Invalid length: {0}!", length));
        }

        if (arr.length < length) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requireNotLessThan(int[] arr,int length,String message) {
        if (arr == null) {
            throw new NullPointerException("arr is null!");
        }
        if (message == null) {
            throw new NullPointerException("message is null!");
        }
        if (length < 0) {
            throw new IllegalArgumentException(MessageFormat.format("Invalid length: {0}!", length));
        }

        if (arr.length < length) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requireNotLessThan(float[] arr,int length,String message) {
        if (arr == null) {
            throw new NullPointerException("arr is null!");
        }
        if (message == null) {
            throw new NullPointerException("message is null!");
        }
        if (length < 0) {
            throw new IllegalArgumentException(MessageFormat.format("Invalid length: {0}!", length));
        }

        if (arr.length < length) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requireNotLessThan(double[] arr,int length,String message) {
        if (arr == null) {
            throw new NullPointerException("arr is null!");
        }
        if (message == null) {
            throw new NullPointerException("message is null!");
        }
        if (length < 0) {
            throw new IllegalArgumentException(MessageFormat.format("Invalid length: {0}!", length));
        }

        if (arr.length < length) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requireNotLessThan(long[] arr,int length,String message) {
        if (arr == null) {
            throw new NullPointerException("arr is null!");
        }
        if (message == null) {
            throw new NullPointerException("message is null!");
        }
        if (length < 0) {
            throw new IllegalArgumentException(MessageFormat.format("Invalid length: {0}!", length));
        }

        if (arr.length < length) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requireNotLessThan(short[] arr,int length,String message) {
        if (arr == null) {
            throw new NullPointerException("arr is null!");
        }
        if (message == null) {
            throw new NullPointerException("message is null!");
        }
        if (length < 0) {
            throw new IllegalArgumentException(MessageFormat.format("Invalid length: {0}!", length));
        }

        if (arr.length < length) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requireNotLessThan(char[] arr,int length,String message) {
        if (arr == null) {
            throw new NullPointerException("arr is null!");
        }
        if (message == null) {
            throw new NullPointerException("message is null!");
        }
        if (length < 0) {
            throw new IllegalArgumentException(MessageFormat.format("Invalid length: {0}!", length));
        }

        if (arr.length < length) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requireNotLessThan(byte[] arr,int length,String message) {
        if (arr == null) {
            throw new NullPointerException("arr is null!");
        }
        if (message == null) {
            throw new NullPointerException("message is null!");
        }
        if (length < 0) {
            throw new IllegalArgumentException(MessageFormat.format("Invalid length: {0}!", length));
        }

        if (arr.length < length) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requireNotLessThan(boolean[] arr,int length,String message) {
        if (arr == null) {
            throw new NullPointerException("arr is null!");
        }
        if (message == null) {
            throw new NullPointerException("message is null!");
        }
        if (length < 0) {
            throw new IllegalArgumentException(MessageFormat.format("Invalid length: {0}!", length));
        }
        if (arr.length < length) {
            throw new IllegalArgumentException(message);
        }
    }

    public static <T> void requireNotLessThan(T[] arr,int length) {
        requireNotLessThan(arr,
                length,
                "The actual length of the array: %d is less than the specified length: %d!"
                        .formatted(arr.length, length)
        );
    }

    public static void requireNotLessThan(int[] arr,int length) {
        requireNotLessThan(arr,
                length,
                "The actual length of the array: %d is less than the specified length: %d!"
                        .formatted(arr.length, length)
        );
    }

    public static void requireNotLessThan(long[] arr,int length) {
        requireNotLessThan(arr,
                length,
                "The actual length of the array: %d is less than the specified length: %d!"
                        .formatted(arr.length, length)
        );
    }

    public static void requireNotLessThan(float[] arr,int length) {
        requireNotLessThan(arr,
                length,
                "The actual length of the array: %d is less than the specified length: %d!"
                        .formatted(arr.length, length)
        );
    }

    public static void requireNotLessThan(double[] arr,int length) {
        requireNotLessThan(arr,
                length,
                "The actual length of the array: %d is less than the specified length: %d!"
                        .formatted(arr.length, length)
        );
    }

    public static void requireNotLessThan(boolean[] arr,int length) {
        requireNotLessThan(arr,
                length,
                "The actual length of the array: %d is less than the specified length: %d!"
                        .formatted(arr.length, length)
        );
    }

    public static void requireNotLessThan(char[] arr,int length) {
        requireNotLessThan(arr,
                length,
                "The actual length of the array: %d is less than the specified length: %d!"
                        .formatted(arr.length, length)
        );
    }

    public static void requireNotLessThan(byte[] arr,int length) {
        requireNotLessThan(arr,
                length,
                "The actual length of the array: %d is less than the specified length: %d!"
                        .formatted(arr.length, length)
        );
    }

    public static void requireNotLessThan(short[] arr,int length) {
        requireNotLessThan(arr,
                length,
                "The actual length of the array: %d is less than the specified length: %d!"
                        .formatted(arr.length, length)
        );
    }

    // ----- requireArrayLengthEqualTo -----

    /**
     * 要求数组的长度必须等于值
     *
     * <p>
     *     传入数组并检查其长度是否符合对应的长度要求，
     *     <br>
     *     若不等于指定的长度则抛出对应的{@link IllegalArgumentException}异常，
     *     <br>
     *     可传入{@code message}参数定义要抛出的异常消息。
     * </p>
     *
     * @param arr 数组
     * @param length 长度
     * @param message 消息
     * @param <T> 类型
     * @since 0.0.1
     * @throws NullPointerException 数组不能为null
     * @throws NullPointerException 消息不能为null
     * @throws IllegalArgumentException 传入的指定的长度参数至少需要大于等于0
     */
    public static <T> void requireArrayLengthEqualTo(T[] arr,int length,String message) {
        if (arr == null) {
            throw new NullPointerException("arr is null!");
        }
        if (message == null) {
            throw new NullPointerException("message is null!");
        }
        if (length < 0) {
            throw new IllegalArgumentException(MessageFormat.format("Invalid length: {0}!", length));
        }

        if (arr.length != length) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requireArrayLengthEqualTo(int[] arr, int length, String message) {
        if (arr == null) {
            throw new NullPointerException("arr is null!");
        }
        if (message == null) {
            throw new NullPointerException("message is null!");
        }
        if (length < 0) {
            throw new IllegalArgumentException(MessageFormat.format("Invalid length: {0}!", length));
        }

        if (arr.length != length) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requireArrayLengthEqualTo(long[] arr, int length, String message) {
        if (arr == null) {
            throw new NullPointerException("arr is null!");
        }
        if (message == null) {
            throw new NullPointerException("message is null!");
        }
        if (length < 0) {
            throw new IllegalArgumentException(MessageFormat.format("Invalid length: {0}!", length));
        }

        if (arr.length != length) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requireArrayLengthEqualTo(short[] arr, int length, String message) {
        if (arr == null) {
            throw new NullPointerException("arr is null!");
        }
        if (message == null) {
            throw new NullPointerException("message is null!");
        }
        if (length < 0) {
            throw new IllegalArgumentException(MessageFormat.format("Invalid length: {0}!", length));
        }

        if (arr.length != length) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requireArrayLengthEqualTo(byte[] arr, int length, String message) {
        if (arr == null) {
            throw new NullPointerException("arr is null!");
        }
        if (message == null) {
            throw new NullPointerException("message is null!");
        }
        if (length < 0) {
            throw new IllegalArgumentException(MessageFormat.format("Invalid length: {0}!", length));
        }

        if (arr.length != length) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requireArrayLengthEqualTo(char[] arr, int length, String message) {
        if (arr == null) {
            throw new NullPointerException("arr is null!");
        }
        if (message == null) {
            throw new NullPointerException("message is null!");
        }
        if (length < 0) {
            throw new IllegalArgumentException(MessageFormat.format("Invalid length: {0}!", length));
        }

        if (arr.length != length) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requireArrayLengthEqualTo(float[] arr, int length, String message) {
        if (arr == null) {
            throw new NullPointerException("arr is null!");
        }
        if (message == null) {
            throw new NullPointerException("message is null!");
        }
        if (length < 0) {
            throw new IllegalArgumentException(MessageFormat.format("Invalid length: {0}!", length));
        }

        if (arr.length != length) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requireArrayLengthEqualTo(double[] arr, int length, String message) {
        if (arr == null) {
            throw new NullPointerException("arr is null!");
        }
        if (message == null) {
            throw new NullPointerException("message is null!");
        }
        if (length < 0) {
            throw new IllegalArgumentException(MessageFormat.format("Invalid length: {0}!", length));
        }

        if (arr.length != length) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requireArrayLengthEqualTo(boolean[] arr, int length, String message) {
        if (arr == null) {
            throw new NullPointerException("arr is null!");
        }
        if (message == null) {
            throw new NullPointerException("message is null!");
        }
        if (length < 0) {
            throw new IllegalArgumentException(MessageFormat.format("Invalid length: {0}!", length));
        }

        if (arr.length != length) {
            throw new IllegalArgumentException(message);
        }
    }


    public static <T> void requireArrayLengthEqualTo(T[] arr,int length) {
        if (arr == null) {
            throw new NullPointerException("arr is null!");
        }
        requireArrayLengthEqualTo(arr,length,
                "The actual length of the array: %d does not equal the specified length:%d!"
                        .formatted(arr.length, length)
        );
    }

    public static void requireArrayLengthEqualTo(int[] arr,int length) {
        if (arr == null) {
            throw new NullPointerException("arr is null!");
        }
        requireArrayLengthEqualTo(arr,length,
                "The actual length of the array: %d does not equal the specified length:%d!"
                        .formatted(arr.length, length)
        );
    }

    public static void requireArrayLengthEqualTo(long[] arr,int length) {
        if (arr == null) {
            throw new NullPointerException("arr is null!");
        }
        requireArrayLengthEqualTo(arr,length,
                "The actual length of the array: %d does not equal the specified length:%d!"
                        .formatted(arr.length, length)
        );
    }

    public static void requireArrayLengthEqualTo(char[] arr,int length) {
        if (arr == null) {
            throw new NullPointerException("arr is null!");
        }
        requireArrayLengthEqualTo(arr,length,
                "The actual length of the array: %d does not equal the specified length:%d!"
                        .formatted(arr.length, length)
        );
    }

    public static void requireArrayLengthEqualTo(short[] arr,int length) {
        if (arr == null) {
            throw new NullPointerException("arr is null!");
        }
        requireArrayLengthEqualTo(arr,length,
                "The actual length of the array: %d does not equal the specified length:%d!"
                        .formatted(arr.length, length)
        );
    }

    public static void requireArrayLengthEqualTo(boolean[] arr,int length) {
        if (arr == null) {
            throw new NullPointerException("arr is null!");
        }
        requireArrayLengthEqualTo(arr,length,
                "The actual length of the array: %d does not equal the specified length:%d!"
                        .formatted(arr.length, length)
        );
    }

    public static void requireArrayLengthEqualTo(byte[] arr,int length) {
        if (arr == null) {
            throw new NullPointerException("arr is null!");
        }
        requireArrayLengthEqualTo(arr,length,
                "The actual length of the array: %d does not equal the specified length:%d!"
                        .formatted(arr.length, length)
        );
    }

    public static void requireArrayLengthEqualTo(float[] arr,int length) {
        if (arr == null) {
            throw new NullPointerException("arr is null!");
        }
        requireArrayLengthEqualTo(arr,length,
                "The actual length of the array: %d does not equal the specified length:%d!"
                        .formatted(arr.length, length)
        );
    }

    public static void requireArrayLengthEqualTo(double[] arr,int length) {
        if (arr == null) {
            throw new NullPointerException("arr is null!");
        }
        requireArrayLengthEqualTo(arr,length,
                "The actual length of the array: %d does not equal the specified length:%d!"
                        .formatted(arr.length, length)
        );
    }

}


