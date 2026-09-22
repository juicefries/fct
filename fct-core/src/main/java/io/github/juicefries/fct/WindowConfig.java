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
// Data 2026/08/11 00:46
//

package io.github.juicefries.fct;

import io.github.juicefries.fct.glfw.Hint;
import io.github.juicefries.fct.logging.LoggerFactory;
import io.github.juicefries.fct.sign.Copyable;
import io.github.juicefries.fct.sign.Readonly;
import io.github.juicefries.fct.util.Array;
import io.github.juicefries.fct.util.TraverseList;
import io.github.juicefries.fct.util.Util;
import java.util.function.Consumer;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class WindowConfig implements Readonly, Copyable,Cloneable {

    private final static Logger logger = LoggerFactory.getLogger(WindowConfig.class);

    Consumer<Window> notice = Util.emptyConsumer();

    final TraverseList<Hint> hints = new TraverseList<>(Hint.class);
    Color background;
    boolean initialize = true;

    public WindowConfig(boolean useDefaultBehavior) {
        if (useDefaultBehavior) {
            setSamples(4);
            hints.add(Hint.visible(false));
            hints.add(Hint.resizable(true));
        }
    }

    public WindowConfig() {
        this(true);
    }

    @Contract("_ -> new")
    public static @NotNull WindowConfig build(boolean useDefaultBehavior) {
        return new WindowConfig(useDefaultBehavior);
    }

    @Contract(" -> new")
    public static @NotNull WindowConfig build() {
        return build(true);
    }

    public void setSamples(int samples) {
        hints.add(Hint.samples(samples));
    }

    public void setBackground(Color background) {
        if (background == null) {
            throw new NullPointerException("background is null!");
        }

        this.background = background;
    }

    public void addHint(Hint hint) {
        if (hint == null) {
            throw new NullPointerException("hint is null!");
        }
        if (Array.contains(hint.hint(),
                GLFW.GLFW_CONTEXT_VERSION_MAJOR,
                GLFW.GLFW_CONTEXT_VERSION_MINOR,
                GLFW.GLFW_OPENGL_PROFILE,
                GLFW.GLFW_OPENGL_FORWARD_COMPAT
        )) {
            logger.warn("Currently, settings related to certain versions of OpenGL are not available.");
            return;
        }
        hints.add(hint);
    }

    public void setInitialize(boolean initialize) {
        this.initialize = initialize;
    }

    public TraverseList<Hint> getHints() {
        return hints.clone();
    }

    public void setNotice(Consumer<Window> notice) {
        if (notice == null) {
            throw new NullPointerException("notice is null!");
        }

        this.notice = notice;
    }

    public Color getBackground() {
        return background;
    }

    public Consumer<Window> getNotice() {
        return notice;
    }

    public boolean isInitialize() {
        return initialize;
    }

    @Override
    public WindowConfig clone() {
        try {
            WindowConfig configuration = (WindowConfig) super.clone();
            configuration.hints.clear();
            configuration.hints.addAll(hints.clone());
            configuration.background = background;
            configuration.notice = notice;
            configuration.initialize = initialize;
            return configuration;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("clone failed!",e);
        }
    }

    @Override
    public WindowConfig copy() {
        WindowConfig configuration = new WindowConfig(false);
        configuration.hints.addAll(hints.clone());
        configuration.background = background;
        configuration.notice = notice;
        configuration.initialize = initialize;
        return configuration;
    }
}
