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

package io.github.juicefries.fct.callback;

import io.github.juicefries.fct.sign.Copyable;
import io.github.juicefries.fct.sign.Readonly;
import io.github.juicefries.fct.util.Array;
import io.github.juicefries.fct.util.Settings;
import java.util.List;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryUtil;

// field
public class Data extends Settings implements Copyable, Readonly {

    public Data() {

    }

    public Data(GLFWCallbackList list,long window,boolean keep) {
        putGLFWCallbackList(list);
        putLong("Window",window);
        putBoolean("Keep",keep);
    }

    public void putGLFWCallbackList(GLFWCallbackList list) {
        put("GLFWCallbackList",list);
    }

    public long getWindow() {
        return getLong("Window", MemoryUtil.NULL);
    }

    public boolean isKeep() {
        return getBoolean("Keep",false);
    }

    public List<Callback> getGlfwCallbacks() {

        Object object = get("GlfwCallbacks");
        if (object == null) return List.of();

        if (!(object instanceof List<?> list)) {
            return List.of();
        }

        if (list.isEmpty())  {
            return List.of();
        }

        if (!Array.allMatch(list,Callback.class)) {
            return List.of();
        }

        //noinspection unchecked
        return (List<Callback>) list;
    }

    public GLFWCallbackList getGlfwCallbackList() {
        var list = get("GLFWCallbackList");
        if (list == null) {
            return new GLFWCallbackList();
        }
        if (!(list instanceof GLFWCallbackList callbackList)) {
            return new GLFWCallbackList();
        }
        return callbackList;
    }

    @Override
    public Data copy() {
        Data data = new Data();
        data.clear();
        data.putAll(this);
        return data;
    }

}
