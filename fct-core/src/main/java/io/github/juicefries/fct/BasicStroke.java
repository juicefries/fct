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
// Data 2026/09/02 04:11
//

package io.github.juicefries.fct;

import java.util.Arrays;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public record BasicStroke(float width, float[] dash, float dashPhase) implements Stroke {
    public static final BasicStroke DEFAULT = new BasicStroke(1.0f);

    public BasicStroke(float width) {
        this(width, null, 0f);
    }

    public BasicStroke(float width, float[] dash, float dashPhase) {
        if (width <= 0) throw new IllegalArgumentException("width must be > 0");
        this.width = width;
        this.dash = dash == null ? null : dash.clone();
        this.dashPhase = dashPhase;
    }

    @Override
    public float @Nullable [] dash() {
        return dash == null ? null : dash.clone();
    }

    public boolean isDashed() {
        return dash != null && dash.length > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BasicStroke s)) return false;
        return Float.compare(s.width, width) == 0
                && Float.compare(s.dashPhase, dashPhase) == 0
                && Arrays.equals(dash, s.dash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, dashPhase, Arrays.hashCode(dash));
    }
}
