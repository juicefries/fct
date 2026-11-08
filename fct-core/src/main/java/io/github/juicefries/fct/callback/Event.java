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
// Data 2026/11/07 17:06
//

package io.github.juicefries.fct.callback;

import io.github.juicefries.fct.callback.event.CallbackEventData;
import io.github.juicefries.fct.callback.listener.CallbackListener;
import io.github.juicefries.fct.util.List;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.lwjgl.system.MemoryUtil.NULL;

public final class Event {

    private final static Map<Long,Container> containers = new ConcurrentHashMap<>();

    public final static String IME_OPEN = "IME-OPEN";

    public static void bind(long window) {
        if (window == NULL) {
            throw new IllegalArgumentException("Invalid window!");
        }
        var container = new Container();
        container.bind(window);
        container.initialize();

        var oldContainer = containers.get(window);
        if (oldContainer != null) {
            oldContainer.dispose();
        }

        containers.put(window,container);
    }

    public static void free(long window) {
        if (window == NULL) {
            throw new IllegalArgumentException("Invalid window!");
        }
        if (!containers.containsKey(window)) {
            throw new IllegalArgumentException("This window is not included.");
        }
        var container = containers.remove(window);   // 直接移除并拿到
        if (container == null) {
            throw new IllegalArgumentException("This window is not included.");
        }
        container.dispose();
    }

    public static void update(long window) {
        if (window == NULL) {
            throw new IllegalArgumentException("Invalid window!");
        }
        if (!containers.containsKey(window)) {
            throw new IllegalArgumentException("This window is not included.");
        }
        var container = get(window);
        container.update();
    }

    public static void run(long window) {
        if (window == NULL) {
            throw new IllegalArgumentException("Invalid window!");
        }
        if (!containers.containsKey(window)) {
            throw new IllegalArgumentException("This window is not included.");
        }
        var container = get(window);
        container.run();
    }

    public static Container get(long window) {
        if (window == NULL) {
            throw new IllegalArgumentException("Invalid window!");
        }
        if (!containers.containsKey(window)) {
            throw new IllegalArgumentException("Invalid parameter!");
        }
        return containers.get(window);
    }

    public static List<CallbackEventData> getEvents(long window) {
        return get(window).getEvents();
    }

    public static <T extends CallbackEventData> T[] getEvents(long window,Class<T> type) {
        return get(window).getEvents(type);
    }

    public static List<CallbackListener> getListeners(long window) {
        return get(window).getListeners();
    }

    public static<T extends CallbackListener> T[] getListeners(long window,Class<T> type) {
        return get(window).getListeners(type);
    }

}
