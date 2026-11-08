//
// Created by juicefries
// The project name is fct
// Data 2026/11/08 13:32
//

package io.github.juicefries.fct.callback.event;

import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.system.Callback;

public final class ButtonEvent extends CallbackEventData {

    public final static String BUTTON_PRESS_EVENT = registerEventType("ButtonPress");
    public final static String BUTTON_RELEASE_EVENT = registerEventType("ButtonRelease");

    public final static int MK_LEFT = GLFW.GLFW_MOUSE_BUTTON_LEFT;
    public final static int MK_RIGHT = GLFW.GLFW_MOUSE_BUTTON_RIGHT;
    public final static int MK_MIDDLE = GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

    double x = 0.0d;
    double y = 0.0d;

    @MagicConstant(intValues = {MK_LEFT,MK_MIDDLE,MK_RIGHT})
    int button = MK_LEFT;
    int mods = 0;

    private ButtonEvent() {

    }

    @Contract(pure = true)
    @MagicConstant(intValues = {MK_LEFT,MK_MIDDLE,MK_RIGHT})
    public int button() {
        return button;
    }

    @Contract(pure = true)
    public double x() {
        return x;
    }

    @Contract(pure = true)
    public double y() {
        return y;
    }

    @Contract(pure = true)
    public int mods() {
        return mods;
    }


    private static @NotNull ButtonEvent event(long window, @MagicConstant(intValues = {
            MK_LEFT,MK_MIDDLE,MK_RIGHT
    }) int button, int mods, double x, double y, String type) {
        var e = new ButtonEvent();
        e.window = window;
        e.button = button;
        e.mods = mods;
        e.x = x;
        e.y = y;
        e.type = type;
        return e;
    }

    public static @NotNull ButtonEvent press(long window, @MagicConstant(intValues = {
            MK_LEFT,MK_MIDDLE,MK_RIGHT
    }) int button, int mods, double x, double y) {
        return event(window,button,mods,x,y,BUTTON_PRESS_EVENT);
    }

    public static @NotNull ButtonEvent release(long window, @MagicConstant(intValues = {
            MK_LEFT,MK_MIDDLE,MK_RIGHT
    }) int button, int mods, double x, double y) {
        return event(window,button,mods,x,y,BUTTON_RELEASE_EVENT);
    }

    public static @NotNull ButtonEvent press(@MagicConstant(intValues = {
            MK_LEFT,MK_MIDDLE,MK_RIGHT
    })int button, int mods, double x, double y) {
        return press(NULL_WINDOW,button,mods,x,y);
    }

    public static @NotNull ButtonEvent release(@MagicConstant(intValues = {
            MK_LEFT,MK_MIDDLE,MK_RIGHT
    }) int button, int mods, double x, double y) {
        return release(NULL_WINDOW,button,mods,x,y);
    }

    public static @NotNull ButtonEvent press(long window,@MagicConstant(intValues = {
            MK_LEFT,MK_MIDDLE,MK_RIGHT
    })int button, double x, double y) {
        return press(window,button,0,x,y);
    }


    public static @NotNull ButtonEvent press(@MagicConstant(intValues = {
            MK_LEFT,MK_MIDDLE,MK_RIGHT
    })int button, double x, double y) {
        return press(NULL_WINDOW,button,0,x,y);
    }

    public static @NotNull ButtonEvent release(long window,@MagicConstant(intValues = {
            MK_LEFT,MK_MIDDLE,MK_RIGHT
    }) int button, double x, double y) {
        return release(window,button,0,x,y);
    }

    public static @NotNull ButtonEvent release(@MagicConstant(intValues = {
            MK_LEFT,MK_MIDDLE,MK_RIGHT
    }) int button, double x, double y) {
        return release(NULL_WINDOW,button,x,y);
    }

    @Override
    public @NotNull ButtonEvent copy() {
        return event(window(),button(),mods(),x(),y(),type());
    }

    @Override
    @Contract(pure = true)
    public Class<? extends Callback> getRequiredType() {
        return GLFWMouseButtonCallback.class;
    }

}
