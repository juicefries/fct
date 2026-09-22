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
// Data 2026/08/11 03:37
//

package io.github.juicefries.fct;

import io.github.juicefries.fct.sign.Initializable;
import io.github.juicefries.fct.util.Array;
import io.github.juicefries.fct.util.Math;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

public abstract class Graphics implements Initializable {

    // ========================= SET =========================

    /**
     * 设置当前字体
     * <p>
     *     设置时将当前传入的字体写入Graphics的{@link Attribute}中，
     *     <br>
     *     也可通过{@link #setAttribute(Attribute)}统一设置。
     * </p>
     * @param font 字体
     * @since 0.0.1
     * @throws NullPointerException 字体不能为null
     * @throws NullPointerException 请勿在未设置{@link Attribute}时调用此方法
     */
    public abstract void setFont(Font font);
    public abstract void setColor(Color color);
    public abstract void setPaint(Paint paint);
    public abstract void setAttribute(Attribute attribute);
    public abstract void setMatrix4f(Matrix4f matrix4f);
    public abstract void setStroke(Stroke stroke);
    public abstract void setOpacity(float opacity);
    public abstract void setBlendMode(BlendMode blendMode);

    // ========================= GET =========================

    public abstract Font getFont();
    public abstract Color getColor();
    public abstract Paint getPaint();
    public abstract Attribute getAttribute();
    public abstract Matrix4f getMatrix4f();
    public abstract FontMetrics getFontMetrics();
    public abstract FontMetrics getFontMetrics(Font font);
    public abstract Stroke getStroke();
    public abstract float getOpacity();
    public abstract BlendMode getBlendMode();

    @Override
    public abstract boolean isInitialize();

    public abstract Graphics create();

    // ========================= OTM =========================

    /**
     * 设置一处裁剪区域
     * <p>
     *     自{@code 0.0.5}版本后默认将为空方法，部分类仍可使用实现。
     * </p>
     * @param x 起始点X
     * @param y 起始点Y
     * @param width 宽度
     * @param height 高度
     * @deprecated 由于GL的限制，一次中只能设置一处裁剪，因此废弃，但仍可使用
     * @since 0.0.1
     */
    @Deprecated(since = "0.0.1",forRemoval = true)
    public void scissor(int x, int y, int width, int height) {

    }

    /**
     * 释放裁剪区域
     * <p>
     *     自{@code 0.0.5}版本后默认将为空方法，部分类仍可使用实现。
     * </p>
     * @deprecated 由于GL的限制，一次中只能设置一处裁剪，因此废弃，但仍可使用
     * @since 0.0.1
     */
    @Deprecated(since = "0.0.1",forRemoval = true)
    public void release() {

    }


    /**
     * 设置一处裁剪区域
     *
     * <p>
     *     在当前裁剪区域的基础上，将裁剪范围收窄为传入的矩形区域，
     *     <br>
     *     设置后此区域内可被绘制，区域外的内容将被裁掉，
     *     <br>
     *     与{@link #unclip()}成对使用，支持嵌套裁剪。
     * </p>
     * @param x 起始点X
     * @param y 起始点Y
     * @param width 宽
     * @param height 高
     * @since 0.0.4
     * @see #unclip()
     * @throws IllegalArgumentException 宽度或高度小于0
     */
    public abstract void clip(int x, int y, int width, int height);

    /**
     * 释放裁剪区域
     *
     * <p>
     *     撤销最近一次{@link #clip(int, int, int, int)}设置的裁剪，
     *     <br>
     *     恢复为上一层裁剪区域，
     *     <br>
     *     若无剩余裁剪区域则完全释放裁剪。
     * </p>
     * @since 0.0.4
     * @see #clip(int, int, int, int)
     */
    public abstract void unclip();

    public abstract void translate(float x, float y);
    public abstract void translate(float x, float y, float z);

    public abstract void scale(float x, float y, float z);
    public abstract void scale(float x, float y);

    public abstract void rotate(float angle, float x, float y, float z);
    public abstract void rotate(float angle, float x, float y);

    /**
     * 保存当前属性状态
     * @since 0.0.2
     * @see Attribute
     */
    public abstract void save();

    /**
     * 恢复当前属性状态
     * @since 0.0.2
     * @see Attribute
     */
    public abstract void restore();

    /**
     * 清屏
     * @since 0.0.1
     */
    public abstract void clear();

    /**
     * 范围清屏
     *
     * <p>
     *     指定在屏幕上进行清屏，
     *     x向左，y向下,
     *     清理时的范围根据窗口的缓冲区大小决定，
     *     非逻辑尺寸。
     * </p>
     * @param x 起始点X
     * @param y 起始点Y
     * @param width 宽
     * @param height 高
     * @since 0.0.1
     * @see #clear()
     * @see #save()
     * @see #clip(int, int, int, int)
     * @see #unclip()
     * @see #restore()
     */
    public void clear(int x,int y,int width,int height) {
        // 保存当前状态
        save();

        // 裁剪
        clip(x, y, width, height);

        // 清屏
        clear();

        // 释放裁剪
        unclip();

        // 恢复
        restore();
    }

    /**
     *
     * 初始化
     * <p>
     *     初始化Graphics对象的实际各项操作
     * </p>
     * @throws IllegalStateException 在初始化后不建议再次调用
     * @since 0.0.1
     */
    @Override
    public abstract void initialize();

    public abstract void update();

    @Override
    public abstract void dispose();

    // ========================= FILL =========================

    public abstract void fillRect(float x, float y, float width, float height);

    public abstract void fillTriangle(float x1,float y1,float x2,float y2,float x3,float y3);

    // ========================= DRAW =========================

    /**
     * 渲染字符
     *
     * <p>
     *     在渲染时建议使用{@link #getFontMetrics()}预先计算好合理位置。
     * </p>
     * @param str 字符
     * @param x 位置X
     * @param y 位置Y
     * @since 0.0.3
     * @throws NullPointerException 字符不能为null
     * @see FontMetrics
     */
    public abstract void drawString(String str, float x, float y);

    /**
     * 绘制图片
     *
     * <p>
     *     通过传入图片和对应的顶点数组位置使用,
     *     <br>
     *     传入的数组至少需要大于8个顶点，受矩阵影响，
     *     <br>
     *     格式为{@code [[x1,y1],[x2,y2],...]}共需要4个顶点解释在平面上的位置,
     *     <br>
     *     顶点以顺时针渲染，图片四个角落关系如下:
     *     <br>
     *     图片的左上角渲染在{@code [x1,y1]}，
     *     <br>
     *     图片的右上角渲染在{@code [x2,y2]}，
     *     <br>
     *     图片的右下降渲染在{@code [x3,y3]}，
     *     <br>
     *     图片的左下角渲染在{@code [x4,y4]}。
     * </p>
     * @param image 图片
     * @param vertex 位置
     * @since 0.0.1
     * @see Image
     * @see Matrix4f
     * @throws IllegalArgumentException 传入的数组长度不能小于8
     * @throws NullPointerException 传入的图片和数组不能为{@code null}
     */
    public abstract void drawImage(Image image,float[] vertex);

    /**
     * 绘制图片
     *
     * <p>
     *     通过传入图片参数与其对应的8个顶点渲染图片，
     *     <br>
     *     {@link #drawImage(Image, float[])}对顶点展开版。
     *     <br>
     *     更简易使用{@link #drawImage(Image, float[])}
     * </p>
     *
     * @param image 图片
     * @param x1 顶点X1
     * @param y1 顶点Y1
     * @param x2 顶点X2
     * @param y2 顶点Y2
     * @param x3 顶点X3
     * @param y3 顶点Y3
     * @param x4 顶点X4
     * @param y4 顶点Y4
     * @since 0.0.1
     * @see #drawImage(Image, float[])
     * @throws NullPointerException 图片不能为null
     */
    public void drawImage(
            Image image,
            float x1,float y1, // 顶点 1
            float x2,float y2, // 顶点 2
            float x3,float y3, // 顶点 3
            float x4,float y4  // 顶点 4
    ) {
        if (image == null) {
            throw new NullPointerException("image is null!");
        }

        final float[] vertex = Array.createF(8);
        vertex[0] = x1;
        vertex[1] = y1;

        vertex[2] = x2;
        vertex[3] = y2;

        vertex[4] = x3;
        vertex[5] = y3;

        vertex[6] = x4;
        vertex[7] = y4;

        drawImage(image,vertex);
    }

    /**
     * 绘制图片
     *
     * <p>
     *     传入图片与对应的位置与尺寸渲染图片，<br>
     *     不建议在修改矩阵后使用，仅适合在{@link Window}默认创建的矩阵下使用。
     * </p>
     *
     * @param image 图片
     * @param x 起始点X
     * @param y 起始点Y
     * @param width 宽
     * @param height 高
     * @since 0.0.1
     * @see #drawImage(Image, float[])
     * @see #drawImage(Image, float, float, float, float, float, float, float, float)
     * @throws NullPointerException 图片不能为null
     * @throws IllegalArgumentException 传入的尺寸不能小于0
     */
    public void drawImage(Image image, float x, float y, float width, float height) {
        if (image == null) {
            throw new NullPointerException("image is null!");
        }
        if (width < 0) {
            throw new IllegalArgumentException("width is less than or equal to 0!");
        }
        if (height < 0) {
            throw new IllegalArgumentException("height is less than or equal to 0!");
        }
        final float w = x + width;
        final float h = y + height;
        drawImage(image, x, y, w, y, w, h, x, h);
    }

    /**
     * 绘制图片
     *
     * <p>
     *     传入图片与对应的位置与尺寸渲染图片，
     *     <br>
     *     使用图片的{@link Image#width()}与{@link Image#height()}作为尺寸，
     *     <br>
     *     不建议在修改矩阵后使用，仅适合在{@link Window}默认创建的矩阵下使用，
     *     <br>
     *     该方法会使用图片的原始尺寸显示，不建议使用。
     * </p>
     *
     * @param image 图片
     * @param x 起始点X
     * @param y 起始点Y
     * @since 0.0.1
     * @see #drawImage(Image, float[])
     * @see #drawImage(Image, float, float, float, float, float, float, float, float)
     * @see #drawImage(Image, float, float, float, float)
     * @throws NullPointerException 图片不能为null
     * @throws IllegalArgumentException 传入的尺寸不能小于0
     */
    public void drawImage(Image image,float x,float y) {
        if (image == null) {
            throw new NullPointerException("image is null!");
        }
        drawImage(image,x,y,image.width(),image.height());
    }

}
