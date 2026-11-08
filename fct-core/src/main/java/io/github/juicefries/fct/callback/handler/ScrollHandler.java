//
// Created by juicefries
// The project name is fct
// Data 2026/11/08 02:32
//

package io.github.juicefries.fct.callback.handler;

import io.github.juicefries.fct.callback.Event;
import io.github.juicefries.fct.callback.event.CallbackEventData;
import io.github.juicefries.fct.callback.event.ScrollEvent;
import io.github.juicefries.fct.callback.listener.ScrollListener;
import io.github.juicefries.fct.lwjgl.Mouse;
import io.github.juicefries.fct.util.Array;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWScrollCallback;

public final class ScrollHandler extends GLFWScrollCallback implements Handler {

    public ScrollHandler() {

    }

    @Override
    public void register(long window) {
        check(GLFW.glfwSetScrollCallback(window,this));
    }

    @Override
    public void simulate(@NotNull CallbackEventData event) {
        if ((!(event instanceof ScrollEvent e))) return;
        var listeners = Event.getListeners(e.window(), ScrollListener.class);
        Array.forArr(listeners,listener -> listener.scroll(e.copy()));
    }

    @Override
    public void invoke(long window, double xoffset, double yoffset) {
        var listeners = Event.getListeners(window, ScrollListener.class);
        var location = Mouse.getCursorPos(window);
        double x = location.x;
        double y = location.y;
        var e = ScrollEvent.scroll(window,xoffset,yoffset,x,y);
        Array.forArr(listeners,listener -> listener.scroll(e.copy()));
    }

}
