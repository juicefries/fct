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
// Data 2026/11/07 17:16
//

package io.github.juicefries.fct.callback;

import io.github.juicefries.fct.Toolkit;
import io.github.juicefries.fct.callback.event.CallbackEventData;
import io.github.juicefries.fct.callback.handler.Handler;
import io.github.juicefries.fct.callback.listener.CallbackListener;
import io.github.juicefries.fct.lwjgl.GLFWUtil;
import io.github.juicefries.fct.lwjgl._GL_API;
import io.github.juicefries.fct.sign.ApiSign;
import io.github.juicefries.fct.sign.CopyFailedException;
import io.github.juicefries.fct.sign.Initializable;
import io.github.juicefries.fct.sign.Readonly;
import io.github.juicefries.fct.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.Callback;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static org.lwjgl.system.MemoryUtil.*;

public final class Container implements Initializable,Runnable {

    private static final Logger log = LogManager.getLogger(Container.class);
    final Lock ccl = Lock.create("ContextCheckLock");
    final Lock sel = Lock.create("SimulateEventLock");
    final Lock hsl = Lock.create("HandlerStatusLock");
    final Lock cil = Lock.create("ContainerInitLock");
    final Lock spl = Lock.create("StatusParameterLock");

    long window = NULL;

    List<CallbackListener> listeners = List.build();
    List<CallbackEventData> events = List.build();
    List<CallbackEventData> buffer = List.build();

    Map<Class<? extends Callback>, Handler> handlers = new ConcurrentHashMap<>();

    final AtomicBoolean initialize = new AtomicBoolean(false);

    Map<String,BehaviorParameter<?>> status = new ConcurrentHashMap<>();

    private void resetImp() {
        status.put(Event.IME_OPEN, builder(true)
                .initialValue(true)
                .defaultValue(true)
                .inquiry(() -> GLFWUtil.getIME(getWindow()))
                .overwrite(bp -> {
                    var actualValue = bp.getActualValue();
                    var value = bp.get();
                    if (actualValue != value) {
                        GLFWUtil.setIME(getWindow(), value);
                        return new BehaviorParameter.ModifyTo<>(actualValue, value);
                    }
                    return null;
                }).result()
        );
    }

    {
        reset();
    }

    Container() {

    }

    public BehaviorParameter<Boolean> getIME() {
        synchronized (spl) {
            //noinspection unchecked
            return (BehaviorParameter<Boolean>) status.get(Event.IME_OPEN);
        }
    }

    public BehaviorParameter<?> getBehaviorParameter(String key) {
        synchronized (spl) {
            return status.get(key);
        }
    }

    public void reset() {
        synchronized (spl) {
            resetImp();
        }
    }

    public void bind(long window) {
        if (window == NULL) {
            throw new IllegalArgumentException("Invalid window!");
        }
        this.window = window;
    }

    public long getWindow() {
        return window;
    }

    @Override
    public void initialize() {
        synchronized (cil) {
            if (isInitialize()) {
                throw new IllegalArgumentException("Repeated initialization!");
            }
            if (window == NULL) {
                throw new IllegalArgumentException("Unbound window!");
            }

            Handlers handlers;
            var defHandlers = Toolkit.getHandlers();
            if (defHandlers.isEmpty()) {
                handlers = Toolkit.getDefaultHandlers();
            } else {
                handlers = defHandlers;
            }

            for (var type : handlers.keySet()) {
                if (type == null) continue;
                var hc = handlers.get(type);
                if (hc == null) continue;

                try {
                    Handler handler = Util.newInstance(hc);

                    if (this.handlers.containsKey(type)) {
                        var h = this.handlers.get(type);
                        if (h != null) {
                            log.warn("The type[{}] position of container[{}] has already been registered, handler[{}] has been skipped.",
                                    type.getCanonicalName(),
                                    toString(),hc.getCanonicalName()
                            );
                            continue;
                        }
                    }
                    this.handlers.put(type,handler);
                    log.trace("Successfully initialized Handler[{}] for container[{}].",
                            toString(), handler.getClass().getCanonicalName()
                    );
                } catch (Exception e) {
                    log.error("An error occurred while initializing the Handler[{}] of container[{}].",
                            this.toString(), hc.getCanonicalName(), e
                    );
                }
            }

            update();

            initialize.set(true);
        }
    }

    public void update() {
        for (var handler : handlers.values()) {
            handler.register(window);
        }
    }

    @Override
    public boolean isInitialize() {
        synchronized (cil) {
            return initialize.get();
        }
    }

    @Override
    public void dispose() {
        synchronized (cil) {
            if (!isInitialize()) return;
            synchronized (sel) {
                buffer.clear();
                events.clear();
            }

            Callbacks.glfwFreeCallbacks(window);

            synchronized (hsl) {
                for (var handler : handlers.values()) {
                    if (handler == null) continue;
                    try {
                        handler.free();
                    } catch (Exception e) {
                        log.error("An error occurred in container[{}] when freeing handler[{}].",
                                toString(), handler.getClass().getCanonicalName(), e
                        );
                    }
                }
                handlers.clear();
            }

            listeners.clear();
            reset();
            window = NULL;
            initialize.set(false);
        }
    }

    @Override
    public void run() {
        synchronized (spl) {
            java.util.List<String> list = new ArrayList<>();
            for (var key : status.keySet()) {
                if (key == null) continue;
                var val = status.get(key);
                if (val == null) {
                    list.add(key);
                    continue;
                }
                val.overwrite();
            }
            list.forEach(key -> status.remove(key));
        }

        synchronized (ccl) {
            check();
            _GL_API._poll_events();
        }

        synchronized (sel) {
            try {
                //noinspection removal
                buffer.copy(events);
            } catch (CopyFailedException e) {
                throw new RuntimeException("An error occurred while merging buffers!",e);
            }
            buffer.clear();
        }

        if (events.isEmpty()) return;
        synchronized (hsl) {
            events.forList((elements) -> {
                var event = elements.copy();
                if (event == null) return;
                event.correction(window);
                var types = event.getRequiredTypes();
                if (types.length == 0) return;
                if (types.length == 1) {
                    var handler = handlers.get(types[0]);
                    handler.simulate(event);
                    return;
                }
                for (var type : types) {
                    var handler = handlers.get(type);
                    if (handler == null) continue;
                    handler.simulate(event);
                }
            });
        }

        events.clear();
    }

    @Contract(pure = true)
    public List<CallbackEventData> getEvents() {
        synchronized (sel) {
            return events;
        }
    }

    public <T extends CallbackEventData> T[] getEvents(Class<T> type) {
        return getEvents().getElements(type);
    }

    @Contract(pure = true)
    public List<CallbackListener> getListeners() {
        return listeners;
    }

    public <T extends CallbackListener> T[] getListeners(Class<T> type) {
        return getListeners().getElements(type);
    }



    @Contract("null, _ -> fail; !null, null -> fail")
    public Handler put(Class<? extends Callback> type, Handler handler) {
        if (type == null) {
            throw new NullPointerException("type is null!");
        }
        if (handler == null) {
            throw new NullPointerException("handler is null!");
        }
        synchronized (hsl) {
            var result = handlers.getOrDefault(type, null);
            handlers.put(type, handler);
            return result;
        }
    }

    public void check() {
        if (window == NULL) {
            throw new IllegalArgumentException("Invalid window!");
        }
        long context = GLFW.glfwGetCurrentContext();
        if (context == NULL) {
            throw new IllegalArgumentException();
        }
        if (window != context) {
            var message = "Mismatched context! window[%d] context[%d]".formatted(window, context);
            throw new IllegalStateException(message);
        }
    }

    @Override
    public @NotNull String toString() {
        return getClass().getCanonicalName() + "[" + window + "]";
    }

    public interface BehaviorParameter<V> extends Readonly {
        V get();
        V getActualValue();
        V set(V value);
        V defaultValue();
        V initialValue();
        void reset();
        void overwrite();
        ModifyTo<V> modifyTo();
        record ModifyTo<T>(T old,T news) { }
    }

    public static final class BehaviorParameterBuilder<V> {

        private DefaultBehaviorParameter<V> behaviorParameter;

        @Contract(pure = true)
        private BehaviorParameterBuilder() {

        }

        @Contract("_ -> this")
        public BehaviorParameterBuilder<V> initialValue(V value) {
            behaviorParameter.initialValue = value;
            behaviorParameter.value = value;
            return this;
        }

        @Contract("_ -> this")
        public BehaviorParameterBuilder<V> defaultValue(V value) {
            behaviorParameter.defaultValue = value;
            return this;
        }

        @Contract("null -> fail")
        public BehaviorParameterBuilder<V> inquiry(Ret<V> inquiry) {
            if (inquiry == null) {
                throw new NullPointerException("inquiry is null!");
            }

            behaviorParameter.inquiry = inquiry;
            return this;
        }

        @Contract("null -> fail")
        public BehaviorParameterBuilder<V> overwrite(Function<BehaviorParameter<V>, BehaviorParameter.ModifyTo<V>> overwrite) {
            if (overwrite == null) {
                throw new NullPointerException("overwrite is null!");
            }
            behaviorParameter.apply = overwrite;
            return this;
        }

        @Contract(pure = true)
        public BehaviorParameter<V> result() {
            var value = behaviorParameter;
            behaviorParameter = null;
            return value;
        }

        static final class DefaultBehaviorParameter<T> implements BehaviorParameter<T> {

            T initialValue;
            T defaultValue;
            T value;
            Ret<T> inquiry;
            Function<BehaviorParameter<T>,ModifyTo<T>> apply;
            ModifyTo<T> modifyTo;

            @Contract(pure = true)
            @Override
            public T get() {
                return value;
            }

            @Override
            public T getActualValue() {
                try {
                    return inquiry.ret();
                } catch (Exception e) {
                    throw new IllegalStateException("An error occurred during the query!",e);
                }
            }

            @Contract(mutates = "this")
            @Override
            public T set(T value) {
                T old = this.value;
                this.value = value;
                return old;
            }

            @Contract(pure = true)
            @Override
            public T defaultValue() {
                return defaultValue;
            }

            @Contract(pure = true)
            @Override
            public T initialValue() {
                return initialValue;
            }

            @Contract(mutates = "this")
            @Override
            public void reset() {
                value = defaultValue;
            }

            @Override
            public void overwrite() {
                try {
                    modifyTo = apply.apply(this);
                } catch (Exception e) {
                    throw new IllegalStateException("An error occurred during overwrite!",e);
                }
            }

            @Override
            @Contract(mutates = "this")
            public ModifyTo<T> modifyTo() {
                var val = modifyTo;
                modifyTo = null;
                return val;
            }
        }

    }

    @ApiStatus.Experimental
    @SuppressWarnings("unused")
    @ApiSign.InternalApi(since = "1.0.7")
    @Contract(value = "_ -> new", pure = true)
    public static <V> @NotNull BehaviorParameterBuilder<V> builder(V type) {
        var bp = new BehaviorParameterBuilder<V>();
        bp.behaviorParameter = new BehaviorParameterBuilder.DefaultBehaviorParameter<>();
        return bp;
    }

}
