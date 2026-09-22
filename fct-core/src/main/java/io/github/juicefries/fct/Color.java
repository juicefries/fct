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

import io.github.juicefries.fct.sign.Copyable;
import io.github.juicefries.fct.sign.Readonly;
import io.github.juicefries.fct.util.Array;
import io.github.juicefries.fct.util.Util;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * 表示一种 RGBA 颜色，红、绿、蓝、透明度四个分量均以浮点数存储，
 * <br>
 * 取值范围为 {@code [0.0, 1.0]}，构造时超出范围的值会被自动截断。
 *
 * <p>该颜色对象是不可变的：分量在构造时确定，之后无法修改。
 * 需要"改变"颜色时应通过构造器或 {@link #copy()} 创建新实例。
 *
 * <p>支持以下构造方式：
 * <ul>
 *     <li>{@link #Color(float, float, float, float)}：指定 RGBA 四个分量</li>
 *     <li>{@link #Color(float, float, float)}：指定 RGB 三个分量，透明度默认为 1.0</li>
 *     <li>{@link #Color(float)}：由单个 float 的位模式解析 RGBA</li>
 *     <li>{@link #Color(String)}：由颜色名称字符串解析（如 {@code "red"}）</li>
 *     <li>{@link #Color(Color)}：拷贝另一个颜色</li>
 *     <li>{@link #Color(java.awt.Color)}：由 {@link java.awt.Color} 转换</li>
 * </ul>
 *
 * @author juicefries
 * @since 0.0.1
 */
public final class Color implements Paint, Cloneable, Copyable, Readonly, Serializable {

    @Serial
    private final static long serialVersionUID = Util.turn("Color");

    /** 白色，全不透明。 */
    public final static Color WHITE = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    /** 黑色，全不透明。 */
    public final static Color BLACK = new Color(0.0f, 0.0f, 0.0f, 1.0f);
    /** 无色，完全透明（透明度为 0）。 */
    public final static Color NONE = new Color(0.0f, 0.0f, 0.0f, 0.0f);
    /** 近黑色。 */
    public final static Color NEAR_BLACK = new Color(0.05f, 0.05f, 0.05f, 1.0f);
    /** 近白色。 */
    public final static Color NEAR_WHITE = new Color(0.975f, 0.975f, 0.975f, 1.0f);

    /** 红色。 */
    public final static Color RED = new Color(1.0f, 0.0f, 0.0f, 1.0f);
    /** 绿色。 */
    public final static Color GREEN = new Color(0.0f, 1.0f, 0.0f, 1.0f);
    /** 蓝色。 */
    public final static Color BLUE = new Color(0.0f, 0.0f, 1.0f, 1.0f);
    /** 黄色。 */
    public final static Color YELLOW = new Color(1.0f, 1.0f, 0.0f, 1.0f);
    /** 深灰色。 */
    public final static Color DARK_GRAY = new Color(0.25f, 0.25f, 0.25f, 1.0f);
    /** 浅灰色。 */
    public final static Color LIGHT_GRAY = new Color(0.75f, 0.75f, 0.75f, 1.0f);
    /** 灰色。 */
    public final static Color GRAY = new Color(0.5f, 0.5f, 0.5f, 1.0f);
    /** 青色。 */
    public final static Color CYAN = new Color(0.0f, 1.0f, 1.0f, 1.0f);
    /** 品红色。 */
    public final static Color MAGENTA = new Color(1.0f, 0.0f, 1.0f, 1.0f);
    /** 橙色。 */
    public final static Color ORANGE = new Color(1.0f, 0.5f, 0.0f, 1.0f);
    /** 粉红色。 */
    public final static Color PINK = new Color(1.0f, 0.75f, 0.8f, 1.0f);
    /** 紫色。 */
    public final static Color PURPLE = new Color(0.5f, 0.0f, 0.5f, 1.0f);
    /** 棕色。 */
    public final static Color BROWN = new Color(0.65f, 0.16f, 0.16f, 1.0f);
    /** 鸭绿色。 */
    public final static Color TEAL = new Color(0.0f, 0.5f, 0.5f, 1.0f);
    /** 藏青色。 */
    public final static Color NAVY = new Color(0.0f, 0.0f, 0.5f, 1.0f);
    /** 栗色。 */
    public final static Color MAROON = new Color(0.5f, 0.0f, 0.0f, 1.0f);
    /** 橄榄绿。 */
    public final static Color OLIVE = new Color(0.5f, 0.5f, 0.0f, 1.0f);
    /** 紫罗兰色。 */
    public final static Color VIOLET = new Color(0.93f, 0.51f, 0.93f, 1.0f);
    /** 靛蓝色。 */
    public final static Color INDIGO = new Color(0.29f, 0.0f, 0.51f, 1.0f);
    /** 金色。 */
    public final static Color GOLD = new Color(1.0f, 0.84f, 0.0f, 1.0f);
    /** 银色。 */
    public final static Color SILVER = new Color(0.75f, 0.75f, 0.75f, 1.0f);
    /** 珊瑚色。 */
    public final static Color CORAL = new Color(1.0f, 0.5f, 0.31f, 1.0f);
    /** 深红色。 */
    public final static Color CRIMSON = new Color(0.86f, 0.08f, 0.24f, 1.0f);
    /** 绿松石色。 */
    public final static Color TURQUOISE = new Color(0.25f, 0.88f, 0.82f, 1.0f);
    /** 鲑鱼色。 */
    public final static Color SALMON = new Color(0.98f, 0.5f, 0.45f, 1.0f);
    /** 薄荷绿。 */
    public final static Color MINT = new Color(0.6f, 1.0f, 0.6f, 1.0f);
    /** 天蓝色。 */
    public final static Color SKY_BLUE = new Color(0.53f, 0.81f, 0.92f, 1.0f);
    /** 薰衣草色。 */
    public final static Color LAVENDER = new Color(0.9f, 0.9f, 0.98f, 1.0f);
    /** 米色。 */
    public final static Color BEIGE = new Color(0.96f, 0.96f, 0.86f, 1.0f);

    /** 红色分量，范围 [0.0, 1.0]。 */
    private float red;
    /** 绿色分量，范围 [0.0, 1.0]。 */
    private float green;
    /** 蓝色分量，范围 [0.0, 1.0]。 */
    private float blue;
    /** 透明度分量，范围 [0.0, 1.0]。 */
    private float alpha;

    /**
     * 使用指定的 RGBA 分量创建颜色，各分量会被截断到 [0.0, 1.0] 范围。
     *
     * @param red   红色分量
     * @param green 绿色分量
     * @param blue  蓝色分量
     * @param alpha 透明度分量
     * @since 0.0.1
     */
    public Color(float red, float green, float blue, float alpha) {
        this.red   = rgbaFClamp(red);
        this.green = rgbaFClamp(green);
        this.blue  = rgbaFClamp(blue);
        this.alpha = rgbaFClamp(alpha);
    }

    /**
     * 使用指定的 RGB 分量创建颜色，透明度默认为 1.0（完全不透明）。
     *
     * @param red   红色分量
     * @param green 绿色分量
     * @param blue  蓝色分量
     * @since 0.0.1
     * @see #Color(float, float, float, float)
     */
    public Color(float red, float green, float blue) {
        this.red   = rgbaFClamp(red);
        this.green = rgbaFClamp(green);
        this.blue  = rgbaFClamp(blue);
        this.alpha = 1.0f;
    }

    /**
     * 由单个 float 的位模式解析 RGBA 颜色。
     * <br>
     * 32 位从高位到低位依次为：红（8 位）、绿（8 位）、蓝（8 位）、透明度（8 位）。
     *
     * @param rgba 打包了 RGBA 的 float 位模式
     * @since 0.0.1
     */
    public Color(float rgba) {
        int bits = Float.floatToIntBits(rgba);
        this.red   = rgbaFClamp(((bits >> 24) & 0xFF) / 255.0f);
        this.green = rgbaFClamp(((bits >> 16) & 0xFF) / 255.0f);
        this.blue  = rgbaFClamp(((bits >> 8) & 0xFF) / 255.0f);
        this.alpha = rgbaFClamp((bits & 0xFF) / 255.0f);
    }

    /**
     * 拷贝构造，创建与指定颜色分量相同的新颜色。
     *
     * @param color 源颜色
     * @throws NullPointerException 如果 {@code color} 为 {@code null}
     * @since 0.0.1
     */
    @Contract("null -> fail")
    public Color(Color color) {
        if (color == null) {
            throw new NullPointerException("color is null!");
        }
        this.red   = rgbaFClamp(color.red);
        this.green = rgbaFClamp(color.green);
        this.blue  = rgbaFClamp(color.blue);
        this.alpha = rgbaFClamp(color.alpha);
    }

    /**
     * 根据颜色名称创建颜色，名称不区分大小写，支持灰色（GRAY/GREY）等别名。
     * <br>
     * 无法识别或为 {@code null} / 空白时，默认使用 {@link #BLACK}。
     *
     * @param name 颜色名称，如 {@code "red"}、{@code "DARK_GRAY"}
     * @since 0.0.2
     */
    public Color(String name) {
        Color color;
        if (name == null || name.isBlank()) {
            color = BLACK;
        } else {
            color = switch (name.toUpperCase()) {
                case "WHITE" -> WHITE;
                case "NONE" -> NONE;
                case "RED" -> RED;
                case "GREEN" -> GREEN;
                case "BLUE", "COLOR" -> BLUE;
                case "YELLOW" -> YELLOW;
                case "CYAN" -> CYAN;
                case "MAGENTA" -> MAGENTA;
                case "ORANGE" -> ORANGE;
                case "PINK" -> PINK;
                case "GRAY", "GREY" -> GRAY;
                case "DARK_GRAY", "DARK_GREY" -> DARK_GRAY;
                case "LIGHT_GRAY", "LIGHT_GREY" -> LIGHT_GRAY;
                case "NEAR_WHITE" -> NEAR_WHITE;
                case "NEAR_BLACK" -> NEAR_BLACK;
                case "PURPLE" -> PURPLE;
                case "BROWN" -> BROWN;
                case "TEAL" -> TEAL;
                case "NAVY" -> NAVY;
                case "MAROON" -> MAROON;
                case "OLIVE" -> OLIVE;
                case "VIOLET" -> VIOLET;
                case "INDIGO" -> INDIGO;
                case "GOLD" -> GOLD;
                case "SILVER" -> SILVER;
                case "CORAL" -> CORAL;
                case "CRIMSON" -> CRIMSON;
                case "TURQUOISE" -> TURQUOISE;
                case "SALMON" -> SALMON;
                case "MINT" -> MINT;
                case "SKY_BLUE" -> SKY_BLUE;
                case "LAVENDER" -> LAVENDER;
                case "BEIGE" -> BEIGE;
                default -> BLACK;
            };
        }

        red = color.red;
        green = color.green;
        blue = color.blue;
        alpha = color.alpha;
    }

    /**
     * 由 {@link java.awt.Color} 转换创建颜色。
     *
     * @param color AWT 颜色
     * @throws NullPointerException 如果 {@code color} 为 {@code null}
     * @since 0.0.5
     * @see #Color(float, float, float, float)
     */
    @Contract("null -> fail")
    public Color(java.awt.Color color) {
        if (color == null) {
            throw new NullPointerException("color is null!");
        }
        float r = color.getRed()   / 255.0f;
        float g = color.getGreen() / 255.0f;
        float b = color.getBlue()  / 255.0f;
        float a = color.getAlpha() / 255.0f;
        this(r, g, b, a);
    }

    /**
     * 创建默认颜色（黑色）。
     * @since 0.0.1
     */
    public Color() {
        this("BLACK");
    }

    // ========================= GET =========================

    /**
     * 返回红色分量。
     *
     * @return 红色分量，范围 [0.0, 1.0]
     */
    @Contract(pure = true)
    public float getRed() {
        return red;
    }

    /**
     * 返回红色分量（{@link #getRed()} 的别名）。
     *
     * @return 红色分量，范围 [0.0, 1.0]
     */
    @Contract(pure = true)
    public float red() {
        return red;
    }

    /**
     * 返回绿色分量。
     *
     * @return 绿色分量，范围 [0.0, 1.0]
     */
    @Contract(pure = true)
    public float getGreen() {
        return green;
    }

    /**
     * 返回绿色分量（{@link #getGreen()} 的别名）。
     *
     * @return 绿色分量，范围 [0.0, 1.0]
     */
    @Contract(pure = true)
    public float green() {
        return green;
    }

    /**
     * 返回蓝色分量。
     *
     * @return 蓝色分量，范围 [0.0, 1.0]
     */
    @Contract(pure = true)
    public float getBlue() {
        return blue;
    }

    /**
     * 返回蓝色分量（{@link #getBlue()} 的别名）。
     *
     * @return 蓝色分量，范围 [0.0, 1.0]
     */
    @Contract(pure = true)
    public float blue() {
        return blue;
    }

    /**
     * 返回透明度分量。
     *
     * @return 透明度分量，范围 [0.0, 1.0]
     */
    @Contract(pure = true)
    public float getAlpha() {
        return alpha;
    }

    /**
     * 返回透明度分量（{@link #getAlpha()} 的别名）。
     *
     * @return 透明度分量，范围 [0.0, 1.0]
     */
    @Contract(pure = true)
    public float alpha() {
        return alpha;
    }

    /**
     * 返回 RGB 三分量数组。
     *
     * @return 长度为 3 的数组，顺序为红、绿、蓝
     */
    public float @NotNull [] getColor3f() {
        float[] color = Array.createF(3);
        color[0] = red;
        color[1] = green;
        color[2] = blue;
        return color;
    }

    /**
     * 返回 RGBA 四分量数组。
     *
     * @return 长度为 4 的数组，顺序为红、绿、蓝、透明度
     */
    public float @NotNull [] getColor4f() {
        float[] color = Array.createF(4);
        color[0] = red;
        color[1] = green;
        color[2] = blue;
        color[3] = alpha;
        return color;
    }

    /**
     * 返回当前颜色的副本（RGBA 分量相同）。
     *
     * @return 新的颜色副本
     */
    @Contract(" -> new")
    public @NotNull Color getColor() {
        return new Color(red, green, blue, alpha);
    }

    /**
     * 返回当前颜色的副本（{@link #getColor()} 的别名）。
     *
     * @return 新的颜色副本
     */
    @Contract(" -> new")
    public @NotNull Color color() {
        return new Color(red, green, blue, alpha);
    }

    /**
     * 转换为 RGBA 打包的 32 位整数，从高位到低位依次为红、绿、蓝、透明度（各 8 位）。
     *
     * @return RGBA 整数
     */
    public int toColorRGBA() {
        int red = Math.clamp((int) (this.red * 255.0f + 0.5f), 0, 255);
        int green = Math.clamp((int) (this.green * 255.0f + 0.5f), 0, 255);
        int blue = Math.clamp((int) (this.blue * 255.0f + 0.5f), 0, 255);
        int alpha = Math.clamp((int) (this.alpha * 255.0f + 0.5f), 0, 255);

        return (red << 24) | (green << 16) | (blue << 8) | alpha;
    }

    /**
     * 转换为 ARGB 打包的 32 位整数，从高位到低位依次为透明度、红、绿、蓝（各 8 位）。
     *
     * @return ARGB 整数
     */
    public int toColorARGB() {
        int red = Math.clamp((int) (this.red * 255.0f + 0.5f), 0, 255);
        int green = Math.clamp((int) (this.green * 255.0f + 0.5f), 0, 255);
        int blue = Math.clamp((int) (this.blue * 255.0f + 0.5f), 0, 255);
        int alpha = Math.clamp((int) (this.alpha * 255.0f + 0.5f), 0, 255);

        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    /**
     * 转换为 {@link java.awt.Color}。
     *
     * @return 对应的 AWT 颜色
     */
    @Contract(" -> new")
    public java.awt.@NotNull Color toAwtColor() {
        float r = red() * 255.0f;
        float g = green()  * 255.0f;
        float b = blue() * 255.0f;
        float a = alpha() * 255.0f;
        return new java.awt.Color(r,g,b,a);
    }

    // ========================= OTM =========================

    /**
     * 返回当前颜色的副本。
     *
     * @return 颜色副本
     * @deprecated 请使用 {@link #copy()} 代替
     */
    @Override
    @Deprecated
    public @NotNull Color clone() {
        try {
            Color color = (Color) super.clone();
            color.red = red;
            color.green = green;
            color.blue = blue;
            color.alpha = alpha;
            return color;

        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 返回当前颜色的副本。
     *
     * @return 颜色副本
     */
    @Override
    public @NotNull Color copy() {
        Color c = new Color();
        c.red   = red;
        c.green = green;
        c.blue  = blue;
        c.alpha = alpha;
        return c;
    }

    /**
     * 比较两个颜色的四个分量是否完全相同。
     *
     * @param object 要比较的对象
     * @return 如果为四个分量都相同的 {@code Color} 则返回 {@code true}，否则 {@code false}
     */
    @Override
    @Contract(value = "null -> false", pure = true)
    public boolean equals(Object object) {
        if (!(object instanceof Color color)) return false;
        float red = Float.compare(this.red, color.red);
        float green = Float.compare(this.green, color.green);
        float blue = Float.compare(this.blue, color.blue);
        float alpha = Float.compare(this.alpha, color.alpha);
        return (red == 0) && (green == 0) && (blue == 0) && (alpha == 0);
    }

    /**
     * 基于四个分量计算哈希值。
     *
     * @return 哈希值
     */
    @Override
    public int hashCode() {
        return Objects.hash(red, green, blue, alpha);
    }

    /**
     * 返回形如 {@code Color[red=...,green=...,blue=...,alpha=...]} 的字符串表示。
     *
     * @return 字符串表示
     */
    @Override
    public String toString() {
        return getClass().getCanonicalName() +
                "[red=" + red +
                ",green=" + green +
                ",blue=" + blue +
                ",alpha=" + alpha +
                "]"
                ;
    }

    /**
     * 将分量截断到 [0.0, 1.0] 范围。
     *
     * @param val 原始分量值
     * @return 截断后的分量值
     */
    @Contract(pure = true)
    private static float rgbaFClamp(float val) {
        return Math.clamp(val, 0.0f, 1.0f);
    }

    /**
     * 序列化钩子：按固定顺序写出四个颜色分量。
     *
     * @param out 对象输出流
     * @throws java.io.IOException 如果写入失败
     */
    @Serial
    @Contract("null -> fail")
    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        if (out == null) {
            throw new NullPointerException("(ObjectOutputStream) value is null!");
        }

        out.writeFloat(red);
        out.writeFloat(green);
        out.writeFloat(blue);
        out.writeFloat(alpha);
    }

    /**
     * 反序列化钩子：按相同顺序读入四个分量，并重新截断到 [0.0, 1.0]，
     * 防止被篡改的序列化数据产生越界分量。
     *
     * @param in 对象输入流
     * @throws java.io.IOException            如果读取失败
     * @throws ClassNotFoundException 如果找不到类
     */
    @Serial
    @Contract("null -> fail")
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        if (in == null) {
            throw new NullPointerException("(ObjectInputStream) value is null!");
        }

        red   = rgbaFClamp(in.readFloat());
        green = rgbaFClamp(in.readFloat());
        blue  = rgbaFClamp(in.readFloat());
        alpha = rgbaFClamp(in.readFloat());
    }


}
