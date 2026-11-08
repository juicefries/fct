//
// Created by juicefries
// The project name is fct
// Data 2026/11/08 02:57
//

package io.github.juicefries.fct.callback.event;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFWDropCallback;
import org.lwjgl.system.Callback;

public final class DropEvent extends CallbackEventData {

    public final static String MOUSE_DROP_EVENT = registerEventType("Drop");

    String[] filePaths = new String[0];
    double x = 0.0d;
    double y = 0.0d;

    private DropEvent() {

    }

    @Contract(pure = true)
    public String[] getFilePaths() {
        return filePaths;
    }

    @Contract(pure = true)
    public double x() {
        return x;
    }

    @Contract(pure = true)
    public double y(){
        return y;
    }

    @Contract("_, _, _, null -> fail")
    public static @NotNull DropEvent drop(long window, double x, double y, String... filePaths) {
        if (filePaths == null) {
            throw new NullPointerException("filePaths is null!");
        }

        var e = new DropEvent();
        e.window = window;
        e.type = MOUSE_DROP_EVENT;
        e.filePaths = filePaths.clone();
        e.x = x;
        e.y = y;
        return e;
    }

    @Contract("_, _, null -> fail")
    public static @NotNull DropEvent drop(double x, double y, String... filesPaths) {
        return drop(NULL_WINDOW,x,y,filesPaths);
    }

    @Override
    public @NotNull DropEvent copy() {
        return drop(window(),x(),y(),getFilePaths());
    }

    @Override
    @Contract(pure = true)
    public Class<? extends Callback> getRequiredType() {
        return GLFWDropCallback.class;
    }
}
