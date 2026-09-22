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

import io.github.juicefries.fct.image.Format;
import io.github.juicefries.fct.sign.Copyable;
import io.github.juicefries.fct.sign.Readonly;
import java.nio.ByteBuffer;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

/**
 * 默认图片实现
 */
public class DefaultImage extends Image implements Copyable, Readonly,Cloneable {

    protected int width;
    protected int height;
    protected Format format;
    protected ByteBuffer data;

    protected DefaultImage() {

    }

    public DefaultImage(int width, int height, Format format, ByteBuffer data) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("width and height must be positive");
        }
        if (format == null) {
            throw new NullPointerException("format is null");
        }
        if (data == null) {
            throw new NullPointerException("data is null");
        }
        this.width = width;
        this.height = height;
        this.format = format;
        this.data = data.asReadOnlyBuffer();
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public Format getFormat() {
        return format;
    }

    @Override
    public @NotNull ByteBuffer getData() {
        return data.duplicate().rewind();
    }

    @Override
    public int getSizeInBytes() {
        return data.capacity();
    }

    @Override
    public int getBytesPerPixel() {
        return switch (format) {
            case RGB -> 3;
            case RGBA, ARGB -> 4;
            case GRAYSCALE, ALPHA -> 1;
            //noinspection UnnecessaryDefault
            default -> throw new IllegalStateException("unknown format");
        };
    }

    @Override
    public @NotNull DefaultImage copy() {
        DefaultImage image = new DefaultImage();
        image.width = width;
        image.height = height;
        image.format = format;
        image.data = data;
        return image;
    }

    @Override
    public @NotNull DefaultImage clone() {
        try {
            DefaultImage image = (DefaultImage) super.clone();
            image.width = width;
            image.height = height;
            image.format = format;
            image.data = data;
            return image;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("clone failed!",e);
        }
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof DefaultImage that)) return false;
        return width == that.width && height == that.height && format == that.format && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height, format, data);
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName()
                + "[width=" + width
                + ",height=" + height
                + ",format=" + format
                + ",buffer=" + data.toString()
                + "]";
    }
}