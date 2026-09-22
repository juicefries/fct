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

import java.util.Arrays;
import java.util.Objects;

public class RadialGradientPaint implements Paint {
    private final float cx, cy, radius;
    private final float fx, fy;
    private final float[] fractions;
    private final Color[] colors;
    private final CycleMethod cycleMethod;

    public RadialGradientPaint(float cx, float cy, float radius,
                               float fx, float fy,
                               float[] fractions, Color[] colors, CycleMethod cycleMethod) {
        this.cx = cx;
        this.cy = cy;
        this.radius = radius;
        this.fx = fx;
        this.fy = fy;
        this.fractions = fractions.clone();
        this.colors = colors.clone();
        this.cycleMethod = cycleMethod != null ? cycleMethod : CycleMethod.NO_CYCLE;
        validate();
    }

    public RadialGradientPaint(float cx, float cy, float radius,
                               float[] fractions, Color[] colors, CycleMethod cycleMethod) {
        this(cx, cy, radius, cx, cy, fractions, colors, cycleMethod);
    }

    public RadialGradientPaint(float cx, float cy, float radius,
                               float fx, float fy,
                               float[] fractions, Color[] colors) {
        this(cx, cy, radius, fx, fy, fractions, colors, CycleMethod.NO_CYCLE);
    }

    public RadialGradientPaint(float cx, float cy, float radius,
                               float[] fractions, Color[] colors) {
        this(cx, cy, radius, cx, cy, fractions, colors, CycleMethod.NO_CYCLE);
    }

    private void validate() {
        if (fractions.length != colors.length) {
            throw new IllegalArgumentException("fractions and colors must have same length");
        }
        if (fractions.length < 2) {
            throw new IllegalArgumentException("need at least 2 color points");
        }
        if (fractions.length > 16) {
            throw new IllegalArgumentException("max 16 color points supported");
        }
        if (radius <= 0) {
            throw new IllegalArgumentException("radius must be positive");
        }
        for (int i = 0; i < fractions.length; i++) {
            if (fractions[i] < 0 || fractions[i] > 1) {
                throw new IllegalArgumentException("fraction must be in [0,1]");
            }
            if (i > 0 && fractions[i] <= fractions[i - 1]) {
                throw new IllegalArgumentException("fractions must be strictly increasing");
            }
        }
        for (Color c : colors) {
            Objects.requireNonNull(c, "color is null");
        }
    }

    public float getCx() {
        return cx;
    }

    public float getCy() {
        return cy;
    }

    public float getRadius() {
        return radius;
    }

    public float getFx() {
        return fx;
    }

    public float getFy() {
        return fy;
    }

    public float[] getFractions() {
        return fractions.clone();
    }

    public Color[] getColors() {
        return colors.clone();
    }

    public CycleMethod getCycleMethod() {
        return cycleMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RadialGradientPaint that)) return false;
        return Float.compare(that.cx, cx) == 0 && Float.compare(that.cy, cy) == 0 &&
                Float.compare(that.radius, radius) == 0 &&
                Float.compare(that.fx, fx) == 0 && Float.compare(that.fy, fy) == 0 &&
                Arrays.equals(fractions, that.fractions) &&
                Arrays.equals(colors, that.colors) &&
                cycleMethod == that.cycleMethod;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cx, cy, radius, fx, fy, Arrays.hashCode(fractions), Arrays.hashCode(colors), cycleMethod);
    }

    @Override
    public String toString() {
        return "RadialGradientPaint[cx=" + cx + ", cy=" + cy + ", radius=" + radius +
                ", fx=" + fx + ", fy=" + fy +
                ", fractions=" + Arrays.toString(fractions) +
                ", colors=" + Arrays.toString(colors) +
                ", cycleMethod=" + cycleMethod + "]";
    }
}

