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

package io.github.juicefries.fct.input;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharCallbackI;
import org.lwjgl.glfw.GLFWCharModsCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;

import java.util.Objects;

public final class KeyboardData implements InputData,Cloneable {

    // 功能键
    public final static int KEY_ESC            = GLFW.GLFW_KEY_ESCAPE;
    public final static int KEY_ENTER          = GLFW.GLFW_KEY_ENTER;
    public final static int KEY_TAB            = GLFW.GLFW_KEY_TAB;
    public final static int KEY_BACKSPACE      = GLFW.GLFW_KEY_BACKSPACE;
    public final static int KEY_INSERT         = GLFW.GLFW_KEY_INSERT;
    public final static int KEY_DELETE         = GLFW.GLFW_KEY_DELETE;
    public final static int KEY_RIGHT          = GLFW.GLFW_KEY_RIGHT;
    public final static int KEY_LEFT           = GLFW.GLFW_KEY_LEFT;
    public final static int KEY_DOWN           = GLFW.GLFW_KEY_DOWN;
    public final static int KEY_UP             = GLFW.GLFW_KEY_UP;
    public final static int KEY_PAGE_UP        = GLFW.GLFW_KEY_PAGE_UP;
    public final static int KEY_PAGE_DOWN      = GLFW.GLFW_KEY_PAGE_DOWN;
    public final static int KEY_HOME           = GLFW.GLFW_KEY_HOME;
    public final static int KEY_END            = GLFW.GLFW_KEY_END;
    public final static int KEY_CAPS_LOCK      = GLFW.GLFW_KEY_CAPS_LOCK;
    public final static int KEY_SCROLL_LOCK    = GLFW.GLFW_KEY_SCROLL_LOCK;
    public final static int KEY_NUM_LOCK       = GLFW.GLFW_KEY_NUM_LOCK;
    public final static int KEY_PRINT_SCREEN   = GLFW.GLFW_KEY_PRINT_SCREEN;
    public final static int KEY_PAUSE          = GLFW.GLFW_KEY_PAUSE; // 修饰键
    public final static int KEY_LEFT_SHIFT     = GLFW.GLFW_KEY_LEFT_SHIFT;
    public final static int KEY_LEFT_CONTROL   = GLFW.GLFW_KEY_LEFT_CONTROL;
    public final static int KEY_LEFT_ALT       = GLFW.GLFW_KEY_LEFT_ALT;
    public final static int KEY_LEFT_SUPER     = GLFW.GLFW_KEY_LEFT_SUPER;
    public final static int KEY_RIGHT_SHIFT    = GLFW.GLFW_KEY_RIGHT_SHIFT;
    public final static int KEY_RIGHT_CONTROL  = GLFW.GLFW_KEY_RIGHT_CONTROL;
    public final static int KEY_RIGHT_ALT      = GLFW.GLFW_KEY_RIGHT_ALT;
    public final static int KEY_RIGHT_SUPER    = GLFW.GLFW_KEY_RIGHT_SUPER;
    public final static int KEY_MENU           = GLFW.GLFW_KEY_MENU;

    // F键
    public final static int KEY_F1             = GLFW.GLFW_KEY_F1;
    public final static int KEY_F2             = GLFW.GLFW_KEY_F2;
    public final static int KEY_F3             = GLFW.GLFW_KEY_F3;
    public final static int KEY_F4             = GLFW.GLFW_KEY_F4;
    public final static int KEY_F5             = GLFW.GLFW_KEY_F5;
    public final static int KEY_F6             = GLFW.GLFW_KEY_F6;
    public final static int KEY_F7             = GLFW.GLFW_KEY_F7;
    public final static int KEY_F8             = GLFW.GLFW_KEY_F8;
    public final static int KEY_F9             = GLFW.GLFW_KEY_F9;
    public final static int KEY_F10            = GLFW.GLFW_KEY_F10;
    public final static int KEY_F11            = GLFW.GLFW_KEY_F11;
    public final static int KEY_F12            = GLFW.GLFW_KEY_F12;
    public final static int KEY_F13            = GLFW.GLFW_KEY_F13;
    public final static int KEY_F14            = GLFW.GLFW_KEY_F14;
    public final static int KEY_F15            = GLFW.GLFW_KEY_F15;
    public final static int KEY_F16            = GLFW.GLFW_KEY_F16;
    public final static int KEY_F17            = GLFW.GLFW_KEY_F17;
    public final static int KEY_F18            = GLFW.GLFW_KEY_F18;
    public final static int KEY_F19            = GLFW.GLFW_KEY_F19;
    public final static int KEY_F20            = GLFW.GLFW_KEY_F20;
    public final static int KEY_F21            = GLFW.GLFW_KEY_F21;
    public final static int KEY_F22            = GLFW.GLFW_KEY_F22;
    public final static int KEY_F23            = GLFW.GLFW_KEY_F23;
    public final static int KEY_F24            = GLFW.GLFW_KEY_F24;
    public final static int KEY_F25            = GLFW.GLFW_KEY_F25;

    // 数字键
    public final static int KEY_0              = GLFW.GLFW_KEY_0;
    public final static int KEY_1              = GLFW.GLFW_KEY_1;
    public final static int KEY_2              = GLFW.GLFW_KEY_2;
    public final static int KEY_3              = GLFW.GLFW_KEY_3;
    public final static int KEY_4              = GLFW.GLFW_KEY_4;
    public final static int KEY_5              = GLFW.GLFW_KEY_5;
    public final static int KEY_6              = GLFW.GLFW_KEY_6;
    public final static int KEY_7              = GLFW.GLFW_KEY_7;
    public final static int KEY_8              = GLFW.GLFW_KEY_8;
    public final static int KEY_9              = GLFW.GLFW_KEY_9;

    // 字母键
    public final static int KEY_A              = GLFW.GLFW_KEY_A;
    public final static int KEY_B              = GLFW.GLFW_KEY_B;
    public final static int KEY_C              = GLFW.GLFW_KEY_C;
    public final static int KEY_D              = GLFW.GLFW_KEY_D;
    public final static int KEY_E              = GLFW.GLFW_KEY_E;
    public final static int KEY_F              = GLFW.GLFW_KEY_F;
    public final static int KEY_G              = GLFW.GLFW_KEY_G;
    public final static int KEY_H              = GLFW.GLFW_KEY_H;
    public final static int KEY_I              = GLFW.GLFW_KEY_I;
    public final static int KEY_J              = GLFW.GLFW_KEY_J;
    public final static int KEY_K              = GLFW.GLFW_KEY_K;
    public final static int KEY_L              = GLFW.GLFW_KEY_L;
    public final static int KEY_M              = GLFW.GLFW_KEY_M;
    public final static int KEY_N              = GLFW.GLFW_KEY_N;
    public final static int KEY_O              = GLFW.GLFW_KEY_O;
    public final static int KEY_P              = GLFW.GLFW_KEY_P;
    public final static int KEY_Q              = GLFW.GLFW_KEY_Q;
    public final static int KEY_R              = GLFW.GLFW_KEY_R;
    public final static int KEY_S              = GLFW.GLFW_KEY_S;
    public final static int KEY_T              = GLFW.GLFW_KEY_T;
    public final static int KEY_U              = GLFW.GLFW_KEY_U;
    public final static int KEY_V              = GLFW.GLFW_KEY_V;
    public final static int KEY_W              = GLFW.GLFW_KEY_W;
    public final static int KEY_X              = GLFW.GLFW_KEY_X;
    public final static int KEY_Y              = GLFW.GLFW_KEY_Y;
    public final static int KEY_Z              = GLFW.GLFW_KEY_Z;

    // 符号键
    public final static int KEY_SPACE          = GLFW.GLFW_KEY_SPACE;
    public final static int KEY_APOSTROPHE     = GLFW.GLFW_KEY_APOSTROPHE;
    public final static int KEY_COMMA          = GLFW.GLFW_KEY_COMMA;
    public final static int KEY_MINUS          = GLFW.GLFW_KEY_MINUS;
    public final static int KEY_PERIOD         = GLFW.GLFW_KEY_PERIOD;
    public final static int KEY_SLASH          = GLFW.GLFW_KEY_SLASH;
    public final static int KEY_SEMICOLON      = GLFW.GLFW_KEY_SEMICOLON;
    public final static int KEY_EQUAL          = GLFW.GLFW_KEY_EQUAL;
    public final static int KEY_LEFT_BRACKET   = GLFW.GLFW_KEY_LEFT_BRACKET;
    public final static int KEY_BACKSLASH      = GLFW.GLFW_KEY_BACKSLASH;
    public final static int KEY_RIGHT_BRACKET  = GLFW.GLFW_KEY_RIGHT_BRACKET;
    public final static int KEY_GRAVE_ACCENT   = GLFW.GLFW_KEY_GRAVE_ACCENT;

    // 小键盘
    public final static int KEY_KP_0           = GLFW.GLFW_KEY_KP_0;
    public final static int KEY_KP_1           = GLFW.GLFW_KEY_KP_1;
    public final static int KEY_KP_2           = GLFW.GLFW_KEY_KP_2;
    public final static int KEY_KP_3           = GLFW.GLFW_KEY_KP_3;
    public final static int KEY_KP_4           = GLFW.GLFW_KEY_KP_4;
    public final static int KEY_KP_5           = GLFW.GLFW_KEY_KP_5;
    public final static int KEY_KP_6           = GLFW.GLFW_KEY_KP_6;
    public final static int KEY_KP_7           = GLFW.GLFW_KEY_KP_7;
    public final static int KEY_KP_8           = GLFW.GLFW_KEY_KP_8;
    public final static int KEY_KP_9           = GLFW.GLFW_KEY_KP_9;
    public final static int KEY_KP_DECIMAL     = GLFW.GLFW_KEY_KP_DECIMAL;
    public final static int KEY_KP_DIVIDE      = GLFW.GLFW_KEY_KP_DIVIDE;
    public final static int KEY_KP_MULTIPLY    = GLFW.GLFW_KEY_KP_MULTIPLY;
    public final static int KEY_KP_SUBTRACT    = GLFW.GLFW_KEY_KP_SUBTRACT;
    public final static int KEY_KP_ADD         = GLFW.GLFW_KEY_KP_ADD;
    public final static int KEY_KP_ENTER       = GLFW.GLFW_KEY_KP_ENTER;
    public final static int KEY_KP_EQUAL       = GLFW.GLFW_KEY_KP_EQUAL;

    public final static long KEYBOARD_PRESS_EVENT     = 0X0011FL;
    public final static long KEYBOARD_RELEASE_EVENT   = 0X0012FFL;
    public final static long KEYBOARD_REPEAT_EVENT    = 0X0013FFFL;
    public final static long KEYBOARD_CHAR_EVENT      = 0X0014FFFFL;
    public final static long KEYBOARD_CHAR_MODS_EVENT = 0X0015FFFFFL;

    // 窗口句柄
    private long window;

    // 事件类型
    private long type;

    // 键盘键值
    private int key;

    // 硬件扫描码
    private int scancode;

    // 修饰符
    private int mods;

    // 字符输入事件的char值
    private int codepoint;


    /**
     * 键盘key事件构造参数
     *
     * <p>
     *     用于为{@link KeyboardInput}的非{@link KeyboardInput#inputKey}事件的键盘事件参数复制，
     *     主要位于{@link GLFW#glfwSetKeyCallback}中。
     * </p>
     *
     * @param value0 固定为窗口句柄
     * @param value1 键盘事件传入的key
     * @param value2 硬件扫描码
     * @param value3 固定为修饰符
     * @param value4 固定为类型
     * @see GLFW#glfwSetKeyCallback
     * @see KeyboardInput#pressKey
     * @see KeyboardInput#loosenKey
     * @see KeyboardInput#longPress
     * @see KeyboardInput
     * @see GLFW
     * @see GLFWKeyCallbackI
     * @see InputManager
     * @since 25
     */
    @Internal()
    KeyboardData(long value0, int value1, int value2, int value3, long value4) {
        window = value0;
        key = value1;
        scancode = value2;
        mods = value3;
        codepoint = NOT_MATCH_EVENT_FIELD_INT_VALUE;
        type = value4;
    }

    /**
     * 键盘字符输入事件构造参数
     *
     * <p>
     *     当{@code value3}为{@link #KEYBOARD_CHAR_EVENT} 时,<br>
     *     {@code value2}为{@link #NOT_MATCH_EVENT_FIELD_INT_VALUE},<br>
     *     此时的{@code vlue2}是无效的。
     * </p>
     *
     * @param value0 固定为窗口句柄
     * @param value1 输入事件传入的{@code char}字符的int值
     * @param value2 传入的修饰符
     * @param value3 固定为事件类型
     * @see GLFW#glfwSetCharCallback
     * @see GLFW#glfwSetCharModsCallback
     * @see KeyboardInput#inputKey
     * @see GLFW
     * @see GLFWCharCallbackI
     * @see GLFWCharModsCallbackI
     * @see InputManager
     * @see KeyboardInput
     * @since 25
     */
    @Internal
    KeyboardData(long value0, int value1, int value2, long value3) {
        window = value0;
        key = NOT_MATCH_EVENT_FIELD_INT_VALUE;
        scancode = NOT_MATCH_EVENT_FIELD_INT_VALUE;
        mods = value2;
        codepoint = value1;
        type = value3;
    }

    @Deprecated
    KeyboardData(long value0, long value1, int value2, int value3, int value4, int value5) {
        window = value0;
        type = value1;
        key = value2;
        scancode = value3;
        mods = value4;
        codepoint = value5;
    }

    public KeyboardData(int key ,int scancode,int mods,int codepoint) {
        this.key       = key;
        this.scancode  = scancode;
        this.mods      = mods;
        this.codepoint = codepoint;
    }

    KeyboardData() {

    }

    /**
     * @return 获取窗口句柄
     * @since 25
     */
    public long getWindow() {
        return window;
    }

    /**
     * @return 获取事件类型
     * @since 25
     */
    public long getType() {
        return type;
    }

    @Override
    public long getState() {
        return type;
    }

    public int getKey() {
        return key;
    }

    public int getScancode() {
        return scancode;
    }

    public int getMods() {
        return mods;
    }

    public int getCodepoint() {
        return codepoint;
    }

    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public @NotNull Object clone() {
        try {
            KeyboardData e = new KeyboardData();
            e.window = window;
            e.type = type;
            e.key = key;
            e.scancode = scancode;
            e.mods = mods;
            e.codepoint = codepoint;
            return e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof KeyboardData that)) return false;
        return window == that.window && type == that.type && key == that.key && scancode == that.scancode && mods == that.mods && codepoint == that.codepoint;
    }

    @Override
    public int hashCode() {
        return Objects.hash(window, type, key, scancode, mods, codepoint);
    }

    @Override
    public String toString() {
        return "KeyboardEvent[" +
                "window=" + window +
                ", state=" + type +
                ", key=" + key +
                ", scancode=" + scancode +
                ", mods=" + mods +
                ", codepoint=" + codepoint +
                ']';
    }
}
