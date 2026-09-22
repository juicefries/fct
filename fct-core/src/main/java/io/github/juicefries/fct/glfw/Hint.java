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

package io.github.juicefries.fct.glfw;

import io.github.juicefries.fct.Sys;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

/**
 * 提示类
 * <p>
 *     简单的提示封装类
 * </p>
 * @param hint 提示类型
 * @param value 提示的值
 * @see GLFW#glfwWindowHint(int, int)
 * @since 0
 * @version 1.0
 */
public record Hint(int hint, int value) implements Cloneable {

    /**
     * 复制提示的值
     * @param hint 提示
     * @throws NullPointerException 提示类不能为{@code null}
     */
    public Hint(final Hint hint) {
        if (hint == null) {
            throw new NullPointerException("hint is null!");
        }
        this(hint.hint, hint.value);
    }

    /**
     * 应用提示
     * @see GLFW#glfwWindowHint(int, int)
     */
    public void apply() {
        Sys.checkInit(true);
        GLFW.glfwWindowHint(hint,value);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Hint(int hint2, int value1))) {
            return false;
        }
        return (hint == hint2) && (value == value1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hint, value);
    }

    @Override
    public @NotNull String toString() {
        return "Hint{" + "hint=" + hint + ", value=" + value + '}';
    }

    public @NotNull @Unmodifiable Object clone() {
        try {
            Hint hc = (Hint) super.clone();
            return create(hc);
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Contract(" -> new")
    public @NotNull Hint cloned() {
        return create(hint,value);
    }

    // reset

    public static void reset() {
        GLFW.glfwDefaultWindowHints();
    }

    // create

    @Contract("_, _ -> new")
    public static @NotNull Hint create(int hint, int value) {
        return new Hint(hint, value);
    }

    @Contract("_ -> new")
    public static @NotNull Hint create(Hint hint) {
        return new Hint(hint);
    }

    // 窗口相关
    public static @NotNull Hint resizable(boolean enabled) {
        return create(GLFW.GLFW_RESIZABLE, enabled ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
    }

    public static @NotNull Hint visible(boolean enabled) {
        return create(GLFW.GLFW_VISIBLE, enabled ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
    }

    public static @NotNull Hint decorated(boolean enabled) {
        return create(GLFW.GLFW_DECORATED, enabled ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
    }

    public static @NotNull Hint focused(boolean enabled) {
        return create(GLFW.GLFW_FOCUSED, enabled ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
    }

    public static @NotNull Hint autoIconify(boolean enabled) {
        return create(GLFW.GLFW_AUTO_ICONIFY, enabled ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
    }

    public static @NotNull Hint floating(boolean enabled) {
        return create(GLFW.GLFW_FLOATING, enabled ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
    }

    public static @NotNull Hint maximized(boolean enabled) {
        return create(GLFW.GLFW_MAXIMIZED, enabled ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
    }

    public static @NotNull Hint centerCursor(boolean enabled) {
        return create(GLFW.GLFW_CENTER_CURSOR, enabled ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
    }

    public static @NotNull Hint transparentFramebuffer(boolean enabled) {
        return create(GLFW.GLFW_TRANSPARENT_FRAMEBUFFER, enabled ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
    }

    public static @NotNull Hint focusOnShow(boolean enabled) {
        return create(GLFW.GLFW_FOCUS_ON_SHOW, enabled ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
    }

    public static @NotNull Hint scaleToMonitor(boolean enabled) {
        return create(GLFW.GLFW_SCALE_TO_MONITOR, enabled ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
    }

    // 帧缓冲位深
    public static @NotNull Hint redBits(int bits) {
        return create(GLFW.GLFW_RED_BITS, bits);
    }

    public static @NotNull Hint greenBits(int bits) {
        return create(GLFW.GLFW_GREEN_BITS, bits);
    }

    public static @NotNull Hint blueBits(int bits) {
        return create(GLFW.GLFW_BLUE_BITS, bits);
    }

    public static @NotNull Hint alphaBits(int bits) {
        return create(GLFW.GLFW_ALPHA_BITS, bits);
    }

    public static @NotNull Hint depthBits(int bits) {
        return create(GLFW.GLFW_DEPTH_BITS, bits);
    }

    public static @NotNull Hint stencilBits(int bits) {
        return create(GLFW.GLFW_STENCIL_BITS, bits);
    }

    public static @NotNull Hint accumRedBits(int bits) {
        return create(GLFW.GLFW_ACCUM_RED_BITS, bits);
    }

    public static @NotNull Hint accumGreenBits(int bits) {
        return create(GLFW.GLFW_ACCUM_GREEN_BITS, bits);
    }

    public static @NotNull Hint accumBlueBits(int bits) {
        return create(GLFW.GLFW_ACCUM_BLUE_BITS, bits);
    }

    public static @NotNull Hint accumAlphaBits(int bits) {
        return create(GLFW.GLFW_ACCUM_ALPHA_BITS, bits);
    }

    // 缓冲相关
    public static @NotNull Hint auxBuffers(int num) {
        return create(GLFW.GLFW_AUX_BUFFERS, num);
    }

    public static @NotNull Hint samples(int num) {
        return create(GLFW.GLFW_SAMPLES, num);
    }

    public static @NotNull Hint refreshRate(int rate) {
        return create(GLFW.GLFW_REFRESH_RATE, rate);
    }

    public static @NotNull Hint doubleBuffer(boolean enabled) {
        return create(GLFW.GLFW_DOUBLEBUFFER, enabled ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
    }

    public static @NotNull Hint stereo(boolean enabled) {
        return create(GLFW.GLFW_STEREO, enabled ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
    }

    // OpenGL 上下文
    public static @NotNull Hint clientApi(int api) {
        return create(GLFW.GLFW_CLIENT_API, api);
    }

    public static @NotNull Hint contextVersionMajor(int major) {
        return create(GLFW.GLFW_CONTEXT_VERSION_MAJOR, major);
    }

    public static @NotNull Hint contextVersionMinor(int minor) {
        return create(GLFW.GLFW_CONTEXT_VERSION_MINOR, minor);
    }

    public static @NotNull Hint contextRobustness(int robustness) {
        return create(GLFW.GLFW_CONTEXT_ROBUSTNESS, robustness);
    }

    public static @NotNull Hint contextReleaseBehavior(int behavior) {
        return create(GLFW.GLFW_CONTEXT_RELEASE_BEHAVIOR, behavior);
    }

    public static @NotNull Hint openglForwardCompat(boolean enabled) {
        return create(GLFW.GLFW_OPENGL_FORWARD_COMPAT, enabled ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
    }

    public static @NotNull Hint openglDebugContext(boolean enabled) {
        return create(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, enabled ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
    }

    public static @NotNull Hint openglProfile(int profile) {
        return create(GLFW.GLFW_OPENGL_PROFILE, profile);
    }

}