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

package io.github.juicefries.fct.event;

/**
 * 鼠标按钮监听器
 * <p>
 *     用于监听鼠标按钮的按下与释放事件，
 *     <br>
 *     由{@link EventTrigger}在命中组件后分发。
 * </p>
 * @since 0.0.4
 * @author juicefries
 * @see MouseEvent
 * @see EventTrigger
 */
public interface MouseButtonListener extends Listener {

    /**
     * 按下事件
     * @param e 事件
     * @since 0.0.4
     */
    void press(MouseEvent e);

    /**
     * 释放事件
     * @param e 事件
     * @since 0.0.4
     */
    void release(MouseEvent e);
}
