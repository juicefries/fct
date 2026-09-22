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

import io.github.juicefries.fct.Sys;
import java.util.function.Consumer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * <h2>SystemListener</h2>
 *
 * <p>
 *     用于在{@link Sys}监听{@code fct}对{@code glfw}的启用状态。
 * </p>
 * @since 0.0.1
 * @author juicefries
 */
public interface SystemListener extends Listener {


    /**
     * 发生的事件
     * @param e 事件
     * @since 0.0.4
     */
    void event(SysEvent e);

    /**
     * @return 创建时的类型，用于用途
     */
    SysListenerType getType();

//
//    /**
//     * 注销事件
//     *
//     * <p>
//     *     一般由{@link Sys#cancel}在注销监听器时调用,
//     *     <br>
//     *     通常情况下只是被注销通知,
//     *     <br>
//     *     在{@link Sys#terminate}时也会通过批量注销提前触发此方法。
//     * </p>
//     *
//     * @since 0.0.1
//     * @see Sys
//     * @see Sys#cancel
//     * @see Sys#terminate
//     */
//    void cancel();
//
//    /**
//     * 停止事件
//     *
//     * <p>
//     *     由{@link Sys#terminate()}被调用时对所有监听器发出的通知，
//     *     <br>
//     *     被调用时应当尽量去处理相关事宜。
//     * </p>
//     * @since 0.0.1
//     * @see Sys
//     * @see Sys#terminate()
//     */
//    void terminate();
//
//    default String getListenerName() {
//        return "None";
//    }

    /**
     * 获取监听器名称
     *
     * <p>
     *     一般没什么用。
     * </p>
     * @return 返回监听器的名称
     * @since 0.0.3
     */
    String getListenerName();

    /**
     * 获取监听器名称
     *
     * <p>
     *     一般没什么用。
     * </p>
     * @return 返回监听器的名称
     * @since 0.0.3
     */
    default String listenerName() {
        return getListenerName();
    }

    Object getSource();

    /**
     * 获取监听器名称
     *
     * <p>
     *     一般没什么用。
     * </p>
     * @return 返回监听器的名称
     * @since 0.0.3
     */
    default String toListenerName() {
        return getListenerName();
    }

    @Contract(value = "_, _, _, _ -> new", pure = true)
    static @NotNull SystemListener createListener(
            SysListenerType type,
            String listenerName,
            Object source,
            Consumer<SysEvent> event
    ) {
        return new SystemListener() {
            @Override
            public SysListenerType getType() {
                return type;
            }

            @Override
            public String getListenerName() {
                return listenerName;
            }

            @Override
            public Object getSource() {
                return source;
            }

            @Override
            public void event(SysEvent e) {
                event.accept(e);
            }
        };
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    static @NotNull SystemListener createPassiveListener(
            String listenerName,
            Object source,
            Consumer<SysEvent> event) {
        return createListener(SysListenerType.Passive,listenerName,source,event);
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    static @NotNull SystemListener createProactiveListener(
            String listenerName,
            Object source,
            Consumer<SysEvent> event) {
        return createListener(SysListenerType.Proactive,listenerName,source,event);
    }

    /**
     * 系统监听器类型
     * @since 0.0.4
     * @author juicefries
     */
    enum SysListenerType {
        /**
         * 主动型
         * <p>
         *     频繁地移除创建，适合窗口。
         * </p>
         * @since 0.0.4
         */
        Proactive,
        /**
         * 被动型
         * <p>
         *     并不频繁移除，一般都是所有活跃型监听器移除完成后自动移除。
         * </p>
         * @since 0.0.4
         */
        Passive

    }

}
