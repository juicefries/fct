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
// Data 2026/10/06 03:01
//

package io.github.juicefries.fct;

import io.github.juicefries.fct.event.ActionListener;
import io.github.juicefries.fct.event.EventData;
import io.github.juicefries.fct.event.EventListenerList;

/**
 * FCT 定时器
 * <p>
 *     行为参照{@link FTimer}，但不自己开线程，
 *     <br>
 *     而是把计时逻辑作为常驻任务交给{@link Sys}的辅助事件线程，
 *     <br>
 *     因此回调执行在AET线程上，而不是调用{@link #start()}的线程，
 *     <br>
 *     回调中若需操作界面，请通过{@link Window#invoke(Runnable)}回到窗口线程。
 * </p>
 * @since 1.0.2
 * @author ds(AI)
 * @see FTimer
 * @see Sys
 */
public class Timer {

    /** 每秒的纳秒数 */
    private final static long NANOS_PER_SECOND = 1_000_000_000L;

    /** 监听器列表 */
    private final EventListenerList listenerList = new EventListenerList();

    /** 初始间隔（秒） */
    private final float initialDelay;

    /** 间隔时长（秒） */
    private volatile float delay;

    /** 暂停标记 */
    private volatile boolean pause;

    /** 运行标记 */
    private volatile boolean running;

    /** 触发次数 */
    private volatile long count;

    /** 累计运行时长（纳秒） */
    private volatile long totalTime;

    /** 累计未触发的时间（纳秒），仅AET线程访问 */
    private long elapsed;

    /** 提交给AET的任务实体 */
    private Sys.TaskEntity entity;

    /**
     * 以指定间隔创建定时器，并添加默认监听器
     * @param delay 间隔时长（秒），必须 >= 0
     * @param listener 默认监听器
     * @throws IllegalArgumentException 间隔小于 0
     * @since 1.0.2
     */
    public Timer(float delay, ActionListener listener) {
        if (delay < 0) {
            throw new IllegalArgumentException("delay must be >= 0");
        }
        this.delay = delay;
        this.initialDelay = delay;
        if (listener != null) {
            listenerList.add(ActionListener.class, listener);
        }
    }

    /**
     * 以指定间隔创建定时器
     * @param delay 间隔时长（秒），必须 >= 0
     * @throws IllegalArgumentException 间隔小于 0
     * @since 1.0.2
     */
    public Timer(float delay) {
        this(delay, null);
    }

    // ========================= 生命周期 =========================

    /**
     * 启动定时器
     * <p>
     *     将一个无限次任务提交给AET，之后由AET每轮调用，
     *     <br>
     *     重复启动将抛出异常。
     * </p>
     * @throws IllegalStateException 已经启动
     * @since 1.0.2
     */
    public void start() {
        if (running) {
            throw new IllegalStateException("Timer already started!");
        }
        elapsed = 0L;
        totalTime = 0L;
        count = 0L;
        pause = false;
        running = true;
        entity = Sys.invokeLater(Sys.InfiniteTask, this::tick);
    }

    /**
     * 停止定时器
     * <p>
     *     把任务标记为待移除，AET在下一轮取出它时会直接丢弃，
     *     <br>
     *     未启动时不做任何事。
     * </p>
     * @since 1.0.2
     */
    public void stop() {
        if (!running) return;
        running = false;

        var te = entity;
        entity = null;
        if (te != null) {
            Sys.markDiscard(te);
        }
    }

    // ========================= 控制 =========================

    /**
     * 设置间隔时长
     * @param delay 间隔时长（秒），必须 >= 0
     * @throws IllegalArgumentException 间隔小于 0
     * @since 1.0.2
     */
    public void setDelay(float delay) {
        if (delay < 0) {
            throw new IllegalArgumentException("delay must be >= 0");
        }
        this.delay = delay;
    }

    /**
     * 恢复为创建时的间隔时长
     * @since 1.0.2
     */
    public void resetDelay() {
        this.delay = initialDelay;
    }

    /**
     * 设置暂停
     * <p>
     *     暂停期间不再累计时间，恢复后从暂停处继续计时。
     * </p>
     * @param value 暂停
     * @since 1.0.2
     */
    public void setPause(boolean value) {
        pause = value;
    }

    // ========================= LISTENER =========================

    /**
     * 添加监听器
     * @param l 监听器
     * @since 1.0.2
     */
    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }

    /**
     * 移除监听器
     * @param l 监听器
     * @since 1.0.2
     */
    public void removeActionListener(ActionListener l) {
        listenerList.remove(ActionListener.class, l);
    }

    /**
     * 获取全部监听器
     * @return 监听器数组
     * @since 1.0.2
     */
    public ActionListener[] getActionListeners() {
        return listenerList.getListeners(ActionListener.class);
    }

    // ========================= GET =========================

    /** 获取当前间隔时长（秒） */
    public float getDelay() { return delay; }

    /** 获取初始间隔时长（秒） */
    public float getInitialDelay() { return initialDelay; }

    /** 获取运行状态 */
    public boolean isRunning() { return running; }

    /** 获取暂停状态 */
    public boolean isPause() { return pause; }

    /** 获取触发次数 */
    public long getCount() { return count; }

    /** 获取累计运行时长（秒） */
    public float getTotalTime() { return totalTime / (float) NANOS_PER_SECOND; }

    // ========================= UTIL =========================

    /**
     * 由AET每轮调用
     * @param interval 距上次执行的间隔，单位纳秒，首次执行为{@code 0}
     * @since 1.0.2
     */
    private void tick(long interval) {
        if (!running) return;
        if (interval <= 0) return;

        totalTime += interval;
        if (pause) return;

        elapsed += interval;

        long period = (long) (delay * NANOS_PER_SECOND);
        if (period <= 0) return;

        while (elapsed >= period) {
            elapsed -= period;
            fire();
        }
    }

    /**
     * 触发监听器
     * @since 1.0.2
     */
    private void fire() {
        ActionListener[] listeners = listenerList.getListeners(ActionListener.class);
        EventData e = FTimer.TimerEvent.timer(
                delay,
                totalTime / (float) NANOS_PER_SECOND,
                ++count,
                System.nanoTime()
        );
        for (ActionListener l : listeners) {
            if (l != null) {
                l.action(e);
            }
        }
    }
}

