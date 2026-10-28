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
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

//
// Created by juicefries
// The project name is fct
// Data 2026/10/28 17:49
//

package io.github.juicefries.fct;

import org.jetbrains.annotations.ApiStatus;

/**
 * 离屏画布。
 *
 * <pre>{@code
 * try (DrawBuffer buffer = new GLDrawBuffer(256, 256, graphics)) {
 *     buffer.graphics().fillRect(0, 0, 256, 256);
 *     Image image = buffer.output(Image.class);
 * }
 * }</pre>
 *
 * @since 1.0.5
 * @author juicefries
 */
@ApiStatus.Experimental
public abstract class DrawBuffer implements AutoCloseable {

    protected final int width;

    protected final int height;

    protected boolean closed;

    /**
     * @param width  宽度（像素）
     * @param height 高度（像素）
     * @throws IllegalArgumentException 宽或高不大于 0
     */
    protected DrawBuffer(int width, int height) {
        if (width <= 0) {
            throw new IllegalArgumentException("width is less than or equal to 0!");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("height is less than or equal to 0!");
        }
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isClosed() {
        return closed;
    }

    /**
     * @return 画笔
     * @throws IllegalStateException 已关闭
     */
    public abstract Graphics2D graphics();

    /**
     * 拷出当前内容，返回独立对象。
     *
     * @param type 目标类型
     * @param <T>  目标类型
     * @return 新对象
     * @throws IllegalArgumentException 后端不支持该类型
     * @throws IllegalStateException    已关闭
     */
    public abstract <T> T output(Class<T> type);

    /**
     * 释放资源，幂等。
     */
    @Override
    public abstract void close();

}
