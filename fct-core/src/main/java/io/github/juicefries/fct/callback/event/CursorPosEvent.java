//
// Created by juicefries
// The project name is fct
// Data 2026/11/08 02:43
//

package io.github.juicefries.fct.callback.event;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.system.Callback;

public final class CursorPosEvent extends CallbackEventData{

    public final static String MOUSE_CURSOR_POS_EVENT = registerEventType("CursorPos");

    double x = 0.0d;
    double y = 0.0d;

    private CursorPosEvent() {

    }

    @Contract(pure = true)
    public double x() {
        return x;
    }

    @Contract(pure = true)
    public double y() {
        return y;
    }

    public static @NotNull CursorPosEvent pos(long window, double x, double y) {
        var e = new CursorPosEvent();
        e.window = window;
        e.type = MOUSE_CURSOR_POS_EVENT;
        e.x = x;
        e.y = y;
        return e;
    }

    public static @NotNull CursorPosEvent pos(double x, double y) {
        return pos(NULL_WINDOW,x,y);
    }


    @Override
    public @NotNull CursorPosEvent copy() {
        return pos(window(),x(),y());
    }

    @Override
    @Contract(pure = true)
    public Class<? extends Callback> getRequiredType() {
        return GLFWCursorPosCallback.class;
    }

}
