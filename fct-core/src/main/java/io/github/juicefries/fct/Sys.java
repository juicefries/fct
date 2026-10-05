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
// Data 2026/08/10 19:40
//

package io.github.juicefries.fct;

import io.github.juicefries.fct.event.SysEvent;
import io.github.juicefries.fct.event.SystemListener;
import io.github.juicefries.fct.lwjgl.WindowHint;
import io.github.juicefries.fct.sign.ApiSign;
import io.github.juicefries.fct.sign.Manager;
import io.github.juicefries.fct.sign.Uninitialized;
import io.github.juicefries.fct.util.Lock;
import io.github.juicefries.fct.util.TraverseList;
import io.github.juicefries.fct.util.Util;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

/**
 * <h2>Sys</h2>
 *
 * <p>
 *     用于控制{@link io.github.juicefries.fct fct}的全局行为与启用状态。
 *     <br>
 *     同时提供基础的控制API。
 *     <br>
 *     自{@code 1.0.2}后，绝大部分文档我将交给AI生成，基本不检查，就算反映我也不一定搭理。
 * </p>
 *
 * @since 0.0.1
 * @see Toolkit
 * @see GLFW
 * @author juicefries
 */
public final class Sys implements Uninitialized, Manager, Serializable {

    /**
     * 启用危险操作的虚拟机参数名
     * <p>
     *     启动时添加{@code -D_FCT_EDO}即可启用，
     *     <br>
     *     不需要赋值，只要参数存在就视为启用。
     * </p>
     * @since 1.0.1
     */
    public final static String ENABLE_DANGEROUS_OPERATIONS_PROPERTY = "_FCT_EDO";
    public final static int InfiniteTask = -1;
    public final static int DisposableTask = 1;

    /**
     * 猜
     * @since 0.0.2
     */
    @Deprecated(since = "0.0.2")
    private final static byte[] CHAR_1263_BYTE = {
            102, 117, 99, 107, 32, 121, 111, 117, 96, 32, 109, 111, 109
    };

    @Serial
    private final static long serialVersionUID = Util.turn("System");

    private static final Logger sysLog = LogManager.getLogger(Sys.class);
    private static final Logger aetLog = LogManager.getLogger("FCT-AET");

    /**
     * 锁
     * @since 0.0.1
     */
    private final static Lock updateLock = Lock.create();
    private final static Lock taskLock = Lock.create();

    /**
     * 初始化标记
     * @since 0.0.1
     */
    private final static AtomicBoolean initialize = new AtomicBoolean(false);

    /**
     * 系统监听器
     * @since 0.0.3
     */
    final static TraverseList<SystemListener> listeners = new TraverseList<>(SystemListener.class);

    /**
     * 危险操作开关
     * <p>
     *     由虚拟机参数{@link #ENABLE_DANGEROUS_OPERATIONS_PROPERTY}决定，
     *     <br>
     *     未添加该参数时为{@code false}。
     * </p>
     * @since 1.0.1
     */
    private final static boolean _enable_dangerous_operations = System.getProperty(ENABLE_DANGEROUS_OPERATIONS_PROPERTY) != null;

    /**
     * 自动命名用的计数，只用来保证不重名
     * @since 1.0.2
     */
    private final static AtomicLong invokeCounter = new AtomicLong();

    public enum SystemStatus {
        /**
         * 开始
         * @since 1.0.2
         */
        Start,
        /**
         * 进行中
         * @since 1.0.2
         */
        Process,
        /**
         * 完成
         * @since 1.0.2
         */
        Complete,
        /**
         * 无事
         * @since 1.0.2
         */
        Nothing
    }

    // ========================= 参数表 =========================

    static Map<String,Boolean> properties = new HashMap<>();

    // ========================= AET =========================

    static Thread _auxiliary_event_thread;
    static LinkedList<TaskEntity> buffer = new LinkedList<>();
    static LinkedList<TaskEntity> tasks = new LinkedList<>();
    static LinkedList<TaskEntity> discardList = new LinkedList<>();

    // ========================= STA =========================

    static AtomicReference<SystemStatus> initStatus = new AtomicReference<>(SystemStatus.Nothing);

    static {
        _reset_properties();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (isAutomaticCleaning() && isAutomaticShutdown()) {
                if (isInitialize()) {
                    terminate();
                }
            }
        },"FCT-AutomaticStreamCleanup"));
    }

    private Sys(Sys @NotNull [] sysArr) {
        if (sysArr[114514] != this) {
            throw new IllegalArgumentException("我故意的。");
        } else {
            sysLog.trace("你是怎么做到的？");
        }
    }

    // ========================= OTM =========================

    /**
     * 标记任务待移除
     * <p>
     *     只是把引用放进待移除清单，
     *     <br>
     *     真正的清理由AET线程在{@link #discard()}中完成。
     * </p>
     * @param task 目标任务
     * @throws NullPointerException 任务为{@code null}
     * @since 1.0.2
     */
    @Contract("null -> fail")
    public static void markDiscard(TaskEntity task) {
        if (task == null) {
            throw new NullPointerException("task is null!");
        }
        Sys.synchronizedAETTask(() -> discardList.add(task));
    }

    /**
     * 创建任务实体
     * @param name 任务名
     * @param count 执行次数，非正数表示{@link #InfiniteTask}
     * @param task 任务本体
     * @param append {@code true}为追加，{@code false}为延后
     * @return 任务实体
     * @since 1.0.2
     */
    @Contract("null, _, _, _ -> fail; !null, _, null, _ -> fail")
    private static @NotNull TaskEntity invoke(String name, int count, AETTask task, boolean append) {
        if (name == null) {
            throw new NullPointerException("name is null!");
        }
        if (task == null) {
            throw new NullPointerException("task is null!");
        }
        TaskEntity te = new TaskEntity();
        te.name = name;
        if (count > 0) {
            te.count = count;
        } else {
            te.count = Sys.InfiniteTask;
        }
        te.task = task;

        Sys.synchronizedAETTask(() -> {
            if (append) {
                buffer.push(te);
            } else {
                buffer.add(te);
            }
        });
        return te;
    }

    // ========================= 追加 =========================

    /**
     * 追加任务
     * <p>
     *     任务会被插到队列前端，下一轮优先执行，
     *     <br>
     *     与{@link #invokeAppend(String, int, Runnable)}的区别仅在于首次执行的先后。
     * </p>
     * @param name 任务名
     * @param count 执行次数，非正数表示{@link #InfiniteTask}
     * @param task 任务本体
     * @return 任务实体，可用于取消
     * @since 1.0.2
     */
    @Contract("null, _, _ -> fail; !null, _, null -> fail")
    public static @NotNull TaskEntity invokeAppend(String name, int count, AETTask task) {
        return Sys.invoke(name, count, task, true);
    }

    /**
     * 追加任务，只执行一次
     * @param name 任务名
     * @param task 任务本体
     * @return 任务实体，可用于取消
     * @see #invokeAppend(String, int, AETTask)
     * @since 1.0.2
     */
    @Contract("null, _ -> fail; !null, null -> fail")
    public static @NotNull TaskEntity invokeAppend(String name, AETTask task) {
        return Sys.invoke(name, DisposableTask, task, true);
    }

    /**
     * 追加任务，名称自动生成
     * @param count 执行次数，非正数表示{@link #InfiniteTask 无限次}
     * @param task 任务本体
     * @return 任务实体，可用于取消
     * @see #invokeAppend(String, int, AETTask)
     * @since 1.0.2
     */
    public static @NotNull TaskEntity invokeAppend(int count, AETTask task) {
        return Sys.invoke(autoName(true), count, task, true);
    }

    /**
     * 追加任务，名称自动生成且只执行一次
     * @param task 任务本体
     * @return 任务实体，可用于取消
     * @see #invokeAppend(String, int, AETTask)
     * @since 1.0.2
     */
    public static @NotNull TaskEntity invokeAppend(AETTask task) {
        return Sys.invoke(autoName(true), DisposableTask, task, true);
    }

    /**
     * 追加任务
     * <p>
     *     {@link Runnable}会被适配为忽略间隔的{@link AETTask}。
     * </p>
     * @param name 任务名
     * @param count 执行次数，非正数表示{@link #InfiniteTask}
     * @param task 任务本体
     * @return 任务实体，可用于取消
     * @see #invokeAppend(String, int, AETTask)
     * @since 1.0.2
     */
    @Contract("null, _, _ -> fail")
    public static @NotNull TaskEntity invokeAppend(String name, int count, Runnable task) {
        return Sys.invoke(name, count, asTask(task), true);
    }

    /**
     * 追加任务，只执行一次
     * @param name 任务名
     * @param task 任务本体
     * @return 任务实体，可用于取消
     * @see #invokeAppend(String, int, Runnable)
     * @since 1.0.2
     */
    @Contract("null, _ -> fail")
    public static @NotNull TaskEntity invokeAppend(String name, Runnable task) {
        return Sys.invoke(name, DisposableTask, asTask(task), true);
    }

    /**
     * 追加任务，名称自动生成
     * @param count 执行次数，非正数表示{@link #InfiniteTask}
     * @param task 任务本体
     * @return 任务实体，可用于取消
     * @see #invokeAppend(String, int, Runnable)
     * @since 1.0.2
     */
    public static @NotNull TaskEntity invokeAppend(int count, Runnable task) {
        return Sys.invoke(autoName(true), count, asTask(task), true);
    }

    /**
     * 追加任务，名称自动生成且只执行一次
     * @param task 任务本体
     * @return 任务实体，可用于取消
     * @see #invokeAppend(String, int, Runnable)
     * @since 1.0.2
     */
    @SuppressWarnings("UnusedReturnValue")
    public static @NotNull TaskEntity invokeAppend(Runnable task) {
        return Sys.invoke(autoName(true), DisposableTask, asTask(task), true);
    }

    // ========================= 延后 =========================

    /**
     * 延后任务
     * <p>
     *     任务被排到队列末尾，等前面轮完一遍才会首次执行，
     *     <br>
     *     与{@link #invokeAppend(String, int, AETTask)}的区别仅在于首次执行的先后。
     * </p>
     * @param name 任务名
     * @param count 执行次数，非正数表示{@link #InfiniteTask}
     * @param task 任务本体
     * @return 任务实体，可用于取消
     * @since 1.0.2
     */
    @Contract("null, _, _ -> fail; !null, _, null -> fail")
    public static @NotNull TaskEntity invokeLater(String name, int count, AETTask task) {
        return Sys.invoke(name, count, task, false);
    }

    /**
     * 延后任务，只执行一次
     * @param name 任务名
     * @param task 任务本体
     * @return 任务实体，可用于取消
     * @see #invokeLater(String, int, AETTask)
     * @since 1.0.2
     */
    @Contract("null, _ -> fail; !null, null -> fail")
    public static @NotNull TaskEntity invokeLater(String name, AETTask task) {
        return Sys.invoke(name, DisposableTask, task, false);
    }

    /**
     * 延后任务，名称自动生成
     * @param count 执行次数，非正数表示{@link #InfiniteTask 无限次}
     * @param task 任务本体
     * @return 任务实体，可用于取消
     * @see #invokeLater(String, int, AETTask)
     * @since 1.0.2
     */
    public static @NotNull TaskEntity invokeLater(int count, AETTask task) {
        return Sys.invoke(autoName(false), count, task, false);
    }

    /**
     * 延后任务，名称自动生成且只执行一次
     * @param task 任务本体
     * @return 任务实体，可用于取消
     * @see #invokeLater(String, int, AETTask)
     * @since 1.0.2
     */
    public static @NotNull TaskEntity invokeLater(AETTask task) {
        return Sys.invoke(autoName(false), DisposableTask, task, false);
    }

    /**
     * 延后任务
     * <p>
     *     {@link Runnable}会被适配为忽略间隔的{@link AETTask}。
     * </p>
     * @param name 任务名
     * @param count 执行次数，非正数表示{@link #InfiniteTask 无限次}
     * @param task 任务本体
     * @return 任务实体，可用于取消
     * @see #invokeLater(String, int, AETTask)
     * @since 1.0.2
     */
    @Contract("null, _, _ -> fail")
    public static @NotNull TaskEntity invokeLater(String name, int count, Runnable task) {
        return Sys.invoke(name, count, asTask(task), false);
    }

    /**
     * 延后任务，只执行一次
     * @param name 任务名
     * @param task 任务本体
     * @return 任务实体，可用于取消
     * @see #invokeLater(String, int, Runnable)
     * @since 1.0.2
     */
    @Contract("null, _ -> fail")
    public static @NotNull TaskEntity invokeLater(String name, Runnable task) {
        return Sys.invoke(name, DisposableTask, asTask(task), false);
    }

    /**
     * 延后任务，名称自动生成
     * @param count 执行次数，非正数表示{@link #InfiniteTask 无限次}
     * @param task 任务本体
     * @return 任务实体，可用于取消
     * @see #invokeLater(String, int, Runnable)
     * @since 1.0.2
     */
    public static @NotNull TaskEntity invokeLater(int count, Runnable task) {
        return Sys.invoke(autoName(false), count, asTask(task), false);
    }

    /**
     * 延后任务，名称自动生成且只执行一次
     * @param task 任务本体
     * @return 任务实体，可用于取消
     * @see #invokeLater(String, int, Runnable)
     * @since 1.0.2
     */
    @SuppressWarnings("UnusedReturnValue")
    public static @NotNull TaskEntity invokeLater(Runnable task) {
        return Sys.invoke(autoName(false), DisposableTask, asTask(task), false);
    }

    /**
     * 在系统锁内执行任务
     * <p>
     *     执行期间持有{@link #updateLock}，
     *     <br>
     *     用于与初始化、终止等全局操作互斥。
     * </p>
     * @param task 任务
     * @throws NullPointerException 任务为{@code null}
     * @since 1.0.2
     */
    @Contract("null -> fail")
    @ApiStatus.Internal
    @ApiSign.InternalApi(since = "1.0.2")
    public static void synchronizedTask(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task is null!");
        }
        synchronized (updateLock) {
            task.run();
        }
    }

    /**
     * 在系统锁与任务锁内执行任务
     * <p>
     *     相比{@link #synchronizedTask(Runnable)}多持有{@link #taskLock}，
     *     <br>
     *     用于操作AET的缓冲区与待移除清单。
     * </p>
     * @param task 任务
     * @throws NullPointerException 任务为{@code null}
     * @since 1.0.2
     */
    @Contract("null -> fail")
    @ApiStatus.Internal
    @ApiSign.InternalApi(since = "1.0.2")
    public static void synchronizedAETTask(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task is null!");
        }
        synchronized (updateLock) {
            synchronized (taskLock) {
                task.run();
            }
        }
    }

    /**
     * 初始化
     * <p>
     *     初始化GLFW与辅助事件线程，并通知全部系统监听器，
     *     <br>
     *     整个过程在{@link #updateLock}内完成，
     *     <br>
     *     其他线程的调用会被锁挡在外面等待，
     *     <br>
     *     同线程重入则直接返回。
     * </p>
     * @throws IllegalStateException 已经初始化过
     * @throws IllegalStateException GLFW初始化失败
     * @since 0.0.1
     */
    @ApiSign.Dangerous()
    public static void initialize() {
        synchronized (updateLock) {
            if (isInitialize()) {
                throw new IllegalStateException("Repeated initialization call!");
            }

            // 别的线程被锁挡在外面，所以这里非空闲只可能是自己重入
            if (initStatus.get() != SystemStatus.Nothing) {
                return;
            }

            initStatus.set(SystemStatus.Start);
            initStatus.set(SystemStatus.Process);

            try {
                if (!GLFW.glfwInit()) {
                    throw new IllegalStateException("GLFW initialization failed!");
                }
                _reset_properties();
                WindowHint.initialize();
                initializeAET();
                _auxiliary_event_thread.start();

                initialize.set(true);
                listeners.forEach(listener -> listener.event(SysEvent.init()));
                initStatus.set(SystemStatus.Complete);
            } finally {
                initStatus.set(SystemStatus.Nothing);
            }
        }
    }

    /**
     * 终止
     * <p>
     *     停止辅助事件线程、通知监听器、终止GLFW，
     *     <br>
     *     并清空残留的任务队列。
     * </p>
     * @throws IllegalStateException 尚未初始化
     * @since 0.0.1
     */
    @ApiSign.Dangerous
    public static void terminate() {
        synchronized (updateLock) {
            if (!isInitialize()) {
                throw new IllegalStateException("Duplicate termination call!");
            }

            // 同 initialize：别人被锁挡着,非空闲只可能是自己重入
            if (initStatus.get() != SystemStatus.Nothing) {
                return;
            }

            initStatus.set(SystemStatus.Start);
            initStatus.set(SystemStatus.Process);
            initialize.set(false);   // 通知 AET 退出循环
        }

        // 必须在锁外等——AET 每轮都要进 updateLock，持着锁等它等于互相等
        stopAET();

        synchronized (updateLock) {
            try {
                traversalListener();
                GLFW.glfwTerminate();

                Sys.synchronizedAETTask(() -> {
                    buffer.clear();
                    tasks.clear();
                    discardList.clear();
                });
            } finally {
                initStatus.set(SystemStatus.Nothing);
            }
        }
    }

    /**
     * 停止辅助事件线程
     * <p>
     *     等待{@code FCT-AET}退出循环并将其引用置空，
     *     <br>
     *     置空是为了下次初始化能重新创建该线程。
     * </p>
     * @since 1.0.2
     */
    private static void stopAET() {
        var thread = _auxiliary_event_thread;
        if (thread == null) return;

        if (thread.isAlive()) {
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        synchronized (updateLock) {
            _auxiliary_event_thread = null;
        }
    }

    /**
     * 注册系统监听器
     * @param listener 监听器
     * @throws NullPointerException 监听器为{@code null}
     * @since 0.0.3
     */
    @Contract("null -> fail")
    public static void register(SystemListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener is null!");
        }
        checkInit(true);
        synchronized (updateLock) {
            listeners.add(listener);
        }
    }

    /**
     * 注销系统监听器
     * <p>
     *     注销时向该监听器派发取消事件，
     *     <br>
     *     若开启自动关闭且满足关闭条件将触发{@link #terminate()}。
     * </p>
     * @param listener 监听器
     * @throws NullPointerException 监听器为{@code null}
     * @throws IllegalArgumentException 监听器列表为空，或该监听器不存在
     * @see #setAutomaticShutdown(boolean)
     * @since 0.0.3
     */
    @Contract("null -> fail")
    public static void cancel(SystemListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener is null!");
        }
        if (listeners.isEmpty()) {
            throw new IllegalArgumentException("There is no listener to cancel!");
        }
        synchronized (updateLock) {
            if (!listeners.contains(listener)) {
                throw new IllegalArgumentException("Listener does not exist!");
            }
            listeners.remove(listener);
            listener.event(SysEvent.cancel());
            if (isAutomaticShutdown()) {
                var checked = checkClosingConditions();
                if (checked) {
                    terminate();
                }
            }
        }
    }

    /**
     * 检查并初始化
     * <p>
     *     已初始化时直接返回{@code true}，
     *     <br>
     *     未初始化且{@code init}为{@code true}时执行{@link #initialize()}。
     * </p>
     * @param init 未初始化时是否自动初始化
     * @return 是否处于已初始化状态
     * @since 0.0.1
     */
    public static boolean checkInit(boolean init) {
        synchronized (updateLock) {
            if (isInitialize()) return true;
        }
        if (init) {
            synchronized (updateLock) {
                if (!isInitialize()) {
                    initialize();
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 检查并初始化
     * <p>
     *     {@link #checkInit(boolean)}的强制版本，
     *     <br>
     *     始终要求处于已初始化状态。
     * </p>
     * @throws IllegalStateException 检查或初始化过程中发生错误
     * @since 0.0.5
     */
    @ApiStatus.Experimental
    public static void checkInit() {
        try {
            if (!checkInit(true)) {
                throw new IllegalStateException("Still returns false after checking and initializing.");
            }
        } catch (Exception e) {
            throw new IllegalStateException("An error occurred during checking and initialization.",e);
        }
    }

    // ========================= SET =========================

    /**
     * 设置自动关闭
     *
     * <p>
     *     设置在{@link #listeners}为空时是否自动调用{@link #terminate()},
     *     <br>
     *     若为{@code true}则在调用{@link #cancel(SystemListener)}时检查，
     *     <br>
     *     若为{@code true}且{@link #listeners}为空将调用{@link #terminate()}
     * </p>
     *
     * @param automatic 自动
     * @since 0.0.1
     * @see #listeners
     * @see #cancel(SystemListener)
     * @see #terminate()
     */
    public static void setAutomaticShutdown(boolean automatic) {
        synchronized (updateLock) {
            if (isAutomaticShutdown() == automatic) return;
            properties.put("AutomaticShutdown",automatic);
        }
    }

    /**
     * 设置自动清理
     * @param automatic 自动
     * @see #setAutomaticShutdown(boolean)
     * @since 0.0.1
     */
    public static void setAutomaticCleaning(boolean automatic) {
        synchronized (updateLock) {
            if (isAutomaticCleaning() == automatic) return;
            properties.put("AutomaticCleaning",automatic);
        }
    }

    /**
     * 设置初始化标志位
     *
     * <p>
     *     若GLFW已经初始化，但内部标志位还是否可以调用此方法，
     *     <br>
     *     用于在标志位和实际状态不符时调用。
     * </p>
     * @param init 初始化
     * @see #initialize
     * @see #initialize()
     * @see #terminate()
     */
    @ApiStatus.Experimental
    @ApiSign.Dangerous(since = "0.0.1")
    public static void setInit(boolean init) {
        synchronized (updateLock) {
            if (initialize.get() == init) return;
            initialize.set(init);
        }
    }

    /**
     * 调试API调用警告
     * <p>
     *     对于调试API的调用会产生警告输出，
     *     <br>
     *     可通过此方法控制是否输出警告。
     * </p>
     * @param warning 警告
     * @since 0.0.5
     * @see _DEBUG_API
     */
    public static void setDebugApiWarning(boolean warning) {
        synchronized (updateLock) {
            if (isDebugApiWarning() == warning) return;
            properties.put("DebugApiWarning",warning);
        }
    }

    // ========================= GET =========================

    /** 获取自动关闭开关 */
    public static boolean isAutomaticShutdown() {
        return properties.get("AutomaticShutdown");
    }

    /** 获取自动清理开关 */
    public static boolean isAutomaticCleaning() {
        return properties.get("AutomaticCleaning");
    }

    /** 获取初始化状态 */
    public static boolean isInitialize() {
        return initialize.get();
    }

    /**
     * 获取{@link #CHAR_1263_BYTE}的副本
     * @return 常量数组的副本
     * @since 0.0.2
     */
    public static byte[] getChar1263Byte() {
        return CHAR_1263_BYTE.clone();
    }

    /**
     * 获取监听器列表的副本
     * @return 副本，改动它不会影响内部列表
     * @since 0.0.3
     */
    public static TraverseList<SystemListener> getListeners() {
        return listeners.clone();
    }

    /** 获取调试API警告开关 */
    public static boolean isDebugApiWarning() {
        return properties.get("DebugApiWarning");
    }

    /**
     * 获取危险操作开关，由虚拟机参数{@link #ENABLE_DANGEROUS_OPERATIONS_PROPERTY}决定
     * @since 1.0.2
     */
    @Contract(pure = true)
    public static boolean isEnableDangerousOperations() {
        return _enable_dangerous_operations;
    }

    /** 获取当前FCT初始化状态 */
    public static SystemStatus getInitStatus() {
        return initStatus.get();
    }

    // ========================= UTIL =========================

    /**
     * 生成自动名称
     * @param append {@code true} 为追加，{@code false} 为延后
     * @return 形如{@code invokeTask-Append-1}的名称
     * @since 1.0.2
     */
    private static @NotNull String autoName(boolean append) {
        return "invokeTask-%s-%d".formatted(append ? "Append" : "Later", invokeCounter.incrementAndGet());
    }

    /**
     * 把{@link Runnable}适配成{@link AETTask}
     * @param task 任务
     * @return 忽略间隔参数的任务
     * @throws NullPointerException 任务为{@code null}
     * @since 1.0.2
     */
    private static @NotNull AETTask asTask(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task is null!");
        }
        return interval -> task.run();
    }

    static void _reset_properties() {
        properties.put("AutomaticShutdown",true);
        properties.put("AutomaticCleaning",false);
        properties.put("DebugApiWarning",true);
    }

    /**
     * 创建辅助事件线程
     * <p>
     *     已存在且仍存活时不会重复创建。
     * </p>
     * @since 1.0.2
     */
    private static void initializeAET() {
        if (_auxiliary_event_thread == null) {
            _auxiliary_event_thread = new Thread(Sys::loop,"FCT-AET");
        }
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName();
    }


    // ========================= EEE =========================

    /**
     * 辅助事件线程主循环
     * <p>
     *     每轮依次执行任务、合并缓冲区、处理待移除清单。
     * </p>
     * @since 1.0.2
     */
    private static void loop() {
        while (isInitialize()) {
            execute();
            merge();
            synchronized (updateLock) {
                discard();
            }
        }
    }

    /**
     * 处理待移除清单
     * <p>
     *     清单里存的就是任务实体引用，
     *     <br>
     *     直接把次数清零即可，
     *     <br>
     *     任务下次被取出时会因次数为{@code 0}而自行丢弃，
     *     <br>
     *     不需要在队列里查找它。
     * </p>
     * @since 1.0.1
     */
    static void discard() {
        if (discardList.isEmpty()) return;
        Sys.synchronizedAETTask(() -> {
            while (!discardList.isEmpty()) {
                var dte = discardList.poll();
                if (dte == null) continue;
                dte.count = 0;
            }
        });
    }

    /**
     * 执行一轮任务
     * @since 1.0.2
     */
    static void execute() {
        for (int i = 0; i < tasks.size(); i++) {
            var te = tasks.poll();
            if (te == null) continue;
            if (te.count < InfiniteTask) te.count = DisposableTask;

            if (te.count == InfiniteTask) {
                execute(te);
                tasks.add(te);
                te = null;
            }

            if (te != null && te.count == 0) {
                te = null;
            }

            if (te != null && te.count - 1 <= 0) {
                te.count--;
                execute(te);
                te = null;
            }

            if (te == null) continue;

            te.count--;
            execute(te);


            // 放回队列：对方是无限任务（-1）或剩余次数比自己多，就有理由插队，但一次最多插一位
            int size = tasks.size();

            if (size == 0) {
                tasks.add(te);
            } else if (tasks.get(0).count == InfiniteTask || te.count < tasks.get(0).count) {
                tasks.addFirst(te);
            } else if (size > 1 && (tasks.get(1).count == InfiniteTask || te.count < tasks.get(1).count)) {
                tasks.add(1, te);
            } else {
                tasks.add(te);
            }
        }

    }

    static void execute(@NotNull TaskEntity te) {
        try {
            te.run();
        } catch (Exception e) {
            aetLog.error("An error occurred while executing the auxiliary event,event : {} count : {} .",
                    te.name, te.count, e
            );
        }
    }

    /**
     * 合并缓冲区到任务表
     * @since 1.0.2
     */
    static void merge() {
        try {
            synchronized (updateLock) {
                LinkedList<TaskEntity> clone;
                synchronized (taskLock) {
                    if (buffer.isEmpty()) return;
                    //noinspection unchecked
                    clone = (LinkedList<TaskEntity>) buffer.clone();
                    buffer.clear();
                }
                var te = clone.poll();
                tasks.addAll(clone);
                tasks.push(te);
                clone.clear();
            }
        } catch (Exception e) {
            sysLog.error("An error occurred while merging AET's queue.",e);
        }
    }

    /**
     * 辅助事件任务
     * @since 1.0.2
     */
    public interface AETTask {

        /**
         * 执行任务
         * @param interval 距上次执行的间隔，单位纳秒，首次执行为{@code 0}
         * @since 1.0.2
         */
        void execute(long interval);

    }

    public static final class TaskEntity implements Runnable {

        // 名称
        String name;

        // 次数
        volatile int count = Sys.DisposableTask;

        // 上次执行时的时间戳（纳秒，{@code 0} 表示尚未执行）
        long time = 0L;

        AETTask task;

        @Contract(pure = true)
        private TaskEntity() {}

        @Override
        public void run() {
            // 取 run 开始执行的这一刻
            long now = System.nanoTime();
            // 首次执行没有上一次的记录，间隔为 0
            long interval = time == 0L ? 0L : now - time;
            time = now;

            if (task != null) {
                task.execute(interval);
            }
        }

        public String getName() {
            return name;
        }

        public int getCount() {
            return count;
        }

        public long getTime() {
            return time;
        }

        public AETTask getTask() {
            return task;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            TaskEntity that = (TaskEntity) o;
            return getCount() == that.getCount() && getTime() == that.getTime() && Objects.equals(getName(), that.getName()) && Objects.equals(getTask(), that.getTask());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getName(), getCount(), getTime(), getTask());
        }

        @Override
        public @NotNull String toString() {
            return getClass().getCanonicalName()
                    + "[name=" + name
                    + ",count=" + count
                    + ",time=" + time
                    + "task=" + task.toString()
                    + "]";
        }
    }

    /**
     * 检查关闭条件
     * <p>
     *     监听器为空、或其中没有主动型监听器时返回{@code true}。
     * </p>
     * @return 是否满足关闭条件
     * @since 0.0.3
     */
    @ApiSign.InternalApi(since = "0.0.3")
    public static boolean checkClosingConditions() {
        if (listeners.isEmpty()) return true;

        for (var l : listeners) {
            if (l == null) continue;

            if (l.getType() == SystemListener.SysListenerType.Proactive) {
                return false;
            }
        }

        return true;
    }

    /**
     * 向全部监听器派发终止事件并清空列表
     * @since 0.0.3
     */
    static void traversalListener() {
        if (listeners.isEmpty()) return;

        for (SystemListener listener : listeners) {
            if (listener == null) continue;
            listener.event(SysEvent.terminate());
        }
        listeners.clear();
    }

}
