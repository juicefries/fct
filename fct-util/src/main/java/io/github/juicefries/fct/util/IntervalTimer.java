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

package io.github.juicefries.fct.util;

import java.util.concurrent.atomic.AtomicBoolean;

@Deprecated
public final class IntervalTimer implements Runnable {

    private final transient Lock lock = Lock.create();

    private final TraverseList<IntervalTask> tasks = new TraverseList<>(IntervalTask.class);
    private final NanoTimer timer = new NanoTimer();
    private final float initialDelay;

    private volatile float delay;
    private volatile float time = 0.0f;
    private final AtomicBoolean pause = new AtomicBoolean(false);
    private final AtomicBoolean end = new AtomicBoolean(false);
    private final AtomicBoolean init = new AtomicBoolean(false);
    private Thread thread;

    public IntervalTimer(float delay, IntervalTask task) {
        if (delay < 0) {
            throw new IllegalArgumentException("delay must be >= 0");
        }
        this.delay = delay;
        this.initialDelay = delay;
        this.tasks.add(task);
    }

    public IntervalTimer(float delay) {
        if (delay < 0) {
            throw new IllegalArgumentException("delay must be >= 0");
        }
        this.delay = delay;
        this.initialDelay = delay;
    }

    @Override
    public void run() {
        // 初始化定时器，避免第一帧 tpf 异常
        timer.reset();
        timer.update();

        while (!end.get()) {
            // 检查暂停状态，若暂停则进入等待
            synchronized (lock) {
                while (pause.get() && !end.get()) {
                    try {
                        lock.wait(); // 等待唤醒
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }

            // 再次检查结束标志（可能在等待期间被设置）
            if (end.get()) {
                break;
            }

            timer.update();
            float tpf = timer.getTimePerFrame();

            // 处理延迟
            float currentDelay;
            synchronized (lock) {
                currentDelay = delay;
            }

            if (currentDelay >= 0) {
                time += tpf;
                if (time >= currentDelay) {
                    time = time - currentDelay; // 保留超出部分
                    forInts(tpf);
                }
            } else {
                // delay < 0 表示立即执行（无间隔）
                forInts(tpf);
            }

        }
    }

    private void forInts(float tpf) {
        if (tasks.isEmpty() || end.get()) {
            return;
        }
        // 复制一份任务列表，避免遍历时被修改
        IntervalTask[] snapshot;
        synchronized (lock) {
            snapshot = tasks.toArray(new IntervalTask[0]);
        }
        for (IntervalTask task : snapshot) {
            if (pause.get() || end.get()) {
                break;
            }
            if (task != null) {
                synchronized (lock) {
                    task.accept(tpf);
                }
            }
        }
    }

    // ===================== 生命周期 =====================

    public void start(long delayMillis) {
        if (init.get()) {
            throw new IllegalStateException("Timer already initialized!");
        }
        init.set(true);
        end.set(false);
        pause.set(false);
        time = 0.0f;
        timer.reset();

        thread = new Thread(() -> {
            if (delayMillis > 0) {
                try {
                    Thread.sleep(delayMillis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            run();
        });
        thread.start();
    }

    public void start() {
        this.start(0L);
    }

    public void stop() {
        synchronized (lock) {
            end.set(true);
            pause.set(false);
            lock.notifyAll(); // 唤醒等待线程
        }
        // 等待线程结束
        Thread t = thread;
        if (t != null && t.isAlive()) {
            try {
                t.interrupt();
                t.join(1000); // 最多等待1秒
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
        thread = null;
        init.set(false);
    }

    public void clear() {
        synchronized (lock) {
            tasks.clear();
        }
    }

    // ===================== 控制 =====================

    public void setDelay(float delay) {
        if (delay < 0) {
            throw new IllegalArgumentException("delay must be >= 0");
        }
        synchronized (lock) {
            this.delay = delay;
        }
    }

    public void resetDelay() {
        synchronized (lock) {
            this.delay = initialDelay;
        }
    }

    public void setPause(boolean value) {
        synchronized (lock) {
            pause.set(value);
            if (!value) {
                lock.notifyAll(); // 唤醒等待线程
            }
        }
    }

    public void addTask(IntervalTask task) {
        if (task == null) {
            throw new NullPointerException("task is null");
        }
        synchronized (lock) {
            tasks.add(task);
        }
    }

    public float getRemainingTime() {
        float d;
        synchronized (lock) {
            d = delay;
        }
        if (d < 0) return 0;
        float remain = d - time;
        return remain > 0 ? remain : 0;
    }

    // ===================== 获取属性 =====================

    public TraverseList<IntervalTask> getTasks() {
        return tasks;
    }

    public boolean isEnd() {
        return end.get();
    }

    public boolean isInit() {
        return init.get();
    }

    public boolean isPause() {
        return pause.get();
    }

    public float getDelay() {
        return delay;
    }

    public float getInitialDelay() {
        return initialDelay;
    }

    public Thread getThread() {
        return thread;
    }

    public Lock getLock() {
        return lock;
    }
}