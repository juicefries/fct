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
// Data 2026/08/31 21:21
//

package io.github.juicefries.fct;

import java.util.Objects;

public class GradientPaint implements Paint {
    private final float x1, y1, x2, y2;
    private final Color color1, color2;
    private final CycleMethod cycleMethod;

    public GradientPaint(float x1, float y1, Color color1, float x2, float y2, Color color2, CycleMethod cycleMethod) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.color1 = Objects.requireNonNull(color1, "color1 is null");
        this.color2 = Objects.requireNonNull(color2, "color2 is null");
        this.cycleMethod = cycleMethod != null ? cycleMethod : CycleMethod.NO_CYCLE;
    }

    public GradientPaint(float x1, float y1, Color color1, float x2, float y2, Color color2) {
        this(x1, y1, color1, x2, y2, color2, CycleMethod.NO_CYCLE);
    }

    public float getX1() {
        return x1;
    }

    public float getY1() {
        return y1;
    }

    public float getX2() {
        return x2;
    }

    public float getY2() {
        return y2;
    }

    public Color getColor1() {
        return color1;
    }

    public Color getColor2() {
        return color2;
    }

    public CycleMethod getCycleMethod() {
        return cycleMethod;
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GradientPaint that)) return false;
        return Float.compare(that.x1, x1) == 0 && Float.compare(that.y1, y1) == 0 &&
                Float.compare(that.x2, x2) == 0 && Float.compare(that.y2, y2) == 0 &&
                color1.equals(that.color1) && color2.equals(that.color2) &&
                cycleMethod == that.cycleMethod;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x1, y1, x2, y2, color1, color2, cycleMethod);
    }

    @Override
    public String toString() {
        return "GradientPaint[x1=" + x1 + ", y1=" + y1 + ", x2=" + x2 + ", y2=" + y2 +
                ", color1=" + color1 + ", color2=" + color2 + ", cycleMethod=" + cycleMethod + "]";
    }
}
