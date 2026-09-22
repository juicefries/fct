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


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SleepTimer {

    private final transient Lock lock = Lock.create();

    private final List<Runnable> runnables = new ArrayList<>();
    private volatile int delay;
    private final int initialDelay;
    private final AtomicBoolean pause = new AtomicBoolean(false);
    private final AtomicBoolean end = new AtomicBoolean(false);
    private final AtomicBoolean init = new AtomicBoolean(false);
    private Thread thread;

    public SleepTimer(int delay,Runnable task) {
        this.setDelay(delay);
        this.initialDelay = delay;
        this.addInt(task);
    }

    // ========================= OTM =========================

    protected void loop() throws InterruptedException {
        while (!end.get()) {
            if (pause.get()) {
                continue;
            }

            forInts();
            if (delay < 0) {
                synchronized (lock) {
                    delay = 0;
                }
            }
            if (end.get()) {
                return;
            }
            //noinspection BusyWait
            Thread.sleep(delay);
        }

    }

    protected void forInts() {
        if (runnables.isEmpty() || end.get()) {
            return;
        }
        for (Runnable runnable : runnables) {
            if (pause.get()) {
                return;
            }
            if (runnable == null || end.get()) {
                continue;
            }
            synchronized (lock) {
                runnable.run();
            }
        }
    }

    public void start(long delay) {
        if (init.get()) {
            throw new IllegalStateException("Do not initialize the timer repeatedly!");
        }
        init.set(true);
        end.set(false);
        thread = new Thread(() -> {
            try {
                if (delay > 0) {
                    Thread.sleep(delay);
                }
                loop();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
    }

    public void start() {
        this.start(0L);
    }

    public void stop() {
        synchronized (lock) {
            end.set(true);
            init.set(false);
            thread = null;
        }
    }

    public void clear() {
        synchronized (lock) {
            runnables.clear();
        }
    }

    // ========================= SET =========================

    public void setDelay(int delay) {
        if (delay < 0) {
            throw new IllegalArgumentException("The delay cannot be less than 0!");
        }
        synchronized (lock) {
            this.delay = delay;
        }
    }

    public void addInt(Runnable runnable) {
        if (runnable == null) {
            throw new NullPointerException("runnable is null!");
        }
        runnables.add(runnable);
    }

    public void setPause(boolean value) {
        synchronized (lock) {
            pause.set(value);
        }
    }

    // ========================= GET =========================


    public List<Runnable> getRunnables() {
        return runnables;
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

    public int getDelay() {
        return delay;
    }

    public int getInitialDelay() {
        return initialDelay;
    }

    public Thread getThread() {
        return thread;
    }

    public Lock getLock() {
        return lock;
    }

    // ========================= SXD =========================



}
