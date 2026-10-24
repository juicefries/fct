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

/**
 * <h2>callback</h2>
 *
 * <p>
 *     GLFW 回调的统一外壳：窗口 / 键盘 / 鼠标的事件都变成 {@link io.github.juicefries.fct.callback.CallbackData}，
 *     <br>
 *     交给 {@link io.github.juicefries.fct.callback.Handler} 分发。
 *     <br>
 *     想造假事件就 {@code CallbackManager.push(...)} 灌一个，走同一条路；
 *     <br>
 *     只管窗口自己的回调，组件层不归这里，具体处理器在 {@code callback.handler}。
 * </p>
 *
 * @author juicefries
 * @author DeepSeek (AI) 文档
 * @since 0.0.3
 * @deprecated 有些烂，打算扔掉
 */
@ApiStatus.Internal
@ApiStatus.Experimental
@ApiSign.Dangerous(since = "0.0.5")
@Deprecated(since = "1.0.4",forRemoval = true)
package io.github.juicefries.fct.callback;

import io.github.juicefries.fct.sign.ApiSign;
import org.jetbrains.annotations.ApiStatus;
