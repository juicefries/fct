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

package io.github.juicefries.fct.input;

public interface WindowStateInput extends WindowInput {

    /**
     * 窗口尺寸事件
     * <p>
     *     当触发尺寸更新时触发
     * </p>
     * @param e 原始数据
     */
    void size(WindowData e);

    /**
     * 窗口移动事件
     *
     * <p>
     *     当触发移动时触发
     * </p>
     * @param e 原始数据
     */
    void move(WindowData e);

    /**
     * 窗口焦点事件
     * @param e 数据
     */
    void focus(WindowData e);

    /**
     * 窗口重绘事件
     * @param e 数据
     */
    void refresh(WindowData e);

    /**
     * 窗口缩放事件
     * @param e 数据
     */
    void contentScale(WindowData e);

    void framebufferSize(WindowData e);


}
