//
// Created by juicefries
// The project name is fct
// Data 2026/11/08 02:33
//

package io.github.juicefries.fct.callback.event;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.system.Callback;

public final class ScrollEvent extends CallbackEventData {

    public final static String MOUSE_SCROLL_EVENT = registerEventType("MouseScroll");

    double xOffset = 0.0d;
    double yOffset = 0.0d;
    double x = 0.0d;
    double y = 0.0d;

    private ScrollEvent() {

    }

    @Contract(pure = true)
    public double xOffset() {
        return xOffset;
    }

    @Contract(pure = true)
    public double yOffset() {
        return yOffset;
    }

    @Contract(pure = true)
    public double x() {
        return x;
    }

    @Contract(pure = true)
    public double y() {
        return y;
    }

    public static @NotNull ScrollEvent scroll(long window, double xOffset, double yOffset,double x,double y) {
        var e = new ScrollEvent();
        e.window = window;
        e.type = MOUSE_SCROLL_EVENT;
        e.xOffset = xOffset;
        e.yOffset = yOffset;
        e.x = x;
        e.y = y;
        return e;
    }

    public static @NotNull ScrollEvent scroll(double xOffset, double yOffset,double x,double y) {
        return scroll(NULL_WINDOW,xOffset,yOffset,x,y);
    }

    public static @NotNull ScrollEvent scroll(long window, double yOffset,double x,double y) {
        return scroll(window,0.0d,yOffset,x,y);
    }

    public static @NotNull ScrollEvent scroll(double yOffset,double x,double y) {
        return scroll(0.0d,yOffset,x,y);
    }


    @Override
    public @NotNull ScrollEvent copy() {
        return scroll(window(),xOffset(),yOffset(),x(),y());
    }

    @Override
    @Contract(pure = true)
    public Class<? extends Callback> getRequiredType() {
        return GLFWScrollCallback.class;
    }

}
