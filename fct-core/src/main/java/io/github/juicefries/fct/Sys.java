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
import io.github.juicefries.fct.logging.LoggerFactory;
import io.github.juicefries.fct.sign.ApiSign;
import io.github.juicefries.fct.sign.Manager;
import io.github.juicefries.fct.sign.Uninitialized;
import io.github.juicefries.fct.util.Array;
import io.github.juicefries.fct.util.Lock;
import io.github.juicefries.fct.util.TraverseList;
import io.github.juicefries.fct.util.Util;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.glfw.GLFW;

/**
 * <h2>Sys</h2>
 *
 * <p>
 *     用于控制{@link io.github.juicefries.fct fct}的全局行为与启用状态。
 *     <br>
 *     同时提供基础的控制API。
 * </p>
 *
 * @since 0.0.1
 * @see Toolkit
 * @see GLFW
 * @author juicefries
 */
public final class Sys implements Uninitialized, Manager, Serializable {

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

    private static final Logger logger = LoggerFactory.getLogger(Sys.class);

    /**
     * 锁
     * @since 0.0.1
     */
    private final static Lock updateLock = Lock.create();

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
     * 调试API调用警告
     * @since 0.0.5
     */
    private final static AtomicBoolean _debug_api_warning = new AtomicBoolean(true);


    static Map<String,Boolean> properties = new HashMap<>();

    static void _reset_properties() {
        properties.put("AutomaticShutdown",true);
        properties.put("AutomaticCleaning",false);
    }

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

    // ========================= OTM =========================

    public static void initialize() {
        if (isInitialize()) {
            throw new IllegalStateException("Repeated initialization call!");
        }
        synchronized (updateLock) {
            if (!GLFW.glfwInit()) {
                throw new IllegalStateException("GLFW initialization failed!");
            }
            initialize.set(true);
            listeners.forEach(listener -> listener.event(SysEvent.init()));
        }
    }

    public static void terminate() {
        if (!isInitialize()) {
            throw new IllegalStateException("Duplicate termination call!");
        }

        traversalListener();
        GLFW.glfwTerminate();
        initialize.set(false);
    }

    public static void register(SystemListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener is null!");
        }
        checkInit(true);
        synchronized (updateLock) {
            listeners.add(listener);
        }
    }

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
            if (_debug_api_warning.get() == warning) return;
            _debug_api_warning.set(warning);
        }
    }

    // ========================= GET =========================

    public static boolean isAutomaticShutdown() {
        return properties.get("AutomaticShutdown");
    }

    public static boolean isAutomaticCleaning() {
        return properties.get("AutomaticCleaning");
    }

    public static boolean isInitialize() {
        return initialize.get();
    }

    public static byte[] getChar1263Byte() {
        return CHAR_1263_BYTE.clone();
    }

    public static TraverseList<SystemListener> getListeners() {
        return listeners.clone();
    }

    public static boolean isDebugApiWarning() {
        return _debug_api_warning.get();
    }

    // ========================= EEE =========================

    /**
     * 真的会有人乱搞吗？
     * @param sys sys
     * @since 0.0.3
     */
    @Deprecated(since = "0.0.3",forRemoval = true)
    private Sys(Sys[] sys) {
        if (sys == null) {
            throw new NullPointerException("sys is null!");
        }
        Array.requireArrayLengthEqualTo(sys,0);
        char[] chars = {'S','Y','S'};
        int i = 0;
        for (char c : chars) {
            i += c;
        }

        System.exit(i);
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName();
    }


    // ========================= EEE =========================

    private static boolean checkClosingConditions() {
        if (listeners.isEmpty()) return true;

        for (var l : listeners) {
            if (l == null) continue;

            if (l.getType() == SystemListener.SysListenerType.Proactive) {
                return false;
            }
        }

        return true;
    }

    private static void traversalListener() {
        if (listeners.isEmpty()) return;

        for (SystemListener listener : listeners) {
            if (listener == null) continue;
            listener.event(SysEvent.terminate());
        }
        listeners.clear();
    }

}
