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
// Data 2026/08/21 21:23
//

package io.github.juicefries.fct.util;

import io.github.juicefries.fct.sign.ApiSign;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector2i;

public class Math {

    public static char[] toBytesChar(byte[] bytes) {
        char[] chars = new char[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            chars[i] = (char) bytes[i];
        }
        return chars;
    }

    /**
     * 判断 val 是否能严格匹配为 unit 的整数倍,至少一个单位
     * @param unit 每个单位的尺寸,如 4*3=12
     * @param val  总数
     * @return true 表示 val 是 unit 的正整数倍,false 表示不能整除或 val <= 0
     */
    public static boolean match(int unit, int val) {
        return val > 0 && val % unit == 0;
    }

    public static @NotNull Matrix4f ortho(float left, float right, float bottom, float top, float zNear, float zFar) {
        return new Matrix4f().ortho(left, right, bottom, top, zNear, zFar);
    }

    @ApiSign.NotRecommended(since = "0.0.4")
    public static @NotNull Matrix4f create(float width, int height) {
        return ortho(0, width, height, 0, -1, 1);
    }

    @ApiSign.NotRecommended(since = "0.0.4")
    public static @NotNull Matrix4f create(Vector2i size) {
        if (size == null) {
            throw new NullPointerException("size is null!");
        }
        return create(size.x,size.y);
    }

}
