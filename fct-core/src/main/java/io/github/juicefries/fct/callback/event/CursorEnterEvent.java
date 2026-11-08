//
// Created by juicefries
// The project name is fct
// Data 2026/11/08 13:17
//

package io.github.juicefries.fct.callback.event;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFWCursorEnterCallback;
import org.lwjgl.system.Callback;

public final class CursorEnterEvent extends CallbackEventData {

    public final static String CURSOR_ENTER_EVENT = registerEventType("CursorEnter");
    public final static String CURSOR_LEAVE_EVENT = registerEventType("CursorLeave");

    private CursorEnterEvent() {

    }

    private static @NotNull CursorEnterEvent event(long window, String type) {
        var e = new CursorEnterEvent();
        e.window = window;
        e.type = type;
        return e;
    }

    public static @NotNull CursorEnterEvent enter(long window) {
        return event(window,CURSOR_ENTER_EVENT);
    }

    public static @NotNull CursorEnterEvent enter() {
        return enter(NULL_WINDOW);
    }

    public static @NotNull CursorEnterEvent leave(long window) {
        return event(window,CURSOR_LEAVE_EVENT);
    }

    public static @NotNull CursorEnterEvent leave() {
        return leave(NULL_WINDOW);
    }

    @Override
    public CursorEnterEvent copy() {
        return event(window(),type());
    }

    @Override
    public Class<? extends Callback> getRequiredType() {
        return GLFWCursorEnterCallback.class;
    }

}
