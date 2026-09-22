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
// Data 2026/08/22 03:42
//

package io.github.juicefries.fct.callback;

import io.github.juicefries.fct.sign.Copyable;
import io.github.juicefries.fct.sign.Readonly;
import io.github.juicefries.fct.util.Array;
import io.github.juicefries.fct.util.Util;
import java.util.Objects;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;

public final class MouseData implements CallbackData, Copyable, Cloneable, Readonly {

    public final static long MOUSE_SCROLL_EVENT = Util.turn("MouseEvent-%s".formatted(Handler.SCROLL));
    public final static long MOUSE_CURSOR_POS_EVENT = Util.turn("MouseEvent-%s".formatted(Handler.CURSOR_POS));
    public final static long MOUSE_DROP_EVENT = Util.turn("MouseEvent-%s".formatted(Handler.DROP));
    public final static long MOUSE_CURSOR_ENTER_EVENT = Util.turn("MouseEvent-%s".formatted(Handler.CURSOR_ENTER));
    public final static long MOUSE_BUTTON_EVENT = Util.turn("MouseEvent-%s".formatted(Handler.BUTTON));

    public final static int MK_LEFT = GLFW.GLFW_MOUSE_BUTTON_LEFT;
    public final static int MK_RIGHT = GLFW.GLFW_MOUSE_BUTTON_RIGHT;
    public final static int MK_MIDDLE = GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

    private long window = MemoryUtil.NULL;
    private long type;
    private double x = 0.0d;
    private double y = 0.0d;
    private String[] filePaths = Array.createS(0);
    private boolean entered = false;
    private boolean action = false;
    private int button;
    private int mods = 0;


    private MouseData() {

    }

    @Contract(value = " -> new", pure = true)
    private static @NotNull MouseData create() {
        return new MouseData();
    }

    public static @NotNull MouseData scroll(long window, double xOffset, double yOffset) {
        MouseData data = create();
        data.type = MOUSE_SCROLL_EVENT;
        data.window = window;
        data.x = xOffset;
        data.y = yOffset;


        return data;
    }

    public static @NotNull MouseData scroll(double xOffset, double yOffset) {
        return scroll(MemoryUtil.NULL, xOffset, yOffset);
    }

    public static @NotNull MouseData cursorPos(long window, double xPos, double yPos) {
        MouseData data = create();
        data.type = MOUSE_CURSOR_POS_EVENT;
        data.window = window;
        data.x = xPos;
        data.y = yPos;

        return data;
    }

    public static @NotNull MouseData cursorPos(double xPos, double yPos) {
        return cursorPos(MemoryUtil.NULL, xPos, yPos);
    }

    public static @NotNull MouseData drop(long window, String[] filePaths) {
        MouseData data = create();
        data.type = MOUSE_DROP_EVENT;
        data.window = window;
        data.filePaths = filePaths;
        return data;
    }

    public static @NotNull MouseData drop(String[] filePaths) {
        return drop(MemoryUtil.NULL, filePaths);
    }

    public static @NotNull MouseData _drop_(String... filePaths) {
        return drop(filePaths);
    }

    public static @NotNull MouseData cursorEnter(long window, boolean entered) {
        MouseData data = create();
        data.type = MOUSE_CURSOR_ENTER_EVENT;
        data.window = window;
        data.entered = entered;
        return data;
    }

    public static @NotNull MouseData cursorEnter(boolean entered) {
        return cursorEnter(MemoryUtil.NULL, entered);
    }

    public static @NotNull MouseData enter(long window) {
        return cursorEnter(window, true);
    }

    public static @NotNull MouseData enter() {
        return cursorEnter(true);
    }

    public static @NotNull MouseData leave(long window) {
        return cursorEnter(window, false);
    }

    public static @NotNull MouseData leave() {
        return cursorEnter(false);
    }

    public static @NotNull MouseData button(long window, int button, boolean action, int mods) {
        MouseData data = create();
        data.type = MOUSE_BUTTON_EVENT;
        data.window = window;
        data.button = button;
        data.action = action;
        data.mods = mods;
        return data;
    }

    public static @NotNull MouseData button(int button, boolean action, int mods) {
        return button(MemoryUtil.NULL, button, action, mods);
    }

    public static @NotNull MouseData button(int button, boolean action) {
        return button(button, action, 0);
    }

    public static @NotNull MouseData press(int button, int mods) {
        return button(button, true, mods);
    }

    public static @NotNull MouseData press(int button) {
        return button(button, true);
    }

    public static @NotNull MouseData release(int button, int mods) {
        return button(button, false, mods);
    }

    public static @NotNull MouseData release(int button) {
        return button(button, false);
    }

    @Contract("_, null -> fail")
    public static @NotNull MouseData check(long window, MouseData md) {
        if (md == null) {
            throw new NullPointerException("data is null!");
        }

        if (md.getWindow() == window) return md;

        MouseData copy = md.copy();
        copy.window = window;
        return copy;
    }

    public static @NotNull MouseData check(Data mud, MouseData data) {
        if (mud == null) {
            throw new NullPointerException("mud is null!");
        }
        if (data == null) {
            throw new NullPointerException("data is null!");
        }
        return check(mud.getWindow(), data);
    }

    // ========================= =========================
    // ========================= =========================
    // ========================= OTM =========================

    @Override
    public @NotNull MouseData copy() {
        MouseData data = new MouseData();
        copy(data);
        return data;
    }

    public void copy(MouseData data) {
        if (data == null) {
            throw new NullPointerException("data is null!");
        }
        data.type = type;
        data.window = window;
        data.x = x;
        data.y = y;
        data.filePaths = filePaths;
        data.entered = entered;
        data.button = button;
        data.action = action;
        data.mods = mods;
    }

    @Contract(value = " -> new", pure = true)
    public static long @NotNull [] toEventTypes() {
        return new long[]{
                MouseData.MOUSE_BUTTON_EVENT,
                MouseData.MOUSE_CURSOR_ENTER_EVENT,
                MouseData.MOUSE_DROP_EVENT,
                MouseData.MOUSE_CURSOR_POS_EVENT,
                MouseData.MOUSE_SCROLL_EVENT
        };
    }

    @Override
    public @NotNull MouseData clone() {
        try {
            MouseData data = (MouseData) super.clone();
            copy(data);
            return data;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("clone failed!",e);
        }
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

    public double getXOffset() {
        if (getType() != MOUSE_SCROLL_EVENT) return 0.0d;
        return x;
    }

    public double getYOffset() {
        if (getType() != MOUSE_SCROLL_EVENT) return 0.0d;
        return y;
    }

    public double xOffset() {
        return getXOffset();
    }

    public double yOffset() {
        return getYOffset();
    }

    @Contract(" -> new")
    public @NotNull Vector2d getOffset() {
        return new Vector2d(xOffset(), yOffset());
    }

    @Contract(" -> new")
    public @NotNull Vector2d offset() {
        return getOffset();
    }

    public double getXPos() {
        if (getType() != MOUSE_CURSOR_POS_EVENT) return 0.0d;
        return x;
    }

    public double getYPos() {
        if (getType() != MOUSE_CURSOR_POS_EVENT) return 0.0d;
        return y;
    }

    public double xPos() {
        return getXPos();
    }

    public double yPos() {
        return getYPos();
    }

    @Contract(" -> new")
    public @NotNull Vector2d getPos() {
        return new Vector2d(xPos(), yPos());
    }

    @Contract(" -> new")
    public @NotNull Vector2d pos() {
        return getPos();
    }

    public String[] getFilePaths() {
        if (getType() != MOUSE_DROP_EVENT) return Array.createS(0);
        return filePaths;
    }

    public boolean isEntered() {
        if (getType() != MOUSE_CURSOR_ENTER_EVENT) return false;
        return entered;
    }

    public boolean isLeave() {
        if (getType() != MOUSE_CURSOR_ENTER_EVENT) return false;
        return !entered;
    }

    @MagicConstant(intValues = {MK_LEFT, MK_RIGHT, MK_MIDDLE})
    public int getButton() {
        if (getType() != MOUSE_BUTTON_EVENT) { //noinspection MagicConstant
            return 0;
        }
        //noinspection MagicConstant
        return button;
    }

    @MagicConstant(intValues = {MK_LEFT, MK_RIGHT, MK_MIDDLE})
    public int button() {
        return getButton();
    }

    public int getMods() {
        if (getType() != MOUSE_BUTTON_EVENT) return 0;
        return mods;
    }

    public boolean isAction() {
        if (getType() != MOUSE_BUTTON_EVENT) return false;
        return action;
    }

    public boolean isPress() {
        return isAction();
    }

    public boolean isRelease() {
        return !isAction();
    }

}
