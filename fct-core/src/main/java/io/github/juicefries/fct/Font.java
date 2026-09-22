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

import java.util.Objects;

public class Font {

    public static final String SANS_SERIF = "fct.font.source.han.sans.sc.regular";
    public static final String WEN_KAI = "fct.font.lxgw.wenkai.mono.gb.screen";
    public static final String MONO = "fct.font.jetbrains.mono.regular";
    public static final String MONO_BOLD = "fct.font.jetbrains.mono.bold";
    public static final String MONO_ITALIC = "fct.font.jetbrains.mono.italic";
    public static final String MONO_BOLD_ITALIC = "fct.font.jetbrains.mono.bold.italic";
    public static final String DEFAULT = SANS_SERIF;

    private final String name;
    private final float width;
    private final float height;

    public Font(String name, float width, float height) {
        this.name = Objects.requireNonNull(name, "name is null!");
        this.width = width;
        this.height = height;
    }

    public Font(float width, float height) {
        this(DEFAULT, width, height);
    }

    public Font(String name, float size) {
        this(name, size, size);
    }

    public Font(float size) {
        this(DEFAULT, size);
    }

    public String name() {
        return name;
    }

    public float width() {
        return width;
    }

    public float height() {
        return height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Font f)) return false;
        return Float.compare(f.width, width) == 0
                && Float.compare(f.height, height) == 0
                && name.equals(f.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, width, height);
    }

    @Override
    public String toString() {
        return "Font[" + name + ", " + width + "x" + height + "]";
    }
}
