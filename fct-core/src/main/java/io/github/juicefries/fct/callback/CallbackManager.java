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
// Data 2026/08/22 00:29
//

package io.github.juicefries.fct.callback;

import io.github.juicefries.fct.Sys;
import io.github.juicefries.fct.Toolkit;
import io.github.juicefries.fct.glfw._GL_API;
import io.github.juicefries.fct.input.InputManager;
import io.github.juicefries.fct.logging.LoggerFactory;
import io.github.juicefries.fct.sign.ApiSign;
import io.github.juicefries.fct.sign.Initializable;
import io.github.juicefries.fct.sign.Manager;
import io.github.juicefries.fct.util.Lock;
import io.github.juicefries.fct.util.NanoTimer;
import io.github.juicefries.fct.util.Timer;
import io.github.juicefries.fct.util.TraverseList;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryUtil;

/**
 * <h2>回调管理器 </h2>
 *
 * <p>
 *     用于替代{@link InputManager}对于增加一种回调就需要大量重写的操作，
 *     <br>
 *     同时增加{@link #push(CallbackData)}方法，用于推送事件，
 *     <br>
 *     由于线程安全问题，{@link #push(CallbackData)}之后将模拟的事件推送到列表等待执行，
 *     <br>
 *     所有回调的操作基本在{@link #run()}被调用的线程操作，
 *     <br>
 *     可通过{@link Toolkit}替换管理器自身的{@link Handler}类对回调的处理逻辑，
 *     <br>
 *     本身仅关闭窗口本身的回调分发，不对组件层负责。
 * </p>
 *
 * @since  0.0.3
 * @author juicefries
 * @see InputManager
 * @see io.github.juicefries.fct.input
 * @version 0.4
 */
public class CallbackManager implements Manager, Initializable,Runnable {

    //TODO 待完成后集成至Window类

    private final static Logger logger = LoggerFactory.getLogger(CallbackManager.class);

    private final Lock lock = Lock.create();

    protected volatile long window;
    final AtomicBoolean initialize = new AtomicBoolean(false);
    final AtomicBoolean keep = new AtomicBoolean(false);

    // ------------------------- time -------------------------

    private final Timer timer = new NanoTimer();
    private float interval = 5.0f;
    private float time = 0.0f;
    private volatile boolean firstTime = true;

    // ------------------------- handler -------------------------

    private final Handlers handlersClassMap = Toolkit.getHandlers();
    public final Map<String, Handler> handlers = new ConcurrentHashMap<>();

    private Data data = new Data();

    // ------------------------- simulate -------------------------

    volatile TraverseList<CallbackData> simulate = new TraverseList<>(CallbackData.class);
    volatile TraverseList<CallbackData> buffer = new TraverseList<>(CallbackData.class);

    final AtomicReference<ExchangeStatus> status = new AtomicReference<>(ExchangeStatus.ExchangeCompleted);

    // ------------------------- GLFWCallback -------------------------

    private final TraverseList<Callback> callbackList = new TraverseList<>(Callback.class);

    // ------------------------- FCTCallback -------------------------

    GLFWCallbackList callbacks = new GLFWCallbackList();

    // ========================= 构造方法 =========================

    public CallbackManager() {

    }

    // ========================= OTM =========================

    @ApiStatus.Internal
    @ApiStatus.Experimental
    @ApiSign.Dangerous(since = "0.0.3")
    public void bind(long window) {
        Sys.checkInit();
        if (window == MemoryUtil.NULL) {
            throw new IllegalArgumentException("Invalid window!");
        }

        if (this.window != MemoryUtil.NULL) {
            dispose();
        }

        this.window = window;
    }

    @Override
    public void initialize() {
        if (isInitialize()) {
            throw new IllegalStateException("CallbackManager have been initialized!");
        }
        timer.reset();
        register();
        registerHandlers();
        initialize.set(true);
    }

    @Override
    public void run() {
        if (!isInitialize()) return;

        long context = GLFW.glfwGetCurrentContext();
        if (context == MemoryUtil.NULL || context != window) return;

        timer.update();

        float tpf = timer.getTimePerFrame();

        if (firstTime) {
            firstTime = false;
            registerHandlers();
        }

        // ---------- 定时执行回调防止覆盖 ----------
        if (interval >= 0) {                     // 仅当启用时
            time += tpf;                // 每帧累加

            if (time >= interval) {
                // 减去间隔值，保留超出部分避免累计误差
                time -= interval;
                registerHandlers();
            }
        }

        _GL_API._poll_events();
        simulate();            // 处理模拟
    }

    @Override
    public void dispose() {
        synchronized (getLock()) {
            if (!callbackList.isEmpty()) {
                for (Callback callback : callbackList) {
                    if (callback == null) continue;
                    try {
                        callback.free();
                    } catch (Exception e) {
                        logger.error("An error occurred while clearing GLFW callbacks.",e);
                    }
                }
            }

            clear();

            Callbacks.glfwFreeCallbacks(window);
            try {
                for (Handler callback : handlers.values()) {
                    if (callback == null) continue;
                    callback.free();
                }
                synchronized (lock) {
                    handlers.clear();
                }
            } catch (Exception e) {
                logger.error("An error occurred while clearing the callbacks it holds.",e);
            }
            synchronized (lock) {
                buffer.clear();
                simulate.clear();
                data.clear();
            }
            timer.reset();
            window = MemoryUtil.NULL;
            handlersClassMap.clear();
            firstTime = true;
            initialize.set(false);
        }
    }



    public void push(WindowData data) {
        check();
        if (data == null) {
            throw new NullPointerException("data is null!");
        }
        synchronized (getLock()) {
            buffer.add(WindowData.check(window,data.copy()));
            uhd();
        }
    }

    public void push(@NotNull MouseData data) {
        check();
        synchronized (lock) {
            buffer.add(MouseData.check(window,data.copy()));
            uhd();
        }
    }

    public void push(KeyData data) {
        check();
        if (data == null) {
            throw new NullPointerException("data is null!");
        }
        synchronized (getLock()) {
            buffer.add(KeyData.check(window,data.copy()));
            uhd();
        }
    }

    public void push(CallbackData data) {
        if (data == null) {
            throw new NullPointerException("data is null!");
        }
        check();

        if (data instanceof WindowData wd) {
            push(wd);
        }
        if (data instanceof MouseData md) {
            push(md);
        }
        if (data instanceof KeyData kd) {
            push(kd);
        }
    }

    public void updateHandlerData() {
        if (data.isEmpty() || handlers.isEmpty()) return;

        synchronized (lock) {
            List<Handler> handlers = new ArrayList<>(this.handlers.values());
            data.clear();
            data.putBoolean("Keep",isKeep());
            data.putLong("Window",window);
            data.put("GlfwCallbacks",callbackList);
            data.putGLFWCallbackList(callbacks);

            for (var handler : handlers) {
                if (handler == null) continue;
                handler.update(data.copy());
            }
        }
    }

    @ApiStatus.Experimental
    public void uhd() {
        updateHandlerData();
    }

    public <T extends GLFWCallback> void add(Class<T> type,T c) {
        callbacks.add(type,c);
        uhd();
    }

    public <T extends GLFWCallback> void remove(Class<T> type,T c) {
        callbacks.remove(type,c);
        uhd();
    }

    public void clear() {

    }

    // ========================= SET =========================

    public void setInterval(float interval) {
        synchronized (getLock()) {
            this.interval = interval;
            if (interval < 0.0f) {
                time = 0.0f;
            }
        }
    }

    /**
     * 设置是否保留
     *
     * <p>
     *     设置管理器在初始化或每次检查时若拿到非自身的回调时的行为，
     *     <br>
     *     若为{@code true}则检查是否存在于{@link #callbackList}然后决定是否添加，
     *     <br>
     *     在管理器的回调执行时若{@link #isKeep()}为{@code true}则附带执行{@link #callbackList}中的回调。
     * </p>
     *
     * @param keep 保留
     * @since 0.0.3
     */
    public void setKeep(boolean keep) {
        if (isKeep() == keep) return;
        synchronized (getLock()) {
            this.keep.set(keep);
        }
    }

    public void setHandlers(Handlers handlers) {
        if (handlers == null) {
            throw new NullPointerException("handlers is null!");
        }

        synchronized (getLock()) {
            handlersClassMap.clear();
            handlersClassMap.putAll(handlers);
        }
    }

    public void putHandler(String type,Class<? extends Handler> handler) {
        if (handler == null) {
            throw new NullPointerException("handler is null!");
        }
        synchronized (lock) {
            handlersClassMap.put(type,handler);
        }
    }

    public void putHandler(String type,String name) {
        if (type == null) {
            throw new NullPointerException("type is null!");
        }
        if (name == null) {
            throw new NullPointerException("name is null!");
        }
        try {
            Class<?> handlerClass = Class.forName(name);
            if (!Handler.class.isAssignableFrom(handlerClass)) {
                throw new ClassNotFoundException(
                        String.format("Class [%s] is not of type Handler!", name)
                );
            }

            synchronized (getLock()) {
                //noinspection unchecked
                Class<? extends Handler> hc = (Class<? extends Handler>) handlerClass;
                handlersClassMap.put(type,hc);
            }
        } catch (ClassNotFoundException e) {
            logger.error("An error occurred while referencing the class of the registration handler.",e);
        }

    }

    // ========================= GET =========================

    @Override
    public boolean isInitialize() {
        return initialize.get();
    }

    public boolean isKeep() {
        return keep.get();
    }

    protected final Lock getLock() {
        return lock;
    }

    public ExchangeStatus getExchangeStatus() {
        return status.get();
    }

    public int getBuffrSize() {
        return buffer.size();
    }

    public Handlers getHandlers() {
        Handlers handlers = new Handlers(false);
        handlers.putAll(handlersClassMap);
        return handlers;
    }

    // ========================= EEE =========================







    // ========================= ITM =========================

    protected final void register() {

        if (handlersClassMap.isEmpty()) {
            synchronized (getLock()) {
                Handlers handlers = Toolkit.forMultipleAttempts();
                handlersClassMap.putAll(handlers);
            }
        }

        for (String type : handlersClassMap.keySet()) {
            if (type == null) continue;

            Class<? extends Handler> handlerClass = handlersClassMap.get(type);

            if (handlerClass == null) continue;

            try {
                Handler handler = handlerClass.getDeclaredConstructor().newInstance();
                String _type__;
                if (!type.equals(handler.type())) {
                    _type__ = handler.getType();
                } else _type__ = type;

                handlers.put(_type__, handler);
            } catch (InstantiationException e) {
                logger.error("The handler to be initialized is an abstract class.", e);
            } catch (IllegalAccessException e) {
                logger.error("The constructor of the handler to be initialized is private.", e);
            } catch (InvocationTargetException e) {
                logger.error("The handler to be initialized generated an error during initialization.", e);
            } catch (NoSuchMethodException e) {
                logger.error("The handler to be initialized is an interface or something else.", e);
            }
        }
        data = new Data(callbacks,window,isKeep());
    }

    public void registerHandlers() {
        if (handlers.isEmpty()) return;

        synchronized (getLock()) {
            for (Handler handler : handlers.values()) {
                if (handler == null) continue;
                try {
                    uhd();
                    check(handler.register(window));
                } catch (Exception exception) {
                    logger.error(
                            "An error occurred while registering the corresponding handler, handler: {}",
                            handler.getClass().getCanonicalName(),
                            exception
                    );
                }
            }
        }
    }

    protected void simulate() {
        if (!isInitialize()) return;


        status.set(ExchangeStatus.ExchangeBegins);

        synchronized (getLock()) {
            if (!buffer.isEmpty()) {
                status.set(ExchangeStatus.ClearArea);

                simulate.clear();

                status.set(ExchangeStatus.ExchangeEnded);

                simulate.addAll(buffer);

                status.set(ExchangeStatus.ClearBuffer);

                buffer.clear();
            }
        }


        status.set(ExchangeStatus.ExchangeCompleted);


        // 若调用关闭逻辑这里会产生错误 所以检查到已经关闭初始化了就退出

        if (simulate.isEmpty()) return;
        for (CallbackData data : simulate) {
            if (!isInitialize()) return;
            if (data == null) continue;

            for (Handler handler : handlers.values()) {
                if (!isInitialize()) return;
                if (handler == null) continue;
                uhd();
                handler.simulate(data);
            }
        }
        simulate.clear();

    }

    protected void check(Callback callback) {
        if (callback == null) return;

        if (isKeep()) {
            if (!callbackList.contains(callback)) {
                callbackList.add(callback);
            }
        } else {
            callback.free();
        }
    }

    protected void check() {
        if (isInitialize()) return;
        throw new IllegalStateException("Please call this method after the callback manager is initialized.");
    }

    protected void handlerPut(Handler handler) {
        if (handler == null) {
            throw new NullPointerException("handler is null!");
        }
        try {
            handlers.put(handler.getType(),handler);
        } catch (Exception exception) {
            logger.error("An error occurred while handler a specific handler, handler: {}",
                    handler.getClass().getCanonicalName(),
                    exception
            );
        }
    }


    // ========================= RRR =========================

    /**
     * {@code toString}
     *
     * <p>
     *     此方法被重写为返回类名称
     * </p>
     *
     * @return 类的名称
     * @since 0.0.3
     * @see Class#getCanonicalName()
     */
    @Override
    public String toString() {
        return getClass().getCanonicalName();
    }

}
