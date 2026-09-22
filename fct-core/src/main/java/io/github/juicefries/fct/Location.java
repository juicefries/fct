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
// Data 2026/08/08 02:53
//

package io.github.juicefries.fct;

import io.github.juicefries.fct.sign.Copyable;
import java.util.Objects;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Location implements Copyable {

    @Deprecated(since = "0.0.4",forRemoval = true)
    public final static Location ZERO = zero();

    @Deprecated(since = "0.0.4",forRemoval = true)
    public final static Location MAX = max();

    @Deprecated(since = "0.0.4",forRemoval = true)
    public final static Location MIN = min();

    public float x;
    public float y;

    public Location(float x,float y) {
        this.x = x;
        this.y = y;
    }

    public Location(Location location) {
        if (location == null) {
            throw new NullPointerException("location is null!");
        }
        this.x = location.x;
        this.y = location.y;
    }

    public Location() {
        this(zero());
    }

    // ========================= OTM =========================

    public void resetToZero() {
        this.x = 0.0f;
        this.y = 0.0f;
    }

    public void posX(float x) {
        this.x += x;
    }

    public void posY(float y) {
        this.y += y;
    }

    public void pos(float x,float y) {
        this.x += x;
        this.y += y;
    }

    public void pos(Location location) {
        if (location == null) {
            throw new NullPointerException("location is null!");
        }
        x += location.x;
        y += location.y;
    }

    // ========================= SET =========================
    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setLocation(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setLocation(Location location) {
        if (location == null) {
            throw new NullPointerException("location is null!");
        }
        this.x = location.x;
        this.y = location.y;
    }

    // ========================= GET =========================

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Location getLocation() {
        return copy();
    }

    // ========================= EEE =========================

    @Contract(value = " -> new", pure = true)
    public static @NotNull Location zero() {
        return new Location(0.0f,0.0f);
    }

    @Contract(value = " -> new", pure = true)
    public static @NotNull Location max() {
        return new Location(Float.MAX_VALUE,Float.MAX_VALUE);
    }

    @Contract(value = " -> new", pure = true)
    public static @NotNull Location min() {
        return new Location(Float.MIN_VALUE,Float.MIN_VALUE);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Location location)) return false;
        return Float.compare(x, location.x) == 0 && Float.compare(y, location.y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName() + "[x=" + x + ",y=" + y + "]";
    }

    @Override
    public Object clone() {
        try {
            Location location = (Location) super.clone();
            location.x = x;
            location.y = y;
            return location;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("clone failed!",e);
        }
    }

    @Override
    public Location copy() {
        Location location = new Location();
        location.x = x;
        location.y = y;
        return location;
    }

}
