//
// Created by juicefries
// The project name is fct
// Data 2026/11/08 00:44
//

package io.github.juicefries.fct.callback.handler;

import io.github.juicefries.fct.callback.Event;
import io.github.juicefries.fct.callback.event.CallbackEventData;
import io.github.juicefries.fct.callback.event.ToggleEvent;
import io.github.juicefries.fct.callback.listener.ToggleListener;
import io.github.juicefries.fct.util.Array;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWWindowMaximizeCallback;

import java.util.Objects;

public final class MaximizeHandler extends GLFWWindowMaximizeCallback implements Handler {

    public MaximizeHandler() {

    }

    @Override
    public void register(long window) {
        check(GLFW.glfwSetWindowMaximizeCallback(window,this));
    }

    @Override
    public void simulate(@NotNull CallbackEventData event) {
        if (!(event instanceof ToggleEvent te)) return;
        if (Objects.equals(event.type(), ToggleEvent.WINDOW_MAXIMIZED_EVENT)) {
            invoke(te.window(),true);
        }
        if (Objects.equals(event.type(), ToggleEvent.WINDOW_RESTORED_EVENT)) {
            invoke(te.window(),false);
        }
    }

    @Override
    public void invoke(long window, boolean maximized) {
        var listeners = Event.getListeners(window, ToggleListener.class);
        Array.forArr(listeners,listener -> {
            if (maximized) {
                listener.maximized(ToggleEvent.maximized(window,true));
            } else {
                listener.restored(ToggleEvent.maximized(window,false));
            }
        });
    }
}
