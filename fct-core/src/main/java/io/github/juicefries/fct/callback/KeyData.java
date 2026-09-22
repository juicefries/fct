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
// Data 2026/08/22 03:43
//

package io.github.juicefries.fct.callback;

import io.github.juicefries.fct.sign.Copyable;
import io.github.juicefries.fct.sign.Readonly;
import io.github.juicefries.fct.util.Util;
import java.util.Objects;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;

public final class KeyData implements CallbackData, Copyable,Cloneable, Readonly {

    // 功能键
    public final static int VK_ESC            = GLFW.GLFW_KEY_ESCAPE;
    public final static int VK_ENTER          = GLFW.GLFW_KEY_ENTER;
    public final static int VK_TAB            = GLFW.GLFW_KEY_TAB;
    public final static int VK_BACKSPACE      = GLFW.GLFW_KEY_BACKSPACE;
    public final static int VK_INSERT         = GLFW.GLFW_KEY_INSERT;
    public final static int VK_DELETE         = GLFW.GLFW_KEY_DELETE;
    public final static int VK_RIGHT          = GLFW.GLFW_KEY_RIGHT;
    public final static int VK_LEFT           = GLFW.GLFW_KEY_LEFT;
    public final static int VK_DOWN           = GLFW.GLFW_KEY_DOWN;
    public final static int VK_UP             = GLFW.GLFW_KEY_UP;
    public final static int VK_PAGE_UP        = GLFW.GLFW_KEY_PAGE_UP;
    public final static int VK_PAGE_DOWN      = GLFW.GLFW_KEY_PAGE_DOWN;
    public final static int VK_HOME           = GLFW.GLFW_KEY_HOME;
    public final static int VK_END            = GLFW.GLFW_KEY_END;
    public final static int VK_CAPS_LOCK      = GLFW.GLFW_KEY_CAPS_LOCK;
    public final static int VK_SCROLL_LOCK    = GLFW.GLFW_KEY_SCROLL_LOCK;
    public final static int VK_NUM_LOCK       = GLFW.GLFW_KEY_NUM_LOCK;
    public final static int VK_PRINT_SCREEN   = GLFW.GLFW_KEY_PRINT_SCREEN;
    public final static int VK_PAUSE          = GLFW.GLFW_KEY_PAUSE; // 修饰键
    public final static int VK_LEFT_SHIFT     = GLFW.GLFW_KEY_LEFT_SHIFT;
    public final static int VK_LEFT_CONTROL   = GLFW.GLFW_KEY_LEFT_CONTROL;
    public final static int VK_LEFT_ALT       = GLFW.GLFW_KEY_LEFT_ALT;
    public final static int VK_LEFT_SUPER     = GLFW.GLFW_KEY_LEFT_SUPER;
    public final static int VK_RIGHT_SHIFT    = GLFW.GLFW_KEY_RIGHT_SHIFT;
    public final static int VK_RIGHT_CONTROL  = GLFW.GLFW_KEY_RIGHT_CONTROL;
    public final static int VK_RIGHT_ALT      = GLFW.GLFW_KEY_RIGHT_ALT;
    public final static int VK_RIGHT_SUPER    = GLFW.GLFW_KEY_RIGHT_SUPER;
    public final static int VK_MENU           = GLFW.GLFW_KEY_MENU;

    // F键
    public final static int VK_F1             = GLFW.GLFW_KEY_F1;
    public final static int VK_F2             = GLFW.GLFW_KEY_F2;
    public final static int VK_F3             = GLFW.GLFW_KEY_F3;
    public final static int VK_F4             = GLFW.GLFW_KEY_F4;
    public final static int VK_F5             = GLFW.GLFW_KEY_F5;
    public final static int VK_F6             = GLFW.GLFW_KEY_F6;
    public final static int VK_F7             = GLFW.GLFW_KEY_F7;
    public final static int VK_F8             = GLFW.GLFW_KEY_F8;
    public final static int VK_F9             = GLFW.GLFW_KEY_F9;
    public final static int VK_F10            = GLFW.GLFW_KEY_F10;
    public final static int VK_F11            = GLFW.GLFW_KEY_F11;
    public final static int VK_F12            = GLFW.GLFW_KEY_F12;
    public final static int VK_F13            = GLFW.GLFW_KEY_F13;
    public final static int VK_F14            = GLFW.GLFW_KEY_F14;
    public final static int VK_F15            = GLFW.GLFW_KEY_F15;
    public final static int VK_F16            = GLFW.GLFW_KEY_F16;
    public final static int VK_F17            = GLFW.GLFW_KEY_F17;
    public final static int VK_F18            = GLFW.GLFW_KEY_F18;
    public final static int VK_F19            = GLFW.GLFW_KEY_F19;
    public final static int VK_F20            = GLFW.GLFW_KEY_F20;
    public final static int VK_F21            = GLFW.GLFW_KEY_F21;
    public final static int VK_F22            = GLFW.GLFW_KEY_F22;
    public final static int VK_F23            = GLFW.GLFW_KEY_F23;
    public final static int VK_F24            = GLFW.GLFW_KEY_F24;
    public final static int VK_F25            = GLFW.GLFW_KEY_F25;

    // 数字键
    public final static int VK_0              = GLFW.GLFW_KEY_0;
    public final static int VK_1              = GLFW.GLFW_KEY_1;
    public final static int VK_2              = GLFW.GLFW_KEY_2;
    public final static int VK_3              = GLFW.GLFW_KEY_3;
    public final static int VK_4              = GLFW.GLFW_KEY_4;
    public final static int VK_5              = GLFW.GLFW_KEY_5;
    public final static int VK_6              = GLFW.GLFW_KEY_6;
    public final static int VK_7              = GLFW.GLFW_KEY_7;
    public final static int VK_8              = GLFW.GLFW_KEY_8;
    public final static int VK_9              = GLFW.GLFW_KEY_9;

    // 字母键
    public final static int VK_A              = GLFW.GLFW_KEY_A;
    public final static int VK_B              = GLFW.GLFW_KEY_B;
    public final static int VK_C              = GLFW.GLFW_KEY_C;
    public final static int VK_D              = GLFW.GLFW_KEY_D;
    public final static int VK_E              = GLFW.GLFW_KEY_E;
    public final static int VK_F              = GLFW.GLFW_KEY_F;
    public final static int VK_G              = GLFW.GLFW_KEY_G;
    public final static int VK_H              = GLFW.GLFW_KEY_H;
    public final static int VK_I              = GLFW.GLFW_KEY_I;
    public final static int VK_J              = GLFW.GLFW_KEY_J;
    public final static int VK_K              = GLFW.GLFW_KEY_K;
    public final static int VK_L              = GLFW.GLFW_KEY_L;
    public final static int VK_M              = GLFW.GLFW_KEY_M;
    public final static int VK_N              = GLFW.GLFW_KEY_N;
    public final static int VK_O              = GLFW.GLFW_KEY_O;
    public final static int VK_P              = GLFW.GLFW_KEY_P;
    public final static int VK_Q              = GLFW.GLFW_KEY_Q;
    public final static int VK_R              = GLFW.GLFW_KEY_R;
    public final static int VK_S              = GLFW.GLFW_KEY_S;
    public final static int VK_T              = GLFW.GLFW_KEY_T;
    public final static int VK_U              = GLFW.GLFW_KEY_U;
    public final static int VK_V              = GLFW.GLFW_KEY_V;
    public final static int VK_W              = GLFW.GLFW_KEY_W;
    public final static int VK_X              = GLFW.GLFW_KEY_X;
    public final static int VK_Y              = GLFW.GLFW_KEY_Y;
    public final static int VK_Z              = GLFW.GLFW_KEY_Z;

    // 符号键
    public final static int VK_SPACE          = GLFW.GLFW_KEY_SPACE;
    public final static int VK_APOSTROPHE     = GLFW.GLFW_KEY_APOSTROPHE;
    public final static int VK_COMMA          = GLFW.GLFW_KEY_COMMA;
    public final static int VK_MINUS          = GLFW.GLFW_KEY_MINUS;
    public final static int VK_PERIOD         = GLFW.GLFW_KEY_PERIOD;
    public final static int VK_SLASH          = GLFW.GLFW_KEY_SLASH;
    public final static int VK_SEMICOLON      = GLFW.GLFW_KEY_SEMICOLON;
    public final static int VK_EQUAL          = GLFW.GLFW_KEY_EQUAL;
    public final static int VK_LEFT_BRACKET   = GLFW.GLFW_KEY_LEFT_BRACKET;
    public final static int VK_BACKSLASH      = GLFW.GLFW_KEY_BACKSLASH;
    public final static int VK_RIGHT_BRACKET  = GLFW.GLFW_KEY_RIGHT_BRACKET;
    public final static int VK_GRAVE_ACCENT   = GLFW.GLFW_KEY_GRAVE_ACCENT;

    // 小键盘
    public final static int VK_KP_0           = GLFW.GLFW_KEY_KP_0;
    public final static int VK_KP_1           = GLFW.GLFW_KEY_KP_1;
    public final static int VK_KP_2           = GLFW.GLFW_KEY_KP_2;
    public final static int VK_KP_3           = GLFW.GLFW_KEY_KP_3;
    public final static int VK_KP_4           = GLFW.GLFW_KEY_KP_4;
    public final static int VK_KP_5           = GLFW.GLFW_KEY_KP_5;
    public final static int VK_KP_6           = GLFW.GLFW_KEY_KP_6;
    public final static int VK_KP_7           = GLFW.GLFW_KEY_KP_7;
    public final static int VK_KP_8           = GLFW.GLFW_KEY_KP_8;
    public final static int VK_KP_9           = GLFW.GLFW_KEY_KP_9;
    public final static int VK_KP_DECIMAL     = GLFW.GLFW_KEY_KP_DECIMAL;
    public final static int VK_KP_DIVIDE      = GLFW.GLFW_KEY_KP_DIVIDE;
    public final static int VK_KP_MULTIPLY    = GLFW.GLFW_KEY_KP_MULTIPLY;
    public final static int VK_KP_SUBTRACT    = GLFW.GLFW_KEY_KP_SUBTRACT;
    public final static int VK_KP_ADD         = GLFW.GLFW_KEY_KP_ADD;
    public final static int VK_KP_ENTER       = GLFW.GLFW_KEY_KP_ENTER;
    public final static int VK_KP_EQUAL       = GLFW.GLFW_KEY_KP_EQUAL;

    public final static long KEY_INPUT_EVENT = Util.turn("KeyEvent-%s".formatted(Handler.KEY_INPUT));
    public final static long KEY_CHAR_INPUT_EVENT = Util.turn("KeyEvent-%s".formatted(Handler.CHAR_INPUT));
    public final static long KEY_CHAR_MODES_INPUT_EVENT = Util.turn("KeyEvent-%s".formatted(Handler.CHAR_MODES_INPUT));

    public final static int PRESS = GLFW.GLFW_PRESS;
    public final static int RELEASE = GLFW.GLFW_RELEASE;
    public final static int REPEAT = GLFW.GLFW_REPEAT;

    private long window = MemoryUtil.NULL;
    private long type;

    // 键盘键值
    private int key;

    // 硬件扫描码
    private int scancode;

    // 修饰符
    private int mods;

    // 字符输入事件的char值
    private int codepoint;

    private int action;


    private KeyData() {

    }


    // ========================= CREATE =========================

    @Contract(value = " -> new", pure = true)
    static @NotNull KeyData create() {
        return new KeyData();
    }

    public static @NotNull KeyData keyInput(long window, int key, int scancode, int action, int mods) {
        KeyData data = create();
        data.type = KEY_INPUT_EVENT;
        data.window = window;
        data.key = key;
        data.scancode = scancode;
        data.action = action;
        data.mods = mods;
        return data;
    }

    public static @NotNull KeyData keyInput(int key, int scancode, int action, int mods) {
        return keyInput(MemoryUtil.NULL,key,scancode,action,mods);
    }

    public static @NotNull KeyData charInput(long window, int codepoint) {
        var data = create();
        data.window = window;
        data.type = KEY_CHAR_INPUT_EVENT;
        data.codepoint = codepoint;
        return data;
    }

    public static @NotNull KeyData charInput(int codepoint) {
        return charInput(MemoryUtil.NULL,codepoint);
    }

    public static @NotNull KeyData charInput(char codepoint) {
        return charInput((int) codepoint);
    }

    public static @NotNull KeyData charInput() {
        return charInput('\u0000');
    }

    public static @NotNull KeyData charModesInput(long window, int codepoint, int mods) {
        KeyData data = create();
        data.window = window;
        data.type = KEY_CHAR_MODES_INPUT_EVENT;
        data.codepoint = codepoint;
        data.mods = mods;
        return data;
    }

    public static @NotNull KeyData charModesInput(long window, char codepoint, int mods) {
        return charModesInput(window,(int) codepoint,mods);
    }

    public static @NotNull KeyData charModesInput(int codepoint, int mods) {
        return charModesInput(MemoryUtil.NULL,codepoint,mods);
    }

    public static @NotNull KeyData charModesInput(char codepoint, int mods) {
        return charModesInput((int) codepoint,mods);
    }

    @Contract("_, null -> fail")
    public static @NotNull KeyData check(long window, KeyData data) {
        if (data == null) {
            throw new NullPointerException("data is null!");
        }

        if (data.getWindow() == window) return data;

        KeyData copy = data.copy();
        copy.window = window;
        return copy;
    }

    @Contract("null, _ -> fail; !null, null -> fail")
    public static @NotNull KeyData check(Data kud, KeyData data) {
        if (kud == null) {
            throw new NullPointerException("wud is null!");
        }
        if (data == null) {
            throw new NullPointerException("data is null!");
        }
        return check(kud.getWindow(),data);
    }

    // ========================= SBSB =========================


    @Override
    public KeyData clone() {
        try {
            KeyData data = (KeyData) super.clone();
            copy(data);
            return data;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("clone failed!",e);
        }
    }

    @Override
    public @NotNull KeyData copy() {
        KeyData date = create();
        copy(date);
        return date;
    }

    public void copy(KeyData date) {
        if (date == null) {
            throw new NullPointerException("date is null!");
        }
        date.codepoint = this.getCodepoint();
        date.key = this.getKey();
        date.mods = this.getMods();
        date.type = this.getType();
        date.window = this.getWindow();
        date.scancode = this.getScancode();
        date.action = this.getAction();
    }

    @Contract(value = " -> new", pure = true)
    public static long @NotNull [] toEventTypes() {
        return new long[] {
                KEY_INPUT_EVENT,
                KEY_CHAR_INPUT_EVENT,
                KEY_CHAR_MODES_INPUT_EVENT
        };
    }

    // ========================= GET =========================

    @Override
    public long getWindow() {
        return window;
    }

    @Override
    public long getType() {
        return type;
    }

    public int getKey() {
        if (type != KEY_INPUT_EVENT) {
            return GLFW.GLFW_KEY_UNKNOWN;
        }
        return key;
    }

    public int getMods() {
        if (type == KEY_CHAR_INPUT_EVENT) return 0;
        return mods;
    }

    public int getCodepoint() {
        if (type != KEY_CHAR_INPUT_EVENT && type != KEY_CHAR_MODES_INPUT_EVENT) {

            return '\u0000';
        }
        return codepoint;
    }

    public int getScancode() {
        if (type != KEY_INPUT_EVENT) return 0;
        return scancode;
    }

    public int getAction() {
        if (type != KEY_INPUT_EVENT) return 0;
        return action;
    }

    public boolean isPress() {
        if (type != KEY_INPUT_EVENT) return false;
        return action == PRESS;
    }

    public boolean isRelease() {
        if (type != KEY_INPUT_EVENT) return true;
        return action == RELEASE;
    }

    public boolean isRepeat() {
        if (type != KEY_INPUT_EVENT) return false;
        return action == REPEAT;
    }

}
