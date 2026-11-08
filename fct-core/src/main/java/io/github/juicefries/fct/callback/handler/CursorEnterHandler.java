//
// Created by juicefries
// The project name is fct
// Data 2026/11/08 03:18
//

package io.github.juicefries.fct.callback.handler;

import io.github.juicefries.fct.callback.Event;
import io.github.juicefries.fct.callback.event.CallbackEventData;
import io.github.juicefries.fct.callback.event.CursorEnterEvent;
import io.github.juicefries.fct.callback.listener.CursorEnterListener;
import io.github.juicefries.fct.util.Array;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorEnterCallback;

import java.util.Objects;

public final class CursorEnterHandler extends GLFWCursorEnterCallback implements Handler {

    public CursorEnterHandler() {

    }

    @Override
    public void register(long window) {
        check(GLFW.glfwSetCursorEnterCallback(window,this));
    }

    @Override
    public void simulate(@NotNull CallbackEventData event) {
        if (!(event instanceof CursorEnterEvent e)) return;
        if (Objects.equals(e.type(), CursorEnterEvent.CURSOR_ENTER_EVENT)) {
            invoke(e.window(),true);
        }
        if (Objects.equals(e.type(), CursorEnterEvent.CURSOR_LEAVE_EVENT)) {
            invoke(e.window(),false);
        }
    }

    @Override
    public void invoke(long window, boolean entered) {
        var listeners = Event.getListeners(window, CursorEnterListener.class);
        Array.forArr(listeners,listener -> {
            if (entered) {
                listener.enter(CursorEnterEvent.enter(window));
            } else {
                listener.leave(CursorEnterEvent.leave(window));
            }
        });
    }

}
