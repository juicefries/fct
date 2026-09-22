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

package io.github.juicefries.fct.layout;

import io.github.juicefries.fct.Component;

public enum LayoutBehavior {
    /**
     * 按普通尺寸设定
     * @since 0.0.3
     * @see Component#getSize()
     */
    SIZE,
    /**
     * 按最小尺寸设定
     * @since 0.0.3
     * @see Component#getMinSize()
     */
    MIN_SIZE,
    /**
     * 按最大尺寸设定
     * @since 0.0.3
     * @see Component#getMaxSize()
     */
    MAX_SIZE,
    /**
     * 按照理想尺寸设定，也就是最佳尺寸
     * @since 0.0.3
     * @see Component#getIdealSize()
     */
    IDEAL_SIZE,
    /**
     * 填充
     * <p>
     *     使布局将所有控件填充完全
     * </p>
     * @since 0.0.3
     */
    FILL,
    /**
     * 由布局自行决定
     * @since 0.0.3
     */
    LAYOUT_DETERMINES
}

