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

    /**
     * <p>
     *     创建一个新的对象，并将自身属性赋值，覆盖该空白对象，
     *     方法仅为对字段等值复制而非深度克隆,<br>
     *     方法本身没必要抛出{@link CopyFailedException},<br>
     *     因为字段赋值几乎不会出错，除非特殊情况,<br>
     *     简单说就是相当于把旧的内容誊写到新的纸上。<br>
     * </p>
     * @return 返回一个已经复制好的新实例
     * @throws CopyFailedException 复制时发生错误抛出
     * @since 1.0.0
     * @see CopyFailedException
     */
    Copyable copy() throws CopyFailedException;

    /**
     * <p>
     *     传入一个对象，并将自身的字段属性覆盖对方，
     *     <br>
     *     与{@link #copy}一致，但该方法需要传入对象，而{@link #copy}则返回新对象。
     *     <br>
     *     该方法默认为空，此方法不建议使用，
     *     <br>
     *     尤其是在内部字段拥有{@code final}时。
     * </p>
     * @param template 要用来被覆写的模板
     * @since 1.0.4
     * @throws CopyFailedException 在复制时若对象不匹配或为null抛出
     * @see CopyFailedException
     */
    default void copy(Copyable template) throws CopyFailedException {

    }

}
