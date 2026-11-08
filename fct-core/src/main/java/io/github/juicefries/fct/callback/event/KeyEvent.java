//
// Created by juicefries
// The project name is fct
// Data 2026/11/08 01:47
//

package io.github.juicefries.fct.callback.event;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.system.Callback;

public final class KeyEvent extends CallbackEventData {

    public final static String KEY_PRESS_EVENT = registerEventType("KeyPressInput");
    public final static String KEY_REPEAT_EVENT = registerEventType("KeyRepeatInput");
    public final static String KEY_RELEASE_EVENT = registerEventType("KeyReleaseInput");

    int key = GLFW.GLFW_KEY_UNKNOWN;
    int mods = 0;

    private KeyEvent() {

    }

    public static @NotNull KeyEvent input(long window, int key, int mods,String type) {
        var e = new KeyEvent();
        e.window = window;
        e.key = key;
        e.mods = mods;
        e.type = type;
        return e;
    }

    public static @NotNull KeyEvent input(int key,int mods,String type) {
        return input(NULL_WINDOW,key,mods,type);
    }

    public static @NotNull KeyEvent input(long window,int key,String type) {
        return input(window,key,0,type);
    }

    public static @NotNull KeyEvent input(int key,String type) {
        return input(NULL_WINDOW,key,type);
    }

    @Contract(pure = true)
    public int key() {
        return key;
    }

    @Contract(pure = true)
    public int mods() {
        return mods;
    }


    @Override
    public @NotNull CallbackEventData copy() {
        return input(window(),key(),mods(),type());
    }

    @Override
    @Contract(pure = true)
    public Class<? extends Callback> getRequiredType() {
        return GLFWKeyCallback.class;
    }

    public int getKeyScancode(){
        return GLFW.glfwGetKeyScancode(key());
    }

    public static int getKeyScancode(int key) {
        return GLFW.glfwGetKeyScancode(key);
    }

}
