//
// Created by juicefries
// The project name is fct
// Data 2026/11/08 00:25
//

package io.github.juicefries.fct.callback.event;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.system.Callback;

public final class FocusEvent extends CallbackEventData {

    public final static String WINDOW_FOCUS_EVENT = registerEventType("Focus");

    boolean focused = false;

    private FocusEvent() {

    }

    @Contract(pure = true)
    public boolean focused() {
        return focused;
    }

    public static @NotNull FocusEvent focus(long window, boolean focused) {
        var e = new FocusEvent();
        e.window = window;
        e.focused = focused;
        e.type = WINDOW_FOCUS_EVENT;
        return e;
    }

    public static @NotNull FocusEvent focus(boolean focused) {
        return focus(NULL_WINDOW,focused);
    }

    @Override
    @Contract(pure = true)
    public Class<? extends Callback> getRequiredType() {
        return GLFWWindowFocusCallback.class;
    }

    public @NotNull FocusEvent copy() {
        return focus(window(),focused());
    }
}
