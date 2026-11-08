//
// Created by juicefries
// The project name is fct
// Data 2026/11/07 23:55
//

package io.github.juicefries.fct.callback.event;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFWWindowContentScaleCallback;
import org.lwjgl.system.Callback;

public final class ContentScaleEvent extends CallbackEventData {

    public final static String WINDOW_CONTENT_SCALE_EVENT = registerEventType("ContentScale");

    private float xScale = 1.0f;
    private float yScale = 1.0f;

    private ContentScaleEvent() {

    }

    public static @NotNull ContentScaleEvent scale(long window, float xScale, float yScale) {
        var e = new ContentScaleEvent();
        e.window = window;
        e.xScale = xScale;
        e.yScale = yScale;
        e.type = WINDOW_CONTENT_SCALE_EVENT;
        return e;
    }

    public static @NotNull ContentScaleEvent scale(float xScale, float yScale) {
        return scale(NULL_WINDOW,xScale,yScale);
    }

    @Contract(pure = true)
    public float xScale() {
        return xScale;
    }

    @Contract(pure = true)
    public float yScale() {
        return yScale;
    }

    @Override
    public @NotNull ContentScaleEvent copy() {
        return scale(window(),xScale(),yScale());
    }

    @Contract(pure = true)
    @Override
    public Class<? extends Callback> getRequiredType() {
        return GLFWWindowContentScaleCallback.class;
    }
}
