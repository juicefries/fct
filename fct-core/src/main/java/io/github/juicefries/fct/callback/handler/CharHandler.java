//
// Created by juicefries
// The project name is fct
// Data 2026/11/08 02:20
//

package io.github.juicefries.fct.callback.handler;

import io.github.juicefries.fct.callback.Event;
import io.github.juicefries.fct.callback.event.CallbackEventData;
import io.github.juicefries.fct.callback.event.CharEvent;
import io.github.juicefries.fct.callback.listener.CharListener;
import io.github.juicefries.fct.util.Array;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharCallback;

public final class CharHandler extends GLFWCharCallback implements Handler {

    public CharHandler() {

    }

    @Override
    public void register(long window) {
        check(GLFW.glfwSetCharCallback(window,this));
    }

    @Override
    public void simulate(@NotNull CallbackEventData event) {
        if (!(event instanceof CharEvent e)) return;
        invoke(e.window(),e.code());
    }

    @Override
    public void invoke(long window, int codepoint) {
        var listeners = Event.getListeners(window, CharListener.class);
        CharEvent e = CharEvent.input(window,(char) codepoint);
        Array.forArr(listeners,listener -> listener.input(e.copy()));
    }
}
