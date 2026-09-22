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
// Data 2026/08/11 03:52
//

package io.github.juicefries.fct;

import io.github.juicefries.fct.glfw.GLFWUtil;
import io.github.juicefries.fct.image.Format;
import io.github.juicefries.fct.logging.LoggerFactory;
import io.github.juicefries.fct.shader.ShaderLoader;
import io.github.juicefries.fct.sign.ApiSign;
import io.github.juicefries.fct.sign.Initializable;
import io.github.juicefries.fct.util.Array;
import io.github.juicefries.fct.util.Math;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL33;

/**
 * <h>GLGraphics2D</h>
 *
 * <p>
 * 默认基本没有矩阵，{@link Window}类默认初始化的默认矩阵为左上角原点，即x向右y向下。
 * <br>
 * <blockquote><pre>
 *         GLGraphics g = new GLGraphics2D();
 *         g.initialize();
 *         Attribute attribute = new Attribute();
 *         Matrix4f matrix4f = new Matrix4f().ortho(0, width, height, 0, -1, 1);
 *         attribute.setMatrix4f(val);
 *         g.setAttribute(attribute);
 *         g.update();
 *
 *         g.setColor(Color.RED);
 *         g.fillRect(0.0f,0.0f,50.0f,50.0f);
 * </pre></blockquote><p>
 * 要注意的一点是，{@link GLGraphics2D}类的所有方法应当在绑定了对应上下文的线程调用，
 * <br>
 * 在{@link Window}类中可以通过重写{@link Window#paint(Graphics)}方法绘制。
 * </p>
 *
 * @author juicefries
 * @version 1.2
 * @see Graphics
 * @see Graphics2D
 * @see Shader
 * @see Attribute
 * @see Matrix4f
 * @since 0.0.1
 */
public class GLGraphics2D extends Graphics2D implements Initializable {

    private final static String DEFAULT_SHADER_SRC_PATH = "/io/github/juicefries/fct/shaders/";

    @Deprecated(since = "0.0.2")
    public final static String DEFAULT_VERTEX_SHADER_SRC_PATH = "/io/github/juicefries/fct/shaders/graphics2d.vert";
    @Deprecated(since = "0.0.2")
    public final static String DEFAULT_FRAGMENT_SHADER_SRC_PATH = "/io/github/juicefries/fct/shaders/graphics2d.frag";

    private static final Logger logger = LoggerFactory.getLogger(GLGraphics2D.class);

    private final AtomicBoolean initialize = new AtomicBoolean(false);
    private final AtomicBoolean cachedTexture = new AtomicBoolean(true);

    final Map<String, Shader> shaders = new HashMap<>();

    final Map<Image, Integer> textures = new HashMap<>();

    final Map<FontFace, int[]> fontTextures = new HashMap<>();

    final ArrayDeque<int[]> clipStack = new ArrayDeque<>();

    /**
     * 构造方法
     * 初始化{@link GLGraphics2D}类
     *
     * @since 0.0.1
     */
    public GLGraphics2D() {

    }

    public static @NotNull Graphics build() {
        return build(true);
    }

    public static @NotNull Graphics build(boolean init) {
        Graphics g = new GLGraphics2D();
        if (init) {
            g.initialize();
        }
        return g;
    }

    @Override
    public FontMetrics getFontMetrics() {
        return getFontMetrics(getFont());
    }

    @Override
    public FontMetrics getFontMetrics(Font font) {
        if (font == null) throw new NullPointerException("font is null!");
        return resolveMetrics(font);
    }

    @Contract("_ -> new")
    private @NotNull FontMetrics resolveMetrics(@NotNull Font font) {
        int[] fw = {0}, fh = {0};
        long window = GLFW.glfwGetCurrentContext();
        GLFW.glfwGetFramebufferSize(window, fw, fh);

        Matrix4f m = getMatrix4f();
        float scaleX = fw[0] * java.lang.Math.abs(m.m00()) / 2f;
        float scaleY = fh[0] * java.lang.Math.abs(m.m11()) / 2f;

        FontFace face = Toolkit.getOrCreate(font.name(), font.width() * scaleX, font.height() * scaleY);
        return new FontMetrics(font, face, scaleX, scaleY);
    }


    @Override
    public Graphics create() {
        synchronized (lock) {
            Graphics g = new GLGraphics2D();
            g.setAttribute(getAttribute().copy());
            return g;
        }
    }

    @Override
    public void initialize() {
        var shadersInit = new AtomicBoolean(false);

        if (!shaders.isEmpty()) {
            for (Shader shader : shaders.values()) {
                if (shader == null) continue;
                if (!shader.isInitialize()) continue;
                shadersInit.set(true);
                break;
            }
        }

        if (isInit() || shadersInit.get()) {
            throw new IllegalStateException("Graphics have been initialized!");
        }

        var pattern = Shader.build();
        pattern.useProgram();
        // 位置属性（location = 0）
        GL33.glVertexAttribPointer(0, 2, GL33.GL_FLOAT, false, 4 * 4, 0);
        GL33.glEnableVertexAttribArray(0);
        // 纹理坐标属性（location = 1）
        GL33.glVertexAttribPointer(1, 2, GL33.GL_FLOAT, false, 4 * 4, 2 * 4);
        GL33.glEnableVertexAttribArray(1);

        pattern.linkShader(
                ShaderLoader._fct_load_shader_utf_8("%spattern.vert".formatted(DEFAULT_SHADER_SRC_PATH)),
                ShaderLoader._fct_load_shader_utf_8("%spattern.frag".formatted(DEFAULT_SHADER_SRC_PATH))
        );
        GL33.glEnableVertexAttribArray(0);
        shaders.put("pattern", pattern);

        var image = Shader.build();
        image.useProgram();
        // 位置属性（location = 0）
        GL33.glVertexAttribPointer(0, 2, GL33.GL_FLOAT, false, 4 * 4, 0);
        GL33.glEnableVertexAttribArray(0);
        // 纹理坐标属性（location = 1）
        GL33.glVertexAttribPointer(1, 2, GL33.GL_FLOAT, false, 4 * 4, 2 * 4);
        GL33.glEnableVertexAttribArray(1);
        image.linkShader(
                ShaderLoader._fct_load_shader_utf_8("%simage.vert".formatted(DEFAULT_SHADER_SRC_PATH)),
                ShaderLoader._fct_load_shader_utf_8("%simage.frag".formatted(DEFAULT_SHADER_SRC_PATH))
        );
        shaders.put("image", image);

        var text = Shader.build();
        text.useProgram();
        GL33.glVertexAttribPointer(0, 2, GL33.GL_FLOAT, false, 4 * 4, 0);
        GL33.glEnableVertexAttribArray(0);
        GL33.glVertexAttribPointer(1, 2, GL33.GL_FLOAT, false, 4 * 4, 2 * 4);
        GL33.glEnableVertexAttribArray(1);
        text.linkShader(
                ShaderLoader._fct_load_shader_utf_8("%stext.vert".formatted(DEFAULT_SHADER_SRC_PATH)),
                ShaderLoader._fct_load_shader_utf_8("%stext.frag".formatted(DEFAULT_SHADER_SRC_PATH))
        );
        shaders.put("text", text);


        GL33.glGetError();
        initialize.set(true);
    }

    @Override
    public void fillRect(float x, float y, float width, float height) {
        Paint paint = getPaint();
        float[] vert = buildFillVertices(x, y, width, height, paint);

        Shader shader = shaders.get("pattern");
        shader.useProgram();
        GL33.glBindVertexArray(shader.vao());
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, shader.vbo());
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, vert, GL33.GL_STREAM_DRAW);
        GL33.glVertexAttribPointer(0, 2, GL33.GL_FLOAT, false, 4 * 4, 0);
        GL33.glEnableVertexAttribArray(0);
        GL33.glVertexAttribPointer(1, 2, GL33.GL_FLOAT, false, 4 * 4, 2 * 4);
        GL33.glEnableVertexAttribArray(1);

        setupPaintUniforms(shader, paint);
        GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, 6);
    }

    @Override
    public void fillTriangle(float x1, float y1, float x2, float y2, float x3, float y3) {
        Paint paint = getPaint();
        float[] vert = buildTriangleVertices(x1, y1, x2, y2, x3, y3, paint);

        Shader shader = shaders.get("pattern");
        shader.useProgram();
        GL33.glBindVertexArray(shader.vao());
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, shader.vbo());
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, vert, GL33.GL_STREAM_DRAW);
        GL33.glVertexAttribPointer(0, 2, GL33.GL_FLOAT, false, 4 * 4, 0);
        GL33.glEnableVertexAttribArray(0);
        GL33.glVertexAttribPointer(1, 2, GL33.GL_FLOAT, false, 4 * 4, 2 * 4);
        GL33.glEnableVertexAttribArray(1);

        setupPaintUniforms(shader, paint);
        GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, 3);
    }

    @Override
    public void drawImageUV(Image image, float[] vertex) {
        if (image == null) {
            throw new NullPointerException("image is null!");
        }
        Array.requireNotLessThan(vertex, 4 * 3);
        if (!Math.match(4 * 3, vertex.length)) {
            throw new IllegalArgumentException("Mismatched units:" + vertex.length);
        }

        int tid = checkIfTextureIsCreated(image);
        if (tid == 0) return;

        Shader shader = shaders.get("image");
        shader.useProgram();
        GL33.glUniform1f(shader.getUniform("uOpacity"), getOpacity());
        GL33.glBindVertexArray(shader.vao());
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, shader.vbo());
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, vertex, GL33.GL_STREAM_DRAW);
        // 位置属性（location = 0）
        GL33.glVertexAttribPointer(0, 2, GL33.GL_FLOAT, false, 4 * 4, 0);
        GL33.glEnableVertexAttribArray(0);
        // 纹理坐标属性（location = 1）
        GL33.glVertexAttribPointer(1, 2, GL33.GL_FLOAT, false, 4 * 4, 2 * 4);
        GL33.glEnableVertexAttribArray(1);

        // 绑定纹理到纹理单元 0
        GL33.glUniform1i(shader.getUniform("image"), 0);
        GL33.glActiveTexture(GL33.GL_TEXTURE0);
        GL33.glBindTexture(GL33.GL_TEXTURE_2D, tid);

        // ---------- 绘制 ----------
        GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, vertex.length / 4);
    }

    @Override
    public void drawImage(Image image, float[] vertex) {
        if (image == null) {
            throw new NullPointerException("image is null!");
        }
        Array.requireNotLessThan(vertex, 8);

        int tid = checkIfTextureIsCreated(image);
        if (tid == 0) {
            return;
        }

        // ---------- 构建顶点数据（位置 + 纹理坐标，交错排列） ----------
        float x1 = vertex[0], y1 = vertex[1];
        float x2 = vertex[2], y2 = vertex[3];
        float x3 = vertex[4], y3 = vertex[5];
        float x4 = vertex[6], y4 = vertex[7];

        // 两个三角形： (0,1,2) 和 (0,2,3)
        float[] vert = {
                // 三角形1：左上 -> 右上 -> 右下
                x1, y1, 0f, 0f,   // 左上角 → UV (0,0)
                x2, y2, 1f, 0f,   // 右上角 → UV (1,0)
                x3, y3, 1f, 1f,   // 右下角 → UV (1,1)
                // 三角形2：左上 -> 右下 -> 左下
                x1, y1, 0f, 0f,   // 左上角 → UV (0,0)
                x3, y3, 1f, 1f,   // 右下角 → UV (1,1)
                x4, y4, 0f, 1f    // 左下角 → UV (0,1)
        };
        Shader shader = shaders.get("image");
        shader.useProgram();
        GL33.glUniform1f(shader.getUniform("uOpacity"), getOpacity());
        GL33.glBindVertexArray(shader.vao());
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, shader.vbo());
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, vert, GL33.GL_STREAM_DRAW);
        // 位置属性（location = 0）
        GL33.glVertexAttribPointer(0, 2, GL33.GL_FLOAT, false, 4 * 4, 0);
        GL33.glEnableVertexAttribArray(0);
        // 纹理坐标属性（location = 1）
        GL33.glVertexAttribPointer(1, 2, GL33.GL_FLOAT, false, 4 * 4, 2 * 4);
        GL33.glEnableVertexAttribArray(1);

        // 绑定纹理到纹理单元 0
        GL33.glUniform1i(shader.getUniform("image"), 0);
        GL33.glActiveTexture(GL33.GL_TEXTURE0);
        GL33.glBindTexture(GL33.GL_TEXTURE_2D, tid);

        // ---------- 绘制 ----------
        GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, 6);
    }

    @Override
    public void drawString(String str, float x, float y) {
        if (str == null) throw new NullPointerException("str is null!");

        Font font = getFont();
        if (font == null || str.isEmpty()) return;

        int[] fw = {0}, fh = {0};
        long window = GLFW.glfwGetCurrentContext();
        GLFW.glfwGetFramebufferSize(window, fw, fh);

        Matrix4f m = getMatrix4f();
        float scaleX = fw[0] * java.lang.Math.abs(m.m00()) / 2f;
        float scaleY = fh[0] * java.lang.Math.abs(m.m11()) / 2f;
        float pixelW = font.width() * scaleX;
        float pixelH = font.height() * scaleY;
        if (pixelW <= 0 || pixelH <= 0) return;

        FontFace face = Toolkit.getOrCreate(font.name(), pixelW, pixelH);
        ensureFontTextures(face);

        int pw = face.pageWidth(), ph = face.pageHeight();
        float penX = x * scaleX, penY = y * scaleY;

        Map<Integer, List<Float>> byPage = new HashMap<>();
        for (int i = 0; i < str.length(); ) {
            int cp = str.codePointAt(i);
            i += Character.charCount(cp);

            Glyph g = Toolkit.ensureGlyph(face, cp);
            if (g == null || g.page < 0) {
                if (g != null) penX += g.xadvance;
                continue;
            }



            float left = (penX + g.xoff) / scaleX;
            float top = (penY + g.yoff) / scaleY;   // 顶部 = baseline + yoff（yoff 为负→在基线上方）
            float right = left + (g.x1 - g.x0) / scaleX;
            float bottom = top + (g.y1 - g.y0) / scaleY;

            float u0 = g.x0 / (float) pw, v0 = g.y0 / (float) ph;
            float u1 = g.x1 / (float) pw, v1 = g.y1 / (float) ph;

            List<Float> list = byPage.computeIfAbsent(g.page, _ -> new ArrayList<>());
            Float[] q = {left, top, u0, v0, right, top, u1, v0, right, bottom, u1, v1,
                    left, top, u0, v0, right, bottom, u1, v1, left, bottom, u0, v1};
            list.addAll(java.util.Arrays.asList(q));

            penX += g.xadvance;
        }
        if (byPage.isEmpty()) return;


        Shader shader = shaders.get("text");
        shader.useProgram();


        GL33.glUniform1f(shader.getUniform("uOpacity"), getOpacity());

        GL33.glEnable(GL33.GL_BLEND);
        GL33.glBlendFunc(GL33.GL_SRC_ALPHA, GL33.GL_ONE_MINUS_SRC_ALPHA);

        GL33.glBindVertexArray(shader.vao());
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, shader.vbo());
        GL33.glUniform1i(shader.getUniform("glyphTexture"), 0);
        GL33.glActiveTexture(GL33.GL_TEXTURE0);
        float[] color = getColor().getColor4f();
        GL33.glUniform4f(shader.getUniform("color"), color[0], color[1], color[2], color[3]);

        int[] tex = fontTextures.get(face);
        for (Map.Entry<Integer, List<Float>> e : byPage.entrySet()) {
            float[] arr = new float[e.getValue().size()];
            int idx = 0;
            for (float f : e.getValue()) arr[idx++] = f;

            GL33.glBufferData(GL33.GL_ARRAY_BUFFER, arr, GL33.GL_STREAM_DRAW);
            GL33.glVertexAttribPointer(0, 2, GL33.GL_FLOAT, false, 4 * 4, 0);
            GL33.glEnableVertexAttribArray(0);
            GL33.glVertexAttribPointer(1, 2, GL33.GL_FLOAT, false, 4 * 4, 2 * 4);
            GL33.glEnableVertexAttribArray(1);
            GL33.glBindTexture(GL33.GL_TEXTURE_2D, tex[e.getKey()]);
            GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, arr.length / 4);
        }
    }

    // ===================== 图元 =====================



    private void drawTriangles(float[] vert, Paint paint) {
        Shader shader = shaders.get("pattern");
        shader.useProgram();
        applyBlendMode();
        GL33.glBindVertexArray(shader.vao());
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, shader.vbo());
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, vert, GL33.GL_STREAM_DRAW);
        GL33.glVertexAttribPointer(0, 2, GL33.GL_FLOAT, false, 4 * 4, 0);
        GL33.glEnableVertexAttribArray(0);
        GL33.glVertexAttribPointer(1, 2, GL33.GL_FLOAT, false, 4 * 4, 2 * 4);
        GL33.glEnableVertexAttribArray(1);
        setupPaintUniforms(shader, paint);
        GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, vert.length / 4);
    }

    private void addV(@NotNull List<Float> l, float x, float y) {
        l.add(x);
        l.add(y);
        l.add(0f);
        l.add(0f);
    }

    private void addTri(List<Float> l, float x1, float y1, float x2, float y2, float x3, float y3) {
        addV(l, x1, y1);
        addV(l, x2, y2);
        addV(l, x3, y3);
    }

    private float @NotNull [] toF(@NotNull List<Float> l) {
        float[] a = new float[l.size()];
        for (int i = 0; i < a.length; i++) a[i] = l.get(i);
        return a;
    }

    // 椭圆弧点（x,y 交替，含两端点）
    private void appendArcXY(List<Float> l, float cx, float cy, float rx, float ry, float startDeg, float endDeg, int steps) {
        double s = java.lang.Math.toRadians(startDeg), e = java.lang.Math.toRadians(endDeg);
        for (int i = 0; i <= steps; i++) {
            double t = s + (e - s) * i / steps;
            l.add(cx + rx * (float) java.lang.Math.cos(t));
            l.add(cy + ry * (float) java.lang.Math.sin(t));
        }
    }

    // 中心三角扇填充（pts 为 x,y 交替的凸轮廓）
    private float @NotNull [] fan(@NotNull List<Float> pts, float cx, float cy) {
        List<Float> out = new ArrayList<>();
        int n = pts.size() / 2;
        for (int i = 0; i < n - 1; i++)
            addTri(out, cx, cy, pts.get(2 * i), pts.get(2 * i + 1), pts.get(2 * i + 2), pts.get(2 * i + 3));
        return toF(out);
    }

    // 环带（外轮廓 + 内轮廓，点数相同）
    private float @NotNull [] ring(@NotNull List<Float> o, List<Float> in) {
        List<Float> out = new ArrayList<>();
        int n = o.size() / 2;
        for (int i = 0; i < n; i++) {
            int j = (i + 1) % n;
            float ox = o.get(2 * i), oy = o.get(2 * i + 1), ox2 = o.get(2 * j), oy2 = o.get(2 * j + 1);
            float ix = in.get(2 * i), iy = in.get(2 * i + 1), ix2 = in.get(2 * j), iy2 = in.get(2 * j + 1);
            addTri(out, ox, oy, ix, iy, ox2, oy2);
            addTri(out, ox2, oy2, ix, iy, ix2, iy2);
        }
        return toF(out);
    }

    // 线段矩形
    private void addLine(List<Float> l, float x1, float y1, float x2, float y2, float width) {
        float hw = width / 2f;
        float dx = x2 - x1, dy = y2 - y1;
        float len = (float) java.lang.Math.sqrt(dx * dx + dy * dy);
        if (len == 0) return;
        float nx = -dy / len * hw, ny = dx / len * hw;
        addTri(l, x1 + nx, y1 + ny, x1 - nx, y1 - ny, x2 + nx, y2 + ny);
        addTri(l, x2 + nx, y2 + ny, x1 - nx, y1 - ny, x2 - nx, y2 - ny);
    }

    private void appendRoundRect(@NotNull List<Float> l, float x, float y, float w, float h, float rx, float ry, int seg) {
        l.add(x + rx);
        l.add(y);
        l.add(x + w - rx);
        l.add(y);
        appendArcXY(l, x + w - rx, y + ry, rx, ry, 270, 360, seg);
        l.add(x + w);
        l.add(y + h - ry);
        appendArcXY(l, x + w - rx, y + h - ry, rx, ry, 0, 90, seg);
        l.add(x + rx);
        l.add(y + h);
        appendArcXY(l, x + rx, y + h - ry, rx, ry, 90, 180, seg);
        l.add(x);
        l.add(y + ry);
        appendArcXY(l, x + rx, y + ry, rx, ry, 180, 270, seg);
    }

    @Override
    public void drawLine(float x1, float y1, float x2, float y2) {
        List<Float> l = new ArrayList<>();
        addLine(l, x1, y1, x2, y2, getStroke().width());
        drawTriangles(toF(l), getPaint());
    }

    @Override
    public void drawRect(float x, float y, float width, float height) {
        float hw = getStroke().width() / 2f;
        List<Float> o = new ArrayList<>(), in = new ArrayList<>();
        o.add(x);
        o.add(y);
        o.add(x + width);
        o.add(y);
        o.add(x + width);
        o.add(y + height);
        o.add(x);
        o.add(y + height);
        in.add(x + hw);
        in.add(y + hw);
        in.add(x + width - hw);
        in.add(y + hw);
        in.add(x + width - hw);
        in.add(y + height - hw);
        in.add(x + hw);
        in.add(y + height - hw);
        drawTriangles(ring(o, in), getPaint());
    }

    @Override
    public void fillOval(float x, float y, float width, float height) {
        float cx = x + width / 2f, cy = y + height / 2f;
        float rx = width / 2f, ry = height / 2f;
        int seg = segments(rx, ry);
        List<Float> pts = new ArrayList<>();
        appendArcXY(pts, cx, cy, rx, ry, 0, 360, seg);
        drawTriangles(fan(pts, cx, cy), getPaint());
    }

    @Override
    public void drawOval(float x, float y, float width, float height) {
        float cx = x + width / 2f, cy = y + height / 2f;
        float rx = width / 2f, ry = height / 2f;
        float hw = getStroke().width() / 2f;
        int seg = segments(rx, ry);
        List<Float> o = new ArrayList<>(), in = new ArrayList<>();
        appendArcXY(o, cx, cy, rx, ry, 0, 360, seg);
        appendArcXY(in, cx, cy, rx - hw, ry - hw, 0, 360, seg);
        drawTriangles(ring(o, in), getPaint());
    }

    @Override
    public void fillArc(float x, float y, float width, float height, float startAngle, float arcAngle) {
        float cx = x + width / 2f, cy = y + height / 2f;
        float rx = width / 2f, ry = height / 2f;
        int seg = segments(rx, ry);
        int steps = java.lang.Math.max(1, (int) (seg * java.lang.Math.abs(arcAngle) / 360f));
        List<Float> pts = new ArrayList<>();
        appendArcXY(pts, cx, cy, rx, ry, startAngle, startAngle + arcAngle, steps);
        drawTriangles(fan(pts, cx, cy), getPaint());
    }

    @Override
    public void drawArc(float x, float y, float width, float height, float startAngle, float arcAngle) {
        float cx = x + width / 2f, cy = y + height / 2f;
        float rx = width / 2f, ry = height / 2f;
        float hw = getStroke().width() / 2f;
        int seg = segments(rx, ry);
        int steps = java.lang.Math.max(1, (int) (seg * java.lang.Math.abs(arcAngle) / 360f));
        List<Float> o = new ArrayList<>(), in = new ArrayList<>();
        appendArcXY(o, cx, cy, rx, ry, startAngle, startAngle + arcAngle, steps);
        appendArcXY(in, cx, cy, rx - hw, ry - hw, startAngle, startAngle + arcAngle, steps);
        List<Float> out = new ArrayList<>();
        for (int i = 0; i < steps; i++) {
            addTri(out, o.get(2 * i), o.get(2 * i + 1), in.get(2 * i), in.get(2 * i + 1), o.get(2 * i + 2), o.get(2 * i + 3));
            addTri(out, o.get(2 * i + 2), o.get(2 * i + 3), in.get(2 * i), in.get(2 * i + 1), in.get(2 * i + 2), in.get(2 * i + 3));
        }
        drawTriangles(toF(out), getPaint());
    }

    @Override
    public void fillRoundRect(float x, float y, float width, float height, float arcWidth, float arcHeight) {
        float rx = java.lang.Math.min(arcWidth / 2f, width / 2f);
        float ry = java.lang.Math.min(arcHeight / 2f, height / 2f);
        int seg = java.lang.Math.max(4, segments(rx, ry) / 4);
        List<Float> pts = new ArrayList<>();
        appendRoundRect(pts, x, y, width, height, rx, ry, seg);
        drawTriangles(fan(pts, x + width / 2f, y + height / 2f), getPaint());
    }

    @Override
    public void drawRoundRect(float x, float y, float width, float height, float arcWidth, float arcHeight) {
        float rx = java.lang.Math.min(arcWidth / 2f, width / 2f);
        float ry = java.lang.Math.min(arcHeight / 2f, height / 2f);
        float hw = getStroke().width() / 2f;
        int seg = java.lang.Math.max(4, segments(rx, ry) / 4);
        List<Float> o = new ArrayList<>(), in = new ArrayList<>();
        appendRoundRect(o, x, y, width, height, rx, ry, seg);
        appendRoundRect(in, x + hw, y + hw, width - 2 * hw, height - 2 * hw,
                java.lang.Math.max(0, rx - hw), java.lang.Math.max(0, ry - hw), seg);
        drawTriangles(ring(o, in), getPaint());
    }

    @Override
    public void fillPolygon(float[] xPoints, float[] yPoints, int nPoints) {
        if (xPoints == null || yPoints == null) throw new NullPointerException("points is null!");
        if (nPoints < 3 || nPoints > xPoints.length || nPoints > yPoints.length)
            throw new IllegalArgumentException("invalid nPoints");
        List<Float> pts = new ArrayList<>();
        for (int i = 0; i < nPoints; i++) {
            pts.add(xPoints[i]);
            pts.add(yPoints[i]);
        }
        drawTriangles(fan(pts, xPoints[0], yPoints[0]), getPaint());
    }

    @Override
    public void drawPolygon(float[] xPoints, float[] yPoints, int nPoints) {
        if (xPoints == null || yPoints == null) throw new NullPointerException("points is null!");
        if (nPoints < 3 || nPoints > xPoints.length || nPoints > yPoints.length)
            throw new IllegalArgumentException("invalid nPoints");
        List<Float> l = new ArrayList<>();
        float w = getStroke().width();
        for (int i = 0; i < nPoints; i++) {
            int j = (i + 1) % nPoints;
            addLine(l, xPoints[i], yPoints[i], xPoints[j], yPoints[j], w);
        }
        drawTriangles(toF(l), getPaint());
    }


    @Override
    public void drawString(String str, float x, float y, float maxWidth) {
        if (str == null) throw new NullPointerException("str is null!");
        if (str.isEmpty()) return;
        if (maxWidth <= 0) {
            drawString(str, x, y);
            return;
        }

        FontMetrics fm = getFontMetrics();
        float lineHeight = fm.getHeight();
        float curY = y;
        StringBuilder line = new StringBuilder();

        for (int i = 0; i < str.length(); ) {
            int cp = str.codePointAt(i);
            i += Character.charCount(cp);

            if (cp == '\n') {
                if (!line.isEmpty()) {
                    drawString(line.toString(), x, curY);
                    line.setLength(0);
                }
                curY += lineHeight;
                continue;
            }

            String ch = new String(Character.toChars(cp));
            if (!line.isEmpty() && fm.stringWidth(line + ch) > maxWidth) {
                drawString(line.toString(), x, curY);
                line.setLength(0);
                curY += lineHeight;
            }
            line.append(ch);
        }
        if (!line.isEmpty()) drawString(line.toString(), x, curY);
    }


    @Override
    public void drawImage(Image image, int srcX, int srcY, int srcW, int srcH,
                          float dstX, float dstY, float dstW, float dstH) {
        if (image == null) throw new NullPointerException("image is null!");
        if (srcW <= 0 || srcH <= 0 || dstW <= 0 || dstH <= 0) return;

        int tid = checkIfTextureIsCreated(image);
        if (tid == 0) return;

        float iw = image.width(), ih = image.height();
        float u0 = srcX / iw, v0 = srcY / ih;
        float u1 = (srcX + srcW) / iw, v1 = (srcY + srcH) / ih;

        float x1 = dstX, y1 = dstY;
        float x2 = dstX + dstW, y2 = dstY;
        float x3 = dstX + dstW, y3 = dstY + dstH;
        float x4 = dstX, y4 = dstY + dstH;

        float[] vert = {
                x1, y1, u0, v0, x2, y2, u1, v0, x3, y3, u1, v1,
                x1, y1, u0, v0, x3, y3, u1, v1, x4, y4, u0, v1
        };

        Shader shader = shaders.get("image");
        shader.useProgram();
        GL33.glUniform1f(shader.getUniform("uOpacity"), getOpacity());
        applyBlendMode();
        GL33.glBindVertexArray(shader.vao());
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, shader.vbo());
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, vert, GL33.GL_STREAM_DRAW);
        GL33.glVertexAttribPointer(0, 2, GL33.GL_FLOAT, false, 4 * 4, 0);
        GL33.glEnableVertexAttribArray(0);
        GL33.glVertexAttribPointer(1, 2, GL33.GL_FLOAT, false, 4 * 4, 2 * 4);
        GL33.glEnableVertexAttribArray(1);
        GL33.glUniform1i(shader.getUniform("image"), 0);
        GL33.glActiveTexture(GL33.GL_TEXTURE0);
        GL33.glBindTexture(GL33.GL_TEXTURE_2D, tid);
        GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, 6);
    }


    private void ensureFontTextures(FontFace face) {
        int[] tex = fontTextures.computeIfAbsent(face, _ -> new int[face.pages.size()]);
        if (tex.length < face.pages.size()) {
            int[] n = new int[face.pages.size()];
            System.arraycopy(tex, 0, n, 0, tex.length);
            tex = n;
            fontTextures.put(face, n);
        }
        for (int p = 0; p < face.pages.size(); p++) {
            if (tex[p] != 0) continue;
            tex[p] = GL33.glGenTextures();
            GL33.glBindTexture(GL33.GL_TEXTURE_2D, tex[p]);
            GL33.glPixelStorei(GL33.GL_UNPACK_ALIGNMENT, 1);

            tex[p] = GL33.glGenTextures();
            GL33.glBindTexture(GL33.GL_TEXTURE_2D, tex[p]);
            GL33.glPixelStorei(GL33.GL_UNPACK_ALIGNMENT, 1);

            ByteBuffer data = face.pages.get(p);

            GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_R8, face.pageWidth(), face.pageHeight(),
                    0, GL33.GL_RED, GL33.GL_UNSIGNED_BYTE, data);

            GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_LINEAR);
            GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_LINEAR);
            GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_S, GL33.GL_CLAMP_TO_EDGE);
            GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_T, GL33.GL_CLAMP_TO_EDGE);
        }
        for (int p : face.drainDirty()) {
            if (p < tex.length && tex[p] != 0) {
                GL33.glBindTexture(GL33.GL_TEXTURE_2D, tex[p]);
                GL33.glTexSubImage2D(GL33.GL_TEXTURE_2D, 0, 0, 0, face.pageWidth(), face.pageHeight(),
                        GL33.GL_RED, GL33.GL_UNSIGNED_BYTE, face.pages.get(p));
            }
        }

    }


    // ========================= SET =========================

    @SuppressWarnings("unused")
    public void setCachedTexture(boolean cached) {
        if (isCachedTexture() == cached) return;
        synchronized (getLock()) {
            cachedTexture.set(cached);
        }
    }

    // ========================= GET =========================


    @Override
    public boolean isInitialize() {
        return initialize.get();
    }

    public boolean isCachedTexture() {
        return cachedTexture.get();
    }

    // ========================= OTM =========================

    @Override
    public void scissor(int x, int y, int width, int height) {
        var size = GLFWUtil.getFrameBufferSize();
        GL33.glEnable(GL33.GL_SCISSOR_TEST);
        GL33.glScissor(x, (size.y - y) - height, width, height);
    }

    @Override
    public void release() {
        GL33.glDisable(GL33.GL_SCISSOR_TEST);
    }

    @Override
    public void clip(int x, int y, int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("width or height < 0");
        }

        int cx = x, cy = y, cw = width, ch = height;

        // 跟栈顶裁剪区求交集
        int[] cur = clipStack.peek();
        if (cur != null) {
            int x1 = java.lang.Math.max(x, cur[0]);
            int y1 = java.lang.Math.max(y, cur[1]);
            int x2 = java.lang.Math.min(x + width, cur[0] + cur[2]);
            int y2 = java.lang.Math.min(y + height, cur[1] + cur[3]);
            cx = x1; cy = y1;
            cw = java.lang.Math.max(0, x2 - x1);
            ch = java.lang.Math.max(0, y2 - y1);
        }

        var size = GLFWUtil.getFrameBufferSize();

        GL33.glEnable(GL33.GL_SCISSOR_TEST);
        GL33.glScissor(cx, size.y - cy - ch, cw, ch);

        clipStack.push(new int[]{cx, cy, cw, ch});
    }

    @Override
    public void unclip() {
        clipStack.poll();
        int[] cur = clipStack.peek();
        if (cur == null) {
            GL33.glDisable(GL33.GL_SCISSOR_TEST);
        } else {
            var size = GLFWUtil.getFrameBufferSize();

            GL33.glScissor(cur[0], size.y - cur[1] - cur[3], cur[2], cur[3]);
        }
    }

    @Override
    public void translate(float x, float y) {
        translate(x, y, 0.0f);
    }

    @Override
    public void translate(float x, float y, float z) {
        synchronized (getLock()) {
            getMatrix4f().translate(x, y, z);
        }
        update();
    }

    @Override
    public void scale(float x, float y, float z) {
        synchronized (getLock()) {
            getMatrix4f().scale(x, y, z);
        }
        update();
    }

    @Override
    public void rotate(float angle, float x, float y, float z) {
        synchronized (getLock()) {
            getMatrix4f().rotate(angle, x, y, z);
        }
        update();
    }


    @Override
    public void scale(float x, float y) {
        scale(x, y, 0.0f);
    }

    @Override
    public void rotate(float angle, float x, float y) {
        synchronized (getLock()) {
            getMatrix4f().rotateZ(angle);
        }
        update();
    }


    /**
     * 清屏
     *
     * <p>
     * 使用时会检查{@link GL33#GL_DEPTH_TEST}、{@link GL33#GL_STENCIL_TEST},
     * 根据启用的参数进行判断最终值，
     * 然后决定时候在最终清理时决定{@code mask}会{@code |=}那些参数，
     * 清屏时的颜色由{@link #getColor()}控制。
     * </p>
     *
     * @see #getColor()
     * @see GL33#glClear(int)
     * @see GL33#glClearColor(float, float, float, float)
     * @since 0.0.1
     */
    @Override
    public void clear() {
        int mask = GL11.GL_COLOR_BUFFER_BIT;

        if (GL33.glIsEnabled(GL33.GL_DEPTH_TEST)) {
            mask |= GL33.GL_DEPTH_BUFFER_BIT;
        }

        if (GL33.glIsEnabled(GL33.GL_STENCIL_TEST)) {
            mask |= GL33.GL_STENCIL_BUFFER_BIT;
        }

        float[] color = getColor().getColor4f();

        GL11.glClearColor(color[0], color[1], color[2], color[3]);
        GL11.glClear(mask);
    }

    @Override
    public void update() {
        if (getAttribute() == null) {
            throw new NullPointerException("attribute is null!");
        }
        if (!isInit()) {
            return;
        }
        synchronized (getLock()) {
            float[] proj = getAttribute().getMatrix4f().get(Array.createF(16));
            shadersForEach((_, shader) -> GL33.glUniformMatrix4fv(
                    shader.getUniform("projection"),
                    false, proj
            ));
        }
    }

    @Override
    public void dispose() {
        if (!isInitialize()) return;
        synchronized (getLock()) {
            if (!isInit()) return;

            for (Shader shader : shaders.values()) {
                shader.dispose();
            }
            shaders.clear();

            textures.forEach((_, tid) -> GL33.glDeleteTextures(tid));
            textures.clear();

            for (int[] tex : fontTextures.values()) {
                for (int tid : tex) {
                    if (tid != 0) GL33.glDeleteTextures(tid);
                }
            }
            fontTextures.clear();

            initialize.set(false);
        }
    }


    public void shadersForEach(BiConsumer<String, Shader> action) {
        if (action == null) {
            throw new NullPointerException("action is null!");
        }
        shaders.forEach((string, shader) -> {
            shader.useProgram();
            action.accept(string, shader);
        });
    }

    private int checkIfTextureIsCreated(Image image) {
        int[] arr = Array.createI(1);

        check:
        if (isCachedTexture() && textures.containsKey(image)) {
            int id = textures.get(image);
            if (id == 0) {
                textures.remove(image);
                createTextureID(arr, image);
                break check;
            }
            if (!GL33.glIsTexture(id)) {
                textures.remove(image);
                createTextureID(arr, image);
                break check;
            }
            arr[0] = id;
        } else {
            try {
                createTextureID(arr, image);
            } catch (Exception e) {
                logger.error("An error occurred while checking the texture.", e);
                return 0;
            }
        }

        return arr[0];
    }

    private void createTextureID(int[] id, Image image) {
        try {
            Array.requireNotLessThan(id, 1);
            id[0] = createTextureID(image);
            textures.put(image, id[0]);
        } catch (Exception e) {
            logger.error("An error occurred while creating the texture ID.", e);
        }
    }

    @ApiSign.Dangerous(since = "0.0.3")
    static int createTextureID(Image image) {
        // 空检查
        if (image == null) {
            throw new NullPointerException("Image cannot be null!");
        }

        int width = image.getWidth();
        int height = image.getHeight();
        Format format = image.getFormat();
        ByteBuffer data = image.getData();

        // 尺寸合法性检查
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Image dimensions must be positive: " + width + "x" + height);
        }

        // 生成纹理 ID[reference:0][reference:1]
        int textureId = GL33.glGenTextures();

        // 绑定纹理[reference:3][reference:4]
        GL33.glBindTexture(GL33.GL_TEXTURE_2D, textureId);

        // -------- 设置纹理参数（过滤 & 环绕）--------
        // 缩小和放大都使用线性插值，使图像更平滑[reference:5][reference:6][reference:8]
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_LINEAR);
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_LINEAR);
        // 纹理坐标超出 [0,1] 范围时 clamp 到边缘[reference:9][reference:10]
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_S, GL33.GL_CLAMP_TO_EDGE);
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_T, GL33.GL_CLAMP_TO_EDGE);

        // 设置像素存储对齐方式为 1 字节[reference:11]
        GL33.glPixelStorei(GL33.GL_UNPACK_ALIGNMENT, 1);

        // -------- 根据 Format 映射 OpenGL 内部格式和像素格式 --------
        int internalFormat, pixelFormat;
        switch (format) {
            case RGB:
                internalFormat = GL33.GL_RGB;
                pixelFormat = GL33.GL_RGB;
                break;
            case RGBA:
                internalFormat = GL33.GL_RGBA;
                pixelFormat = GL33.GL_RGBA;
                break;
            case ARGB:
                // OpenGL 原生不支持 ARGB，需要转换为 RGBA
                internalFormat = GL33.GL_RGBA;
                pixelFormat = GL33.GL_RGBA;
                data = convertARGBtoRGBA(data, width, height);
                break;
            case GRAYSCALE:
                internalFormat = GL33.GL_RED;
                pixelFormat = GL33.GL_RED;
                break;
            case ALPHA:
                internalFormat = GL33.GL_ALPHA;
                pixelFormat = GL33.GL_ALPHA;
                break;
            default:
                GL33.glDeleteTextures(textureId);
                throw new IllegalArgumentException("Unsupported format: " + format);
        }

        // -------- 上传图像数据到 GPU --------
        // glTexImage2D(target, level, internalFormat, width, height, border, format, type, data)[reference:12][reference:14]
        GL33.glTexImage2D(
                GL33.GL_TEXTURE_2D,
                0,                    // mipmap level
                internalFormat,
                width,
                height,
                0,                    // border（必须为 0）[reference:15]
                pixelFormat,
                GL33.GL_UNSIGNED_BYTE,
                data
        );

        // 生成 Mipmap（多级渐远纹理），提升缩放时的渲染质量[reference:16]
        GL33.glGenerateMipmap(GL33.GL_TEXTURE_2D);

        // 解绑纹理（防止后续意外修改）[reference:17]
        GL33.glBindTexture(GL33.GL_TEXTURE_2D, 0);

        return textureId;
    }

    /**
     * 将 ARGB 格式的 ByteBuffer 转换为 RGBA 格式
     */
    private static @NotNull ByteBuffer convertARGBtoRGBA(@NotNull ByteBuffer src, int width, int height) {
        int pixelCount = width * height;
        ByteBuffer dst = ByteBuffer.allocateDirect(pixelCount * 4);
        dst.order(ByteOrder.nativeOrder());

        src.rewind();
        for (int i = 0; i < pixelCount; i++) {
            int a = src.get() & 0xFF;
            int r = src.get() & 0xFF;
            int g = src.get() & 0xFF;
            int b = src.get() & 0xFF;
            dst.put((byte) r);
            dst.put((byte) g);
            dst.put((byte) b);
            dst.put((byte) a);
        }
        dst.flip();
        return dst;
    }

    @Contract("_, _, _, _, _ -> new")
    private float @NotNull [] buildFillVertices(float x, float y, float width, float height, Paint paint) {
        float u1, v1, u2, v2, u3, v3, u4, v4;

        if (paint instanceof TexturePaint tp) {
            if (tp.hasCustomUV()) {
                float[] uv = tp.getUV();
                u1 = uv[0];
                v1 = uv[1];
                u2 = uv[2];
                v2 = uv[3];
                u3 = uv[4];
                v3 = uv[5];
                u4 = uv[6];
                v4 = uv[7];
            } else {
                u1 = 0f;
                v1 = 0f;
                u2 = 1f;
                v2 = 0f;
                u3 = 1f;
                v3 = 1f;
                u4 = 0f;
                v4 = 1f;
            }
        } else {
            u1 = 0f;
            v1 = 0f;
            u2 = 0f;
            v2 = 0f;
            u3 = 0f;
            v3 = 0f;
            u4 = 0f;
            v4 = 0f;
        }

        float x1 = x, y1 = y;
        float x2 = x + width, y2 = y;
        float x3 = x + width, y3 = y + height;
        float x4 = x, y4 = y + height;

        return new float[]{
                x1, y1, u1, v1,
                x2, y2, u2, v2,
                x3, y3, u3, v3,

                x1, y1, u1, v1,
                x3, y3, u3, v3,
                x4, y4, u4, v4
        };
    }

    @Contract("_, _, _, _, _, _, _ -> new")
    private float @NotNull [] buildTriangleVertices(float x1, float y1, float x2, float y2, float x3, float y3, Paint paint) {
        float u1, v1, u2, v2, u3, v3;

        if (paint instanceof TexturePaint tp) {
            if (tp.hasCustomUV()) {
                float[] uv = tp.getUV();
                if (uv.length < 6) {
                    throw new IllegalArgumentException("UV array for triangle must have at least 6 values");
                }
                u1 = uv[0];
                v1 = uv[1];
                u2 = uv[2];
                v2 = uv[3];
                u3 = uv[4];
                v3 = uv[5];
            } else {
                u1 = 0f;
                v1 = 0f;
                u2 = 1f;
                v2 = 0f;
                u3 = 0f;
                v3 = 1f;
            }
        } else {
            u1 = 0f;
            v1 = 0f;
            u2 = 0f;
            v2 = 0f;
            u3 = 0f;
            v3 = 0f;
        }

        return new float[]{
                x1, y1, u1, v1,
                x2, y2, u2, v2,
                x3, y3, u3, v3
        };
    }

    private void applyBlendMode() {
        GL33.glEnable(GL33.GL_BLEND);
        switch (getBlendMode()) {
            case NORMAL -> GL33.glBlendFunc(GL33.GL_SRC_ALPHA, GL33.GL_ONE_MINUS_SRC_ALPHA);
            case ADD -> GL33.glBlendFunc(GL33.GL_ONE, GL33.GL_ONE);
            case MULTIPLY -> GL33.glBlendFunc(GL33.GL_DST_COLOR, GL33.GL_ZERO);
            case SCREEN -> GL33.glBlendFunc(GL33.GL_ONE, GL33.GL_ONE_MINUS_SRC_COLOR);
        }
    }

    private void setupPaintUniforms(@NotNull Shader shader, Paint paint) {
        // 统一整体透明度
        GL33.glUniform1f(shader.getUniform("uOpacity"), getOpacity());

        if (paint instanceof TexturePaint tp) {
            GL33.glUniform1i(shader.getUniform("uHasTexture"), 1);
            GL33.glUniform1i(shader.getUniform("uTexture"), 0);
            GL33.glActiveTexture(GL33.GL_TEXTURE0);
            GL33.glBindTexture(GL33.GL_TEXTURE_2D, checkIfTextureIsCreated(tp.getImage()));
            return;
        }

        // 非纹理：关闭纹理采样
        GL33.glUniform1i(shader.getUniform("uHasTexture"), 0);

        if (paint instanceof Color c) {
            GL33.glUniform1i(shader.getUniform("uMode"), 0);
            GL33.glUniform4fv(shader.getUniform("uColor"), c.getColor4f());
            return;
        }

        if (paint instanceof GradientPaint gp) {
            GL33.glUniform1i(shader.getUniform("uMode"), 1);
            GL33.glUniform2f(shader.getUniform("uStart"), gp.getX1(), gp.getY1());
            GL33.glUniform2f(shader.getUniform("uEnd"), gp.getX2(), gp.getY2());
            GL33.glUniform4fv(shader.getUniform("uColors[0]"), gp.getColor1().getColor4f());
            GL33.glUniform4fv(shader.getUniform("uColors[1]"), gp.getColor2().getColor4f());
            GL33.glUniform1i(shader.getUniform("uColorCount"), 2);
            GL33.glUniform1i(shader.getUniform("uCycleMethod"), gp.getCycleMethod().ordinal());
            return;
        }

        if (paint instanceof LinearGradientPaint lgp) {
            GL33.glUniform1i(shader.getUniform("uMode"), 2);
            GL33.glUniform2f(shader.getUniform("uStart"), lgp.startX(), lgp.startY());
            GL33.glUniform2f(shader.getUniform("uEnd"), lgp.endX(), lgp.endY());
            uploadColorsAndFractions(shader, lgp.colors(), lgp.fractions());
            GL33.glUniform1i(shader.getUniform("uCycleMethod"), lgp.cycleMethod().ordinal());
            return;
        }

        if (paint instanceof RadialGradientPaint rgp) {
            GL33.glUniform1i(shader.getUniform("uMode"), 3);
            GL33.glUniform2f(shader.getUniform("uCenter"), rgp.getCx(), rgp.getCy());
            GL33.glUniform1f(shader.getUniform("uRadius"), rgp.getRadius());
            GL33.glUniform2f(shader.getUniform("uFocus"), rgp.getFx(), rgp.getFy());
            uploadColorsAndFractions(shader, rgp.getColors(), rgp.getFractions());
            GL33.glUniform1i(shader.getUniform("uCycleMethod"), rgp.getCycleMethod().ordinal());
        }
    }

    private void uploadColorsAndFractions(@NotNull Shader shader, Color @NotNull [] colors, float[] fractions) {
        int count = colors.length;
        GL33.glUniform1i(shader.getUniform("uColorCount"), count);
        for (int i = 0; i < count; i++) {
            GL33.glUniform4fv(shader.getUniform("uColors[" + i + "]"), colors[i].getColor4f());
            GL33.glUniform1f(shader.getUniform("uFractions[" + i + "]"), fractions[i]);
        }
    }

    private int segments(float rx, float ry) {
        float r = java.lang.Math.max(java.lang.Math.abs(rx), java.lang.Math.abs(ry));
        return (int) java.lang.Math.clamp(r * 2f, 16f, 128f);
    }

    public static void setGlClearColor(Color clearColor) {
        if (clearColor == null) {
            throw new NullPointerException("clearColor is null!");
        }
        float r = clearColor.red();
        float g = clearColor.green();
        float b = clearColor.blue();
        float a = clearColor.alpha();
        GL33.glClearColor(r,g,b,a);
    }

}
