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
// Data 2026/10/23 02:57
//

package io.github.juicefries.fct.lwjgl;

import io.github.juicefries.fct.sign.Initializable;
import io.github.juicefries.fct.util.Lock;
import io.github.juicefries.fct.util.Parameters;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@ApiStatus.Experimental
@Deprecated(since = "1.0.4")
public abstract class LwjglCanvas implements Initializable,Runnable {

    private final static Parameters<String,Object> defaults = new Parameters<>();

    final AtomicBoolean initialize = new AtomicBoolean(false);
    final Lock lwjglCanvasInitLock = Lock.create("LwjglCanvasInitLock");

    long window = MemoryUtil.NULL;
    Parameters<String,Object> parameters = new Parameters<>();
    Map<String,Integer> hints = new HashMap<>();

    static {
        defaults.put("Width",800);
        defaults.put("Height",600);
        defaults.put("Title","");
    }

    public LwjglCanvas() {
        this(true);
    }

    public LwjglCanvas(boolean loadDefaults) {
        if (loadDefaults) {
            for (String key : defaults.keySet()) {
                var val = defaults.get(key);
                parameters.put(key,false,val);
            }
        }
    }

    @Override
    public void initialize() {
        synchronized (lwjglCanvasInitLock) {
            if (isInitialize()) {
                throw new IllegalStateException("LwjglCanvas has been initialized!");
            }
            var width = parameters.get("Width", 800);
            var height = parameters.get("Height", 600);
            var title = parameters.get("Title", "");
            List<Hint> list = new ArrayList<>();
            hints.forEach((_hint_str, _value_integer) -> {
                var value = Objects.requireNonNullElse(_value_integer, 0);
                var hint = WindowHint.hint(_hint_str,value);
                list.add(hint);
            });
            Hint[] hints = list.toArray(new Hint[0]);
            this.window = _GLFW_API._create_window(width, height, title,hints);
            initialize.set(true);
            init();
        }
    }

    @Override
    public void dispose() {
        synchronized (lwjglCanvasInitLock) {
            if (!isInitialize()) {
                throw new IllegalStateException("The window has already been destroyed!");
            }
            destroy();
            GLFW.glfwDestroyWindow(window);
            initialize.set(false);
        }
    }

    @Override
    public void run() {
        if (!isInitialize()) return;
        draw();

        if (!isInitialize()) return;
        GLFW.glfwSwapBuffers(window);

        if (!isInitialize()) return;
        _GL_API._poll_events();
    }

    public abstract void draw();
    public abstract void init();
    public abstract void destroy();

    public void setWindowHint(String hint, int value) {
        if (hint == null) {
            throw new NullPointerException("hint is null!");
        }
        hints.put(hint,value);
    }

    public void setWindowHint(String hint,boolean value) {
        setWindowHint(hint, WindowHint.convert(value));
    }

    public void setWindowWidth(int width) {
        parameters.put("Width",width);
    }

    public void setWindowHeight(int height) {
        parameters.put("Height",height);
    }

    public void setWindowSize(int width,int height) {
        setWindowWidth(width);
        setWindowHeight(height);
    }

    public void setWindowTitle(String title) {
        var value = Objects.requireNonNullElse(title, "");
        parameters.put("Title",value);
    }

    @Override
    public boolean isInitialize() {
        return initialize.get();
    }

    public long getWindow() {
        return window;
    }

}
