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
// Data 2026/09/02 02:38
//

package io.github.juicefries.fct;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryUtil;

public final class FontFace {
    record Key(String name, float pixelW, float pixelH) {}

    static final int PAGE_W = 1024;
    static final int PAGE_H = 1024;
    static final int PADDING = 1;
    static final int LATIN_FIRST = 32, LATIN_LAST = 126;

    final Key key;
    final STBTTFontinfo info;   // 依赖 Toolkit 常驻的原始字节
    final float scaleX, scaleY;
    final float ascent;
    final float descent;

    final List<ByteBuffer> pages = new ArrayList<>();
    final Map<Integer, Glyph> glyphs = new HashMap<>();
    private final Set<Integer> dirtyPages = new HashSet<>();

    private int cx = PADDING, cy = PADDING, rowH = 0;
    private int page = -1;

    FontFace(Key key, STBTTFontinfo info, float scaleX, float scaleY) {
        this.key = key;
        this.info = info;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        newPage();
        for (int cp = LATIN_FIRST; cp <= LATIN_LAST; cp++) ensure(cp);

        int[] asc = {0}, desc = {0}, gap = {0};
        STBTruetype.stbtt_GetFontVMetrics(info, asc, desc, gap);
        this.ascent = asc[0] * scaleY;
        this.descent = desc[0] * scaleY;

    }

    int pageWidth()  { return PAGE_W; }
    int pageHeight() { return PAGE_H; }

    private void newPage() {
        ByteBuffer buf = BufferUtils.createByteBuffer(PAGE_W * PAGE_H);
        org.lwjgl.system.MemoryUtil.memSet(
                org.lwjgl.system.MemoryUtil.memAddress(buf), 0, (long) PAGE_W * PAGE_H);
        pages.add(buf);
        page = pages.size() - 1;
        cx = PADDING; cy = PADDING; rowH = 0;
    }


    Glyph ensure(int cp) {
        Glyph g = glyphs.get(cp);
        if (g != null) return g;
        g = rasterize(cp);
        if (g != null) glyphs.put(cp, g);
        return g;
    }

    private Glyph rasterize(int cp) {
        int[] w = {0}, h = {0}, xoff = {0}, yoff = {0};
        ByteBuffer bmp = STBTruetype.stbtt_GetCodepointBitmap(info, scaleX, scaleY, cp, w, h, xoff, yoff);

        int[] adv = {0}, lsb = {0};
        STBTruetype.stbtt_GetCodepointHMetrics(info, cp, adv, lsb);

        Glyph g = new Glyph();
        g.xadvance = adv[0] * scaleX;
        g.xoff = xoff[0];
        g.yoff = yoff[0];

        int bw = w[0], bh = h[0];
        if (bmp != null && bw > 0 && bh > 0) {
            if (cx + bw + PADDING > PAGE_W) { cy += rowH + PADDING; cx = PADDING; rowH = 0; }
            if (cy + bh + PADDING > PAGE_H) newPage();

            ByteBuffer dst = pages.get(page);
            for (int j = 0; j < bh; j++)
                for (int i = 0; i < bw; i++)
                    dst.put((cy + j) * PAGE_W + cx + i, bmp.get(j * bw + i));

            g.page = page;
            g.x0 = (short) cx;  g.y0 = (short) cy;
            g.x1 = (short) (cx + bw); g.y1 = (short) (cy + bh);
            cx += bw + PADDING;
            rowH = Math.max(rowH, bh);
            markDirty(page);
            STBTruetype.stbtt_FreeBitmap(bmp, MemoryUtil.NULL);
        } else {
            g.page = -1; g.x0 = g.y0 = g.x1 = g.y1 = 0;
            if (bmp != null) STBTruetype.stbtt_FreeBitmap(bmp, MemoryUtil.NULL);
        }
        return g;
    }

    synchronized void markDirty(int p) { dirtyPages.add(p); }
    synchronized Set<Integer> drainDirty() {
        Set<Integer> d = new HashSet<>(dirtyPages);
        dirtyPages.clear();
        return d;
    }

    public float ascent() {
        return ascent;
    }

    public float descent() {
        return descent;
    }
}
