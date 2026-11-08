//
// Created by juicefries
// The project name is fct
// Data 2026/11/08 02:44
//

package io.github.juicefries.fct.callback.handler;

import io.github.juicefries.fct.callback.Event;
import io.github.juicefries.fct.callback.event.CallbackEventData;
import io.github.juicefries.fct.callback.event.CursorPosEvent;
import io.github.juicefries.fct.callback.listener.CursorPosListener;
import io.github.juicefries.fct.util.Array;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;

public final class CursorPosHandler extends GLFWCursorPosCallback implements Handler {

    public CursorPosHandler() {

    }

    @Override
    public void register(long window) {
        check(GLFW.glfwSetCursorPosCallback(window,this));
    }

    @Override
    public void simulate(@NotNull CallbackEventData event) {
        if (!(event instanceof CursorPosEvent e)) return;
        invoke(e.window(),e.x(),e.y());
    }

    @Override
    public void invoke(long window, double x, double y) {
        var listeners = Event.getListeners(window, CursorPosListener.class);
        var e = CursorPosEvent.pos(window,x,y);
        Array.forArr(listeners,listener -> listener.pos(e.copy()));
    }
}
