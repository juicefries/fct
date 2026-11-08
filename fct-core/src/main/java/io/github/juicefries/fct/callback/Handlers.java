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
// Data 2026/08/23 22:09
//

package io.github.juicefries.fct.callback;

import io.github.juicefries.fct.callback.handler.*;
import org.lwjgl.glfw.*;
import org.lwjgl.system.Callback;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 「回调名 → 默认 Handler 类」的注册表。
 *
 * <p>默认那一套（{@code Close → DefaultCloseHandler} 之类）在静态块里灌好，
 * 构造函数传 {@code false} 就能拿到一张空表，自己往里放。
 *
 * <p>{@code CallbackManager} 会用它反射造实例。
 *
 * @author DeepSeek (AI)
 * @since 0.0.3
 */
public final class Handlers extends ConcurrentHashMap<Class<? extends Callback>,Class<? extends Handler>> {

    private final static Handlers defaults = new Handlers(false);

    static {
        // 窗口
        defaults.put(GLFWWindowCloseCallback.class, CloseHandler.class);
        defaults.put(GLFWWindowFocusCallback.class, FocusHandler.class);
        defaults.put(GLFWWindowSizeCallback.class, SizeHandler.class);
        defaults.put(GLFWFramebufferSizeCallback.class, FramebufferSizeHandler.class);
        defaults.put(GLFWWindowPosCallback.class, PosHandler.class);
        defaults.put(GLFWWindowRefreshCallback.class, RefreshHandler.class);
        defaults.put(GLFWWindowContentScaleCallback.class, ContentScaleHandler.class);
        defaults.put(GLFWWindowMaximizeCallback.class, MaximizeHandler.class);
        defaults.put(GLFWWindowIconifyCallback.class, MinimizedHandler.class);

        // 键盘
        defaults.put(GLFWKeyCallback.class, KeyHandler.class);
        defaults.put(GLFWCharCallback.class, CharHandler.class);

        // 鼠标
        defaults.put(GLFWScrollCallback.class, ScrollHandler.class);
        defaults.put(GLFWCursorPosCallback.class, CursorPosHandler.class);
        defaults.put(GLFWDropCallback.class, DropHandler.class);
        defaults.put(GLFWCursorEnterCallback.class, CursorEnterHandler.class);
        defaults.put(GLFWMouseButtonCallback.class, ButtonHandler.class);

        // 输入法
        defaults.put(GLFWIMEStatusCallback.class, IMEStatusHandler.class);
        defaults.put(GLFWPreeditCallback.class, PreeditHandler.class);
        defaults.put(GLFWPreeditCandidateCallback.class, PreeditCandidateHandler.class);
    }


    public Handlers(boolean loadDefaults) {
        if (loadDefaults) {
            putAll(Handlers.defaults);
        }
    }

    public Handlers() {
        this(true);
    }

}
