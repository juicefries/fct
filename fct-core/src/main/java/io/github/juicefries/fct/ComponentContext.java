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
// Data 2026/10/07 17:21
//

package io.github.juicefries.fct;

import io.github.juicefries.fct.event.ActionListener;
import io.github.juicefries.fct.event.EventListenerList;
import io.github.juicefries.fct.event.KeyboardFocusEvent;
import io.github.juicefries.fct.event.StringEvent;
import io.github.juicefries.fct.sign.ApiSign;
import io.github.juicefries.fct.util.Array;
import io.github.juicefries.fct.util.Lock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;

public final class ComponentContext {

    private static final Logger log = LogManager.getLogger(ComponentContext.class);

    public final static String FOCUS_SWITCHING = "FocusSwitching";
    public final static String FOCUS_CLEAR = "Focus Clear";

    final static ThreadLocal<ComponentContext> FCT_CC_LOCAL = new ThreadLocal<>();

    private final Lock udl = Lock.create("FCT.CC.UpdateDataLock");
    private final Lock fcl = Lock.create("FCT.CC.FocusComponentLock");

    volatile KeyEventComponent oldFocusComponent;
    volatile KeyEventComponent focusComponent;

    final EventListenerList listeners = new EventListenerList();
    Window window;

    public ComponentContext() {

    }

    // ========================= OTM =========================

    @ApiSign.Dangerous(since = "1.0.3")
    @ApiSign.InternalApi(since = "1.0.3")
    public void cleanup() {
        synchronized (udl) {
            window = null;
            var arr = getListeners();
            for (var l : arr) {
                if (l == null) continue;
                listeners.remove(ActionListener.class,l);
            }
        }
        synchronized (fcl) {
            oldFocusComponent = null;
            focusComponent = null;
        }
    }

    @Contract("null -> fail")
    public void loseFocus(KeyEventComponent comp) {
        if (comp == null) {
            throw new NullPointerException("comp is null!");
        }
        check();
        synchronized (fcl) {
            if (focusComponent == null || comp != focusComponent) return;
            oldFocusComponent = null;
            var old = focusComponent;
            focusComponent = null;
            notice(FOCUS_CLEAR);
            synchronized (udl) {
                window.invoke(() -> {
                    var arr = old.getKeyboardFocusListeners();
                    for (var l : arr) {
                        if (l == null) continue;
                        l.lose(KeyboardFocusEvent.lose());
                    }
                });
            }
        }
    }

    @Contract("null -> fail")
    public void gainFocus(KeyEventComponent comp) {
        if (comp == null) {
            throw new NullPointerException("comp is null!");
        }
        check();
        synchronized (fcl) {
            if (focusComponent != null && isFocused(comp)) return;
            oldFocusComponent = focusComponent;
            focusComponent = comp;

            notice(FOCUS_SWITCHING);

            synchronized (udl) {
                window.invoke(() -> {
                    if (oldFocusComponent != null) {
                        var arr = oldFocusComponent.getKeyboardFocusListeners();
                        for (var l : arr) {
                            if (l == null) continue;
                            l.lose(KeyboardFocusEvent.lose());
                        }
                    }
                    var arr = focusComponent.getKeyboardFocusListeners();
                    for (var l : arr) {
                        if (l == null) continue;
                        l.gain(KeyboardFocusEvent.gain());
                    }
                });
            }
        }
    }

    public void check() {
        synchronized (udl) {
            if (window == null) {
                throw new IllegalStateException("Invalid context, the current context has not yet been bound to any window!");
            }
        }
    }

    void notice(String s) {
        synchronized (udl) {
            window.invoke(() -> {
                Array.forArr(getListeners(), listener -> {
                    if (listener == null) return;
                    try {
                        listener.action(StringEvent.create(s));
                    } catch (Exception e) {
                        log.error("An error occurred while performing the focus switch notification.", e);
                    }
                });
            });
        }
    }

    // ========================= BIND =========================

    public void bind(Window window) {
        if (window == null) {
            throw new NullPointerException("window is null!");
        }
        synchronized (udl) {
            if (this.window != null) {
                throw new IllegalArgumentException("The current context is already bound to a window!");
            }
            this.window = window;
        }
    }

    // ========================= ADD & REMOVE =========================

    public void addActionListener(ActionListener l) {
        if (l == null) {
            throw new NullPointerException("l is null!");
        }
        synchronized (udl) {
            listeners.add(ActionListener.class,l);
        }
    }

    public void removeActionListener(ActionListener l) {
        if (l == null) {
            throw new NullPointerException("l is null!");
        }
        synchronized (udl) {
            if (!Array.contains(l,getListeners())) {
                throw new IllegalArgumentException("The listener to be removed does not exist!");
            }
            listeners.remove(ActionListener.class,l);
        }
    }

    // ========================= SET =========================



    // ========================= GET =========================

    public boolean isFocused(KeyEventComponent comp) {
        if (comp == null) {
            throw new NullPointerException("comp is null!");
        }
        synchronized (fcl) {
            check();
            return comp == focusComponent;
        }
    }

    ActionListener[] getListeners() {
        synchronized (udl) {
            return listeners.getListeners(ActionListener.class);
        }
    }

    public KeyEventComponent getFocusComponent() {
        synchronized (fcl) {
            return focusComponent;
        }
    }

    public KeyEventComponent getOldFocusComponent() {
        synchronized (fcl) {
            return oldFocusComponent;
        }
    }

    // ========================= EEE =========================

}
