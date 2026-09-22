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
// Data 2026/09/10 15:41
//

package io.github.juicefries.fct.event;

import io.github.juicefries.fct.Toolkit;
import io.github.juicefries.fct.sign.Constant;
import io.github.juicefries.fct.sign.Copyable;
import io.github.juicefries.fct.sign.Readonly;
import io.github.juicefries.fct.sign.Uninitialized;
import io.github.juicefries.fct.util.Util;
import io.github.juicefries.fct.callback.WindowData;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * 窗口事件
 * <p>
 *     窗口事件，简称{@code WED-WindowEventData},
 *     <br>
 *     基础的窗口事件的数据容器。
 * </p>
 * @since 0.0.4
 * @version 1.0
 * @author juicefries
 */
public final class WindowEvent implements EventData,
        Copyable, Cloneable,
        Readonly, Constant,
        Uninitialized, Serializable
{

    @Serial
    private final static long serialVersionUID = -3870179825700029645L;

    static {
        Toolkit.initialize();
    }

    /**
     * 窗口关闭事件
     * @since 0.0.4
     * @see #getType()
     * @see WindowLifeListener
     */
    public final static long WINDOW_CLOSE_EVENT         = Util.turn("WED-Close");

    /**
     * 窗口重绘事件
     * @since 0.0.4
     * @see #getType()
     * @see WindowRefreshListener
     */
    public final static long WINDOW_REFRESH_EVENT       = Util.turn("WED-Refresh");

    /**
     * 窗口焦点事件
     * @since 0.0.4
     * @see #getType()
     * @see #isFocused()
     * @see WindowFocusListener
     */
    public final static long WINDOW_FOCUS_EVENT         = Util.turn("WED-Focus");

    /**
     * 窗口最大化事件
     * @since 0.0.4
     * @see #getType()
     * @see WindowMaximizedListener
     */
    public final static long WINDOW_MAXIMIZE_EVENT      = Util.turn("WED-Maximize");

    /**
     * 窗口最小化事件
     * @since 0.0.4
     * @see #getType()
     * @see WindowMinimizedListener
     */
    public final static long WINDOW_MINIMIZED_EVENT     = Util.turn("WED-Minimized");

    /**
     * 窗口恢复事件
     * @since 0.0.4
     * @see #getType()
     * @see WindowRestoredListener
     */
    public final static long WINDOW_RESTORED_EVENT      = Util.turn("WED-Restored");

    /**
     * 窗口移动事件
     * @since 0.0.4
     * @see #getType()
     * @see #getX()
     * @see #getY()
     * @see WindowMoveListener
     */
    public final static long WINDOW_MOVE_EVENT          = Util.turn("WED-Move");

    /**
     * 窗口尺寸事件
     * @since 0.0.4
     * @see #getType()
     * @see #getWidth()
     * @see #getHeight()
     * @see WindowSizeListener
     */
    public final static long WINDOW_SIZE_EVENT          = Util.turn("WED-Size");

    /**
     * 窗口缓冲区尺寸事件
     * @since 0.0.4
     * @see #getType()
     * @see #getWidth()
     * @see #getHeight()
     * @see WindowFrameBufferSizeListener
     */
    public final static long WINDOW_FRAME_BUFFER_SIZE   = Util.turn("WED-FrameBufferSize");

    /**
     * 窗口内容缩放事件
     * @since 0.0.4
     * @see #getType()
     * @see #getXScale()
     * @see #getYScale()
     * @see WindowContentScaleListener
     */
    public final static long WINDOW_CONTENT_SCALE_EVENT = Util.turn("WED-ContentScale");

    /**
     * 窗口光标进入事件
     * @since 0.0.4
     * @see #getType()
     * @see CursorEnterListener
     */
    public final static long WINDOW_ENTER_EVENT = Util.turn("WED-Enter");

    /**
     * 窗口光标离开事件
     * @since 0.0.4
     * @see #getType()
     * @see CursorEnterListener
     */
    public final static long WINDOW_LEAVE_EVENT = Util.turn("WED-Leave");

    /**
     * 未定义的大小
     * <p>
     *     若窗口事件{@link #getType()}返回值若不为，
     *     <br>
     *     {@link #WINDOW_SIZE_EVENT} 或 {@link #WINDOW_FRAME_BUFFER_SIZE}两个事件，
     *     <br>
     *     {@link #getWidth()}与{@link #getHeight()}则返回该值,
     *     <br>
     *     该值与{@link WindowData}的{@link WindowData#UNDEFINED_SIZE}效果一致，
     *     <br>
     *     均表示为获取到的无效尺寸。
     * </p>
     * @since 0.0.4
     * @see #getType()
     * @see #getWidth()
     * @see #getHeight()
     * @see WindowData#UNDEFINED_SIZE
     */
    public final static int UNDEFINED_SIZE = -1;

    /**
     * 事件类型
     * @since 0.0.4
     * @see #getType()
     */
    private long type;

    /**
     * 窗口焦点
     * @since 0.0.4
     * @see #isFocused()
     */
    private boolean focused;

    /**
     * 窗口X位置
     * @since 0.0.4
     * @see #getX()
     */
    private int x;

    /**
     * 窗口Y位置
     * @since 0.0.4
     * @see #getY()
     */
    private int y;

    /**
     * 窗口宽
     * @since 0.0.4
     * @see #getWidth()
     */
    private int width;

    /**
     * 窗口高
     * @since 0.0.4
     * @see #getHeight()
     */
    private int height;

    /**
     * 窗口缩放X
     * @since 0.0.4
     * @see #getXScale()
     */
    private float xScale;

    /**
     * 窗口缩放Y
     * @since 0.0.4
     * @see #getYScale()
     */
    private float yScale;

    @Contract(pure = true)
    private WindowEvent() {
        // void
    }

    // ---- sta -----

    /**
     * 创建一个窗口事件
     * @param type 事件类型
     * @return 窗口事件雏形
     * @since 0.0.4
     * @see #WINDOW_CLOSE_EVENT
     * @see #WINDOW_REFRESH_EVENT
     * @see #WINDOW_FOCUS_EVENT
     * @see #WINDOW_MAXIMIZE_EVENT
     * @see #WINDOW_MINIMIZED_EVENT
     * @see #WINDOW_RESTORED_EVENT
     * @see #WINDOW_MOVE_EVENT
     * @see #WINDOW_SIZE_EVENT
     * @see #WINDOW_FRAME_BUFFER_SIZE
     * @see #WINDOW_CONTENT_SCALE_EVENT
     * @see #WINDOW_ENTER_EVENT
     * @see #WINDOW_LEAVE_EVENT
     */
    static @NotNull WindowEvent create(long type) {
        var e = new WindowEvent();
        e.type = type;
        return e;
    }

    /**
     * 创建一个窗口关闭事件
     * @return WED-Close
     * @since 0.0.4
     * @see #WINDOW_CLOSE_EVENT
     */
    public static @NotNull WindowEvent close() {
        return create(WINDOW_CLOSE_EVENT);
    }

    /**
     * 创建一个窗口重绘事件
     * @return WED-Refresh
     * @since 0.0.4
     * @see #WINDOW_REFRESH_EVENT
     */
    public static @NotNull WindowEvent refresh() {
        return create(WINDOW_REFRESH_EVENT);
    }

    /**
     * 创建一个窗口焦点事件
     * @param focused 是否获得焦点
     * @return WED-Focus
     * @since 0.0.4
     * @see #WINDOW_FOCUS_EVENT
     * @see #isFocused()
     */
    public static @NotNull WindowEvent focused(boolean focused) {
        var e = create(WINDOW_FOCUS_EVENT);
        e.focused = focused;
        return e;
    }

    /**
     * 创建一个窗口最大化事件
     * @return WED-Maximize
     * @since 0.0.4
     * @see #WINDOW_MAXIMIZE_EVENT
     */
    public static @NotNull WindowEvent maximize() {
        return create(WINDOW_MAXIMIZE_EVENT);
    }

    /**
     * 创建一个窗口最小化事件
     * @return WED-Minimized
     * @since 0.0.4
     * @see #WINDOW_MINIMIZED_EVENT
     */
    public static @NotNull WindowEvent minimized() {
        return create(WINDOW_MINIMIZED_EVENT);
    }

    /**
     * 创建一个窗口恢复事件
     * @return WED-Restored
     * @since 0.0.4
     * @see #WINDOW_RESTORED_EVENT
     */
    public static @NotNull WindowEvent restored() {
        return create(WINDOW_RESTORED_EVENT);
    }

    /**
     * 创建一个窗口移动事件
     * @param x 移动后的X坐标
     * @param y 移动后的Y坐标
     * @return WED-Move
     * @since 0.0.4
     * @see #WINDOW_MOVE_EVENT
     * @see #getX()
     * @see #getY()
     */
    public static @NotNull WindowEvent move(int x, int y) {
        var e = create(WINDOW_MOVE_EVENT);
        e.x = x;
        e.y = y;
        return e;
    }

    /**
     * 创建一个窗口尺寸事件
     * @param width 窗口宽度
     * @param height 窗口高度
     * @return WED-Size
     * @since 0.0.4
     * @throws IllegalArgumentException 宽度或高度小于0
     * @see #WINDOW_SIZE_EVENT
     * @see #getWidth()
     * @see #getHeight()
     */
    public static @NotNull WindowEvent size(int width, int height) {
        if (width < 0) {
            throw new IllegalArgumentException("width is less than or equal to 0!");
        }
        if (height < 0) {
            throw new IllegalArgumentException("height is less than or equal to 0!");
        }

        var e = create(WINDOW_SIZE_EVENT);
        e.width = width;
        e.height = height;
        return e;
    }

    /**
     * 创建一个窗口缓冲区尺寸事件
     * @param width 缓冲区宽度
     * @param height 缓冲区高度
     * @return WED-FrameBufferSize
     * @since 0.0.4
     * @throws IllegalArgumentException 宽度或高度小于0
     * @see #WINDOW_FRAME_BUFFER_SIZE
     * @see #getWidth()
     * @see #getHeight()
     */
    public static @NotNull WindowEvent frameBufferSize(int width, int height) {
        if (width < 0) {
            throw new IllegalArgumentException("width is less than or equal to 0!");
        }
        if (height < 0) {
            throw new IllegalArgumentException("height is less than or equal to 0!");
        }

        var e = create(WINDOW_FRAME_BUFFER_SIZE);
        e.width = width;
        e.height = height;
        return e;
    }

    /**
     * 创建一个窗口内容缩放事件
     * @param xScale X轴缩放
     * @param yScale Y轴缩放
     * @return WED-ContentScale
     * @since 0.0.4
     * @see #WINDOW_CONTENT_SCALE_EVENT
     * @see #getXScale()
     * @see #getYScale()
     */
    public static @NotNull WindowEvent contentScale(float xScale, float yScale) {
        var e = create(WINDOW_CONTENT_SCALE_EVENT);
        e.xScale = xScale;
        e.yScale = yScale;
        return e;
    }

    /**
     * 创建一个窗口光标进入或离开事件
     * @param enter 进入或离开
     * @return WED-Enter | WED-Leave
     * @since 0.0.4
     * @see #WINDOW_LEAVE_EVENT
     * @see #WINDOW_LEAVE_EVENT
     * @see #getType()
     */
    public static @NotNull WindowEvent enter(boolean enter) {
        return create(Util.match(
                enter,
                WINDOW_ENTER_EVENT,
                WINDOW_LEAVE_EVENT
        ));
    }

    /**
     * 获取窗口事件所有事件类型
     * @return 窗口事件的事件类型集合
     * @since 0.0.4
     * @see #WINDOW_CLOSE_EVENT
     * @see #WINDOW_REFRESH_EVENT
     * @see #WINDOW_FOCUS_EVENT
     * @see #WINDOW_MAXIMIZE_EVENT
     * @see #WINDOW_MINIMIZED_EVENT
     * @see #WINDOW_RESTORED_EVENT
     * @see #WINDOW_MOVE_EVENT
     * @see #WINDOW_SIZE_EVENT
     * @see #WINDOW_FRAME_BUFFER_SIZE
     * @see #WINDOW_CONTENT_SCALE_EVENT
     * @see #WINDOW_ENTER_EVENT
     * @see #WINDOW_LEAVE_EVENT
     */
    @Contract(value = " -> new", pure = true)
    public static long @NotNull [] toWEDTypes() {
        return new long[] {
                WINDOW_CLOSE_EVENT,
                WINDOW_REFRESH_EVENT,
                WINDOW_FOCUS_EVENT,
                WINDOW_MAXIMIZE_EVENT,
                WINDOW_MINIMIZED_EVENT,
                WINDOW_RESTORED_EVENT,
                WINDOW_MOVE_EVENT,
                WINDOW_SIZE_EVENT,
                WINDOW_FRAME_BUFFER_SIZE,
                WINDOW_CONTENT_SCALE_EVENT,
                WINDOW_ENTER_EVENT,
                WINDOW_LEAVE_EVENT
        };
    }

    // ==============

    /**
     * 获取窗口事件的事件类型
     * @return 窗口事件类型
     * @since 0.0.4
     * @see #WINDOW_CLOSE_EVENT
     * @see #WINDOW_REFRESH_EVENT
     * @see #WINDOW_FOCUS_EVENT
     * @see #WINDOW_MAXIMIZE_EVENT
     * @see #WINDOW_MINIMIZED_EVENT
     * @see #WINDOW_RESTORED_EVENT
     * @see #WINDOW_MOVE_EVENT
     * @see #WINDOW_SIZE_EVENT
     * @see #WINDOW_FRAME_BUFFER_SIZE
     * @see #WINDOW_CONTENT_SCALE_EVENT
     * @see #WINDOW_ENTER_EVENT
     * @see #WINDOW_LEAVE_EVENT
     */
    @Override
    public long getType() {
        return type;
    }

    /**
     * 获取窗口是否获得焦点
     * @return 若为焦点事件则返回焦点状态，否则返回{@code false}
     * @since 0.0.4
     * @see #WINDOW_FOCUS_EVENT
     */
    public boolean isFocused() {
        if (getType() != WINDOW_FOCUS_EVENT) return false;
        return focused;
    }

    /**
     * 获取窗口移动后的X坐标
     * @return 若为移动事件则返回X坐标，否则返回0
     * @since 0.0.4
     * @see #WINDOW_MOVE_EVENT
     */
    public int getX() {
        if (getType() != WINDOW_MOVE_EVENT) return 0;
        return x;
    }

    /**
     * 获取窗口移动后的Y坐标
     * @return 若为移动事件则返回Y坐标，否则返回0
     * @since 0.0.4
     * @see #WINDOW_MOVE_EVENT
     */
    public int getY() {
        if (getType() != WINDOW_MOVE_EVENT) return 0;
        return y;
    }

    /**
     * 获取窗口宽度
     * @return 若为尺寸事件或缓冲区尺寸事件则返回宽度，否则返回{@link #UNDEFINED_SIZE}
     * @since 0.0.4
     * @see #WINDOW_SIZE_EVENT
     * @see #WINDOW_FRAME_BUFFER_SIZE
     * @see #UNDEFINED_SIZE
     */
    public int getWidth() {
        if (getType() == WINDOW_SIZE_EVENT || getType() == WINDOW_FRAME_BUFFER_SIZE) {
            return width;
        }
        return UNDEFINED_SIZE;
    }


    /**
     * 获取窗口高度
     * @return 若为尺寸事件或缓冲区尺寸事件则返回高度，否则返回{@link #UNDEFINED_SIZE}
     * @since 0.0.4
     * @see #WINDOW_SIZE_EVENT
     * @see #WINDOW_FRAME_BUFFER_SIZE
     * @see #UNDEFINED_SIZE
     */
    public int getHeight() {
        if (getType() == WINDOW_SIZE_EVENT || getType() == WINDOW_FRAME_BUFFER_SIZE) {
            return height;
        }
        return UNDEFINED_SIZE;
    }

    /**
     * 获取X轴缩放
     * @return 若为内容缩放事件则返回X轴缩放，否则返回1.0
     * @since 0.0.4
     * @see #WINDOW_CONTENT_SCALE_EVENT
     */
    public float getXScale() {
        if (getType() != WINDOW_CONTENT_SCALE_EVENT) return 1.0f;
        return xScale;
    }


    /**
     * 获取Y轴缩放
     * @return 若为内容缩放事件则返回Y轴缩放，否则返回1.0
     * @since 0.0.4
     * @see #WINDOW_CONTENT_SCALE_EVENT
     */
    public float getYScale() {
        if (getType() != WINDOW_CONTENT_SCALE_EVENT) return 1.0f;
        return yScale;
    }

    /**
     * 复制窗口事件数据到指定事件
     * @param e 目标事件
     * @since 0.0.4
     * @throws NullPointerException 目标事件不能为{@code null}
     */
    public void copy(WindowEvent e) {
        if (e == null) throw new NullPointerException("e is null!");
        e.type = type;
        e.focused = isFocused();
        e.x = getX();
        e.y = getY();
        e.width = getWidth();
        e.height = getHeight();
        e.xScale = getXScale();
        e.yScale = getYScale();
    }

    /**
     * 复制窗口事件
     * @return 窗口事件副本
     * @since 0.0.4
     */
    @Override
    public @NotNull WindowEvent copy() {
        var e = new WindowEvent();
        copy(e);
        return e;
    }

    /**
     * 克隆窗口事件
     * @return 窗口事件副本
     * @since 0.0.4
     */
    @Override
    public WindowEvent clone() {
        try {
            WindowEvent e = (WindowEvent) super.clone();
            copy(e);
            return e;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("clone failed!", e);
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        WindowEvent that = (WindowEvent) object;
        return getType() == that.getType()
                && isFocused() == that.isFocused()
                && getX() == that.getX()
                && getY() == that.getY()
                && getWidth() == that.getWidth()
                && getHeight() == that.getHeight()
                && Float.compare(getXScale(), that.getXScale()) == 0
                && Float.compare(getYScale(), that.getYScale()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getType(), isFocused(),
                getX(), getY(),
                getWidth(), getHeight(),
                getXScale(), getYScale()
        );
    }

    @Override
    public String toString() {
        //noinspection SpellCheckingInspection
        return getClass().getCanonicalName() + "["
                + "type=" + getType()
                + ",focused=" + isFocused()
                + ",x=" + getX()
                + ",y=" + getY()
                + ",width=" + getWidth()
                + ",height=" + getHeight()
                + ",xScale=" + getXScale()
                + ",yScale=" + getYScale()
                + "]";
    }
}
