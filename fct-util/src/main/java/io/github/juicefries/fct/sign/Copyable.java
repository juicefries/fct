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
// Data 2026/08/08 03:27
//
package io.github.juicefries.fct.sign;

/**
 * 可复制的
 * <p>
 *     通过{@link #copy()}方法复制一个新实例，<br>
 *     方法仅为对字段等值复制而非深度克隆,<br>
 *     方法本身没必要抛出{@link CopyFailedException},<br>
 *     因为字段赋值几乎不会出错，除非特殊情况,<br>
 *     懒得写了,简单说就是相当于把旧的内容誊写到新的纸上。<br>
 * </p>
 * @since 0.0.1
 * @author juicefries
 * @see CopyFailedException
 */
public interface Copyable {

    @Deprecated(since = "0.0.1",forRemoval = true)
    static Object copy(Copyable c) throws CopyFailedException {
        if (c == null) {
            throw new CopyFailedException();
        }
        return c.copy();
    }

    Copyable copy() throws CopyFailedException;

}
