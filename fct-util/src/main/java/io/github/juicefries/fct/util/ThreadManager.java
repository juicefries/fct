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
// Data 2026/08/09 00:37
//

package io.github.juicefries.fct.util;

import io.github.juicefries.fct.sign.Initializable;
import io.github.juicefries.fct.sign.Manager;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * <h2>线程管理器</h2>
 *
 * <p>
 *     持有一条工作线程，按固定顺序循环执行：
 *     <br>
 *     帧首任务 → 合并投递 → 一次性任务 → 常驻任务 → 帧尾任务。
 *     <br>
 *     一次性任务经缓冲合并后在下一轮执行一次，
 *     <br>
 *     常驻任务直接登记，每轮执行，直到被移除或清理。
 *     <br>
 *     自{@code 1.0.5}后线程管理器弃用，不好用，但短时间改不了，
 *     <br>
 *     我不想重新设计窗口的线程模型短时间内懒得给窗口直接设计列队，
 *     <br>
 *     所以保留这玩意儿，只是单纯重写等后续再说。
 * </p>
 *
 * @since 0.0.3
 * @author juicefries
 * @version 1.0.2
 * @deprecated 不好用，多余。
 */
@Deprecated(since = "1.0.5",forRemoval = true)
public class ThreadManager implements Initializable,Manager {

    private static final Logger logger = LogManager.getLogger(ThreadManager.class);

    /** 状态锁 */
    private final Lock lock = Lock.create();

    /** 投递缓冲锁 */
    private final Lock bufferLock = Lock.create();

    /** 工作线程独占的一次性任务 */
    private final Queue<Runnable> invokes = new ArrayDeque<>();

    /** 待合并的一次性任务 */
    private final Queue<Runnable> bufferInvoke = new ArrayDeque<>();

    /** 常驻任务，写时复制，可直接跨线程增删 */
    private final CopyOnWriteArrayList<Runnable> everies = new CopyOnWriteArrayList<>();

    /** 帧首任务，仅工作线程访问 */
    private volatile Runnable headTask;
    /** 帧尾任务，仅工作线程访问 */
    private volatile Runnable tailTask;

    private final AtomicBoolean init = new AtomicBoolean(false);
    private final AtomicBoolean end = new AtomicBoolean(true);

    /** 承载任务的线程，仅在它确实结束之后才会置空 */
    private volatile Thread thread;

    public ThreadManager() {

    }

    // ========================= 生命周期 =========================

    /**
     * 初始化并启动工作线程
     * <p>
     *     上一次的线程若仍存活，将拒绝重复初始化。
     * </p>
     * @throws IllegalStateException 已经初始化，或上一次的线程尚未结束
     * @since 0.0.3
     */
    public void initialize() {
        synchronized (lock) {
            if (init.get()) {
                throw new IllegalStateException("ThreadManager has been initialized!");
            }
            var previous = thread;
            if (previous != null && previous.isAlive()) {
                throw new IllegalStateException("The previous thread is still alive! name : " + previous.getName());
            }

            end.set(false);
            init.set(true);
            thread = new Thread(this::loop, "FCT-LT-" + System.nanoTime());
            thread.start();
        }
    }

    /**
     * 停止工作线程并清空全部任务
     * <p>
     *     中断并等待线程结束，超时记录日志，
     *     <br>
     *     线程引用保留到它确实结束为止，便于排查。
     * </p>
     * @since 0.0.3
     */
    public void cleanup() {
        Thread current;
        synchronized (lock) {
            if (end.get()) return;
            end.set(true);
            current = thread;
        }

        // 自己等自己没有意义
        if (current != null && current.isAlive() && Thread.currentThread() != current) {
            current.interrupt();
            try {
                current.join(1000L);
                if (current.isAlive()) {
                    logger.warn("The thread did not exit within the timeout. name : {}", current.getName());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        synchronized (lock) {
            init.set(false);
            if (current != null && !current.isAlive()) {
                thread = null;
            }
            clear();
        }
    }

    @Override
    public void dispose() {
        cleanup();
    }

    /**
     * 工作线程主循环
     * @since 0.0.3
     */
    private void loop() {
        try {
            while (!end.get()) {
                run(headTask);
                merge();
                runInvokes();
                runEveries();
                run(tailTask);
            }
        } finally {
            // 无论因为什么退出，都要让状态与实际一致
            end.set(true);
        }
    }

    /**
     * 合并待投递的一次性任务
     * @since 0.0.3
     */
    private void merge() {
        synchronized (bufferLock) {
            while (!bufferInvoke.isEmpty()) {
                invokes.add(bufferInvoke.poll());
            }
        }
    }

    /**
     * 执行一次性任务，执行后即为空
     * @since 0.0.3
     */
    private void runInvokes() {
        while (!invokes.isEmpty()) {
            if (end.get()) return;
            run(invokes.poll());
        }
    }

    /**
     * 执行常驻任务，遍历但不出队
     * @since 0.0.3
     */
    private void runEveries() {
        for (Runnable task : everies) {
            if (end.get()) return;
            run(task);
        }
    }

    /**
     * 执行单个任务
     * <p>
     *     任务抛出的异常会被记录，不会中断工作线程。
     * </p>
     * @param task 任务
     * @since 0.0.3
     */
    private void run(@Nullable Runnable task) {
        if (task == null) return;
        try {
            task.run();
        } catch (Throwable e) {
            logger.error("An error occurred while executing the task.", e);
        }
    }

    // ========================= 投递 =========================

    /**
     * 投递一次性任务
     * <p>
     *     在下一轮被执行一次，随后移除。
     * </p>
     * @param task 任务
     * @throws NullPointerException 任务为{@code null}
     * @since 0.0.3
     */
    public void invoke(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task is null!");
        }
        synchronized (bufferLock) {
            bufferInvoke.add(task);
        }
    }

    /**
     * 登记常驻任务
     * <p>
     *     自下一轮起每轮执行一次，直到被
     *     <br>
     *     {@link #removeEvery(Runnable)}移除或{@link #cleanup()}清理，
     *     <br>
     *     同一个任务可以重复登记，届时会被执行多次。
     * </p>
     * @param task 任务
     * @throws NullPointerException 任务为{@code null}
     * @since 0.0.3
     */
    public void every(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task is null!");
        }
        everies.add(task);
    }

    /**
     * 移除常驻任务
     * <p>
     *     按引用匹配，重复登记的任务只移除一个，
     *     <br>
     *     正在执行的那一个不会被中断。
     * </p>
     * @param task 任务
     * @throws NullPointerException 任务为{@code null}
     * @since 1.0.2
     */
    public void removeEvery(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task is null!");
        }
        everies.remove(task);
    }

    // ========================= 清理 =========================

    /**
     * 清空全部任务与帧钩子
     * @since 0.0.3
     */
    public void clear() {
        synchronized (bufferLock) {
            invokes.clear();
            bufferInvoke.clear();
        }
        everies.clear();
        headTask = null;
        tailTask = null;
    }

    /**
     * 清空一次性任务
     * @since 0.0.3
     */
    public void clearInvokes() {
        synchronized (bufferLock) {
            invokes.clear();
            bufferInvoke.clear();
        }
    }

    /**
     * 清空常驻任务
     * @since 0.0.3
     */
    public void clearEveries() {
        everies.clear();
    }

    // ========================= SET =========================

    /**
     * 设置工作线程名称
     * @param name 名称
     * @throws NullPointerException 名称为{@code null}
     * @throws IllegalStateException 线程尚未创建
     * @since 0.0.3
     */
    public void setThreadName(String name) {
        if (name == null) {
            throw new NullPointerException("name is null!");
        }
        synchronized (lock) {
            if (thread == null) {
                throw new IllegalStateException("The thread has not been created yet!");
            }
            thread.setName(name);
        }
    }

    /**
     * 设置帧首任务
     * <p>
     *     每轮最开始执行一次，传{@code null}表示清除。
     * </p>
     * @param headTask 任务
     * @since 0.0.3
     */
    public void setHeadTask(@Nullable Runnable headTask) {
        this.headTask = headTask;
    }

    /**
     * 设置帧尾任务
     * <p>
     *     每轮最后执行一次，传{@code null}表示清除。
     * </p>
     * @param tailTask 任务
     * @since 0.0.3
     */
    public void setTailTask(@Nullable Runnable tailTask) {
        this.tailTask = tailTask;
    }

    // ========================= GET =========================

    /**
     * 判断当前线程是否为工作线程
     * @return 是否为工作线程
     * @since 0.0.3
     */
    @Contract(pure = true)
    public boolean isWorkerThread() {
        return Thread.currentThread() == thread;
    }

    public boolean isEnd() {
        return end.get();
    }

    public boolean isRun() {
        return !isEnd();
    }

    @Override
    public boolean isInitialize() {
        return init.get();
    }

    /**
     * 获取待执行的一次性任务数量
     * @return 数量
     * @since 0.0.3
     */
    public int getInvokeSize() {
        return invokes.size();
    }

    /**
     * 获取缓冲中待合并的一次性任务数量
     * @return 数量
     * @since 0.0.3
     */
    public int getBufferInvokeSize() {
        return bufferInvoke.size();
    }

    /**
     * 获取常驻任务数量
     * @return 数量
     * @since 0.0.3
     */
    public int getEverySize() {
        return everies.size();
    }

    /**
     * 获取工作线程名称
     * @return 名称，线程不存在时为{@code null}
     * @since 0.0.3
     */
    public @Nullable String getThreadName() {
        var current = thread;
        return current == null ? null : current.getName();
    }

    /**
     * 获取工作线程
     * @return 线程，可能为{@code null}
     * @since 0.0.3
     */
    @ApiStatus.Experimental
    public @Nullable Thread getThread() {
        return thread;
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName();
    }
}
