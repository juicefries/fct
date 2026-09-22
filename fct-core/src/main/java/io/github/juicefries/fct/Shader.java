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
// Data 2026/08/19 00:39
//

package io.github.juicefries.fct;

import io.github.juicefries.fct.shader.ShaderLoader;
import io.github.juicefries.fct.sign.Initializable;
import io.github.juicefries.fct.util.Array;
import io.github.juicefries.fct.util.Lock;
import io.github.juicefries.fct.util.TraverseList;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL33;

/**
 * <h>Shader</h>
 *
 * <p>
 *     一个工具类，用于配合{@link GLGraphics2D#shaders},
 *     <br>
 *     用来方便我统一管理多个着色器。
 * </p>
 *
 * @since 0.0.1
 * @author juicefries
 * @see GLGraphics2D
 * @see ShaderLoader
 * @see Initializable
 */
public class Shader implements Initializable {

    final Lock lock = Lock.create();
    AtomicBoolean initialize = new AtomicBoolean(false);
    volatile int program;
    int vao;
    int vbo;


    TraverseList<String[]> shaders = new TraverseList<>(String[].class);

    public Shader() {

    }

    public static @NotNull Shader build(boolean init) {
        Shader shader = new Shader();
        if (init) {
            shader.initialize();
        }
        return shader;
    }

    public static @NotNull Shader build() {
        return build(true);
    }

    @Override
    public void initialize() {
        if (isInitialize()) {
            throw new IllegalStateException("Graphics have been initialized!");
        }
        program = GL33.glCreateProgram();

        vao = GL33.glGenVertexArrays();
        vbo = GL33.glGenBuffers();

        GL33.glBindVertexArray(vao);
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, vbo);

        initialize.set(true);
    }

    // ========================= OTM =========================

    void linkShaderV(String vertSrc, String fragSrc) {
        synchronized (lock) {
            int vs = GL33.glCreateShader(GL33.GL_VERTEX_SHADER);
            int fs = GL33.glCreateShader(GL33.GL_FRAGMENT_SHADER);

            GL33.glShaderSource(vs, vertSrc);
            GL33.glShaderSource(fs, fragSrc);

            GL33.glCompileShader(vs);
            GL33.glCompileShader(fs);

            GL33.glAttachShader(program, vs);
            GL33.glAttachShader(program, fs);

            GL33.glLinkProgram(program);
        }
    }

    public int linkShader(String vertSrc, String fragSrc) {
        if (!isInitialize()) {
            return -1;
        }
        if (vertSrc == null) {
            throw new NullPointerException("vertSrc is null!");
        }
        if (fragSrc == null) {
            throw new NullPointerException("fragSrc is null!");
        }

        linkShaderV(vertSrc, fragSrc);

        String[] shader = Array.createS(2);
        shader[0] = vertSrc;
        shader[1] = fragSrc;

        shaders.add(shader);
        return shaders.indexOf(shader);
    }

    public int linkShader(String[] shader) {
        Array.requireNotLessThan(shader,2);
        return linkShader(shader[0],shader[1]);
    }

    public void useProgram() {
        if (!isInitialize()) return;
        GL33.glUseProgram(program);
    }

    public void useShader(int index) {
        String[] shader = shaders.get(index);
        if (shader == null) {
            throw new NullPointerException("shader is null!");
        }
        linkShaderV(shader[0],shader[1]);
    }

    @Override
    public void dispose() {
        synchronized (lock) {
            if (!isInitialize()) return;
            GL33.glUseProgram(0);
            GL33.glBindVertexArray(0);
            GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, 0);
            if (vao != 0) {
                GL33.glDeleteVertexArrays(vao);
                vao = 0;
            }
            if (vbo != 0) {
                GL33.glDeleteBuffers(vbo);
                vbo = 0;
            }
            if (program != 0) {
                GL33.glDeleteProgram(program);
                program = 0;
            }
            initialize.set(false);
        }
    }

    // ========================= SET =========================


    // ========================= GET =========================

    public int getUniform(String name) {
        if (name == null) {
            throw new NullPointerException("name is null!");
        }
        if (name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty!");
        }
        return GL33.glGetUniformLocation(program,name);
    }

    @Override
    public boolean isInitialize() {
        return initialize.get();
    }

    public int getProgram() {
        return program;
    }

    public int getVao() {
        return vao;
    }

    public int vao() {
        return vao;
    }

    public int getVbo() {
        return vbo;
    }

    public int vbo() {
        return vbo;
    }

    // ========================= EEE =========================
    // ========================= RRR =========================
}
