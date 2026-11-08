//
// Created by juicefries
// The project name is fct
// Data 2026/11/08 17:35
//

package io.github.juicefries.fct.callback.handler;

import io.github.juicefries.fct.callback.Event;
import io.github.juicefries.fct.callback.event.CallbackEventData;
import io.github.juicefries.fct.callback.event.IMEStatusEvent;
import io.github.juicefries.fct.callback.listener.IMEStatusListener;
import io.github.juicefries.fct.util.Array;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWIMEStatusCallback;

public final class IMEStatusHandler extends GLFWIMEStatusCallback implements Handler {

    public IMEStatusHandler() {

    }

    @Override
    public void register(long window) {
        check(GLFW.glfwSetIMEStatusCallback(window,this));
    }

    @Override
    public void simulate(@NotNull CallbackEventData event) {
        if (!(event instanceof IMEStatusEvent e)) return;
        var container = Event.get(e.window());
        var listeners = container.getListeners(IMEStatusListener.class);
        Array.forArr(listeners, listener -> listener.event(e.copy()));
    }

    @Override
    public void invoke(long window) {
        var imeMode = GLFW.glfwGetInputMode(window, GLFW.GLFW_IME);
        var open = imeMode ==GLFW.GLFW_TRUE;
        var container = Event.get(window);
        var listeners = container.getListeners(IMEStatusListener.class);
        IMEStatusEvent event = IMEStatusEvent.open(window,open);
        Array.forArr(listeners, listener -> listener.event(event.copy()));
    }

}
