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
// Data 2026/09/22 17:34
//

package io.github.juicefries.fct;

import io.github.juicefries.fct.sign.ApiSign;

/**
 * <h2>AIComponent</h2>
 *
 * <p>
 *     AI组件,一个标准的标记接口，
 *     <br>
 *     实现该接口的类可以表示为使用了AI进行创作出的组件，
 *     <br>
 *     或者AI生成的代码，占比达到了85%以上,AI组件仅表示能运行功能完整度不保证。
 *     <br>
 *     该接口文档是我自己写的,因为闲得蛋疼，再加上懒所以直接搞了这么个接口。
 * </p>
 * @since 0.0.5
 * @see Component
 * @author juicefries
 * @apiNote AI生成的组件不保证功能完整性、可扩展性,谨慎使用
 * @implSpec 使用AI生成的组件皆可使用该接口作为标记
 * <br>
 * 建议在AI生成的代码大于85%的情况下使用。
 * @version 1
 */
@ApiSign.SignInterface
public interface AIComponent {



}

