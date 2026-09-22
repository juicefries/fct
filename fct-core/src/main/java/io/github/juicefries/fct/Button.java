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
// Data 2026/09/17 15:01
//

package io.github.juicefries.fct;

import io.github.juicefries.fct.event.ActionListener;

public class Button extends AbstractButton {

    String text;

    public Button(String text, ActionListener action) {
        setText(text);
        addActionListener(action);
    }

    public Button(String text) {
        setText(text);
    }

    public Button(ActionListener action) {
        addActionListener(action);
    }

    public Button() {

    }

    // ========================= OTM =========================

    @Override
    public void paint(Graphics g) {
        if (g == null) {return;}

        Color bg = getBackground();
        if (bg == null) bg = Color.NEAR_BLACK.copy();

        // 前景色：未设置则按背景亮度自动判断（暗→白，亮→黑）
        Color fg = getForeground();
        if (fg == null) {
            fg = isDark(bg) ? Color.WHITE : Color.BLACK;
        }

        // 背景（带悬停/选中效果）
        Color fill = bg;
        if (isSelect()) {
            fill = adjust(bg, -0.15f);      // 按下变暗
        } else if (isRollover()) {
            fill = adjust(bg, +0.15f);      // 悬停变亮
        }

        if (isOpaque()) {
            g.setColor(fill);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // 文本
        String str = getText();
        if (str == null || str.isEmpty()) return;

        Font font = getFont();
        if (font == null) {
            float size = Math.min(getWidth(), getHeight()) * 0.5f;  // 综合值自适应
            font = new Font(size);
        }
        g.setFont(font);

        FontMetrics fm = g.getFontMetrics(font);
        g.setColor(fg);

        float maxWidth = getWidth();
        if (fm.stringWidth(str) > maxWidth) {
            str = truncate(str, fm, maxWidth);
        }

        float x = (getWidth() - fm.stringWidth(str)) / 2f;
        float y = (getHeight() + fm.getAscent() + fm.getDescent()) / 2f;  // 垂直居中 baseline

        g.drawString(str, x, y);
    }



    // ========================= SET =========================

    public void setText(String text) {
        this.text = text;
    }

    // ========================= GET =========================

    public String getText() {
        return text;
    }

    // ========================= EEE =========================

    // ========================= UTIL =========================

    private static Color adjust(Color c, float delta) {
        return new Color(
                Math.clamp(c.getRed()   + delta, 0.0f, 1.0f),
                Math.clamp(c.getGreen() + delta, 0.0f, 1.0f),
                Math.clamp(c.getBlue()  + delta, 0.0f, 1.0f),
                c.getAlpha()
        );
    }

    private static boolean isDark(Color c) {
        float lum = 0.299f * c.getRed() + 0.587f * c.getGreen() + 0.114f * c.getBlue();
        return lum < 0.5f;
    }

    private static String truncate(String str, FontMetrics fm, float maxWidth) {
        if (fm.stringWidth(str) <= maxWidth) return str;
        final String ellipsis = "…";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); ) {
            int cp = str.codePointAt(i);
            String ch = new String(Character.toChars(cp));
            if (fm.stringWidth(sb + ch + ellipsis) > maxWidth) break;
            sb.append(ch);
            i += Character.charCount(cp);
        }
        return sb + ellipsis;
    }

}
