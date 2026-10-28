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
// Data 2026/10/28 00:04
//

package io.github.juicefries.fct;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;

/**
 * 绘制状态：一个窗口一份，绑定到当前线程。
 *
 * <ul>
 *   <li><b>缓冲区</b>：本轮想改成的目标值</li>
 *   <li><b>状态栈</b>：从驱动读回来的真实值快照</li>
 * </ul>
 *
 * @since 1.0.5
 */
public abstract class DrawingState {

    // ========================= 状态项常量 =========================
    //
    // 这些字符串是 setXxx / getXxx 的 key。用常量而不是字面量，拼错会在编译期发现。

    // ---- 开关 ----
    /** 深度测试。 */
    public static final String DEPTH_TEST = "DepthTest";
    /** 颜色混合。 */
    public static final String BLEND = "Blend";
    /** 背面剔除。 */
    public static final String CULL_FACE = "CullFace";
    /** 裁剪测试。 */
    public static final String SCISSOR_TEST = "ScissorTest";
    /** 模板测试。 */
    public static final String STENCIL_TEST = "StencilTest";
    /** 抖动。 */
    public static final String DITHER = "Dither";
    /** 多边形偏移（填充模式）。 */
    public static final String POLYGON_OFFSET_FILL = "PolygonOffsetFill";
    /** 颜色逻辑运算。 */
    public static final String COLOR_LOGIC_OP = "ColorLogicOp";
    /** 多重采样。 */
    public static final String MULTISAMPLE = "Multisample";
    /** alpha 转覆盖率（软边）。 */
    public static final String SAMPLE_ALPHA_TO_COVERAGE = "SampleAlphaToCoverage";
    /** alpha 转 1。 */
    public static final String SAMPLE_ALPHA_TO_ONE = "SampleAlphaToOne";
    /** 覆盖率采样。 */
    public static final String SAMPLE_COVERAGE = "SampleCoverage";
    /** 丢弃光栅化输出（只跑顶点阶段）。 */
    public static final String RASTERIZER_DISCARD = "RasterizerDiscard";
    /** 由顶点着色器控制点大小。 */
    public static final String PROGRAM_POINT_SIZE = "ProgramPointSize";

    // ---- 枚举与句柄 ----
    /** 剔除的面。 */
    public static final String CULL_FACE_MODE = "CullFaceMode";
    /** 三角形绕序。 */
    public static final String FRONT_FACE = "FrontFace";
    /** 深度比较函数。 */
    public static final String DEPTH_FUNC = "DepthFunc";
    /** 颜色逻辑运算模式。 */
    public static final String LOGIC_OP_MODE = "LogicOpMode";
    /** 模板值掩码。 */
    public static final String STENCIL_VALUE_MASK = "StencilValueMask";
    /** 活动纹理单元。 */
    public static final String ACTIVE_TEXTURE = "ActiveTexture";
    /** 当前着色器程序。 */
    public static final String PROGRAM = "Program";
    /** 当前顶点数组对象。 */
    public static final String VERTEX_ARRAY = "VertexArray";
    /** 当前数组缓冲。 */
    public static final String ARRAY_BUFFER = "ArrayBuffer";
    /** 当前 2D 纹理绑定。 */
    public static final String TEXTURE_2D = "Texture2D";
    /** 当前 2D 纹理数组绑定。 */
    public static final String TEXTURE_2D_ARRAY = "Texture2DArray";
    /** 当前帧缓冲。 */
    public static final String FRAMEBUFFER = "Framebuffer";

    // ---- 浮点 ----
    /** 线宽。 */
    public static final String LINE_WIDTH = "LineWidth";
    /** 点大小。 */
    public static final String POINT_SIZE = "PointSize";
    /** 深度清屏值。 */
    public static final String CLEAR_DEPTH = "ClearDepth";

    // ---- 向量与组合项 ----
    /** 视口 {@code [x, y, w, h]}。 */
    public static final String VIEWPORT = "Viewport";
    /** 裁剪框 {@code [x, y, w, h]}。 */
    public static final String SCISSOR_BOX = "ScissorBox";
    /** 清屏颜色 {@code [r, g, b, a]}。 */
    public static final String CLEAR_COLOR = "ClearColor";
    /** 混合常量色 {@code [r, g, b, a]}。 */
    public static final String BLEND_COLOR = "BlendColor";
    /** 深度范围 {@code [near, far]}。 */
    public static final String DEPTH_RANGE = "DepthRange";
    /** 混合因子 {@code [srcRgb, dstRgb, srcAlpha, dstAlpha]}。 */
    public static final String BLEND_FUNC = "BlendFunc";
    /** 混合方程 {@code [rgb, alpha]}。 */
    public static final String BLEND_EQUATION = "BlendEquation";
    /** 模板函数 {@code [func, ref, mask]}。 */
    public static final String STENCIL_FUNC = "StencilFunc";
    /** 模板操作 {@code [fail, passDepthFail, passDepthPass]}。 */
    public static final String STENCIL_OP = "StencilOp";
    /** 多边形偏移 {@code [factor, units]}。 */
    public static final String POLYGON_OFFSET = "PolygonOffset";
    /** 采样覆盖率 {@code [value, invert]}。 */
    public static final String SAMPLE_COVERAGE_PARAMS = "SampleCoverageParams";

    // ---- 掩码 ----
    /** 深度写入。 */
    public static final String DEPTH_MASK = "DepthMask";
    /** 颜色写入 {@code [r, g, b, a]}。 */
    public static final String COLOR_MASK = "ColorMask";

    final static ThreadLocal<DrawingState> fct_drawing_state_thread_local = new ThreadLocal<>();

    String name;

    @Contract(pure = true)
    protected DrawingState() {

    }

    // ========================= TTT =========================

    // 设置
    public abstract void setInt(String key, int value);
    public abstract void setInts(String key, int... values);
    public abstract void setFloat(String key, float value);
    public abstract void setFloats(String key, float... values);
    public abstract void setFlag(String key, boolean value);
    public abstract void setFlags(String key, boolean... values);

    // 查询
    public abstract int getInt(String key);
    public abstract int[] getInts(String key);
    public abstract float getFloat(String key);
    public abstract float[] getFloats(String key);
    public abstract boolean getFlag(String key);
    public abstract boolean[] getFlags(String key);


    public abstract void update() throws IllegalDrawingStateException;

    public abstract void save() throws IllegalDrawingStateException;
    public abstract void restore() throws IllegalDrawingStateException;

    public abstract int getStackSize();

    // ========================= OTM =========================

    public abstract DrawingState bind(Object val) throws IllegalDrawingStateException;
    public abstract void stateCheck();

    // ========================= SET =========================
    public void setName(String name) {
        if (name == null) {
            throw new NullPointerException("name is null!");
        }
        this.name = name;
    }

    // ========================= GET =========================

    public @NonNls String getName() {
        return name;
    }

    // ========================= EEE =========================

}






