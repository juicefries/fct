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

package io.github.juicefries.fct.callback.handler;

import io.github.juicefries.fct.callback.Data;
import io.github.juicefries.fct.callback.Handler;
import io.github.juicefries.fct.util.Lock;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.system.Callback;

public abstract class KeyInputHandler extends GLFWKeyCallback implements KeyHandler {

    private final Lock lock = Lock.create();
    private final Data data = new Data();

    protected KeyInputHandler() {

    }

    @Override
    public final @Nullable Callback register(long window) {
        return check(GLFW.glfwSetKeyCallback(window, this));
    }

    @Override
    public final void free() {
        freeHandler();
        synchronized (getLock()) {
            data.clear();
        }
        super.free();
    }

    @Override
    @Contract(pure = true)
    public final @NotNull String getType() {
        return Handler.KEY_INPUT;
    }

    protected Lock getLock() {
        return lock;
    }

    @Override
    public Data getUpdateData() {
        return data;
    }

}
