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
// Data 2026/08/15 16:48
//

package io.github.juicefries.fct;

import io.github.juicefries.fct.sign.Copyable;
import java.util.Objects;

public class Bounds implements Cloneable, Copyable {

    public float x;
    public float y;
    public float width;
    public float height;

    public Bounds(float x,float y,float width,float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Bounds(Bounds bounds) {
        if (bounds == null) {
            throw new NullPointerException("bounds is null!");
        }

        x = bounds.x;
        y = bounds.y;
        width = bounds.width;
        height = bounds.height;
    }

    public Bounds() {
        x = 0;
        y = 0;
        width = 0;
        height = 0;
    }

    // ========================= SET =========================

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setLocation(float x,float y) {
        this.x = x;
        this.y = y;
    }

    public void setLocation(Location location) {
        if (location == null) {
            throw new NullPointerException("location is null!");
        }
        x = location.x;
        y = location.y;
    }

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

    public void setBounds(float x,float y,float width,float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setBounds(Bounds bounds) {
        if (bounds == null) {
            throw new NullPointerException("bounds is null!");
        }

        x = bounds.x;
        y = bounds.y;
        width = bounds.width;
        height = bounds.height;
    }

    // ========================= GET =========================

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Location getLocation() {
        return new Location(x,y);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Size getSize() {
        return new Size(width,height);
    }

    public Bounds getBounds() {
        return copy();
    }

    // ========================= =========================


    @Override
    public Bounds clone() {
        try {
            Bounds bounds = (Bounds) super.clone();
            bounds.x = x;
            bounds.y = y;
            bounds.width = width;
            bounds.height = height;
            return bounds;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("clone failed!",e);
        }
    }

    @Override
    public Bounds copy() {
        Bounds bounds = new Bounds();
        bounds.x = x;
        bounds.y = y;
        bounds.width = width;
        bounds.height = height;
        return bounds;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Bounds bounds)) return false;
        return Float.compare(x, bounds.x) == 0 &&
                Float.compare(y, bounds.y) == 0 &&
                Float.compare(width, bounds.width) == 0 &&
                Float.compare(height, bounds.height) == 0
        ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, width, height);
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName()
                + "[x=" + x
                + ",y=" + y
                + ",width=" + width
                + ",height=" + height
                + "]";
    }
}
