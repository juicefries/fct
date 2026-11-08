//
// Created by juicefries
// The project name is fct
// Data 2026/11/08 02:21
//

package io.github.juicefries.fct.callback.event;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.system.Callback;

public final class CharEvent extends CallbackEventData {

    public final static String KEY_CHAR_EVENT = registerEventType("CharInput");

    private char code = '\u0000';

    private CharEvent() {

    }

    public static @NotNull CharEvent input(long window, char code) {
        var e = new CharEvent();
        e.window = window;
        e.type = KEY_CHAR_EVENT;
        e.code = code;
        return e;
    }

    public static @NotNull CharEvent input(char code) {
        return input(NULL_WINDOW,code);
    }

    @Contract(pure = true)
    public char code() {
        return code;
    }

    @Override
    public @NotNull CharEvent copy() {
        return input(window(),code());
    }

    @Override
    @Contract(pure = true)
    public Class<? extends Callback> getRequiredType() {
        return GLFWCharCallback.class;
    }

}
