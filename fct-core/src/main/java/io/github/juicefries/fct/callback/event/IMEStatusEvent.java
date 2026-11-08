//
// Created by juicefries
// The project name is fct
// Data 2026/11/08 17:56
//

package io.github.juicefries.fct.callback.event;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFWIMEStatusCallback;
import org.lwjgl.system.Callback;

public final class IMEStatusEvent extends CallbackEventData {

    public final static String IME_STATUS_EVENT = registerEventType("IMEStatus");

    private boolean open = true;

    private IMEStatusEvent() {

    }

    @Contract(pure = true)
    public boolean isOpen() {
        return open;
    }

    public static @NotNull IMEStatusEvent open(long window, boolean open) {
        var e = new IMEStatusEvent();
        e.window = window;
        e.type = IME_STATUS_EVENT;
        e.open = open;
        return e;
    }

    public static @NotNull IMEStatusEvent open(boolean open) {
        return open(NULL_WINDOW,open);
    }

    @Override
    public @NotNull IMEStatusEvent copy() {
        return open(window(),isOpen());
    }

    @Override
    @Contract(pure = true)
    public Class<? extends Callback> getRequiredType() {
        return GLFWIMEStatusCallback.class;
    }
}
