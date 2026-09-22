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

public class TexturePaint implements Paint {
    private final Image image;
    private final float[] uv;

    public TexturePaint(Image image) {
        this(image, null);
    }

    public TexturePaint(Image image, float[] uv) {
        this.image = Objects.requireNonNull(image, "image is null");
        if (uv != null) {
            if (uv.length < 8 || uv.length % 4 != 0) {
                throw new IllegalArgumentException("uv array length must be >= 8 and multiple of 4");
            }
            this.uv = uv.clone();
        } else {
            this.uv = null;
        }
    }

    public Image getImage() {
        return image;
    }

    public float[] getUV() {
        return uv != null ? uv.clone() : null;
    }

    public boolean hasCustomUV() {
        return uv != null;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TexturePaint that)) return false;
        return image.equals(that.image) && Arrays.equals(uv, that.uv);
    }

    @Override
    public int hashCode() {
        return Objects.hash(image, Arrays.hashCode(uv));
    }

    @Override
    public String toString() {
        return "TexturePaint[image=" + image + ", uv=" + Arrays.toString(uv) + "]";
    }
}
