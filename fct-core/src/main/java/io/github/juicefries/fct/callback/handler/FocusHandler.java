//
// Created by juicefries
// The project name is fct
// Data 2026/11/08 00:23
//

package io.github.juicefries.fct.callback.handler;

import io.github.juicefries.fct.callback.Event;
import io.github.juicefries.fct.callback.event.CallbackEventData;
import io.github.juicefries.fct.callback.event.FocusEvent;
import io.github.juicefries.fct.callback.listener.FocusListener;
import io.github.juicefries.fct.util.Array;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWWindowFocusCallback;

public final class FocusHandler extends GLFWWindowFocusCallback implements Handler {

    public FocusHandler() {

    }

    @Override
    public void register(long window) {
        check(GLFW.glfwSetWindowFocusCallback(window,this));
    }

    @Override
    public void simulate(@NotNull CallbackEventData event) {
        if (!(event instanceof FocusEvent e)) return;
        invoke(e.window(),e.focused());
    }

    @Override
    public void invoke(long window, boolean focused) {
        var listeners = Event.getListeners(window, FocusListener.class);
        var e = FocusEvent.focus(window, focused);
        Array.forArr(listeners,listener -> listener.focused(e.copy()));
    }
}
