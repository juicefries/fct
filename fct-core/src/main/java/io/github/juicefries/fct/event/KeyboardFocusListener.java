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

import io.github.juicefries.fct.KeyEventComponent;

/**
 * 键盘焦点监听器
 *
 * <p>
 *     组件级，对于{@link KeyEventComponent}用于通知组件获取失去焦点通知。
 *     <br>
 *     一般情况下仅对{@link KeyEventComponent}有效。
 * </p>
 *
 * @since 1.0.3
 * @author juicefries
 */
public interface KeyboardFocusListener extends Listener {

    /**
     * 获取键盘焦点事件
     * @param e 事件
     * @since 1.0.3
     * @see KeyboardFocusEvent
     */
    void gain(KeyboardFocusEvent e);

    /**
     * 失去键盘焦点事件
     *
     * <p>
     *     前提为，当前组件是焦点组件才会在失去焦点时分发该事件。
     * </p>
     *
     * @param e 事件
     * @since 1.0.3
     * @see KeyboardFocusEvent
     */
    void lose(KeyboardFocusEvent e);

}
