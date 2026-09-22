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

import io.github.juicefries.fct.util.Array;
import io.github.juicefries.fct.util.Util;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class MouseEvent implements EventData {

    public final static long MOUSE_MOUSE_POS_EVENT = Util.turn("MED-MousePos");
    public final static long MOUSE_DROP_EVENT = Util.turn("MED-DROP");
    public final static long MOUSE_PRESS_EVENT   = Util.turn("MED-Press");
    public final static long MOUSE_RELEASE_EVENT = Util.turn("MED-Release");
    public final static long MOUSE_SCROLL_EVENT = Util.turn("MED-Scroll");
    public final static long MOUSE_ENTERED_EVENT = Util.turn("MED-Entered");
    public final static long MOUSE_EXITED_EVENT = Util.turn("MED-Exited");

    public final static int MK_LEFT = 0;
    public final static int MK_RIGHT = 1;
    public final static int MK_MIDDLE = 2;

    private long type;

    @MagicConstant(intValues = {MK_LEFT,MK_RIGHT,MK_MIDDLE})
    private int button = MK_LEFT;
    private int mods = 0;
    private float x = 0.0f;
    private float y = 0.0f;
    private double xOffset = 0.0d;
    private double yOffset = 0.0d;
    private String[] filePaths = Array.createS(0);

    private MouseEvent() {

    }

    // ========================= UTIL =========================

    @Contract(value = " -> new", pure = true)
    static @NotNull MouseEvent create() {
        return new MouseEvent();
    }

    static @NotNull MouseEvent create(long type) {
        var e = create();
        e.type = type;
        return e;
    }

    // ========================= CREATE =========================

    public static @NotNull MouseEvent pos(float x, float y) {
        var e = create(MOUSE_MOUSE_POS_EVENT);
        e.x = x;
        e.y = y;
        return e;
    }

    public static @NotNull MouseEvent drop(String[] filePaths, float x, float y) {
        var e = create(MOUSE_DROP_EVENT);
        e.filePaths = filePaths;
        e.x = x;
        e.y = y;
        return e;
    }

    public static @NotNull MouseEvent drop(String... filesPaths) {
        return drop(filesPaths,0.0f,0.0f);
    }

    public static @NotNull MouseEvent press(
            @MagicConstant(intValues = {MK_LEFT,MK_RIGHT,MK_MIDDLE}) int button,
            int mods, float x, float y
    ) {
        var e = create(MOUSE_PRESS_EVENT);
        e.button = button;
        e.mods = mods;
        e.x = x;
        e.y = y;
        return e;
    }

    public static @NotNull MouseEvent press(
            @MagicConstant(intValues = {MK_LEFT,MK_RIGHT,MK_MIDDLE}) int button,
            float x, float y
    ) {
        return press(button, 0, x, y);
    }

    public static @NotNull MouseEvent release(
            @MagicConstant(intValues = {MK_LEFT,MK_RIGHT,MK_MIDDLE}) int button,
            int mods, float x, float y
    ) {
        var e = create(MOUSE_RELEASE_EVENT);
        e.button = button;
        e.mods = mods;
        e.x = x;
        e.y = y;
        return e;
    }

    public static @NotNull MouseEvent release(
            @MagicConstant(intValues = {MK_LEFT,MK_RIGHT,MK_MIDDLE}) int button,
            float x, float y
    ) {
        return release(button, 0, x, y);
    }

    public static @NotNull MouseEvent scroll(double xOffset, double yOffset, float x, float y) {
        var e = create(MOUSE_SCROLL_EVENT);
        e.xOffset = xOffset;
        e.yOffset = yOffset;
        e.x = x;
        e.y = y;
        return e;
    }

    public static @NotNull MouseEvent scroll(double xOffset, double yOffset) {
        return scroll(xOffset, yOffset, 0.0f, 0.0f);
    }

    public static @NotNull MouseEvent entered(float x, float y) {
        var e = create(MOUSE_ENTERED_EVENT);
        e.x = x;
        e.y = y;
        return e;
    }

    public static @NotNull MouseEvent exited(float x, float y) {
        var e = create(MOUSE_EXITED_EVENT);
        e.x = x;
        e.y = y;
        return e;
    }

    // ========================= GET =========================

    @Override
    public long getType() {
        return type;
    }

    public @MagicConstant(intValues = {MK_LEFT,MK_RIGHT,MK_MIDDLE}) int getButton()
    {
        return button;
    }

    public int getMods() {
        return mods;
    }

    public float getY() {
        return y;
    }

    public float getX() {
        return x;
    }

    public double getXOffset() {
        return xOffset;
    }

    public double getYOffset() {
        return yOffset;
    }

    public String[] getFilePaths() {
        return filePaths;
    }

    public void copy(MouseEvent e) {
        if (e == null) {
            throw new NullPointerException("e is null!");
        }
        e.type = type;
        e.button = button;
        e.mods = mods;
        e.x = x;
        e.y = y;
        e.xOffset = xOffset;
        e.yOffset = yOffset;
        e.filePaths = filePaths;
    }

    @Override
    public MouseEvent copy() {
        var e = create();
        copy(e);
        return e;
    }

    @Override
    public MouseEvent clone() {
        try {
            var e = (MouseEvent) super.clone();
            copy(e);
            return e;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("clone failed!", e);
        }

    }
}
