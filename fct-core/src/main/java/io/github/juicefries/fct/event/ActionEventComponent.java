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
 * 动作监听器事件组件
 *
 * <p>
 *     没什么狗屁用途，就是表示该类拥有对应的方法但是在1.0.4版本之后，
 *     <br>
 *     组件原生就拥有了事件承载能力基本上已经没什么屁用了，当做标记接口来看就行
 * </p>
 *
 * @since 1.0.4
 * @see EventComponent
 * @see ActionListener
 * @author juicefries
 */
public interface ActionEventComponent extends EventComponent {

    /**
     * 添加动作监听器
     * @param l 监听器
     * @since 1.0.4
     * @see ActionListener
     */
    void addActionListener(ActionListener l);

    /**
     * 移除动作监听器
     * @param l 监听器
     * @since 1.0.4
     * @see ActionListener
     */
    void removeActionListener(ActionListener l);

    /**
     * 获取所有动作监听器
     * @return 组件所有动作监听器
     * @since 1.0.4
     * @see ActionListener
     */
    default ActionListener[] getActionListeners() {
        return getListeners(ActionListener.class);
    }

}
