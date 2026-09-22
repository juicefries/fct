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

package io.github.juicefries.fct;

import io.github.juicefries.fct.event.EventTrigger;
import io.github.juicefries.fct.event.EventListenerList;
import io.github.juicefries.fct.event.Listener;

/**
 * 事件组件
 * <p>
 *     {@link Component}在设计中并不包含事件功能，
 *     <br>
 *     该类用于提供事件相关API，具体用法参考{@link javax.swing swing},
 *     <br>
 *     该类未标记接口，不提供添加移除API。
 *     <br>
 *     自{@code 0.0.5}起具体的添加移除等API移动至具体分类，
 *     <br>
 *     该类仅保留{@link #getListeners}与{@link #getListenerList}方法。
 *     <br>
 *     应该没人会看我写的文档吧。
 * </p>
 * @since 0.0.4
 * @author juicefries
 * @see EventTrigger
 * @see EventListenerList
 * @see MouseEventComponent
 * @see WindowEventComponent
 * @see KeyEventComponent
 */
public interface EventComponent {

    /**
     * 获取监听器列表
     * @return 组件的监听器列表
     * @since 0.0.4
     * @see EventListenerList
     */
    EventListenerList getListenerList();

    /**
     * 获取监听器列表
     *
     * @param <T>  监听器类型
     * @param type 具体的监听器类型
     * @return 具体类型的监听器列表
     * @see Listener
     * @see #getListenerList
     * @since 0.0.4
     */
    default <T extends Listener> T[] getListeners(Class<T> type) {
        if (type == null) {
            throw new NullPointerException("type is null!");
        }

        EventListenerList list = getListenerList();

        if (list == null) {
            throw new NullPointerException("ListenerList is null!");
        }

        T[] listeners = list.getListeners(type);

        if (listeners == null) {
            throw new NullPointerException("listeners is null!");
        }

        return listeners;
    }

}
