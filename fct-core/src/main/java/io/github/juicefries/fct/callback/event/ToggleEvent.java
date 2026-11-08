//
// Created by juicefries
// The project name is fct
// Data 2026/11/08 00:44
//

package io.github.juicefries.fct.callback.event;

import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFWWindowIconifyCallback;
import org.lwjgl.glfw.GLFWWindowMaximizeCallback;
import org.lwjgl.system.Callback;

public final class ToggleEvent extends CallbackEventData {

    /**
     * 窗口最大化事件
     * @since 1.0.7
     */
    public final static String WINDOW_MAXIMIZED_EVENT = registerEventType("Maximized");

    /**
     * 窗口最小化事件
     * @since 1.0.7
     */
    public final static String WINDOW_MINIMIZED_EVENT = registerEventType("Minimized");

    /**
     * 窗口恢复事件
     * @since 1.0.7
     */
    public final static String WINDOW_RESTORED_EVENT = registerEventType("Restored");

    /**
     * 无
     * <p>
     *     即{@link io.github.juicefries.fct.util.Util#turn(String) Util.turn("None")}
     * </p>
     * @since 1.0.7
     */
    public final static long NONE = 0x6ADF97F83ACF3453L;

    /**
     * 从最大化恢复
     * <p>
     *     即{@link io.github.juicefries.fct.util.Util#turn(String) Util.trun("RestoreFromMaximize")}
     * </p>
     * @since 1.0.7
     */
    public final static long RESTORE_FROM_MAXIMIZE = 0x71169CE68FAE3C4CL;

    /**
     * 从最小化恢复
     * <p>
     *     {@link io.github.juicefries.fct.util.Util#turn(String) Util.trun("RestoreFromMinimized")}
     * </p>
     * @since 1.0.7
     */
    public final static long RESTORE_FROM_MINIMIZED = 0x6F8CB9D572BD31A7L;

    @MagicConstant(intValues = {NONE,RESTORE_FROM_MAXIMIZE,RESTORE_FROM_MINIMIZED})
    private long restoreFromWhere = NONE;

    private ToggleEvent() {

    }

    /**
     * 从何处恢复
     * @return 返回恢复来源的常量值
     * @since 1.0.7
     * @see #WINDOW_MAXIMIZED_EVENT
     * @see #WINDOW_MINIMIZED_EVENT
     * @see #WINDOW_RESTORED_EVENT
     */
    @Contract(pure = true)
    public @MagicConstant(intValues = {NONE,RESTORE_FROM_MAXIMIZE,RESTORE_FROM_MINIMIZED}) long restoreFromWhere()
    {
        return restoreFromWhere;
    }


    private static @NotNull ToggleEvent toggle(
            long window, String type,
            @MagicConstant(intValues = {NONE,RESTORE_FROM_MAXIMIZE,RESTORE_FROM_MINIMIZED}) long rfw
    ) {
        var e = new ToggleEvent();
        e.window = window;
        e.type = type;
        e.restoreFromWhere = rfw;
        return e;
    }

    public static @NotNull ToggleEvent maximized(long window, boolean maximized) {
        if (maximized) {
            return toggle(window,WINDOW_MAXIMIZED_EVENT,NONE);
        } else {
            return toggle(window,WINDOW_RESTORED_EVENT,RESTORE_FROM_MAXIMIZE);
        }
    }

    public static @NotNull ToggleEvent minimized(long window, boolean minimized) {
        if (minimized) {
            return toggle(window,WINDOW_MINIMIZED_EVENT,NONE);
        } else {
            return toggle(window,WINDOW_RESTORED_EVENT,RESTORE_FROM_MINIMIZED);
        }
    }

    public static @NotNull ToggleEvent maximized(boolean maximized) {
        return maximized(NULL_WINDOW,maximized);
    }

    public static @NotNull ToggleEvent minimized(boolean minimized) {
        return minimized(NULL_WINDOW,minimized);
    }

    @Override
    public @NotNull ToggleEvent copy() {
        return toggle(window(),type(),restoreFromWhere());
    }

    @Override
    @Contract(pure = true)
    @Deprecated(since = "1.0.7",forRemoval = true)
    public Class<? extends Callback> getRequiredType() {
        return GLFWWindowMaximizeCallback.class;
    }

    @Override
    @Contract(value = " -> new", pure = true)
    public Class<? extends Callback> @NotNull [] getRequiredTypes() {
        //noinspection unchecked
        return new Class[] {GLFWWindowMaximizeCallback.class, GLFWWindowIconifyCallback.class};
    }

}
