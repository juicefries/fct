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
// Data 2026/08/31 21:22
//

package io.github.juicefries.fct;

import java.util.Arrays;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public record LinearGradientPaint(
        float startX, float startY,
        float endX, float endY,
        float[] fractions,
        Color[] colors,
        CycleMethod cycleMethod
) implements Paint
{

    public LinearGradientPaint(float startX, float startY, float endX, float endY,
                               float @NotNull [] fractions, Color @NotNull [] colors, CycleMethod cycleMethod) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.fractions = fractions.clone();
        this.colors = colors.clone();
        this.cycleMethod = cycleMethod != null ? cycleMethod : CycleMethod.NO_CYCLE;
        validate();
    }

    public LinearGradientPaint(float startX, float startY, float endX, float endY,
                               float[] fractions, Color[] colors) {
        this(startX, startY, endX, endY, fractions, colors, CycleMethod.NO_CYCLE);
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

    @Override
    public float[] fractions() {
        return fractions.clone();
    }

    @Override
    public Color[] colors() {
        return colors.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LinearGradientPaint that)) return false;
        return Float.compare(that.startX, startX) == 0 && Float.compare(that.startY, startY) == 0 &&
                Float.compare(that.endX, endX) == 0 && Float.compare(that.endY, endY) == 0 &&
                Arrays.equals(fractions, that.fractions) &&
                Arrays.equals(colors, that.colors) &&
                cycleMethod == that.cycleMethod;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startX, startY, endX, endY, Arrays.hashCode(fractions), Arrays.hashCode(colors), cycleMethod);
    }

    @Override
    public @NotNull String toString() {
        return "LinearGradientPaint[startX=" + startX + ", startY=" + startY +
                ", endX=" + endX + ", endY=" + endY +
                ", fractions=" + Arrays.toString(fractions) +
                ", colors=" + Arrays.toString(colors) +
                ", cycleMethod=" + cycleMethod + "]";
    }
}

