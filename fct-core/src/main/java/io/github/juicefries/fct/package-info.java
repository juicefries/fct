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
 * <h2>fan concept toolkit</h2>
 *
 * <div style="float:right;text-align:center">
 *   <p><b>ICON:</b>
 *   <p><img src="doc-files/icon.png"
 *      alt="The following text describes this graphic."
 *      width="160" height="160">
 * </div>
 *
 * <p>
 *     {@code FCT}的核心包，承载了组件体系、绘制体系与布局体系，
 *     以及框架运行所依赖的基础类型与工具类。
 * </p>
 *
 * <p>
 *     组件体系的根是 {@link io.github.juicefries.fct.Component}，
 *     <br>
 *     容器能力由 {@link io.github.juicefries.fct.Container} 提供，
 *     <br>
 *     顶层窗口见 {@link io.github.juicefries.fct.Window} 与 {@link io.github.juicefries.fct.Frame}。
 * </p>
 *
 * <p>
 *     绘制体系以 {@link io.github.juicefries.fct.Graphics} 为抽象接口，
 *     <br>
 *     其中{@link io.github.juicefries.fct.Graphics2D}为2D类型的绘制标准，
 *     <br>
 *     由 {@link io.github.juicefries.fct.GLGraphics2D} 基于 OpenGL 实现；
 *     <br>
 *     填充与颜色见 {@link io.github.juicefries.fct.Paint} 及其实现类。
 * </p>
 *
 * <p>
 *     布局接口为 {@link io.github.juicefries.fct.Layout}，
 *     <br>
 *     具体布局实现位于 {@link io.github.juicefries.fct.layout} 包。
 * </p>
 *
 * <p>
 *     部分文档由ds(AI)生成。
 * </p>
 *
 * @author juicefries
 * @since 0.0.1
 */
package io.github.juicefries.fct;
