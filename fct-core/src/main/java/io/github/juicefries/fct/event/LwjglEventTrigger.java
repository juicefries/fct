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
import io.github.juicefries.fct.Toolkit;
import io.github.juicefries.fct.UIManager;
import io.github.juicefries.fct.Window;
import io.github.juicefries.fct.callback.Event;
import io.github.juicefries.fct.callback.event.ButtonEvent;
import io.github.juicefries.fct.callback.event.CharEvent;
import io.github.juicefries.fct.callback.event.CloseEvent;
import io.github.juicefries.fct.callback.event.ContentScaleEvent;
import io.github.juicefries.fct.callback.event.CursorEnterEvent;
import io.github.juicefries.fct.callback.event.CursorPosEvent;
import io.github.juicefries.fct.callback.event.DropEvent;
import io.github.juicefries.fct.callback.event.FocusEvent;
import io.github.juicefries.fct.callback.event.FrameBufferSizeEvent;
import io.github.juicefries.fct.callback.event.PosEvent;
import io.github.juicefries.fct.callback.event.RefreshEvent;
import io.github.juicefries.fct.callback.event.ScrollEvent;
import io.github.juicefries.fct.callback.event.SizeEvent;
import io.github.juicefries.fct.callback.event.ToggleEvent;
import io.github.juicefries.fct.callback.listener.ButtonListener;
import io.github.juicefries.fct.callback.listener.CallbackListener;
import io.github.juicefries.fct.callback.listener.CharListener;
import io.github.juicefries.fct.callback.listener.CloseListener;
import io.github.juicefries.fct.callback.listener.ContentScaleListener;
import io.github.juicefries.fct.callback.listener.CursorPosListener;
import io.github.juicefries.fct.callback.listener.DropListener;
import io.github.juicefries.fct.callback.listener.FocusListener;
import io.github.juicefries.fct.callback.listener.FrameBufferSizeListener;
import io.github.juicefries.fct.callback.listener.KeyListener;
import io.github.juicefries.fct.callback.listener.PosListener;
import io.github.juicefries.fct.callback.listener.RefreshListener;
import io.github.juicefries.fct.callback.listener.ScrollListener;
import io.github.juicefries.fct.callback.listener.SizeListener;
import io.github.juicefries.fct.callback.listener.ToggleListener;
import io.github.juicefries.fct.callback.listener.CursorEnterListener;
import io.github.juicefries.fct.lwjgl.Mouse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;


public final class LwjglEventTrigger extends EventTrigger {

    private static final Logger logger = LogManager.getLogger(LwjglEventTrigger.class);
    private final AtomicBoolean initialize = new AtomicBoolean(false);

    private Component captureComponent;
    private Component hoverComponent;
    /**
     * 当前已应用到窗口上的光标名，避免重复设置
     */
    private String currentCursorName = UIManager.DEFAULT_CURSOR;

    Window window;

    List<CallbackAdapter> list = new ArrayList<>();

    public LwjglEventTrigger() {
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
                logger.error("An error occurred while unregistering the old EventTrigger.", e);
                return;
            }
        }
        this.window = window;
    }

    @Override
    public void initialize() {
        if (isInitialize()) {
            throw new IllegalStateException("CallbackManager have been initialized!");
        }
        list.add(new DropAdapter());
        list.add(new ButtonAdapter());
        list.add(new CursorPosAdapter());
        list.add(new ScrollAdapter());
        list.add(new KeyAdapter());
        list.add(new CharAdapter());
        list.add(new CloseAdapter());
        list.add(new SizeAdapter());
        list.add(new FrameBufferSizeAdapter());
        list.add(new PosAdapter());
        list.add(new RefreshAdapter());
        list.add(new ToggleAdapter());
        list.add(new ContentScaleAdapter());
        list.add(new FocusAdapter());
        list.add(new CursorEnterAdapter());
        list.forEach(CallbackAdapter::add);
        initialize.set(true);
    }

    @Override
    public void dispose() {
        if (!isInit()) return;
        list.forEach(CallbackAdapter::remove);
        list.clear();
        window = null;
        initialize.set(false);
    }

    @Override
    public boolean isInitialize() {
        return initialize.get();
    }

    private abstract class CallbackAdapter implements CallbackListener {

        protected abstract Class<? extends CallbackListener> type();

        public <L extends CallbackListener> void add() {
            long window = Toolkit.getWindow(LwjglEventTrigger.this.window);
            var listeners = Event.getListeners(window);
            //noinspection unchecked
            listeners.add((Class<L>) type(), (L) this);
        }

        public <L extends CallbackListener> void remove() {
            long window = Toolkit.getWindow(LwjglEventTrigger.this.window);
            var listeners = Event.getListeners(window);
            //noinspection unchecked
            listeners.remove((Class<L>) type(), (L) this);
        }

        @Override
        public String name() {
            return "LwjglEventTrigger-CallbackAdapter-%s".formatted(getClass().getSimpleName());
        }
    }

    private final class DropAdapter extends CallbackAdapter implements DropListener {

        private DropAdapter() {

        }

        @Override
        public void drop(DropEvent e) {
            if (e == null) return;

            float x = (float) e.x();
            float y = (float) e.y();

            var ec = window.getComponentAt(x, y);
            if (ec == null) return;

            //noinspection deprecation
            var location = ec.getAbsoluteLocation();
            float localX = x - location.x;
            float localY = y - location.y;

            ec.forMouseDropListener(listener -> listener.drop(MouseEvent.drop(e.getFilePaths(), localX, localY)));
        }

        @Contract(pure = true)
        @Override
        protected Class<? extends CallbackListener> type() {
            return DropListener.class;
        }
    }

    private final class ButtonAdapter extends CallbackAdapter implements ButtonListener {

        private ButtonAdapter() {

        }

        @Override
        public void press(ButtonEvent e) {
            if (e == null) return;

            float x = (float) e.x();
            float y = (float) e.y();

            var component = window.getComponentAt(x, y);
            if (component == null) return;

            captureComponent = component;   // 按下即捕获，直到释放才清空

            //noinspection deprecation
            var location = component.getAbsoluteLocation();
            float localX = x - location.x;
            float localY = y - location.y;
            //noinspection MagicConstant
            var event = MouseEvent.press(e.button(), e.mods(), localX, localY);
            component.forMouseButtonListener(listener -> listener.press(event.copy()));
        }

        @Override
        public void release(ButtonEvent e) {
            if (e == null) return;

            var ec = captureComponent;   // 释放发给"按下时"的组件，不重新命中
            if (ec == null) return;

            float x = (float) e.x();
            float y = (float) e.y();

            //noinspection deprecation
            var location = ec.getAbsoluteLocation();
            float localX = x - location.x;
            float localY = y - location.y;
            //noinspection MagicConstant
            var event = MouseEvent.release(e.button(), e.mods(), localX, localY);
            ec.forMouseButtonListener(listener -> listener.release(event.copy()));
            captureComponent = null;   // 释放后清空捕获
        }

        @Override
        @Contract(pure = true)
        protected Class<? extends CallbackListener> type() {
            return ButtonListener.class;
        }

    }

    private final class CursorPosAdapter extends CallbackAdapter implements CursorPosListener {

        private CursorPosAdapter() {

        }

        @Override
        public void pos(CursorPosEvent e) {
            if (e == null) return;

            var x = (float) e.x();
            var y = (float) e.y();

            // 拖动中,按下时捕获的组件优先接收移动事件，
            // 即使鼠标已经离开它的范围
            var captured = captureComponent;
            if (captured != null) {
                dispatchMousePos(captured, x, y);
                return;
            }

            var target = window.getComponentAt(x, y);

            if (target != hoverComponent) {
                if (hoverComponent != null) {
                    dispatchEnters(hoverComponent, false, x, y);
                }
                if (target != null) {
                    dispatchEnters(target, true, x, y);
                }

                hoverComponent = target;
                applyCursor(target);
            }

            if (target == null) return;

            dispatchMousePos(target, x, y);
        }

        @Override
        protected Class<? extends CallbackListener> type() {
            return CursorPosListener.class;
        }
    }

    private final class ScrollAdapter extends CallbackAdapter implements ScrollListener {

        private ScrollAdapter() {
        }

        @Override
        public void scroll(ScrollEvent e) {
            if (e == null) return;

            float x = (float) e.x();
            float y = (float) e.y();

            var ec = window.getComponentAt(x, y);
            if (ec == null) return;

            //noinspection deprecation
            var location = ec.getAbsoluteLocation();
            float localX = x - location.x;
            float localY = y - location.y;

            var event = MouseEvent.scroll(e.xOffset(), e.yOffset(), localX, localY);
            ec.forMouseWheelListener(listener -> listener.scroll(event.copy()));
        }

        @Override
        @Contract(pure = true)
        protected Class<? extends CallbackListener> type() {
            return ScrollListener.class;
        }
    }

    private final class KeyAdapter extends CallbackAdapter implements KeyListener {

        private KeyAdapter() {

        }

        @Override
        public void press(io.github.juicefries.fct.callback.event.KeyEvent e) {
            var focus = window.getComponentContext().getFocusComponent();
            if (focus == null) return;
            var event = KeyEvent.press(e.key(), e.mods());
            focus.forKeyInputListener(listener -> listener.press(event.copy()));
        }

        @Override
        public void repeat(io.github.juicefries.fct.callback.event.KeyEvent e) {
            var focus = window.getComponentContext().getFocusComponent();
            if (focus == null) return;
            var event = KeyEvent.repeat(e.key(), e.mods());
            focus.forKeyInputListener(listener -> listener.repeat(event.copy()));
        }

        @Override
        public void release(io.github.juicefries.fct.callback.event.KeyEvent e) {
            var focus = window.getComponentContext().getFocusComponent();
            if (focus == null) return;
            focus.forKeyInputListener(listener -> listener.release(KeyEvent.release(e.key(), e.mods())));
        }

        @Contract(pure = true)
        @Override
        protected Class<? extends CallbackListener> type() {
            return KeyListener.class;
        }
    }

    private final class CharAdapter extends CallbackAdapter implements CharListener {

        private CharAdapter() {

        }

        @Override
        public void input(CharEvent e) {
            var focus = window.getComponentContext().getFocusComponent();
            if (focus == null) return;
            var event = KeyEvent.charInput(e.code());
            focus.forKeyCharInputListener(listener -> listener.input(event.copy()));
        }

        @Override
        @Contract(pure = true)
        protected Class<? extends CallbackListener> type() {
            return CharListener.class;
        }
    }

    private final class CloseAdapter extends CallbackAdapter implements CloseListener {

        private CloseAdapter() {

        }

        @Override
        public void close(CloseEvent e) {
            window.forWindowLifeListener(listener -> listener.close(WindowEvent.close()));
        }

        @Override
        @Contract(pure = true)
        protected Class<? extends CallbackListener> type() {
            return CloseListener.class;
        }
    }

    private final class SizeAdapter extends CallbackAdapter implements SizeListener {

        private SizeAdapter() {

        }

        @Override
        public void size(SizeEvent e) {
            window.forWindowSizeListener(listener -> listener.size(WindowEvent.size(e.width(), e.height())));
        }

        @Override
        @Contract(pure = true)
        protected Class<? extends CallbackListener> type() {
            return SizeListener.class;
        }
    }

    private final class FrameBufferSizeAdapter extends CallbackAdapter implements FrameBufferSizeListener {

        private FrameBufferSizeAdapter() {

        }

        @Override
        public void size(FrameBufferSizeEvent e) {
            window.forWindowFrameBufferSizeListener(listener -> listener.size(WindowEvent.frameBufferSize(e.width(), e.height())));
        }

        @Override
        @Contract(pure = true)
        protected Class<? extends CallbackListener> type() {
            return FrameBufferSizeListener.class;
        }
    }

    private final class PosAdapter extends CallbackAdapter implements PosListener {

        private PosAdapter() {

        }

        @Override
        public void pos(PosEvent e) {
            window.forWindowMoveListener(listener -> listener.move(WindowEvent.move(e.x(), e.y())));
        }

        @Override
        @Contract(pure = true)
        protected Class<? extends CallbackListener> type() {
            return PosListener.class;
        }
    }

    private final class RefreshAdapter extends CallbackAdapter implements RefreshListener {

        private RefreshAdapter() {

        }

        @Override
        public void refresh(RefreshEvent e) {
            window.forWindowRefreshListener(listener -> listener.refresh(WindowEvent.refresh()));
        }

        @Override
        @Contract(pure = true)
        protected Class<? extends CallbackListener> type() {
            return RefreshListener.class;
        }
    }

    private final class ToggleAdapter extends CallbackAdapter implements ToggleListener {

        private ToggleAdapter() {

        }

        @Override
        public void maximized(ToggleEvent e) {
            window.forWindowMaximizedListener(listener -> listener.maximized(WindowEvent.maximize()));
        }

        @Override
        public void minimized(ToggleEvent e) {
            window.forWindowMinimizedListener(listener -> listener.minimized(WindowEvent.minimized()));
        }

        @Override
        public void restored(ToggleEvent e) {
            window.forWindowRestoredListener(listener -> listener.restored(WindowEvent.restored()));
        }

        @Override
        @Contract(pure = true)
        protected Class<? extends CallbackListener> type() {
            return ToggleListener.class;
        }
    }

    private final class ContentScaleAdapter extends CallbackAdapter implements ContentScaleListener {

        private ContentScaleAdapter() {

        }

        @Override
        public void scale(ContentScaleEvent e) {
            window.forWindowContentScaleListener(listener -> listener.contentScale(WindowEvent.contentScale(e.xScale(), e.yScale())));
        }

        @Override
        @Contract(pure = true)
        protected Class<? extends CallbackListener> type() {
            return ContentScaleListener.class;
        }

    }

    private final class FocusAdapter extends CallbackAdapter implements FocusListener {

        private FocusAdapter() {

        }

        @Override
        public void focused(FocusEvent e) {
            window.forWindowFocusListener(listener -> listener.focused(WindowEvent.focused(e.focused())));
        }

        @Override
        @Contract(pure = true)
        protected Class<? extends CallbackListener> type() {
            return FocusListener.class;
        }

    }

    private final class CursorEnterAdapter extends CallbackAdapter implements CursorEnterListener {

        private CursorEnterAdapter() {

        }

        @Override
        public void enter(CursorEnterEvent e) {
            window.forCursorEnterListener(listener -> listener.event(WindowEvent.enter(true)));
        }

        @Override
        public void leave(CursorEnterEvent e) {
            window.forCursorEnterListener(listener -> listener.event(WindowEvent.enter(false)));

            var component = hoverComponent;

            if (component != null && e != null) {
                var cursorPos = Mouse.getCursorPos(e.window());
                float x = (float) cursorPos.x();
                float y = (float) cursorPos.y();

                //noinspection deprecation
                var location = component.getAbsoluteLocation();
                float oldLocalX = x - location.x;
                float oldLocalY = y - location.y;

                component.forMouseEntersListener(listener -> listener.exited(MouseEvent.exited(oldLocalX, oldLocalY)));

                hoverComponent = null;
            }
            applyCursor(null);
        }

        @Override
        @Contract(pure = true)
        protected Class<? extends CallbackListener> type() {
            return CursorEnterListener.class;
        }
    }

    // ========================= UTIL =========================

    /**
     * 派发鼠标位置事件
     *
     * @param comp 目标组件
     * @param x    窗口坐标X
     * @param y    窗口坐标Y
     */
    private void dispatchMousePos(@NotNull Component comp, float x, float y) {
        //noinspection deprecation
        var location = comp.getAbsoluteLocation();
        float localX = x - location.x;
        float localY = y - location.y;
        comp.forMouseCursorListener(listener -> listener.mousePos(MouseEvent.pos(localX, localY)));
    }

    /**
     * 派发进入 / 离开
     *
     * @param comp    目标事件组件
     * @param entered {@code true} 为进入，{@code false} 为离开
     * @param x       窗口坐标X
     * @param y       窗口坐标Y
     * @since 1.0.1
     */
    private void dispatchEnters(Component comp, boolean entered, float x, float y) {
        if (comp == null) return;

        //noinspection deprecation
        var location = comp.getAbsoluteLocation();
        float localX = x - location.x;
        float localY = y - location.y;

        comp.forMouseEntersListener(listener -> {
            if (entered) {
                listener.entered(MouseEvent.entered(localX, localY));
            } else {
                listener.exited(MouseEvent.exited(localX, localY));
            }
        });
    }

    /**
     * 按组件声明的光标名切换窗口光标
     * <p>
     * 名称未变化时不会重复设置。
     * </p>
     *
     * @param component 目标组件，{@code null}表示回到默认光标
     * @since 1.0.1
     */
    void applyCursor(Component component) {
        if (window == null) return;

        String name = component == null ? UIManager.DEFAULT_CURSOR : component.getCursorName();
        if (name == null) {
            name = UIManager.DEFAULT_CURSOR;
        }
        if (name.equals(currentCursorName)) return;

        currentCursorName = name;
        UIManager.setWindowCursor(window, name);
    }

}
