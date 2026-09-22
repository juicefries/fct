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

module io.github.juicefries.fct.core {
    requires java.desktop;
    requires java.logging;
    requires org.apache.logging.log4j;
    requires org.jetbrains.annotations;
    requires org.joml;
    requires org.lwjgl;
    requires org.lwjgl.glfw;
    requires org.lwjgl.opengl;
    requires org.lwjgl.stb;
    requires io.github.juicefries.fct.lwjgl;
    requires io.github.juicefries.fct.util;
    requires io.github.juicefries.fct.platform;
    requires com.sun.jna.platform;
    requires com.sun.jna;

    exports io.github.juicefries.fct;
    exports io.github.juicefries.fct.callback;
    exports io.github.juicefries.fct.callback.handler;
    //noinspection deprecation
    exports io.github.juicefries.fct.input;
    exports io.github.juicefries.fct.image;
    exports io.github.juicefries.fct.shader;
    exports io.github.juicefries.fct.event;
    exports io.github.juicefries.fct.glfw;
    exports io.github.juicefries.fct.layout;

    opens io.github.juicefries.fct.icons;
    opens io.github.juicefries.fct.shaders;
    opens io.github.juicefries.fct;
    opens io.github.juicefries.fct.event;

}