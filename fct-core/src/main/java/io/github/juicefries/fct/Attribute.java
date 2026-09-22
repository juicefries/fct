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
// Data 2026/08/12 01:02
//

package io.github.juicefries.fct;

import io.github.juicefries.fct.sign.Copyable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class Attribute implements Copyable {

    private Matrix4f matrix4f = new Matrix4f();
    private Paint paint = Color.NEAR_BLACK.copy();
    private Font font;
    private Stroke stroke = BasicStroke.DEFAULT;
    private float opacity = 1.0f;
    private BlendMode blendMode = BlendMode.NORMAL;

    public Attribute() {

    }

    @Contract(" -> new")
    public static @NotNull Attribute create() {
        return new Attribute();
    }

    @Contract("null -> fail")
    public static @NotNull Attribute create(Matrix4f matrix4f) {
        if (matrix4f == null) {
            throw new NullPointerException("matrix4f is null!");
        }

        Attribute attribute = create();
        attribute.setMatrix4f(matrix4f);
        return attribute;
    }

    // ========================= SET =========================

    public void setMatrix4f(Matrix4f matrix4f) {
        if (matrix4f == null) {
            throw new NullPointerException("matrix4f is null!");
        }
        this.matrix4f = matrix4f;
    }

    public void setPaint(Paint paint) {
        if (paint == null) {
            throw new NullPointerException("paint is null!");
        }

        this.paint = paint;
    }

    public void setFont(Font font) {
        if (font == null) {
            throw new NullPointerException("font is null!");
        }

        this.font = font;
    }

    public void setOpacity(float opacity) {
        this.opacity = Math.clamp(opacity,0,1);
    }

    public void setBlendMode(BlendMode blendMode) {
        if (blendMode == null) {
            throw new NullPointerException("blendMode is null!");
        }

        this.blendMode = blendMode;
    }

    public void setStroke(Stroke stroke) {
        if (stroke == null) {
            throw new NullPointerException("stroke is null!");
        }

        this.stroke = stroke;
    }

    // ========================= GET =========================

    public Matrix4f getMatrix4f() {
        return matrix4f;
    }

    public Paint getPaint() {
        return paint;
    }

    public Font getFont() {
        return font;
    }

    public Stroke getStroke() {
        return stroke;
    }

    public float getOpacity() {
        return opacity;
    }

    public BlendMode getBlendMode() {
        return blendMode;
    }

    // ========================= =========================

    @Override
    public Attribute copy() {
        Attribute attribute = new Attribute();
        attribute.matrix4f = new Matrix4f(matrix4f);
        attribute.paint = paint;
        attribute.font = font;
        attribute.stroke = stroke;
        attribute.opacity = opacity;
        attribute.blendMode = blendMode;
        return attribute;
    }

    // ========================= =========================

}
