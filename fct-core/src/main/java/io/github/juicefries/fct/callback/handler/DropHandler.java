//
// Created by juicefries
// The project name is fct
// Data 2026/11/08 02:57
//

package io.github.juicefries.fct.callback.handler;

import io.github.juicefries.fct.callback.Event;
import io.github.juicefries.fct.callback.event.CallbackEventData;
import io.github.juicefries.fct.callback.event.DropEvent;
import io.github.juicefries.fct.callback.listener.DropListener;
import io.github.juicefries.fct.lwjgl.Mouse;
import io.github.juicefries.fct.util.Array;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWDropCallback;

public final class DropHandler extends GLFWDropCallback implements Handler {

    public DropHandler() {

    }

    @Override
    public void register(long window) {
        check(GLFW.glfwSetDropCallback(window,this));
    }

    @Override
    public void simulate(@NotNull CallbackEventData event) {
        if (!(event instanceof DropEvent e)) return;
        var listeners = Event.getListeners(e.window(), DropListener.class);
        Array.forArr(listeners,listener -> listener.drop(e.copy()));
    }

    @Override
    public void invoke(long window, int count, long names) {
        var listeners = Event.getListeners(window, DropListener.class);
        final String[] filePaths = new String[count];
        for (int i = 0; i < count; i++) {
            filePaths[i] = GLFWDropCallback.getName(names, i);
        }
        var location = Mouse.getCursorPos(window);
        double x = location.x;
        double y = location.y;
        DropEvent e = DropEvent.drop(window,x,y,filePaths);
        Array.forArr(listeners,listener -> listener.drop(e.copy()));
    }
}
