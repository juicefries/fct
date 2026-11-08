//
// Created by juicefries
// The project name is fct
// Data 2026/11/08 13:33
//

package io.github.juicefries.fct.callback.handler;

import io.github.juicefries.fct.Location;
import io.github.juicefries.fct.callback.Event;
import io.github.juicefries.fct.callback.event.ButtonEvent;
import io.github.juicefries.fct.callback.event.CallbackEventData;
import io.github.juicefries.fct.callback.listener.ButtonListener;
import io.github.juicefries.fct.lwjgl.Mouse;
import io.github.juicefries.fct.util.Array;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import java.util.Objects;

public final class ButtonHandler extends GLFWMouseButtonCallback implements Handler {

    public ButtonHandler() {

    }

    @Override
    public void register(long window) {
        check(GLFW.glfwSetMouseButtonCallback(window,this));
    }

    @Override
    public void simulate(@NotNull CallbackEventData event) {
        if (!(event instanceof ButtonEvent e)) return;
        var container = Event.get(e.window());
        var listeners = container.getListeners(ButtonListener.class);
        Array.forArr(listeners,listener -> {
            if (Objects.equals(e.type(), ButtonEvent.BUTTON_PRESS_EVENT)) {
                listener.press(e.copy());
            }
            if (Objects.equals(e.type(), ButtonEvent.BUTTON_RELEASE_EVENT)) {
                listener.release(e.copy());
            }
        });
    }

    @Override
    public void invoke(long window, int button, int action, int mods) {
        var container = Event.get(window);
        var listeners = container.getListeners(ButtonListener.class);
        var location = Mouse.getCursorPos(window);
        double x = location.x;
        double y = location.y;
        boolean press = (action == GLFW.GLFW_PRESS);
        Array.forArr(listeners,listener -> {
            if (press) {
                //noinspection MagicConstant
                listener.press(ButtonEvent.press(window,button,mods,x,y));
            } else {
                //noinspection MagicConstant
                listener.release(ButtonEvent.release(window,button,mods,x,y));
            }
        });
    }

}
