//
// Created by juicefries
// The project name is fct
// Data 2026/11/08 01:45
//

package io.github.juicefries.fct.callback.handler;

import io.github.juicefries.fct.callback.Event;
import io.github.juicefries.fct.callback.event.CallbackEventData;
import io.github.juicefries.fct.callback.event.KeyEvent;
import io.github.juicefries.fct.callback.listener.KeyListener;
import io.github.juicefries.fct.util.Array;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

import java.util.Objects;

public final class KeyHandler extends GLFWKeyCallback implements Handler {

    public KeyHandler() {

    }

    @Override
    public void register(long window) {
        check(GLFW.glfwSetKeyCallback(window,this));
    }

    @Override
    public void simulate(@NotNull CallbackEventData event) {
        if (!(event instanceof KeyEvent e)) return;

        if (Objects.equals(e.type(), KeyEvent.KEY_PRESS_EVENT)) {
            invoke(e.window(),e.key(),e.getKeyScancode(),GLFW.GLFW_PRESS,e.mods());
        }
        if (Objects.equals(e.type(), KeyEvent.KEY_REPEAT_EVENT)) {
            invoke(e.window(),e.key(),e.getKeyScancode(),GLFW.GLFW_REPEAT,e.mods());
        }
        if (Objects.equals(e.type(), KeyEvent.KEY_RELEASE_EVENT)) {
            invoke(e.window(),e.key(),e.getKeyScancode(),GLFW.GLFW_RELEASE,e.mods());
        }
    }

    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        var listeners = Event.getListeners(window, KeyListener.class);
        Array.forArr(listeners,listener -> {
            if (action == GLFW.GLFW_PRESS) {
                listener.press(KeyEvent.input(window,key,mods,KeyEvent.KEY_PRESS_EVENT));
            }

            if (action == GLFW.GLFW_REPEAT) {
                listener.repeat(KeyEvent.input(window,key,mods,KeyEvent.KEY_REPEAT_EVENT));
            }

            if (action == GLFW.GLFW_RELEASE) {
                listener.release(KeyEvent.input(window,key,mods,KeyEvent.KEY_RELEASE_EVENT));
            }
        });
    }
}
