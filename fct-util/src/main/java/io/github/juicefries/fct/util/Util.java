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
// Data 2026/08/22 23:35
//

package io.github.juicefries.fct.util;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Util {

    @Contract("null -> fail")
    public static <T> @NotNull T newInstance(Class<? extends T> aClass) throws Exception {
        if (aClass == null) {
            throw new NullPointerException("Class is null!");
        }

        try {
            return aClass.getDeclaredConstructor().newInstance();
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Constructor of "
                    + aClass.getName()
                    + " threw an exception",
                    e.getCause()
            );
        } catch (InstantiationException e) {
            throw new RuntimeException("Cannot instantiate "
                    + aClass.getName()
                    + " (abstract class, interface, array, primitive, or no default constructor)",
                    e
            );
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Constructor of "
                    + aClass.getName()
                    + " is not accessible",
                    e
            );
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("No no-arg constructor found in "
                    + aClass.getName(),
                    e
            );
        }
    }

    @Contract(pure = true)
    public static <T> @NotNull Consumer<T> emptyConsumer() {
        return _ -> {};
    }

    @Contract(pure = true)
    public static <T> @NotNull Consumer<T> emptyConsumer(T t) {
        return _ -> {};
    }

    public static <T> boolean notEqualTo(T a,T b) {
        return a != b;
    }

    public static long turn(@NotNull String val) {
        return UUID.nameUUIDFromBytes(val.getBytes(StandardCharsets.UTF_8))
                .getMostSignificantBits();
    }

    public static <T> T match(boolean condition, T a, T b) {
        if (condition) {
            return a;
        } else {
            return b;
        }
    }

    @SafeVarargs
    public static <T> T match(T val, T defVal, T ... vals) {
        if (vals == null) {
            return defVal;
        }
        for (T t : vals) {
            if (t == val) {
                return t;
            }
        }
        return defVal;
    }

    public static <T> boolean match(T val, T request) {
        return val == request;
    }

    public static <T> boolean toBooleanStrict(T targetValue, T trueValue, T falseValue) {
        if (targetValue == trueValue) {
            return true;
        }
        if (targetValue == falseValue) {
            return false;
        }
        throw new IllegalArgumentException("The target value doesn't meet the requirements!");
    }

    /**
     * 根据条件选择返回值
     *
     * @param valueIfMatch   条件匹配时返回的值
     * @param valueIfNotMatch 条件不匹配时返回的值
     * @param expected       期望值
     * @param actual         实际值
     * @param <T>            返回值类型
     * @return 如果 expected == actual 返回 valueIfMatch，否则返回 valueIfNotMatch
     * @since 0.0.1
     */
    public static <T> T cond(T valueIfMatch, T valueIfNotMatch, long expected, long actual) {
        return expected == actual ? valueIfMatch : valueIfNotMatch;
    }

    public static <T> T cloneFailed(Ret<T> ret,String message) {
        try {
            return ret.ret();
        } catch (Exception e) {
            throw new RuntimeException(message,e);
        }
    }

    public static <T> T cloneFailed(Ret<T> ret) {
        return cloneFailed(ret,"clone failed!");
    }

}
