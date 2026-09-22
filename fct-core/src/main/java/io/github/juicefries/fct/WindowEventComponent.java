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

package io.github.juicefries.fct;

import io.github.juicefries.fct.event.CursorEnterListener;
import io.github.juicefries.fct.event.WindowContentScaleListener;
import io.github.juicefries.fct.event.WindowFocusListener;
import io.github.juicefries.fct.event.WindowFrameBufferSizeListener;
import io.github.juicefries.fct.event.WindowLifeListener;
import io.github.juicefries.fct.event.WindowMaximizedListener;
import io.github.juicefries.fct.event.WindowMinimizedListener;
import io.github.juicefries.fct.event.WindowMoveListener;
import io.github.juicefries.fct.event.WindowRefreshListener;
import io.github.juicefries.fct.event.WindowRestoredListener;
import io.github.juicefries.fct.event.WindowSizeListener;

/**
 * 窗口事件组件
 * <p>
 *     提供窗口相关事件的注册、移除与获取，
 *     <br>
 *     仅有{@link Window}需要实现。
 * </p>
 * @since 0.0.5
 * @author juicefries
 * @see EventComponent
 * @see Window
 */
public interface WindowEventComponent extends EventComponent {

    // ========================= ADD =========================

    /**
     * 添加窗口生命周期监听器
     * @param l 监听器
     * @since 0.0.5
     */
    void addWindowLifeListener(WindowLifeListener l);

    /**
     * 添加窗口刷新监听器
     * @param l 监听器
     * @since 0.0.5
     */
    void addWindowRefreshListener(WindowRefreshListener l);

    /**
     * 添加窗口焦点监听器
     * @param l 监听器
     * @since 0.0.5
     */
    void addWindowFocusListener(WindowFocusListener l);

    /**
     * 添加窗口移动监听器
     * @param l 监听器
     * @since 0.0.5
     */
    void addWindowMoveListener(WindowMoveListener l);

    /**
     * 添加窗口最大化监听器
     * @param l 监听器
     * @since 0.0.5
     */
    void addWindowMaximizedListener(WindowMaximizedListener l);

    /**
     * 添加窗口最小化监听器
     * @param l 监听器
     * @since 0.0.5
     */
    void addWindowMinimizedListener(WindowMinimizedListener l);

    /**
     * 添加窗口还原监听器
     * @param l 监听器
     * @since 0.0.5
     */
    void addWindowRestoredListener(WindowRestoredListener l);

    /**
     * 添加窗口尺寸监听器
     * @param l 监听器
     * @since 0.0.5
     */
    void addWindowSizeListener(WindowSizeListener l);

    /**
     * 添加窗口帧缓冲大小监听器
     * @param l 监听器
     * @since 0.0.5
     */
    void addWindowFrameBufferSizeListener(WindowFrameBufferSizeListener l);

    /**
     * 添加窗口帧缓冲大小监听器
     * <p>
     *     {@link #addWindowFrameBufferSizeListener(WindowFrameBufferSizeListener)}的简写。
     * </p>
     * @param l 监听器
     * @since 0.0.5
     */
    void addWindowFBSListener(WindowFrameBufferSizeListener l);

    /**
     * 添加窗口内容缩放监听器
     * @param l 监听器
     * @since 0.0.5
     */
    void addWindowContentScaleListener(WindowContentScaleListener l);

    /**
     * 添加窗口内容缩放监听器
     * <p>
     *     {@link #addWindowContentScaleListener(WindowContentScaleListener)}的简写。
     * </p>
     * @param l 监听器
     * @since 0.0.5
     */
    void addWindowCSListener(WindowContentScaleListener l);

    /**
     * 添加光标进入监听器
     * @param l 监听器
     * @since 0.0.5
     */
    void addCursorEnterListener(CursorEnterListener l);

    // ========================= REMOVE =========================

    /**
     * 移除窗口生命周期监听器
     * @param l 监听器
     * @since 0.0.5
     */
    void removeWindowLifeListener(WindowLifeListener l);

    /**
     * 移除窗口刷新监听器
     * @param l 监听器
     * @since 0.0.5
     */
    void removeWindowRefreshListener(WindowRefreshListener l);

    /**
     * 移除窗口焦点监听器
     * @param l 监听器
     * @since 0.0.5
     */
    void removeWindowFocusListener(WindowFocusListener l);

    /**
     * 移除窗口移动监听器
     * @param l 监听器
     * @since 0.0.5
     */
    void removeWindowMoveListener(WindowMoveListener l);

    /**
     * 移除窗口还原监听器
     * @param l 监听器
     * @since 0.0.5
     */
    void removeWindowRestoredListener(WindowRestoredListener l);

    /**
     * 移除窗口最大化监听器
     * @param l 监听器
     * @since 0.0.5
     */
    void removeWindowMaximizedListener(WindowMaximizedListener l);

    /**
     * 移除窗口最小化监听器
     * @param l 监听器
     * @since 0.0.5
     */
    void removeWindowMinimizedListener(WindowMinimizedListener l);

    /**
     * 移除窗口尺寸监听器
     * @param l 监听器
     * @since 0.0.5
     */
    void removeWindowSizeListener(WindowSizeListener l);

    /**
     * 移除窗口帧缓冲大小监听器
     * @param l 监听器
     * @since 0.0.5
     */
    void removeWindowFrameBufferSizeListener(WindowFrameBufferSizeListener l);

    /**
     * 移除窗口内容缩放监听器
     * @param l 监听器
     * @since 0.0.5
     */
    void removeWindowContentScaleListener(WindowContentScaleListener l);

    /**
     * 移除光标进入监听器
     * @param l 监听器
     * @since 0.0.5
     */
    void removeCursorEnterListener(CursorEnterListener l);

    // ========================= GET =========================

    /**
     * 获取窗口生命周期监听器
     * @return 监听器数组
     * @since 0.0.5
     */
    default WindowLifeListener[] getWindowLifeListeners() {
        return getListeners(WindowLifeListener.class);
    }

    /**
     * 获取窗口刷新监听器
     * @return 监听器数组
     * @since 0.0.5
     */
    default WindowRefreshListener[] getWindowRefreshListeners() {
        return getListeners(WindowRefreshListener.class);
    }

    /**
     * 获取窗口焦点监听器
     * @return 监听器数组
     * @since 0.0.5
     */
    default WindowFocusListener[] getWindowFocusListeners() {
        return getListeners(WindowFocusListener.class);
    }

    /**
     * 获取窗口移动监听器
     * @return 监听器数组
     * @since 0.0.5
     */
    default WindowMoveListener[] getWindowMoveListeners() {
        return getListeners(WindowMoveListener.class);
    }

    /**
     * 获取窗口还原监听器
     * @return 监听器数组
     * @since 0.0.5
     */
    default WindowRestoredListener[] getWindowRestoredListeners() {
        return getListeners(WindowRestoredListener.class);
    }

    /**
     * 获取窗口最大化监听器
     * @return 监听器数组
     * @since 0.0.5
     */
    default WindowMaximizedListener[] getWindowMaximizedListeners() {
        return getListeners(WindowMaximizedListener.class);
    }

    /**
     * 获取窗口最小化监听器
     * @return 监听器数组
     * @since 0.0.5
     */
    default WindowMinimizedListener[] getWindowMinimizedListeners() {
        return getListeners(WindowMinimizedListener.class);
    }

    /**
     * 获取窗口尺寸监听器
     * @return 监听器数组
     * @since 0.0.5
     */
    default WindowSizeListener[] getWindowSizeListeners() {
        return getListeners(WindowSizeListener.class);
    }

    /**
     * 获取窗口帧缓冲大小监听器
     * @return 监听器数组
     * @since 0.0.5
     */
    default WindowFrameBufferSizeListener[] getWindowFrameBufferSizeListeners() {
        return getListeners(WindowFrameBufferSizeListener.class);
    }

    /**
     * 获取窗口内容缩放监听器
     * @return 监听器数组
     * @since 0.0.5
     */
    default WindowContentScaleListener[] getWindowContentScaleListeners() {
        return getListeners(WindowContentScaleListener.class);
    }

    /**
     * 获取光标进入监听器
     * @return 监听器数组
     * @since 0.0.5
     */
    default CursorEnterListener[] getCursorEnterListeners() {
        return getListeners(CursorEnterListener.class);
    }
}
