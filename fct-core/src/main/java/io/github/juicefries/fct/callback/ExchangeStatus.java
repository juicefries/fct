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

package io.github.juicefries.fct.callback;

/**
 * 模拟队列换缓冲的阶段。
 *
 * <p>{@code push()} 来自别的线程，所以事件先进缓冲，
 * 再按 {@code ExchangeBegins → ClearArea → ExchangeEnded → ClearBuffer → ExchangeCompleted}
 * 的顺序搬到执行队列里，避免边写边读。
 *
 * @author DeepSeek (AI)
 * @since 0.0.3
 */
public enum ExchangeStatus {
    /** 交换开始 */
    ExchangeBegins,
    /** 清理区域 */
    ClearArea,
    /** 交换完成 */
    ExchangeEnded,
    /** 清理缓冲 */
    ClearBuffer,
    /** 交换结束 */
    ExchangeCompleted
}
