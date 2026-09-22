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
// Data 2026/09/06 11:40
//

package io.github.juicefries.fct.event;

import io.github.juicefries.fct.Component;
import io.github.juicefries.fct.Container;
import io.github.juicefries.fct.EventComponent;
import io.github.juicefries.fct.KeyEventComponent;
import io.github.juicefries.fct.MouseEventComponent;
import io.github.juicefries.fct.Window;
import io.github.juicefries.fct.callback.CallbackManager;
import io.github.juicefries.fct.callback.GLFWCallback;
import io.github.juicefries.fct.callback.KeyCallback;
import io.github.juicefries.fct.callback.KeyCallbackA;
import io.github.juicefries.fct.callback.KeyData;
import io.github.juicefries.fct.callback.MouseButtonCallback;
import io.github.juicefries.fct.callback.MouseButtonCallbackA;
import io.github.juicefries.fct.callback.MouseCursorCallback;
import io.github.juicefries.fct.callback.MouseCursorCallbackA;
import io.github.juicefries.fct.callback.MouseData;
import io.github.juicefries.fct.callback.MouseDropCallback;
import io.github.juicefries.fct.callback.MouseDropCallbackA;
import io.github.juicefries.fct.callback.MouseScrollCallback;
import io.github.juicefries.fct.callback.MouseScrollCallbackA;
import io.github.juicefries.fct.callback.WindowData;
import io.github.juicefries.fct.callback.WindowLifeCallback;
import io.github.juicefries.fct.callback.WindowLifeCallbackA;
import io.github.juicefries.fct.callback.WindowStateCallback;
import io.github.juicefries.fct.callback.WindowStateCallbackA;
import io.github.juicefries.fct.callback.WindowToggleCallback;
import io.github.juicefries.fct.callback.WindowToggleCallbackA;
import io.github.juicefries.fct.glfw.Mouse;
import io.github.juicefries.fct.logging.LoggerFactory;
import io.github.juicefries.fct.util.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public final class DefaultEventTrigger extends EventTrigger {

    private static final Logger logger = LoggerFactory.getLogger(DefaultEventTrigger.class);
    private final AtomicBoolean initialize = new AtomicBoolean(false);

    private KeyEventComponent focusComponent;
    private MouseEventComponent captureComponent;
    private MouseEventComponent hoverComponent;

    Window window;

    List<_callback_adapter> list = new ArrayList<>();

    public DefaultEventTrigger() {
        super();
    }

    @Contract("null -> fail")
    @Override
    public void bind(Window window) {
        if (window == null) {
            throw new NullPointerException("mgr is null!");
        }
        if (isInitialize()) {
            try {
                dispose();
            } catch (Exception e) {
                logger.error("An error occurred while unregistering the old EventTrigger.",e);
                return;
            }
        }
        this.window = window;
        this.focusComponent = window;
    }

    @Override
    public void update() {
        var window = this.window;
        var focusComponent = getFocusComponent(window);
        if (focusComponent != null) {
            this.focusComponent = focusComponent;
        }
    }

    @Contract("null -> null")
    @Nullable KeyEventComponent getFocusComponent(Component component) {
        //noinspection IfCanBeSwitch
        if (component == null) return null;
        if (component instanceof KeyEventComponent kec && kec.isFocus()) return kec;


        if (component instanceof Container container) {
            for (var c : container.getComponents()) {
                if (c == null) continue;
                var found = getFocusComponent(c);
                if (found != null) return found;
            }
        }

        return null;
    }


    @Override
    public void initialize() {
        if (isInitialize()) {
            throw new IllegalStateException("CallbackManager have been initialized!");
        }
        list.add(new _button_adapter());
        list.add(new _cursor_adapter());
        list.add(new _drop_adapter());
        list.add(new _scroll_adapter());
        list.add(new _key_adapter());
        list.add(new _life_adapter());
        list.add(new _toggle_adapter());
        list.add(new _state_adapter());
        list.forEach(_callback_adapter::_add);
        initialize.set(true);
    }

    @SuppressWarnings("unchecked")
    <T extends GLFWCallback> void add(Class<T> type, GLFWCallback v) {
        getCallbackManager().add(type, (T) v);
    }

    @SuppressWarnings("unchecked")
    <T extends GLFWCallback> void remove(Class<T> type, GLFWCallback v) {
        getCallbackManager().remove(type, (T) v);
    }

    @Override
    public void dispose() {
        if (!isInit()) return;
        list.forEach(_callback_adapter::_remove);
        window = null;
        initialize.set(false);
    }

    @Override
    public boolean isInitialize() {
        return initialize.get();
    }

    @Nullable EventComponent getEventComponentAt(float x, float y) {
        Component comp = window.getComponentAt(x, y);
        return comp instanceof EventComponent ec ? ec : null;
    }

    @ApiStatus.Internal
    public interface _callback_adapter {
        void _add();
        void _remove();
    }

    @ApiStatus.Internal
    public final class _button_adapter extends MouseButtonCallbackA implements _callback_adapter {

        private _button_adapter() {}

        @Override
        public void _add() {
            add(MouseButtonCallback.class,this);
        }

        @Override
        public void _remove() {
            remove(MouseButtonCallback.class,this);
        }

        @Override
        public void press(MouseData e) {
            if (e == null) return;

            var cursorPos = Mouse.getCursorPos(e.getWindow());
            float x = (float) cursorPos.x;
            float y = (float) cursorPos.y;

            var ec = getEventComponentAt(x, y);
            if (!(ec instanceof MouseEventComponent mec)) return;

            captureComponent = mec;   // 按下即捕获，直到释放才清空

            float localX = x - ((Component) mec).getAbsoluteX();
            float localY = y - ((Component) mec).getAbsoluteY();
            //noinspection MagicConstant
            var _med_p__ = MouseEvent.press(e.getButton(), e.getMods(), localX, localY);

            var listeners = getListeners(mec, MouseButtonListener.class);
            Array.forArr(listeners, listener -> {
                if (listener == null) return;
                listener.press(_med_p__);
            });
        }


        @Override
        public void release(MouseData e) {
            if (e == null) return;

            var ec = captureComponent;   // 释放发给"按下时"的组件，不重新命中
            if (ec == null) return;

            var cursorPos = Mouse.getCursorPos(e.getWindow());
            float x = (float) cursorPos.x;
            float y = (float) cursorPos.y;

            float localX = x - ((Component) ec).getAbsoluteX();
            float localY = y - ((Component) ec).getAbsoluteY();
            //noinspection MagicConstant
            var _med_r__ = MouseEvent.release(e.getButton(), e.getMods(), localX, localY);

            var listeners = getListeners(ec, MouseButtonListener.class);
            Array.forArr(listeners, listener -> {
                if (listener == null) return;
                listener.release(_med_r__);
            });

            captureComponent = null;   // 释放后清空捕获
        }
    }

    @ApiStatus.Internal
    public final class _cursor_adapter extends MouseCursorCallbackA implements _callback_adapter {

        private _cursor_adapter() {}

        @Override
        public void _add() {
            add(MouseCursorCallback.class,this);
        }

        @Override
        public void _remove() {
            remove(MouseCursorCallback.class,this);
        }

        @Override
        public void cursorPos(MouseData e) {
            if (e == null) return;

            float x = (float) e.getXPos();
            float y = (float) e.getYPos();

            var ec = getEventComponentAt(x, y);

            if (ec != hoverComponent) {
                if (hoverComponent != null) {
                    float oldLocalX = x - ((Component) hoverComponent).getAbsoluteX();
                    float oldLocalY = y - ((Component) hoverComponent).getAbsoluteY();
                    var _med_ex__ = MouseEvent.exited(oldLocalX, oldLocalY);
                    var exitedListeners = getListeners(hoverComponent, MouseEntersListener.class);
                    Array.forArr(exitedListeners, listener -> {
                        if (listener == null) return;
                        listener.exited(_med_ex__);
                    });
                }

                if (ec instanceof MouseEventComponent mec) {
                    float newLocalX = x - ((Component) mec).getAbsoluteX();
                    float newLocalY = y - ((Component) mec).getAbsoluteY();
                    var _med_en__ = MouseEvent.entered(newLocalX, newLocalY);
                    var enteredListeners = getListeners(mec, MouseEntersListener.class);
                    Array.forArr(enteredListeners, listener -> {
                        if (listener == null) return;
                        listener.entered(_med_en__);
                    });
                }

                if (ec instanceof MouseEventComponent mec) {
                    hoverComponent = mec;
                }
            }

            if (!(ec instanceof MouseEventComponent mec)) return;

            float localX = x - ((Component) mec).getAbsoluteX();
            float localY = y - ((Component) mec).getAbsoluteY();
            var _med_m_p__ = MouseEvent.pos(localX, localY);

            var listeners = getListeners(mec, MouseCursorListener.class);
            Array.forArr(listeners, listener -> {
                if (listener == null) return;
                listener.mousePos(_med_m_p__);
            });
        }

        @Override
        public void cursorEnter(MouseData e) {
            var listeners = getListeners(CursorEnterListener.class);

            var _wed_e__ = WindowEvent.enter(true);
            Array.forArr(listeners,listener -> {
                if (listener == null) return;
                listener.event(_wed_e__);
            });
        }

        @Override
        public void cursorLeave(MouseData e) {
            var listeners = getListeners(CursorEnterListener.class);

            var _wed_l__ = WindowEvent.enter(false);
            Array.forArr(listeners, listener -> {
                if (listener == null) return;
                listener.event(_wed_l__);
            });

            if (hoverComponent != null && e != null) {
                float x = (float) e.getXPos();
                float y = (float) e.getYPos();
                float oldLocalX = x - ((Component) hoverComponent).getAbsoluteX();
                float oldLocalY = y - ((Component) hoverComponent).getAbsoluteY();
                var _med_ex__ = MouseEvent.exited(oldLocalX, oldLocalY);
                var exitedListeners = getListeners(hoverComponent, MouseEntersListener.class);
                Array.forArr(exitedListeners, listener -> {
                    if (listener == null) return;
                    listener.exited(_med_ex__);
                });
                hoverComponent = null;
            }
        }

    }

    @ApiStatus.Internal
    public final class _drop_adapter extends MouseDropCallbackA implements _callback_adapter {

        private _drop_adapter() {

        }

        @Override
        public void _add() {
            add(MouseDropCallback.class,this);
        }

        @Override
        public void _remove() {
            remove(MouseDropCallback.class,this);
        }

        @Override
        public void drop(MouseData e) {
            if (e == null) return;

            var cursorPos = Mouse.getCursorPos(e.getWindow());
            float x = (float) cursorPos.x;
            float y = (float) cursorPos.y;

            var ec = getEventComponentAt(x, y);
            if (ec == null) return;

            float localX = x - ((Component) ec).getAbsoluteX();
            float localY = y - ((Component) ec).getAbsoluteY();

            var _med_d__ = MouseEvent.drop(e.getFilePaths(), localX, localY);

            var listeners = getListeners(ec, MouseDropListener.class);
            Array.forArr(listeners,listener -> {
                if (listener == null) return;
                listener.drop(_med_d__);
            });
        }

    }

    @ApiStatus.Internal
    public final class _scroll_adapter extends MouseScrollCallbackA implements _callback_adapter {
        private _scroll_adapter() {}

        @Override
        public void _add() {
            add(MouseScrollCallback.class,this);
        }

        @Override
        public void _remove() {
            remove(MouseScrollCallback.class,this);
        }

        @Override
        public void scroll(MouseData e) {
            if (e == null) return;

            var cursorPos = Mouse.getCursorPos(e.getWindow());
            float x = (float) cursorPos.x;
            float y = (float) cursorPos.y;

            var ec = getEventComponentAt(x, y);
            if (ec == null) return;

            float localX = x - ((Component) ec).getAbsoluteX();
            float localY = y - ((Component) ec).getAbsoluteY();

            var _med_s__ = MouseEvent.scroll(e.getXOffset(), e.getYOffset(), localX, localY);

            var listeners = getListeners(ec, MouseWheelListener.class);
            Array.forArr(listeners,listener -> {
                if (listener == null) return;
                listener.scroll(_med_s__);
            });
        }
    }

    @ApiStatus.Internal
    public final class _key_adapter extends KeyCallbackA implements _callback_adapter {

        private _key_adapter() {

        }

        @Override
        public void _add() {
            add(KeyCallback.class,this);
        }

        @Override
        public void _remove() {
            remove(KeyCallback.class,this);
        }

        @Override
        public void pressKey(KeyData e) {
            var focus = focusComponent;
            if (focus == null) return;
            var listeners = getListeners(focus, KeyInputListener.class);

            var _ked_p__ = KeyEvent.press(e.getKey(),e.getMods());
            Array.forArr(listeners,listener -> {
                if (listener == null) return;
                listener.press(_ked_p__);
            });
        }

        @Override
        public void loosenKey(KeyData e) {
            var focus = focusComponent;
            if (focus == null) return;
            var listeners = getListeners(focus, KeyInputListener.class);
            var _ked_r__ = KeyEvent.release(e.getKey(),e.getMods());
            Array.forArr(listeners,listener -> {
                if (listener == null) return;
                listener.release(_ked_r__);
            });
        }

        @Override
        public void longPress(KeyData e) {
            var focus = focusComponent;
            if (focus == null) return;
            var listeners = getListeners(focus, KeyInputListener.class);
            var _ked_l_p__ = KeyEvent.longPress(e.getKey(),e.getMods());
            Array.forArr(listeners,listener -> {
                if (listener == null) return;
                listener.longPress(_ked_l_p__);
            });
        }

        @Override
        public void inputKey(KeyData e) {
            var focus = focusComponent;
            if (focus == null) return;
            var listeners = getListeners(focus, KeyInputListener.class);
            var codepoint = (char) e.getCodepoint();
            var _ked_c_i__ = KeyEvent.charInput(codepoint);
            Array.forArr(listeners,listener -> {
                if (listener == null) return;
                listener.press(_ked_c_i__);
            });
        }
    }

    @ApiStatus.Internal
    public final class _life_adapter extends WindowLifeCallbackA implements _callback_adapter {
        private _life_adapter() {}

        @Override
        public void _add() {
            add(WindowLifeCallback.class,this);
        }

        @Override
        public void _remove() {
            remove(WindowLifeCallback.class,this);
        }

        @Override
        public void close(WindowData data) {
            var listeners = getListeners(WindowLifeListener.class);
            Array.forArr(listeners,listener -> {
                if (listener == null) return;
                listener.close(WindowEvent.close());
            });
        }

    }

    @ApiStatus.Internal
    public final class _toggle_adapter extends WindowToggleCallbackA implements _callback_adapter {
        private _toggle_adapter() {}

        @Override
        public void _add() {
            add(WindowToggleCallback.class,this);
        }

        @Override
        public void _remove() {
            remove(WindowToggleCallback.class,this);
        }

        @Override
        public void maximized(WindowData e) {
            var listeners = getListeners(WindowMaximizedListener.class);

            Array.forArr(listeners, listener -> {
                if (listener == null) return;
                listener.maximized(WindowEvent.maximize());
            });
        }

        @Override
        public void minimized(WindowData e) {
            var listeners = getListeners(WindowMinimizedListener.class);

            Array.forArr(listeners, listener -> {
                if (listener == null) return;
                listener.minimized(WindowEvent.minimized());
            });
        }

        @Override
        public void restored(WindowData e) {
            var listeners = getListeners(WindowRestoredListener.class);

            Array.forArr(listeners, listener -> {
                if (listener == null) return;
                listener.restored(WindowEvent.restored());
            });
        }

    }

    @ApiStatus.Internal
    public final class _state_adapter extends WindowStateCallbackA implements _callback_adapter {

        private _state_adapter() {

        }

        @Override
        public void _add() {
            add(WindowStateCallback.class,this);
        }

        @Override
        public void _remove() {
            remove(WindowStateCallback.class,this);
        }

        @Override
        public void size(WindowData e) {
            if (e == null) return;
            var listeners = getListeners(WindowSizeListener.class);

            var _wed_s__ = WindowEvent.size(e.getWidth(),e.getHeight());

            Array.forArr(listeners,listener -> {
                if (listener == null) return;
                listener.size(_wed_s__);
            });
        }

        @Override
        public void framebufferSize(WindowData e) {
            if (e == null) return;
            var listeners = getListeners(WindowFrameBufferSizeListener.class);

            var width = e.getFrameBufferWidth();
            var height = e.getFrameBufferHeight();
            var _wed_fbs__ = WindowEvent.frameBufferSize(width,height);

            Array.forArr(listeners,listener -> {
                if (listener == null) return;
                listener.size(_wed_fbs__);
            });
        }

        @Override
        public void move(WindowData e) {
            if (e == null) return;
            var listeners = getListeners(WindowMoveListener.class);

            var _wed_m__ = WindowEvent.move(e.getX(),e.getY());

            Array.forArr(listeners,listener -> {
                if (listener == null) return;
                listener.move(_wed_m__);
            });
        }

        @Override
        public void focus(WindowData e) {
            var listeners = getListeners(WindowFocusListener.class);

            Array.forArr(listeners,listener -> {
                if (listener == null) return;
                listener.focused(WindowEvent.focused(e.isFocused()));
            });
        }

        @Override
        public void refresh(WindowData e) {
            var listeners = getListeners(WindowRefreshListener.class);
            Array.forArr(listeners,listener -> {
                if (listener == null) return;
                listener.refresh(WindowEvent.refresh());
            });
        }

        @Override
        public void contentScale(WindowData e) {
            if (e == null) return;

            var listeners = getListeners(WindowContentScaleListener.class);
            var _wed_cs__ = WindowEvent.contentScale(e.getXScale(), e.getYScale());

            Array.forArr(listeners, listener -> {
                if (listener == null) return;
                listener.contentScale(_wed_cs__);
            });
        }


    }

    // ========================= UTIL =========================

    EventListenerList getListenerList(@NotNull EventComponent comp) {
        return comp.getListenerList();
    }

    <T extends Listener> T[] getListeners(EventComponent comp,Class<T> type) {
        return getListenerList(comp).getListeners(type);
    }

    <T extends Listener> T[] getListeners(Class<T> type) {
        return getListeners(window,type);
    }

    CallbackManager getCallbackManager() {
        return window.getCallbackManager();
    }

}
