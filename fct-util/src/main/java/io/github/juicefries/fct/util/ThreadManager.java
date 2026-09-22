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

import io.github.juicefries.fct.sign.Manager;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jetbrains.annotations.ApiStatus;

public class ThreadManager implements Manager {

    private final Lock lock = Lock.create();

    private final Lock invokesLock = Lock.create();
    private final Lock waitingLock  =Lock.create();

    private final AtomicBoolean init = new AtomicBoolean(false);
    private final AtomicBoolean end = new AtomicBoolean(true);
    private Thread thread = null;
    private final TraverseArrayList<Runnable> invokes = new TraverseArrayList<>(Runnable.class);
    private final TraverseArrayList<Runnable> screens = new TraverseArrayList<>(Runnable.class);

    private final TraverseArrayList<Runnable> waitingI = new TraverseArrayList<>(Runnable.class);
    private final TraverseArrayList<Runnable> waitingS = new TraverseArrayList<>(Runnable.class);
    private Runnable headTask = () -> {};
    private Runnable tailTask = () -> {};

    public ThreadManager() {

    }

    // ========================= IUM =========================

    public void initialize() {
        if (init.get()) {
            throw new IllegalStateException("ThreadManager has been initialized!");
        }
        end.set(false);
        init.set(true);
        thread = new Thread(this::loop,"FCT-LT-" + System.nanoTime());
        thread.start();
    }

    void loop() {
        while (isRun()) {
            if (headTask != null) {
                synchronized (lock) {
                    if (isEnd()) return;
                    headTask.run();
                }
            }
            execute();
            merge();
            if (tailTask != null) {
                synchronized (lock) {
                    if (isEnd()) return;
                    tailTask.run();
                }
            }
        }
    }

    void execute() {
        executeInvoke();
        executeScreen();
    }

    void executeInvoke() {
        if (invokes.isEmpty()) return;
        for (Runnable task : invokes) {
            if (isEnd()) return;
            if (task == null) continue;
            task.run();
        }
        invokes.clear();
    }

    void executeScreen() {
        if (screens.isEmpty()) return;
        for (Runnable task : screens) {
            if (isEnd()) return;
            if (task == null) continue;
            task.run();
        }
    }

    void merge() {
        if (isEnd()) return;
        if (!waitingI.isEmpty()) {
            synchronized (invokesLock) {
                invokes.addAll(waitingI);
                waitingI.clear();
            }
        }
        if (!waitingS.isEmpty()) {
            synchronized (waitingLock) {
                screens.clear();
                screens.addAll(waitingS);
                waitingS.clear();
            }
        }
    }



    // ========================= OTM =========================

    public void invoke(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task is null!");
        }
        synchronized (lock) {
            waitingI.add(task);
        }
    }

    public void screen(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task is null!");
        }
        synchronized (lock) {
            waitingS.add(task);
        }
    }

    public void resetAll() {
        resetTask();
        resetHeadTask();
        resetTailTask();
    }

    public void resetScreenTask() {
        if (!screens.isEmpty()) {
            synchronized (waitingLock) {
                screens.clear();
            }
        }
        if (!waitingS.isEmpty()) {
            synchronized (waitingLock) {
                waitingS.clear();
            }
        }
    }

    public void resetInvokeTask() {
        if (!invokes.isEmpty()) {
            synchronized (invokesLock) {
                invokes.clear();
            }
        }
        if (!waitingI.isEmpty()) {
            synchronized (invokesLock) {
                waitingI.clear();
            }
        }
    }

    public void resetTask() {
        resetInvokeTask();
        resetScreenTask();
    }

    public void resetHeadTask() {
        synchronized (lock) {
            headTask = () -> {};
        }
    }

    public void resetTailTask() {
        synchronized (lock) {
            tailTask = () -> {};
        }
    }

    public void cleanup() {
        synchronized (lock) {
            end.set(true);
        }
        resetAll();
        synchronized (lock) {
            thread = null;
            init.set(false);
        }
    }

    // ========================= SET =========================

    public void setThreadName(String name) {
        if (name == null) {
            throw new NullPointerException("name is null!");
        }
        synchronized (lock) {
            thread.setName(name);
        }

    }

    public void setHeadTask(Runnable headTask) {
        if (headTask == null) {
            throw new NullPointerException("headTask is null!");
        }
        synchronized (lock) {
            this.headTask = headTask;
        }
    }

    public void setTailTask(Runnable tailTask) {
        if (tailTask == null) {
            throw new NullPointerException("tailTask is null!");
        }
        synchronized (lock) {
            this.tailTask = tailTask;
        }
    }

    // ========================= GET =========================

    public boolean isWorkerThread() {
        synchronized (lock) {
            return Thread.currentThread() == thread;
        }
    }

    public boolean isEnd() {
        return end.get();
    }

    public boolean isRun() {
        return !isEnd();
    }

    public boolean isInit() {
        return init.get();
    }

    public int getInvokesSize() {
        return invokes.size();
    }

    public int getInvokesWaitingSize(){
        return waitingI.size();
    }

    public int getScreenSize() {
        return screens.size();
    }

    public int getScreensWaitingSize(){
        return waitingS.size();
    }

    public String getThreadName() {
        if (thread == null) {
            return null;
        }
        return thread.getName();
    }

    @ApiStatus.Experimental
    public Thread getThread() {
        return thread;
    }

    // ========================= =========================
    // ========================= =========================

    @Override
    public String toString() {
        return getClass().getCanonicalName();
    }


}
