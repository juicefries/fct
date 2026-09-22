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
// Data 2026/08/19 01:28
//

package io.github.juicefries.fct.shader;

import io.github.juicefries.fct.sign.Copyable;
import io.github.juicefries.fct.sign.Readonly;
import java.text.MessageFormat;
import java.util.Objects;

@Deprecated(since = "0.0.1")
public class SDF extends ShaderLoadingTool implements Copyable, Cloneable, Readonly {

    String name;
    GLSLVersion glslVersion;
    Mode mode;
    ShaderFile vert;
    ShaderFile frag;

    SDF() {

    }

    public String getName() {
        return name;
    }

    public String name() {
        return getName();
    }

    public GLSLVersion getGlslVersion() {
        return glslVersion;
    }

    public GLSLVersion glslVersion() {
        return getGlslVersion();
    }

    public Mode getMode() {
        return mode;
    }

    public Mode mode() {
        return getMode();
    }

    public ShaderFile getVert() {
        return vert;
    }

    public ShaderFile vert() {
        return getVert();
    }

    public ShaderFile getFrag() {
        return frag;
    }

    public ShaderFile frag() {
        return getFrag();
    }

    @Override
    public SDF copy() {
        SDF sdf = new SDF();
        sdf.name = name;
        sdf.version = version;
        sdf.glslVersion = glslVersion;
        sdf.mode = mode;
        sdf.vert = vert;
        sdf.frag = frag;
        return sdf;
    }

    @Override
    public SDF clone() {
        try {
            SDF sdf = (SDF) super.clone();
            sdf.name = name;
            sdf.version = version;
            sdf.glslVersion = glslVersion;
            sdf.mode = mode;
            sdf.vert = vert;
            sdf.frag = frag;
            return sdf;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("clone failed!", e);
        }
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof SDF sdf)) return false;
        return Objects.equals(version,sdf.version) && Objects.equals(name, sdf.name) && glslVersion == sdf.glslVersion && mode == sdf.mode && Objects.equals(vert, sdf.vert) && Objects.equals(frag, sdf.frag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, version, glslVersion, mode, vert, frag);
    }

    public String toCode() {
        return MessageFormat.format("""
                sdf {0} '{'
                    {1} '{'
                        sdf version is {2}
                        glsl version is {3} {4}
                    '}'
                
                    shader vert '{'
                        encoding {5}
                        version is {6}
                        path is {7}
                    '}'
                
                    shader frag '{'
                        encoding {8}
                        version is {9}
                        path is {10}
                    '}'
                
                '}'""",
                name,
                name,
                version,
                glslVersion.name(),
                mode,
                vert.encoding,
                vert.version,
                vert.paths[0],
                frag.encoding,
                frag.version,
                frag.paths[0]);
    }

    @Override
    public String toString() {
        return "SDF{" +
                "name='" + name + '\'' +
                ", glslVersion=" + glslVersion +
                ", mode=" + mode +
                ", vert=" + vert +
                ", frag=" + frag +
                ", version='" + version + '\'' +
                '}';
    }
}
