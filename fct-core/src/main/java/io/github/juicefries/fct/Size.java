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
// Data 2026/08/08 03:28
//

package io.github.juicefries.fct;

import io.github.juicefries.fct.sign.Copyable;
import java.util.Objects;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;
import org.joml.Vector2i;

public class Size implements Copyable {

    public float width;
    public float height;

    public Size(float width,float height) {
        this.width = width;
        this.height = height;
    }

    public Size(Size size) {
        if (size == null) {
            throw new NullPointerException("size is null!");
        }
        width = size.width;
        height = size.height;
    }

    @Contract(pure = true)
    public Size() {
        this(0,0);
    }

    @Contract("null -> fail")
    public Size(float[] size) {
        if (size == null) {
            throw new NullPointerException("size is null!");
        }
        if (size.length < 2) {
            throw new IllegalArgumentException("The array length is less than 2!");
        }
        this.width = size[0];
        this.height = size[1];
    }

    @Contract("null -> fail")
    public Size(int[] size) {
        if (size == null) {
            throw new NullPointerException("size is null!");
        }
        if (size.length < 2) {
            throw new IllegalArgumentException("The array length is less than 2!");
        }
        this.width = size[0];
        this.height = size[1];
    }

    // ========================= OTM =========================

    public void resetToZero() {
        this.width = 0.0f;
        this.height = 0.0f;
    }

    // ========================= SET =========================

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setSize(float width,float height) {
        this.width = width;
        this.height = height;
    }

    public void setSize(Size size) {
        if (size == null) {
            throw new NullPointerException("size is null!");
        }
        width = size.width;
        height = size.height;
    }

    public void setSize(float[] size) {
        if (size == null) {
            throw new NullPointerException("size is null!");
        }
        if (size.length < 2) {
            throw new IllegalArgumentException("The array length is less than 2!");
        }
        this.width = size[0];
        this.height = size[1];
    }

    public void setSize(int[] size) {
        if (size == null) {
            throw new NullPointerException("size is null!");
        }
        if (size.length < 2) {
            throw new IllegalArgumentException("The array length is less than 2!");
        }
        this.width = size[0];
        this.height = size[1];
    }

    // ========================= GET =========================


    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Size getSize() {
        return copy();
    }

    // ========================= EEE =========================

    public static boolean lessThan0(Size size) {
        if (size == null) {
            throw new NullPointerException("size is null!");
        }
        return size.width < 0 || size.height < 0;
    }

    public static boolean lessThanOrEqualTo0(Size size) {
        if (size == null) {
            throw new NullPointerException("size is null!");
        }
        return size.width <= 0 || size.height <= 0;
    }

    public Vector2i toVector2i() {
        Vector2i vector2i = new Vector2i();

        Size size = copy();
        vector2i.x = Math.round(size.width);
        vector2i.y = Math.round(size.height);

        return vector2i;
    }

    @Contract("null -> fail; !null -> new")
    public static @NotNull Size toSize(Vector2i size) {
        if (size == null) {
            throw new NullPointerException("size is null!");
        }
        return new Size(size.x,size.y);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Size size)) return false;
        return Float.compare(width, size.width) == 0 && Float.compare(height, size.height) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height);
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName() + "[width=" + width + ",height=" + height + "]";
    }

    @Override
    public Object clone() {
        try {
            Size size = (Size) super.clone();
            size.width = width;
            size.height = height;
            return size;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("clone failed!",e);
        }
    }

    @Override
    public Size copy() {
        Size size = new Size();
        size.width = width;
        size.height = height;
        return size;
    }

}
