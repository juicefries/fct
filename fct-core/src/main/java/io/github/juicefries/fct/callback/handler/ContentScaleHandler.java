//
// Created by juicefries
// The project name is fct
// Data 2026/11/07 23:54
//

package io.github.juicefries.fct.callback.handler;

import io.github.juicefries.fct.callback.Event;
import io.github.juicefries.fct.callback.event.CallbackEventData;
import io.github.juicefries.fct.callback.event.ContentScaleEvent;
import io.github.juicefries.fct.callback.listener.ContentScaleListener;
import io.github.juicefries.fct.util.Array;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWWindowContentScaleCallback;

public final class ContentScaleHandler extends GLFWWindowContentScaleCallback implements Handler {

    public ContentScaleHandler() {

    }

    @Override
    public void register(long window) {
        check(GLFW.glfwSetWindowContentScaleCallback(window,this));
    }

    @Override
    public void simulate(@NotNull CallbackEventData event) {
        if (!(event instanceof ContentScaleEvent cse)) return;
        invoke(cse.window(),cse.xScale(),cse.yScale());
    }

    @Override
    public void invoke(long window, float xScale, float yScale) {
        var container = Event.get(window);
        if (container == null) return;
        var listeners = container.getListeners(ContentScaleListener.class);
        var e = ContentScaleEvent.scale(window,xScale,yScale);
        Array.forArr(listeners, listener -> listener.scale(e.copy()));
    }

}
