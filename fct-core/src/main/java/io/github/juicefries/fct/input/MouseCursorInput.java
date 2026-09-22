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

import org.lwjgl.glfw.GLFW;

public interface MouseCursorInput extends MouseInput {

    /**
     * 光标移动事件
     * @param e 数据
     */
    void cursorPos(MouseData e);

    /**
     * 光标进入窗口事件
     *
     * <p>
     *     光标进入窗口触发
     * </p>
     *
     * @param e 数据
     * @see GLFW#glfwSetCursorEnterCallback
     */
    void cursorEnter(MouseData e);

    /**
     * 光标离开窗口事件
     *
     * <p>
     *     光标进入离开触发
     * </p>
     *
     * @param e 数据
     * @see GLFW#glfwSetCursorEnterCallback
     */
    void cursorLeave(MouseData e);
}
