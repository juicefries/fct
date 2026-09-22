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

import io.github.juicefries.fct.sign.Constant;

public interface WindowConstants extends Constant {

    /**
     * 在关闭窗口时不做任何动作。
     * @since 0.0.1
     */
    int DO_NOTHING_ON_CLOSE = 0x0e0a0;

    /**
     * 在关闭窗口时隐藏窗口但不释放资源。
     * @since 0.0.1
     * @see Frame#setVisible(boolean)
     */
    int HIDE_ON_CLOSE = 0x0e1b1;

    /**
     * 在关闭窗口时关闭并释放资源。
     * @since 0.0.1
     * @see Frame#dispose()
     */
    int DISPOSE_ON_CLOSE = 0x0e2c2;

    /**
     * 在关闭窗口时关并调用{@link Sys#terminate()}后强行结束程序。
     * @since 0.0.1
     * @see Frame#dispose()
     * @see Sys#terminate()
     * @see System#exit(int)
     */
    int EXIT_ON_CLOSE = 0x0e3d3;

}
