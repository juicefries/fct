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
// Data 2026/10/07 19:23
//

package io.github.juicefries.fct.event;

/**
 * 键盘焦点适配器
 *
 * <p>
 *     键盘焦点监听器的空实现适配器，
 *     <br>
 *     就是个标准空实现。
 * </p>
 *
 * @since 1.0.3
 * @see KeyboardFocusListener
 * @see KeyboardFocusEvent
 * @author juicefries
 */
public abstract class KeyboardFocusAdapter extends Adapter implements KeyboardFocusListener {

    protected KeyboardFocusAdapter() {

    }

    /**
     * 获取焦点事件的空实现
     * @param e 事件
     * @since 1.0.3
     * @see KeyboardFocusEvent
     */
    @Override
    public void gain(KeyboardFocusEvent e) {

    }

    /**
     * 失去焦点事件的空实现
     * @param e 事件
     * @since 1.0.3
     * @see KeyboardFocusEvent
     */
    @Override
    public void lose(KeyboardFocusEvent e) {

    }

    /**
     * 获取类名
     * @return 类名
     * @since 1.0.3
     */
    @Override
    public String toString() {
        return getClass().getCanonicalName();
    }

}
