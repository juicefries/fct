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
// Data 2026/08/22 19:38
//

package io.github.juicefries.fct.callback.handler;

import io.github.juicefries.fct.callback.event.CallbackEventData;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.Callback;

/**
 * 回调处理类
 *
 * <p>
 *     我最烦的体系，但是目前而言，这是最适配的狗东西，
 *     他妈的写这破东西浪费我一整天。
 * </p>
 *
 * @since 0.0.3
 * @author juicefries
 */
public interface Handler {

    /**
     * 注册
     *
     * <p>
     *     用于设置回调，通常建议在方法内调用{@link #check}方法，将回调设置包裹在内避免问题。
     * </p>
     *
     * @param window 窗口
     * @since 0.0.3
     */
    void register(long window);

    /**
     * 模拟
     * <p>
     *     用于一次模拟事件，但没啥用。
     * </p>
     * @param event 事件
     * @since 1.0.7
     */
    void simulate(@NotNull CallbackEventData event);

    /**
     * 清理
     * @since 0.0.4
     */
    void free();

    /**
     * 检查
     * <p>
     *     若参数为null则返回，
     *     <br>
     *     若参数是自身则返回，
     *     <br>
     *     若参数不满足以上条件则调用{@link Callback#free()}方法。
     * </p>
     * @param callback 回调
     * @since 1.0.7
     */
    default void check(Callback callback) {
        if (callback == null) return;
        if (callback == this) return;
        callback.free();
    }

}
