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
// Data 2026/08/22 01:48
//

package io.github.juicefries.fct.callback;

import io.github.juicefries.fct.sign.Copyable;
import io.github.juicefries.fct.sign.Readonly;
import io.github.juicefries.fct.util.Util;
import java.util.Objects;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.MemoryUtil;

public final class WindowData implements CallbackData, Copyable,Cloneable, Readonly {


    public final static long WINDOW_CLOSE_EVENT = Util.turn("WindowEvent-%s".formatted(Handler.CLOSE));
    public final static long WINDOW_SIZE_EVENT = Util.turn("WindowEvent-%s".formatted(Handler.SIZE));
    public final static long WINDOW_FRAME_BUFFER_SIZE_EVENT = Util.turn("WindowEvent-%s".formatted(Handler.FRAME_BUFFER_SIZE));
    public final static long WINDOW_FOCUS_EVENT = Util.turn("WindowEvent-%s".formatted(Handler.FOCUS));
    public final static long WINDOW_REFRESH_EVENT = Util.turn("WindowEvent-%s".formatted(Handler.REFRESH));
    public final static long WINDOW_POS_EVENT = Util.turn("WindowEvent-%s".formatted(Handler.POS));
    public final static long WINDOW_CONTENT_SCALE_EVENT = Util.turn("WindowEvent-%s".formatted(Handler.CONTENT_SCALE));
    public final static long WINDOW_MAXIMIZE_EVENT = Util.turn("WindowEvent-%s".formatted(Handler.MAXIMIZE));
    public final static long WINDOW_MINIMIZED_EVENT = Util.turn("WindowEvent-%s".formatted(Handler.MINIMIZED));

    public final static int UNDEFINED_SIZE = -1;

    private long window = MemoryUtil.NULL;
    private long type;
    private int width = -1;
    private int height = -1;

    private int x = 0;
    private int y = 0;

    private float xScale = 1.0f;
    private float yScale = 1.0f;

    private boolean focused = false;
    private boolean restore = true;

    private WindowData() {

    }

    static @NotNull WindowData create(long type) {
        WindowData data = new WindowData();
        data.type = type;
        return data;
    }

    @Contract(value = " -> new", pure = true)
    static @NotNull WindowData create() {
        return new WindowData();
    }

    public static @NotNull WindowData close(long window) {
        WindowData data = create(WINDOW_CLOSE_EVENT);
        data.window = window;
        return data;
    }

    public static @NotNull WindowData close() {
       return close(MemoryUtil.NULL);
    }

    public static @NotNull WindowData size(long window, int width, int height) {
        if (width < 0) {
            throw new IllegalArgumentException("width is less than or equal to 0!");
        }
        if (height < 0) {
            throw new IllegalArgumentException("height is less than or equal to 0!");
        }

        WindowData data = create(WINDOW_SIZE_EVENT);
        data.window = window;
        data.width = width;
        data.height = height;
        return data;
    }

    public static @NotNull WindowData size(int width, int height) {
        return size(MemoryUtil.NULL,width,height);
    }

    public static @NotNull WindowData frameBufferSize(long window, int width, int height) {
        if (width < 0) {
            throw new IllegalArgumentException("width is less than or equal to 0!");
        }
        if (height < 0) {
            throw new IllegalArgumentException("height is less than or equal to 0!");
        }

        WindowData data = create(WINDOW_FRAME_BUFFER_SIZE_EVENT);
        data.window = window;
        data.width = width;
        data.height = height;
        return data;
    }

    public static @NotNull WindowData frameBufferSize(int width, int height) {
        return frameBufferSize(MemoryUtil.NULL,width,height);
    }

    public static @NotNull WindowData focused(long window, boolean focused) {
        WindowData data = create();
        data.window = window;
        data.type = WINDOW_FOCUS_EVENT;
        data.focused = focused;
        return data;
    }

    public static @NotNull WindowData focused(boolean focused) {
        return focused(MemoryUtil.NULL,focused);
    }

    public static @NotNull WindowData refresh(long window) {
        WindowData data = create();
        data.window = window;
        data.type = WINDOW_REFRESH_EVENT;
        return data;
    }

    public static @NotNull WindowData refresh() {
        return refresh(MemoryUtil.NULL);
    }

    public static @NotNull WindowData pos(long window, int x, int y) {
        WindowData data = create();
        data.type = WINDOW_POS_EVENT;
        data.window = window;
        data.x = x;
        data.y = y;
        return data;
    }

    public static @NotNull WindowData pos(int x, int y) {
        return pos(MemoryUtil.NULL,x,y);
    }

    public static @NotNull WindowData contentScale(long window, float xScale, float yScale) {
        WindowData data = create();
        data.window = window;
        data.type = WINDOW_CONTENT_SCALE_EVENT;
        data.xScale = xScale;
        data.yScale = yScale;
        return data;
    }

    public static @NotNull WindowData contentScale(float xScale, float yScale) {
        return contentScale(MemoryUtil.NULL,xScale,yScale);
    }

    public static @NotNull WindowData maximize(long window, boolean restore) {
        WindowData data = create();
        data.type = WINDOW_MAXIMIZE_EVENT;
        data.window = window;
        data.restore = restore;
        return data;
    }

    public static @NotNull WindowData maximize(boolean restore) {
        return maximize(MemoryUtil.NULL,restore);
    }

    public static @NotNull WindowData maximize(long window) {
        return maximize(window,true);
    }

    public static @NotNull WindowData maximize() {
        return maximize(true);
    }

    public static @NotNull WindowData minimized(long window, boolean restore) {
        WindowData data =create();
        data.type = WINDOW_MINIMIZED_EVENT;
        data.window = window;
        data.restore = restore;
        return data;
    }

    public static @NotNull WindowData minimized(boolean restore) {
        return minimized(MemoryUtil.NULL,restore);
    }

    public static @NotNull WindowData minimized(long window) {
        return minimized(window,true);
    }

    public static @NotNull WindowData minimized() {
        return minimized(MemoryUtil.NULL);
    }

    @Contract("_, null -> fail")
    public static @NotNull WindowData check(long window, WindowData data) {
        if (data == null) {
            throw new NullPointerException("data is null!");
        }

        if (data.getWindow() == window) return data;

        WindowData copy = data.copy();
        copy.window = window;
        return copy;
    }

    public static @NotNull WindowData check(Data wud,WindowData data) {
        if (wud == null) {
            throw new NullPointerException("wud is null!");
        }
        if (data == null) {
            throw new NullPointerException("data is null!");
        }
        return check(wud.getWindow(),data);
    }

    @Contract(value = " -> new", pure = true)
    public static long @NotNull [] toEventTypes() {
        return new long[]{
                WINDOW_FOCUS_EVENT,
                WINDOW_FRAME_BUFFER_SIZE_EVENT,
                WINDOW_CLOSE_EVENT,
                WINDOW_REFRESH_EVENT,
                WINDOW_SIZE_EVENT,
                WINDOW_POS_EVENT,
                WINDOW_CONTENT_SCALE_EVENT,
                WINDOW_MAXIMIZE_EVENT,
                WINDOW_MINIMIZED_EVENT
        };
    }

    // ========================= OTM =========================
    // ========================= GET =========================

    public long getWindow() {
        return window;
    }

    @Override
    public long getType() {
        return type;
    }

    public int getWidth() {
        if (getType() != WINDOW_SIZE_EVENT) return UNDEFINED_SIZE;
        return width;
    }

    public int getHeight() {
        if (type != WINDOW_SIZE_EVENT) return UNDEFINED_SIZE;
        return height;
    }

    public int getFrameBufferWidth() {
        if (type != WINDOW_FRAME_BUFFER_SIZE_EVENT) return UNDEFINED_SIZE;
        return width;
    }

    public int getFrameBufferHeight() {
        if (type != WINDOW_FRAME_BUFFER_SIZE_EVENT) return UNDEFINED_SIZE;
        return height;
    }

    public boolean isFocused() {
        if (type != WINDOW_FOCUS_EVENT) return false;
        return focused;
    }

    public int getX() {
        if (type != WINDOW_POS_EVENT) return 0;
        return x;
    }

    public int getY() {
        if (type != WINDOW_POS_EVENT) return 0;
        return y;
    }

    public float getXScale() {
        if (type != WINDOW_CONTENT_SCALE_EVENT) return 1.0f;
        return xScale;
    }

    public float getYScale() {
        if (type != WINDOW_CONTENT_SCALE_EVENT) return 1.0f;
        return yScale;
    }

    public boolean isRestore() {
        if (type != WINDOW_MAXIMIZE_EVENT || type != WINDOW_MINIMIZED_EVENT) return true;
        return restore;
    }

    public boolean isMaximize() {
        if (type != WINDOW_MAXIMIZE_EVENT) return false;
        return !isRestore();
    }

    public boolean isMinimized() {
        if (type != WINDOW_MINIMIZED_EVENT) return false;
        return !isRestore();
    }

    // ========================= =========================


    @Override
    public @NotNull WindowData clone() {
        try {
            WindowData data = (WindowData) super.clone();
            copy(data);
            return data;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("clone failed!",e);
        }
    }

    @Override
    public @NotNull WindowData copy() {
        WindowData data = new WindowData();
        copy(data);
        return data;
    }

    public void copy(WindowData data) {
        if (data == null) {
            throw new NullPointerException("data is null!");
        }
        data.window  = this.window;
        data.type    = this.type;
        data.width   = this.width;
        data.height  = this.height;
        data.x       = this.x;
        data.y       = this.y;
        data.xScale  = this.xScale;
        data.yScale  = this.yScale;
        data.restore = this.restore;
    }

    // ========================= TTT =========================


    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        WindowData data = (WindowData) object;
        return getWindow() == data.getWindow() && getType() == data.getType() && getWidth() == data.getWidth() && getHeight() == data.getHeight() && getX() == data.getX() && getY() == data.getY() && Float.compare(xScale, data.xScale) == 0 && Float.compare(yScale, data.yScale) == 0 && isFocused() == data.isFocused() && isRestore() == data.isRestore();
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getWindow(),
                getType(),
                getWidth(),
                getHeight(),
                getX(),
                getY(),
                getXScale(),
                getYScale(),
                isFocused(),
                isRestore());
    }

    @Override
    public String toString() {
        return "%s[window=%d,type=%d,width=%d,height=%d,x=%d,y=%d,xScale=%s,yScale=%s,focused=%s,restore=%s]".formatted(
                getClass().getCanonicalName(),
                getWindow(),
                getType(),
                getWidth(),
                getHeight(),
                getX(), getY(),
                getXScale(),
                getYScale(),
                isFocused(),
                isRestore());
    }


    // ========================= =========================
}
