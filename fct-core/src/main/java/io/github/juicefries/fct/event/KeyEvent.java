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
// Data 2026/09/10 16:01
//

package io.github.juicefries.fct.event;

import io.github.juicefries.fct.sign.Copyable;
import io.github.juicefries.fct.sign.Readonly;
import io.github.juicefries.fct.util.Array;
import io.github.juicefries.fct.util.Util;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class KeyEvent implements EventData, Copyable,Cloneable, Readonly {

    // 功能键
    public static final int VK_ESC = 256;
    public static final int VK_ENTER = 257;
    public static final int VK_TAB = 258;
    public static final int VK_BACKSPACE = 259;
    public static final int VK_INSERT = 260;
    public static final int VK_DELETE = 261;
    public static final int VK_RIGHT = 262;
    public static final int VK_LEFT = 263;
    public static final int VK_DOWN = 264;
    public static final int VK_UP = 265;
    public static final int VK_PAGE_UP = 266;
    public static final int VK_PAGE_DOWN = 267;
    public static final int VK_HOME = 268;
    public static final int VK_END = 269;
    public static final int VK_CAPS_LOCK = 280;
    public static final int VK_SCROLL_LOCK = 281;
    public static final int VK_NUM_LOCK = 282;
    public static final int VK_PRINT_SCREEN = 283;
    public static final int VK_PAUSE = 284;
    public static final int VK_LEFT_SHIFT = 340;
    public static final int VK_LEFT_CONTROL = 341;
    public static final int VK_LEFT_ALT = 342;
    public static final int VK_LEFT_SUPER = 343;
    public static final int VK_RIGHT_SHIFT = 344;
    public static final int VK_RIGHT_CONTROL = 345;
    public static final int VK_RIGHT_ALT = 346;
    public static final int VK_RIGHT_SUPER = 347;
    public static final int VK_MENU = 348;

    // F键
    public static final int VK_F1 = 290;
    public static final int VK_F2 = 291;
    public static final int VK_F3 = 292;
    public static final int VK_F4 = 293;
    public static final int VK_F5 = 294;
    public static final int VK_F6 = 295;
    public static final int VK_F7 = 296;
    public static final int VK_F8 = 297;
    public static final int VK_F9 = 298;
    public static final int VK_F10 = 299;
    public static final int VK_F11 = 300;
    public static final int VK_F12 = 301;
    public static final int VK_F13 = 302;
    public static final int VK_F14 = 303;
    public static final int VK_F15 = 304;
    public static final int VK_F16 = 305;
    public static final int VK_F17 = 306;
    public static final int VK_F18 = 307;
    public static final int VK_F19 = 308;
    public static final int VK_F20 = 309;
    public static final int VK_F21 = 310;
    public static final int VK_F22 = 311;
    public static final int VK_F23 = 312;
    public static final int VK_F24 = 313;
    public static final int VK_F25 = 314;

    // 数字键
    public static final int VK_0 = 48;
    public static final int VK_1 = 49;
    public static final int VK_2 = 50;
    public static final int VK_3 = 51;
    public static final int VK_4 = 52;
    public static final int VK_5 = 53;
    public static final int VK_6 = 54;
    public static final int VK_7 = 55;
    public static final int VK_8 = 56;
    public static final int VK_9 = 57;

    // 字母键
    public static final int VK_A = 65;
    public static final int VK_B = 66;
    public static final int VK_C = 67;
    public static final int VK_D = 68;
    public static final int VK_E = 69;
    public static final int VK_F = 70;
    public static final int VK_G = 71;
    public static final int VK_H = 72;
    public static final int VK_I = 73;
    public static final int VK_J = 74;
    public static final int VK_K = 75;
    public static final int VK_L = 76;
    public static final int VK_M = 77;
    public static final int VK_N = 78;
    public static final int VK_O = 79;
    public static final int VK_P = 80;
    public static final int VK_Q = 81;
    public static final int VK_R = 82;
    public static final int VK_S = 83;
    public static final int VK_T = 84;
    public static final int VK_U = 85;
    public static final int VK_V = 86;
    public static final int VK_W = 87;
    public static final int VK_X = 88;
    public static final int VK_Y = 89;
    public static final int VK_Z = 90;

    // 符号键
    public static final int VK_SPACE = 32;
    public static final int VK_APOSTROPHE = 39;
    public static final int VK_COMMA = 44;
    public static final int VK_MINUS = 45;
    public static final int VK_PERIOD = 46;
    public static final int VK_SLASH = 47;
    public static final int VK_SEMICOLON = 59;
    public static final int VK_EQUAL = 61;
    public static final int VK_LEFT_BRACKET = 91;
    public static final int VK_BACKSLASH = 92;
    public static final int VK_RIGHT_BRACKET = 93;
    public static final int VK_GRAVE_ACCENT = 96;

    // 小键盘
    public static final int VK_KP_0 = 320;
    public static final int VK_KP_1 = 321;
    public static final int VK_KP_2 = 322;
    public static final int VK_KP_3 = 323;
    public static final int VK_KP_4 = 324;
    public static final int VK_KP_5 = 325;
    public static final int VK_KP_6 = 326;
    public static final int VK_KP_7 = 327;
    public static final int VK_KP_8 = 328;
    public static final int VK_KP_9 = 329;
    public static final int VK_KP_DECIMAL = 330;
    public static final int VK_KP_DIVIDE = 331;
    public static final int VK_KP_MULTIPLY = 332;
    public static final int VK_KP_SUBTRACT = 333;
    public static final int VK_KP_ADD = 334;
    public static final int VK_KP_ENTER = 335;
    public static final int VK_KP_EQUAL = 336;

    public final static long KEY_CHAR_INPUT_EVENT = Util.turn("KED-CharInput");
    public final static long KEY_PRESS_EVENT = Util.turn("KED-Press");
    public final static long KEY_RELEASE_EVENT = Util.turn("KED-Release");
    public final static long KEY_LONG_PRESS_EVENT = Util.turn("KED-LongPress");

    long type;

    @MagicConstant(intValues = {
            VK_ESC,
            VK_ENTER,
            VK_TAB,
            VK_BACKSPACE,
            VK_INSERT,
            VK_DELETE,
            VK_RIGHT,
            VK_LEFT,
            VK_DOWN,
            VK_UP,
            VK_PAGE_UP,
            VK_PAGE_DOWN,
            VK_HOME,
            VK_END,
            VK_CAPS_LOCK,
            VK_SCROLL_LOCK,
            VK_NUM_LOCK,
            VK_PRINT_SCREEN,
            VK_PAUSE,
            VK_LEFT_SHIFT,
            VK_LEFT_CONTROL,
            VK_LEFT_ALT,
            VK_LEFT_SUPER,
            VK_RIGHT_SHIFT,
            VK_RIGHT_CONTROL,
            VK_RIGHT_ALT,
            VK_RIGHT_SUPER,
            VK_MENU,
            GLFW.GLFW_KEY_UNKNOWN,
            VK_F1,
            VK_F2,
            VK_F3,
            VK_F4,
            VK_F5,
            VK_F6,
            VK_F7,
            VK_F8,
            VK_F9,
            VK_F10,
            VK_F11,
            VK_F12,
            VK_F13,
            VK_F14,
            VK_F15,
            VK_F16,
            VK_F17,
            VK_F18,
            VK_F19,
            VK_F20,
            VK_F21,
            VK_F22,
            VK_F23,
            VK_F24,
            VK_F25,
            VK_0,
            VK_1,
            VK_2,
            VK_3,
            VK_4,
            VK_5,
            VK_6,
            VK_7,
            VK_8,
            VK_9,
            VK_A,
            VK_B,
            VK_C,
            VK_D,
            VK_E,
            VK_F,
            VK_G,
            VK_H,
            VK_I,
            VK_J,
            VK_K,
            VK_L,
            VK_M,
            VK_N,
            VK_O,
            VK_P,
            VK_Q,
            VK_R,
            VK_S,
            VK_T,
            VK_U,
            VK_V,
            VK_W,
            VK_X,
            VK_Y,
            VK_Z,
            VK_SPACE,
            VK_APOSTROPHE,
            VK_COMMA,
            VK_MINUS,
            VK_PERIOD,
            VK_SLASH,
            VK_SEMICOLON,
            VK_EQUAL,
            VK_LEFT_BRACKET,
            VK_BACKSLASH,
            VK_RIGHT_BRACKET,
            VK_GRAVE_ACCENT,
            VK_KP_0,
            VK_KP_1,
            VK_KP_2,
            VK_KP_3,
            VK_KP_4,
            VK_KP_5,
            VK_KP_6,
            VK_KP_7,
            VK_KP_8,
            VK_KP_9,
            VK_KP_DECIMAL,
            VK_KP_DIVIDE,
            VK_KP_MULTIPLY,
            VK_KP_SUBTRACT,
            VK_KP_ADD,
            VK_KP_ENTER,
            VK_KP_EQUAL,
    })
    private int key;

    private int mods;

    private char codepoint;

    private KeyEvent() {

    }

    @Contract(value = " -> new", pure = true)
    static @NotNull KeyEvent create() {
        return new KeyEvent();
    }

    static @NotNull KeyEvent create(long type) {
        var e = create();
        e.type = type;
        return e;
    }

    public static @NotNull KeyEvent charInput(char codepoint) {
        var e = create(KEY_CHAR_INPUT_EVENT);
        e.codepoint = codepoint;
        return e;
    }

    public static @NotNull KeyEvent press(int key, int mods) {
        var e = create(KEY_PRESS_EVENT);
        e.key = key;
        e.mods = mods;
        return e;
    }

    public static @NotNull KeyEvent press(int key) {
        return press(key,0);
    }

    public static @NotNull KeyEvent release(int key, int mods) {
        var e = create(KEY_RELEASE_EVENT);
        e.key = key;
        e.mods = mods;
        return e;
    }

    public static @NotNull KeyEvent release(int key) {
        return release(key,0);
    }

    public static @NotNull KeyEvent longPress(int key, int mods) {
        var e = create(KEY_LONG_PRESS_EVENT);
        e.key = key;
        e.mods = mods;
        return e;
    }

    public static @NotNull KeyEvent longPress(int key) {
        return longPress(key,0);
    }

    // ========================= GET =========================

    @Override
    public long getType() {
        return type;
    }

    public @MagicConstant(intValues = {
            VK_ESC,
            VK_ENTER,
            VK_TAB,
            VK_BACKSPACE,
            VK_INSERT,
            VK_DELETE,
            VK_RIGHT,
            VK_LEFT,
            VK_DOWN,
            VK_UP,
            VK_PAGE_UP,
            VK_PAGE_DOWN,
            VK_HOME,
            VK_END,
            VK_CAPS_LOCK,
            VK_SCROLL_LOCK,
            VK_NUM_LOCK,
            VK_PRINT_SCREEN,
            VK_PAUSE,
            VK_LEFT_SHIFT,
            VK_LEFT_CONTROL,
            VK_LEFT_ALT,
            VK_LEFT_SUPER,
            VK_RIGHT_SHIFT,
            VK_RIGHT_CONTROL,
            VK_RIGHT_ALT,
            VK_RIGHT_SUPER,
            VK_MENU,
            GLFW.GLFW_KEY_UNKNOWN,
            VK_F1,
            VK_F2,
            VK_F3,
            VK_F4,
            VK_F5,
            VK_F6,
            VK_F7,
            VK_F8,
            VK_F9,
            VK_F10,
            VK_F11,
            VK_F12,
            VK_F13,
            VK_F14,
            VK_F15,
            VK_F16,
            VK_F17,
            VK_F18,
            VK_F19,
            VK_F20,
            VK_F21,
            VK_F22,
            VK_F23,
            VK_F24,
            VK_F25,
            VK_0,
            VK_1,
            VK_2,
            VK_3,
            VK_4,
            VK_5,
            VK_6,
            VK_7,
            VK_8,
            VK_9,
            VK_A,
            VK_B,
            VK_C,
            VK_D,
            VK_E,
            VK_F,
            VK_G,
            VK_H,
            VK_I,
            VK_J,
            VK_K,
            VK_L,
            VK_M,
            VK_N,
            VK_O,
            VK_P,
            VK_Q,
            VK_R,
            VK_S,
            VK_T,
            VK_U,
            VK_V,
            VK_W,
            VK_X,
            VK_Y,
            VK_Z,
            VK_SPACE,
            VK_APOSTROPHE,
            VK_COMMA,
            VK_MINUS,
            VK_PERIOD,
            VK_SLASH,
            VK_SEMICOLON,
            VK_EQUAL,
            VK_LEFT_BRACKET,
            VK_BACKSLASH,
            VK_RIGHT_BRACKET,
            VK_GRAVE_ACCENT,
            VK_KP_0,
            VK_KP_1,
            VK_KP_2,
            VK_KP_3,
            VK_KP_4,
            VK_KP_5,
            VK_KP_6,
            VK_KP_7,
            VK_KP_8,
            VK_KP_9,
            VK_KP_DECIMAL,
            VK_KP_DIVIDE,
            VK_KP_MULTIPLY,
            VK_KP_SUBTRACT,
            VK_KP_ADD,
            VK_KP_ENTER,
            VK_KP_EQUAL,
    }) int getKey() {
        var contains = Array.contains(getType(),
                KEY_PRESS_EVENT,
                KEY_LONG_PRESS_EVENT,
                KEY_RELEASE_EVENT
        );
        if (!contains) return GLFW.GLFW_KEY_UNKNOWN;

        return key;
    }

    public int getMods() {
        var contains = Array.contains(getType(),
                KEY_PRESS_EVENT,
                KEY_LONG_PRESS_EVENT,
                KEY_RELEASE_EVENT
        );
        if (!contains) return 0;

        return mods;
    }

    public char getCodepoint() {
        if (type != KEY_CHAR_INPUT_EVENT) {
            return '\u0000';
        }
        return codepoint;
    }

    // ========================= SBSB =========================

    /**
     * 复制事件数据到指定事件
     * @param e 目标事件
     * @since 0.0.4
     * @throws NullPointerException 目标事件不能为{@code null}
     */
    public void copy(KeyEvent e) {
        if (e == null) {
            throw new NullPointerException("e is null!");
        }
        e.type = type;
        e.codepoint = codepoint;
        e.key = key;
        e.mods = mods;
    }

    /**
     * 克隆键盘事件
     * @return 事件副本
     * @since 0.0.4
     */
    @Override
    public KeyEvent clone() {
        try {
            var e = (KeyEvent) super.clone();
            copy(e);
            return e;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("clone failed!", e);
        }
    }

    /**
     * 复制键盘事件
     * @return 事件副本
     * @since 0.0.4
     */
    @Override
    public KeyEvent copy() {
        var e = create();
        copy(e);
        return e;
    }

}
