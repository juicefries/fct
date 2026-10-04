/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2026 juicefries
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

//
// Created by juicefries
// The project name is fct
// Data 2026/10/02 23:40
//

package io.github.juicefries.fct.lwjgl;

import io.github.juicefries.fct.Sys;
import io.github.juicefries.fct.event.SysEvent;
import io.github.juicefries.fct.event.SystemListener;
import io.github.juicefries.fct.util.Lock;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class WindowHint {


    private final static Map<String, Integer> mapping = new ConcurrentHashMap<>();
    private final static Map<Integer, String> nameMapping = new ConcurrentHashMap<>();
    private final static Lock lock = Lock.create();
    private final static SystemListener listener = SystemListener.createPassiveListener(
            "FCTWindowHintSystemListener",
            WindowHint.class,
            e ->
            {
                if (e.getType() == SysEvent.SYS_INIT_EVENT) {
                    initMapping();
                }
                if (e.getType() == SysEvent.SYS_CANCEL_EVENT || e.getType() == SysEvent.SYS_TERMINATE_EVENT) {
                    clearMapping();
                }
            }
    );

    static {
        registerSystemListener();
        synchronized (lock) {
            if (nameMapping.isEmpty() || mapping.isEmpty()) {
                initMapping();
            }
        }
    }

    private static void registerSystemListener() {
        Sys.register(listener);
    }

    private static void initMapping() {
        clearMapping();

        // 窗口
        addMapping("resizable", GLFW.GLFW_RESIZABLE);
        addMapping("visible", GLFW.GLFW_VISIBLE);
        addMapping("decorated", GLFW.GLFW_DECORATED);
        addMapping("focused", GLFW.GLFW_FOCUSED);
        addMapping("autoIconify", GLFW.GLFW_AUTO_ICONIFY);
        addMapping("floating", GLFW.GLFW_FLOATING);
        addMapping("maximized", GLFW.GLFW_MAXIMIZED);
        addMapping("centerCursor", GLFW.GLFW_CENTER_CURSOR);
        addMapping("transparentFramebuffer", GLFW.GLFW_TRANSPARENT_FRAMEBUFFER);
        addMapping("focusOnShow", GLFW.GLFW_FOCUS_ON_SHOW);
        addMapping("scaleToMonitor", GLFW.GLFW_SCALE_TO_MONITOR);
        addMapping("scaleFramebuffer", GLFW.GLFW_SCALE_FRAMEBUFFER);
        addMapping("mousePassthrough", GLFW.GLFW_MOUSE_PASSTHROUGH);
        addMapping("positionX", GLFW.GLFW_POSITION_X);
        addMapping("positionY", GLFW.GLFW_POSITION_Y);

        // 帧缓冲
        addMapping("redBits", GLFW.GLFW_RED_BITS);
        addMapping("greenBits", GLFW.GLFW_GREEN_BITS);
        addMapping("blueBits", GLFW.GLFW_BLUE_BITS);
        addMapping("alphaBits", GLFW.GLFW_ALPHA_BITS);
        addMapping("depthBits", GLFW.GLFW_DEPTH_BITS);
        addMapping("stencilBits", GLFW.GLFW_STENCIL_BITS);
        addMapping("accumRedBits", GLFW.GLFW_ACCUM_RED_BITS);
        addMapping("accumGreenBits", GLFW.GLFW_ACCUM_GREEN_BITS);
        addMapping("accumBlueBits", GLFW.GLFW_ACCUM_BLUE_BITS);
        addMapping("accumAlphaBits", GLFW.GLFW_ACCUM_ALPHA_BITS);
        addMapping("auxBuffers", GLFW.GLFW_AUX_BUFFERS);
        addMapping("stereo", GLFW.GLFW_STEREO);
        addMapping("samples", GLFW.GLFW_SAMPLES);
        addMapping("srgbCapable", GLFW.GLFW_SRGB_CAPABLE);
        addMapping("doubleBuffer", GLFW.GLFW_DOUBLEBUFFER);
        addMapping("refreshRate", GLFW.GLFW_REFRESH_RATE);

        // 上下文
        addMapping("clientApi", GLFW.GLFW_CLIENT_API);
        addMapping("contextCreationApi", GLFW.GLFW_CONTEXT_CREATION_API);
        addMapping("contextVersionMajor", GLFW.GLFW_CONTEXT_VERSION_MAJOR);
        addMapping("contextVersionMinor", GLFW.GLFW_CONTEXT_VERSION_MINOR);
        addMapping("contextRobustness", GLFW.GLFW_CONTEXT_ROBUSTNESS);
        addMapping("contextReleaseBehavior", GLFW.GLFW_CONTEXT_RELEASE_BEHAVIOR);
        addMapping("openglForwardCompat", GLFW.GLFW_OPENGL_FORWARD_COMPAT);
        addMapping("openglDebugContext", GLFW.GLFW_OPENGL_DEBUG_CONTEXT);
        addMapping("openglProfile", GLFW.GLFW_OPENGL_PROFILE);
        addMapping("contextNoError", GLFW.GLFW_CONTEXT_NO_ERROR);

        // 平台专属
        addMapping("win32KeyboardMenu", GLFW.GLFW_WIN32_KEYBOARD_MENU);
        addMapping("win32ShowDefault", GLFW.GLFW_WIN32_SHOWDEFAULT);
        addMapping("cocoaGraphicsSwitching", GLFW.GLFW_COCOA_GRAPHICS_SWITCHING);
    }


    public WindowHint() {

    }

    final List<Hint> hints = new ArrayList<>();

    public void add(String hint,int val) {
        hints.add(hint(hint, val));
    }

    public void remove(String hint) {
        int _hint_id = mapping.get(hint);
        Hint _hint = null;
        for (var _hint_val : hints) {
            if (_hint_val == null) continue;
            if (_hint_val.hint() == _hint_id) _hint = _hint_val;
        }
        hints.remove(_hint);
    }

    public String toHints() {
        StringBuilder text = new StringBuilder();
        text.append("{");

        for (var hint : hints) {
            text.append("\"%s\":\"%d\",".formatted(toHintName(hint.hint()), hint.value()));
        }

        text.append("}");

        return text.toString();
    }



    @Override
    public String toString() {
        return "%s%s".formatted(getClass().getCanonicalName(), toHints());
    }


    // ========================= UTIL =========================

    @Contract("null, _ -> fail")
    public static @NotNull Hint hint(String hint, int val) {
        if (hint == null) {
            throw new NullPointerException("hint is null!");
        }
        return Hint.create(mapping.get(hint),val);
    }

    @Contract("null, _ -> fail")
    public static @NotNull Hint hint(String hint, boolean val) {
        return hint(hint,convert(val));
    }

    @Contract(pure = true)
    public static @NotNull String toHintName(int hint) {
        String name = nameMapping.get(hint);
        return name != null ? name : "0x%08X".formatted(hint);
    }


    @Contract(pure = true)
    public static int convert(boolean bool) {
        if (bool) return GLFW.GLFW_TRUE;
        return GLFW.GLFW_FALSE;
    }

    @Contract(pure = true)
    public static boolean convert(int integer) {
        return integer == GLFW.GLFW_TRUE;
    }

    public static void addMapping(String name, int hint) {
        synchronized (lock) {
            mapping.put(name, hint);
            nameMapping.putIfAbsent(hint, name);
        }
    }

    private static void clearMapping() {
        synchronized (lock) {
            mapping.clear();
            nameMapping.clear();
        }
    }

    // 用来触发类加载
    @Contract(pure = true)
    public static void initialize() {

    }

}
