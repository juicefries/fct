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

/**
 * 文本标签
 * <p>
 *     用于展示单行文本，不响应鼠标键盘事件，
 *     <br>
 *     支持左 / 中 / 右三种对齐方式，超长文本自动截断加省略号。
 * </p>
 * @since 0.0.5
 * @author ds(A)
 * @see Button
 * @see Control
 */
public class ALabel extends Control implements AIComponent {

    /** 左对齐 */
    public static final int LEFT = 0;
    /** 居中对齐 */
    public static final int CENTER = 1;
    /** 右对齐 */
    public static final int RIGHT = 2;

    String text;
    int alignment = LEFT;

    public ALabel() {
    }

    public ALabel(String text) {
        setText(text);
    }

    public ALabel(String text, int alignment) {
        setText(text);
        setAlignment(alignment);
    }

    // ========================= OTM =========================

    @Override
    public void paint(Graphics g) {
        if (g == null) return;

        if (isOpaque()) {
            Color bg = getBackground();
            if (bg != null) {
                g.setColor(bg);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }

        String str = getText();
        if (str == null || str.isEmpty()) return;

        Color fg = getForeground();
        if (fg == null) fg = Color.BLACK;
        g.setColor(fg);

        Font font = getFont();
        if (font == null) {
            font = new Font(Math.min(getWidth(), getHeight()) * 0.5f);
        }
        g.setFont(font);

        FontMetrics fm = g.getFontMetrics(font);

        float maxWidth = getWidth();
        if (fm.stringWidth(str) > maxWidth) {
            str = truncate(str, fm, maxWidth);
        }

        float x = switch (alignment) {
            case CENTER -> (getWidth() - fm.stringWidth(str)) / 2f;
            case RIGHT  -> getWidth() - fm.stringWidth(str);
            default     -> 0f;
        };
        float y = (getHeight() + fm.getAscent() + fm.getDescent()) / 2f;

        g.drawString(str, x, y);
    }

    // ========================= SET =========================

    public void setText(String text) {
        this.text = text;
    }

    public void setAlignment(int alignment) {
        if (alignment < LEFT || alignment > RIGHT) {
            throw new IllegalArgumentException("alignment 非法");
        }
        this.alignment = alignment;
    }

    // ========================= GET =========================

    public String getText() {
        return text;
    }

    public int getAlignment() {
        return alignment;
    }

    // ========================= UTIL =========================

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

