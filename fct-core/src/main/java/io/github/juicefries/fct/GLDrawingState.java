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
// Data 2026/10/28 00:05
//

package io.github.juicefries.fct;

import io.github.juicefries.fct.util.Lock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NonNls;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * GL 上的绘制状态。
 *
 * <p>两处存储：
 * <ul>
 *   <li>{@code buffer}：本轮要改的项（set 过、还没应用）</li>
 *   <li>{@code stack}：状态栈，{@code save()} 时从驱动读真实值压栈，{@code restore()} 时作为
 *       目标值逐项比对后写回</li>
 * </ul>
 *
 * <p>关键约定：**任何写入之前都必须先问驱动现在是什么**。记录里的值只是「我想改成什么」，
 * 不代表真实状态——外部代码完全可以绕过这里直接调 GL。
 *
 * @since 1.0.5
 * @author ds(AI) 文档
 */
public class GLDrawingState extends DrawingState {

    private static final Logger log = LogManager.getLogger(GLDrawingState.class);

    /** 浮点比对容差：驱动读回来的值有精度误差。 */
    private static final float EPSILON = 1e-5f;

    // ========================= 生命周期 =========================

    private final Lock stateLock = Lock.create();
    private final AtomicBoolean bound = new AtomicBoolean(false);
    long glContext;

    // ========================= 两个存储 =========================

    /** 缓冲区：本轮要改的项，key → 目标值（Integer / Float / Boolean / int[] / float[] / boolean[]）。 */
    private final Map<String, Object> buffer = new LinkedHashMap<>();
    /** 状态栈：每一层是「从驱动读回来的真实值」。 */
    private final Deque<Map<String, Object>> stack = new ArrayDeque<>();

    /** 状态项：怎么读、怎么写、怎么比、什么时候写。 */
    private record Item(Supplier<Object> reader, Consumer<Object> writer,
                        BiPredicate<Object, Object> same, int order) {
    }

    private static final Map<String, Item> ITEMS = new LinkedHashMap<>();

    static {
        registerAll();
    }

    public GLDrawingState() {
    }

    // ========================= 注册表 =========================

    private static void registerAll() {
        // ---- 开关（glIsEnabled / glEnable / glDisable）----
        flag(DEPTH_TEST, GL11.GL_DEPTH_TEST);
        flag(BLEND, GL11.GL_BLEND);
        flag(CULL_FACE, GL11.GL_CULL_FACE);
        flag(SCISSOR_TEST, GL11.GL_SCISSOR_TEST);
        flag(STENCIL_TEST, GL11.GL_STENCIL_TEST);
        flag(DITHER, GL11.GL_DITHER);
        flag(POLYGON_OFFSET_FILL, GL11.GL_POLYGON_OFFSET_FILL);
        flag(COLOR_LOGIC_OP, GL11.GL_COLOR_LOGIC_OP);
        flag(MULTISAMPLE, GL13.GL_MULTISAMPLE);
        flag(SAMPLE_ALPHA_TO_COVERAGE, GL13.GL_SAMPLE_ALPHA_TO_COVERAGE);
        flag(SAMPLE_ALPHA_TO_ONE, GL13.GL_SAMPLE_ALPHA_TO_ONE);
        flag(SAMPLE_COVERAGE, GL13.GL_SAMPLE_COVERAGE);
        flag(RASTERIZER_DISCARD, GL30.GL_RASTERIZER_DISCARD);
        flag(PROGRAM_POINT_SIZE, GL32.GL_PROGRAM_POINT_SIZE);

        // ---- 枚举（单个 int）----
        intItem(CULL_FACE_MODE, GL11.GL_CULL_FACE_MODE, GL11::glCullFace);
        intItem(FRONT_FACE, GL11.GL_FRONT_FACE, GL11::glFrontFace);
        intItem(DEPTH_FUNC, GL11.GL_DEPTH_FUNC, GL11::glDepthFunc);
        intItem(LOGIC_OP_MODE, GL11.GL_LOGIC_OP_MODE, GL11::glLogicOp);
        intItem(STENCIL_VALUE_MASK, GL11.GL_STENCIL_VALUE_MASK, GL11::glStencilMask);

        // ---- 深度写入（boolean）----
        ITEMS.put(DEPTH_MASK, new Item(
                () -> GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK),
                value -> GL11.glDepthMask((Boolean) value),
                Objects::equals, 20));

        // ---- 组合项：一次调用设多个值 ----
        ITEMS.put(BLEND_FUNC, new Item(
                () -> new int[]{
                        readInt(GL14.GL_BLEND_SRC_RGB), readInt(GL14.GL_BLEND_DST_RGB),
                        readInt(GL14.GL_BLEND_SRC_ALPHA), readInt(GL14.GL_BLEND_DST_ALPHA)},
                value -> {
                    int[] v = (int[]) value;
                    GL14.glBlendFuncSeparate(v[0], v[1], v[2], v[3]);
                },
                (a, b) -> Arrays.equals((int[]) a, (int[]) b), 30));
        ITEMS.put(BLEND_EQUATION, new Item(
                () -> new int[]{readInt(GL20.GL_BLEND_EQUATION_RGB), readInt(GL20.GL_BLEND_EQUATION_ALPHA)},
                value -> {
                    int[] v = (int[]) value;
                    GL20.glBlendEquationSeparate(v[0], v[1]);
                },
                (a, b) -> Arrays.equals((int[]) a, (int[]) b), 30));
        ITEMS.put(STENCIL_FUNC, new Item(
                () -> new int[]{
                        readInt(GL11.GL_STENCIL_FUNC), readInt(GL11.GL_STENCIL_REF),
                        readInt(GL11.GL_STENCIL_VALUE_MASK)},
                value -> {
                    int[] v = (int[]) value;
                    GL11.glStencilFunc(v[0], v[1], v[2]);
                },
                (a, b) -> Arrays.equals((int[]) a, (int[]) b), 30));
        ITEMS.put(STENCIL_OP, new Item(
                () -> new int[]{
                        readInt(GL11.GL_STENCIL_FAIL), readInt(GL11.GL_STENCIL_PASS_DEPTH_FAIL),
                        readInt(GL11.GL_STENCIL_PASS_DEPTH_PASS)},
                value -> {
                    int[] v = (int[]) value;
                    GL11.glStencilOp(v[0], v[1], v[2]);
                },
                (a, b) -> Arrays.equals((int[]) a, (int[]) b), 30));
        ITEMS.put(POLYGON_OFFSET, new Item(
                () -> new float[]{
                        readFloat(GL11.GL_POLYGON_OFFSET_FACTOR), readFloat(GL11.GL_POLYGON_OFFSET_UNITS)},
                value -> {
                    float[] v = (float[]) value;
                    GL11.glPolygonOffset(v[0], v[1]);
                },
                (a, b) -> sameFloats((float[]) a, (float[]) b), 30));
        ITEMS.put(SAMPLE_COVERAGE_PARAMS, new Item(
                () -> new float[]{
                        readFloat(GL13.GL_SAMPLE_COVERAGE_VALUE),
                        GL11.glGetBoolean(GL13.GL_SAMPLE_COVERAGE_INVERT) ? 1f : 0f},
                value -> {
                    float[] v = (float[]) value;
                    GL13.glSampleCoverage(v[0], v[1] != 0f);
                },
                (a, b) -> sameFloats((float[]) a, (float[]) b), 30));

        // ---- 浮点标量 ----
        floatItem(LINE_WIDTH, GL11.GL_LINE_WIDTH, GL11::glLineWidth);
        floatItem(POINT_SIZE, GL11.GL_POINT_SIZE, GL11::glPointSize);

        // ---- 清屏与常量 ----
        ITEMS.put(CLEAR_COLOR, new Item(
                () -> readFloats(GL11.GL_COLOR_CLEAR_VALUE, 4),
                value -> {
                    float[] v = (float[]) value;
                    GL11.glClearColor(v[0], v[1], v[2], v[3]);
                },
                (a, b) -> sameFloats((float[]) a, (float[]) b), 40));
        ITEMS.put(BLEND_COLOR, new Item(
                () -> readFloats(GL14.GL_BLEND_COLOR, 4),
                value -> {
                    float[] v = (float[]) value;
                    GL14.glBlendColor(v[0], v[1], v[2], v[3]);
                },
                (a, b) -> sameFloats((float[]) a, (float[]) b), 40));
        floatItem(CLEAR_DEPTH, GL11.GL_DEPTH_CLEAR_VALUE, GL11::glClearDepth);
        ITEMS.put(DEPTH_RANGE, new Item(
                () -> readFloats(GL11.GL_DEPTH_RANGE, 2),
                value -> {
                    float[] v = (float[]) value;
                    GL11.glDepthRange(v[0], v[1]);
                },
                (a, b) -> sameFloats((float[]) a, (float[]) b), 40));

        // ---- 颜色写入掩码（4 个 boolean）----
        ITEMS.put(COLOR_MASK, new Item(
                () -> readBooleans(GL11.GL_COLOR_WRITEMASK, 4),
                value -> {
                    boolean[] v = (boolean[]) value;
                    GL11.glColorMask(v[0], v[1], v[2], v[3]);
                },
                (a, b) -> Arrays.equals((boolean[]) a, (boolean[]) b), 20));

        // ---- 区域 ----
        ITEMS.put(VIEWPORT, new Item(
                () -> readInts(GL11.GL_VIEWPORT, 4),
                value -> {
                    int[] v = (int[]) value;
                    GL11.glViewport(v[0], v[1], v[2], v[3]);
                },
                (a, b) -> Arrays.equals((int[]) a, (int[]) b), 0));
        ITEMS.put(SCISSOR_BOX, new Item(
                () -> readInts(GL11.GL_SCISSOR_BOX, 4),
                value -> {
                    int[] v = (int[]) value;
                    GL11.glScissor(v[0], v[1], v[2], v[3]);
                },
                (a, b) -> Arrays.equals((int[]) a, (int[]) b), 10));

        // ---- 纹理单元必须在绑定之前恢复 ----
        intItem(ACTIVE_TEXTURE, GL13.GL_ACTIVE_TEXTURE, GL13::glActiveTexture);

        // ---- 绑定 ----
        intItem(PROGRAM, GL20.GL_CURRENT_PROGRAM, GL20::glUseProgram);
        intItem(VERTEX_ARRAY, GL30.GL_VERTEX_ARRAY_BINDING, GL30::glBindVertexArray);
        intItem(ARRAY_BUFFER, GL15.GL_ARRAY_BUFFER_BINDING, (v) -> GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, v));
        intItem(TEXTURE_2D, GL11.GL_TEXTURE_BINDING_2D, (v) -> GL11.glBindTexture(GL11.GL_TEXTURE_2D, v));
        intItem(TEXTURE_2D_ARRAY, GL30.GL_TEXTURE_BINDING_2D_ARRAY,
                (v) -> GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, v));
        intItem(FRAMEBUFFER, GL30.GL_FRAMEBUFFER_BINDING, (v) -> GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, v));
    }

    // ========================= 注册工具 =========================

    private static void flag(String key, int cap) {
        ITEMS.put(key, new Item(
                () -> GL11.glIsEnabled(cap),
                value -> {
                    if ((Boolean) value) {
                        GL11.glEnable(cap);
                    } else {
                        GL11.glDisable(cap);
                    }
                },
                Objects::equals, 20));
    }

    private static void intItem(String key, int pname, java.util.function.IntConsumer writer) {
        ITEMS.put(key, new Item(
                () -> readInt(pname),
                value -> writer.accept((Integer) value),
                Objects::equals, 20));
    }

    private static void floatItem(String key, int pname, Consumer<Float> writer) {
        ITEMS.put(key, new Item(
                () -> readFloat(pname),
                value -> writer.accept((Float) value),
                (a, b) -> Math.abs((Float) a - (Float) b) < EPSILON, 20));
    }

    // ========================= 取值工具 =========================

    private static int readInt(int pname) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer buffer = stack.mallocInt(1);
            GL11.glGetIntegerv(pname, buffer);
            return buffer.get(0);
        }
    }

    private static float readFloat(int pname) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(1);
            GL11.glGetFloatv(pname, buffer);
            return buffer.get(0);
        }
    }

    private static int[] readInts(int pname, int length) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer buffer = stack.mallocInt(length);
            GL11.glGetIntegerv(pname, buffer);
            int[] out = new int[length];
            buffer.get(out);
            return out;
        }
    }

    private static float[] readFloats(int pname, int length) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(length);
            GL11.glGetFloatv(pname, buffer);
            float[] out = new float[length];
            buffer.get(out);
            return out;
        }
    }

    private static boolean[] readBooleans(int pname, int length) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer buffer = stack.malloc(length);
            GL11.glGetBooleanv(pname, buffer);
            boolean[] out = new boolean[length];
            for (int i = 0; i < length; i++) {
                out[i] = buffer.get(i) != 0;
            }
            return out;
        }
    }

    private static boolean sameFloats(float[] a, float[] b) {
        if (a.length != b.length) {
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            if (Math.abs(a[i] - b[i]) > EPSILON) {
                return false;
            }
        }
        return true;
    }

    // ========================= TTT：设置 =========================

    @Override
    public void setInt(String key, int value) {
        put(key, value);
    }

    @Override
    public void setInts(String key, int... values) {
        put(key, values.clone());
    }

    @Override
    public void setFloat(String key, float value) {
        put(key, value);
    }

    @Override
    public void setFloats(String key, float... values) {
        put(key, values.clone());
    }

    @Override
    public void setFlag(String key, boolean value) {
        put(key, value);
    }

    @Override
    public void setFlags(String key, boolean... values) {
        put(key, values.clone());
    }

    private void put(String key, Object value) {
        stateCheck();
        if (key == null || value == null) {
            throw new NullPointerException("key or value is null!");
        }
        if (!ITEMS.containsKey(key)) {
            throw new IllegalArgumentException("Unknown state: " + key);
        }
        buffer.put(key, value);
    }

    // ========================= TTT：查询（真实值） =========================

    @Override
    public int getInt(String key) {
        return (Integer) read(key);
    }

    @Override
    public int[] getInts(String key) {
        return ((int[]) read(key)).clone();
    }

    @Override
    public float getFloat(String key) {
        return (Float) read(key);
    }

    @Override
    public float[] getFloats(String key) {
        return ((float[]) read(key)).clone();
    }

    @Override
    public boolean getFlag(String key) {
        return (Boolean) read(key);
    }

    @Override
    public boolean[] getFlags(String key) {
        return ((boolean[]) read(key)).clone();
    }

    /** 永远从驱动读，不读缓存。 */
    private Object read(String key) {
        stateCheck();
        Item item = ITEMS.get(key);
        if (item == null) {
            throw new IllegalArgumentException("Unknown state: " + key);
        }
        return item.reader().get();
    }

    // ========================= TTT：生效 =========================

    /**
     * 把 {@code buffer} 里的目标值写进驱动。
     *
     * <p>每一项都先读真实值再比对，相同就跳过——这样即使外部直接调过 GL，也能正确纠正。
     */
    @Override
    public void update() throws IllegalDrawingStateException {
        stateCheck();
        if (buffer.isEmpty()) {
            return;
        }
        List<String> keys = new ArrayList<>(buffer.keySet());
        keys.sort(Comparator.comparingInt(key -> ITEMS.get(key).order()));
        for (String key : keys) {
            Item item = ITEMS.get(key);
            Object target = buffer.get(key);
            Object real = item.reader().get();
            if (!item.same().test(real, target)) {
                item.writer().accept(target);
            }
        }
        buffer.clear();
    }

    // ========================= 状态栈 =========================

    /** 从驱动读全部状态，压栈。 */
    @Override
    public void save() throws IllegalDrawingStateException {
        stateCheck();
        Map<String, Object> snapshot = new LinkedHashMap<>();
        for (Map.Entry<String, Item> entry : ITEMS.entrySet()) {
            snapshot.put(entry.getKey(), entry.getValue().reader().get());
        }
        stack.push(snapshot);
    }

    /**
     * 弹栈并恢复。
     *
     * <p>栈里的值是「当时驱动里的真实值」，但恢复时仍然逐项比对当前真实值，不同才写。
     */
    @Override
    public void restore() throws IllegalDrawingStateException {
        stateCheck();
        Map<String, Object> snapshot = stack.poll();
        if (snapshot == null) {
            throw new IllegalDrawingStateException("State stack is empty!");
        }
        List<String> keys = new ArrayList<>(snapshot.keySet());
        keys.sort(Comparator.comparingInt(key -> ITEMS.get(key).order()));
        for (String key : keys) {
            Item item = ITEMS.get(key);
            Object target = snapshot.get(key);
            Object real = item.reader().get();
            if (!item.same().test(real, target)) {
                item.writer().accept(target);
            }
        }
    }

    @Override
    public int getStackSize() {
        return stack.size();
    }

    // ========================= OTM =========================

    @Override
    public @NonNls DrawingState bind(Object val) throws IllegalDrawingStateException {
        if (val == null) {
            throw new NullPointerException("val is null!");
        }
        synchronized (stateLock) {
            if (bound.get()) {
                throw new IllegalDrawingStateException("Duplicate binding!");
            }
        }
        if (val instanceof Window window) {
            return bindContext(window.window);
        }
        if (val instanceof Long context) {
            return bindContext(context);
        }
        throw new IllegalDrawingStateException("Incorrect parameter, binding failed!");
    }

    private @NonNls DrawingState bindContext(long context) {
        if (context == MemoryUtil.NULL) {
            throw new IllegalArgumentException("Invalid context!");
        }
        DrawingState previous;

        synchronized (stateLock) {
            long currentContext = GLFW.glfwGetCurrentContext();
            if (currentContext == MemoryUtil.NULL || context != currentContext) {
                throw new IllegalDrawingStateException("Bound to the wrong thread!");
            }

            this.glContext = context;
            previous = fct_drawing_state_thread_local.get();
            fct_drawing_state_thread_local.set(this);
            if (getName() == null) {
                setName("GLDrawingState[%d]".formatted(glContext));
            }
            bound.set(true);
        }

        log.trace("Binding GL context [{}] completed.", glContext);
        return previous;
    }

    @Override
    public void setName(String name) {
        if (name == null) {
            throw new NullPointerException("name is null!");
        }
        synchronized (stateLock) {
            super.setName(name);
        }
    }

    @Override
    public void stateCheck() {
        synchronized (stateLock) {
            long currentContext = GLFW.glfwGetCurrentContext();
            if (currentContext == MemoryUtil.NULL || glContext != currentContext) {
                throw new IllegalArgumentException("Incorrect drawing context state!");
            }
        }
    }

    public int getSamples() {
        stateCheck();
        return readInt(GL13.GL_SAMPLES);
    }

    @Override
    public @NonNls String getName() {
        synchronized (stateLock) {
            return super.getName();
        }
    }

    /** 当前线程上绑定的绘制状态；没有就返回 {@code null}。 */
    public static DrawingState current() {
        return fct_drawing_state_thread_local.get();
    }
}
