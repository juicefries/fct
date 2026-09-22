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
// Data 2026/09/02 03:32
//

package io.github.juicefries.fct;

public class FontMetrics {

    private final Font font;
    private final FontFace face;
    private final float scaleX; // 世界 → 像素
    private final float scaleY;

    FontMetrics(Font font, FontFace face, float scaleX, float scaleY) {
        this.font = font;
        this.face = face;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public Font font() { return font; }

    // 单字符 步 进宽度（世界坐标）
    public float charWidth(char ch) { return charWidth((int) ch); }

    public float charWidth(int codepoint) {
        Glyph g = Toolkit.ensureGlyph(face, codepoint);
        return g == null ? 0f : g.xadvance / scaleX;
    }

    // 字符串总宽（世界坐标）
    public float stringWidth(String str) {
        if (str == null || str.isEmpty()) return 0f;
        float w = 0f;
        for (int i = 0; i < str.length(); ) {
            int cp = str.codePointAt(i);
            i += Character.charCount(cp);
            w += charWidth(cp);
        }
        return w;
    }

    // 单字符边界（相对 baseline 起点，世界坐标）
    public TextBounds charBounds(int codepoint) {
        Glyph g = Toolkit.ensureGlyph(face, codepoint);
        if (g == null || g.page < 0) return new TextBounds(0, 0, 0, 0);
        float left = g.xoff / scaleX;
        float top = g.yoff / scaleY;
        return new TextBounds(left, top, left + (g.x1 - g.x0) / scaleX, top + (g.y1 - g.y0) / scaleY);
    }

    // 字符串边界（相对 baseline 起点，世界坐标）
    public TextBounds stringBounds(String str) {
        if (str == null || str.isEmpty()) return new TextBounds(0, 0, 0, 0);
        float penX = 0f;
        float left = Float.POSITIVE_INFINITY, top = Float.POSITIVE_INFINITY;
        float right = Float.NEGATIVE_INFINITY, bottom = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < str.length(); ) {
            int cp = str.codePointAt(i);
            i += Character.charCount(cp);
            Glyph g = Toolkit.ensureGlyph(face, cp);
            if (g == null) continue;
            if (g.page < 0) { penX += g.xadvance; continue; }
            float l = (penX + g.xoff) / scaleX;
            float t = g.yoff / scaleY;
            float r = l + (g.x1 - g.x0) / scaleX;
            float b = t + (g.y1 - g.y0) / scaleY;
            if (l < left) left = l;
            if (t < top) top = t;
            if (r > right) right = r;
            if (b > bottom) bottom = b;
            penX += g.xadvance;
        }
        if (left == Float.POSITIVE_INFINITY) return new TextBounds(0, 0, 0, 0);
        return new TextBounds(left, top, right, bottom);
    }

    public float getAscent()  { return face.ascent() / scaleY; }
    public float getDescent() { return face.descent() / scaleY; }
    public float getHeight()  { return (face.ascent() - face.descent()) / scaleY; }


}


