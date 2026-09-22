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
// Data 2026/08/20 23:09
//

package io.github.juicefries.fct;

import io.github.juicefries.fct.util.Array;
import io.github.juicefries.fct.util.Lock;
import io.github.juicefries.fct.util.Math;
import io.github.juicefries.fct.util.TraverseList;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

public abstract class Graphics2D extends Graphics {

    final Lock lock = Lock.create();

    private Attribute attribute;
    private final TraverseList<Attribute> attributes = new TraverseList<>(Attribute.class);

    protected Graphics2D() {

    }

    @Override
    public abstract Graphics create();

    // ========================= FILL =========================

    public void fillRect(Location location,Size size) {
        if (location == null) {
            throw new NullPointerException("location is null!");
        }
        if (size == null) {
            throw new NullPointerException("size is null!");
        }
        if (size.width < 0) {
            throw new IllegalArgumentException("width is less than or equal to 0!");
        }
        if (size.height < 0) {
            throw new IllegalArgumentException("height is less than or equal to 0!");
        }
        fillRect(location.x,location.y,size.width,size.height);
    }

    public void fillTriangle(float[] triangle) {
        if (triangle == null) {
            throw new NullPointerException("triangle is null!");
        }
        Array.requireNotLessThan(triangle,6);

        fillTriangle(
                triangle[0],triangle[1],
                triangle[2],triangle[3],
                triangle[4],triangle[5]
        );
    }

    public abstract void fillRoundRect(float x, float y, float width, float height, float arcWidth, float arcHeight);
    public abstract void fillOval(float x, float y, float width, float height);
    public abstract void fillPolygon(float[] xPoints, float[] yPoints, int nPoints);
    public abstract void fillArc(float x, float y, float width, float height, float startAngle, float arcAngle);

    // ========================= DRAW =========================

    public abstract void drawString(String str, float x, float y, float maxWidth);

    public abstract void drawRect(float x, float y, float width, float height);
    public abstract void drawRoundRect(float x, float y, float width, float height, float arcWidth, float arcHeight);
    public abstract void drawLine(float x1, float y1, float x2, float y2);
    public abstract void drawOval(float x, float y, float width, float height);
    public abstract void drawPolygon(float[] xPoints, float[] yPoints, int nPoints);
    public abstract void drawArc(float x, float y, float width, float height, float startAngle, float arcAngle);

    /**
     * 绘制图片
     *
     * <p>
     *     通过传入图片和对应的顶点数组位置使用,
     *     <br>
     *     传入的数组至少需要大于3个顶点单位，受矩阵影响，
     *     <br>
     *     格式为{@code [[x1,y1,u,v],[x2,y2,u,v],...]}共需要4个顶点单位解释在平面上的位置,
     *     <br>
     *     允许传入带UV参数的数组，其实就是我懒得写更多复杂实现，这里直接暴露出来。
     *     <br>
     *     不推荐用，因为这是传的裸数据。
     * </p>
     * @param image 图片
     * @param vertex 位置
     * @since 0.0.3
     * @see Image
     * @see Matrix4f
     * @see io.github.juicefries.fct.util.Math#match(int, int)
     * @throws IllegalArgumentException 传入的数组长度不能小于1个顶点单位
     * @throws NullPointerException 传入的图片和数组不能为{@code null}
     */
    public abstract void drawImageUV(Image image,float[] vertex);

    @ApiStatus.Experimental
    public void drawImageUV(Image image,float[] vertex,int length) {
        if (image == null) {
            throw new NullPointerException("image is null!");
        }
        if (vertex == null) {
            throw new NullPointerException("vertex is null!");
        }
        if (!Math.match(4 * 3,length)) {
            throw new IllegalArgumentException("Mismatched units:" + vertex.length);
        }
        Array.requireNotLessThan(vertex,4 * 3);
        var arr = Array.copyArrF(vertex,length);
        drawImageUV(image, arr);
    }

    public abstract void drawImage(Image image, int srcX, int srcY, int srcW, int srcH,
                                   float dstX, float dstY, float dstW, float dstH);

    // ========================= SET =========================

    @Override
    public void setFont(Font font) {
        if (font == null) {
            throw new NullPointerException("font is null!");
        }
        if (attribute == null) {
            throw new NullPointerException("attribute is null!");
        }
        synchronized (getLock()) {
            attribute.setFont(font);
        }
    }

    @Override
    public void setColor(Color color) {
        if (color == null) {
            throw new NullPointerException("color is null!");
        }
        setPaint(color);
    }

    @Override
    public void setPaint(Paint paint) {
        if (paint == null) {
            throw new NullPointerException("paint is null!");
        }
        if (attribute == null) {
            throw new NullPointerException("attribute is null!");
        }
        synchronized (getLock()) {
            attribute.setPaint(paint);
        }
    }

    @Override
    public void setAttribute(Attribute attribute) {
        if (attribute == null) {
            throw new NullPointerException("attribute is null!");
        }
        synchronized (getLock()) {
            this.attribute = attribute;
        }
    }

    @Override
    public void setMatrix4f(Matrix4f matrix4f) {
        if (matrix4f == null) {
            throw new NullPointerException("matrix4f is null!");
        }
        if (attribute == null) {
            throw new NullPointerException("attribute is null!");
        }
        synchronized (getLock()) {
            attribute.setMatrix4f(matrix4f);
        }
    }

    @Override
    public void setStroke(Stroke stroke) {
        if (stroke == null) throw new NullPointerException("stroke is null!");
        synchronized (getLock()) {
            attribute.setStroke(stroke);
        }
    }

    @Override
    public void setOpacity(float opacity) {
        synchronized (getLock()) {
            attribute.setOpacity(opacity);
        }
    }

    @Override
    public void setBlendMode(BlendMode blendMode) {
        if (blendMode == null) throw new NullPointerException("blendMode is null!");
        synchronized (getLock()) {
            attribute.setBlendMode(blendMode);
        }
    }


    // ========================= GET =========================

    public final Lock getLock() {
        return lock;
    }

    @Override
    public Font getFont() {
        var font = attribute.getFont();
        if (attribute == null || font == null) {
            return new Font(Font.DEFAULT, 1.0f);
        }
        return font;
    }

    @Override
    public Color getColor() {
        Paint paint = attribute.getPaint();
        if (paint instanceof Color color) {
            return color;
        }
        return Color.WHITE;
    }

    @Override
    public Paint getPaint() {
        return attribute.getPaint();
    }

    @Override
    public Attribute getAttribute() {
        return attribute;
    }

    @Override
    public Matrix4f getMatrix4f() {
        return attribute.getMatrix4f();
    }

    @Override
    public Stroke getStroke() {
        return attribute.getStroke();
    }

    @Override
    public float getOpacity() {
        return attribute.getOpacity();
    }

    @Override
    public BlendMode getBlendMode() {
        return attribute.getBlendMode();
    }

    // ========================= OTM =========================

    @Override
    public void save() {
        if (attribute == null) {
            throw new NullPointerException("attribute is null!");
        }
        synchronized (getLock()) {
            attributes.add(attribute.copy());
        }
    }

    @Override
    public void restore() {
        if (attributes.isEmpty()) {
            throw new IllegalStateException("No saved state!");
        }
        synchronized (getLock()) {
            Attribute attribute = attributes.removeLast();
            if (attribute == null) {
                throw new NullPointerException("attribute is null!");
            }
            this.attribute = attribute.copy();
        }
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName();
    }
}
