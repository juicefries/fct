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
// Data 2026/08/19 01:38
//

package io.github.juicefries.fct.shader;

import io.github.juicefries.fct.sign.Copyable;
import io.github.juicefries.fct.sign.Readonly;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Objects;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Experimental
@Deprecated(since = "0.0.1")
public final class ShaderFile extends ShaderLoadingTool implements Copyable, Cloneable, Readonly {

    Charset encoding;
    String[] paths;
    URI[] uris;
    URL[] urls;

    ShaderFile() {

    }

    public Charset getEncoding() {
        return encoding;
    }

    public Charset encoding() {
        return getEncoding();
    }

    public String[] getPaths() {
        return paths.clone();
    }

    public String[] paths() {
        return getPaths();
    }

    public URI[] getUris() {
        return uris.clone();
    }

    public URI[] uris() {
        return getUris();
    }

    public URL[] getUrls() {
        return urls.clone();
    }

    public URL[] urls() {
        return getUrls();
    }

    public @NotNull ShaderFile getShaderFile() {
        return copy();
    }

    public @NotNull ShaderFile shaderFile() {
        return getShaderFile();
    }

    @Override
    public @NotNull ShaderFile clone() {
        try {
            ShaderFile shaderFile = (ShaderFile) super.clone();
            shaderFile.encoding = encoding;
            shaderFile.version = version;
            shaderFile.paths = paths;
            shaderFile.uris = uris;
            shaderFile.urls = urls;
            return shaderFile;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("clone failed!", e);
        }


    }

    @Override
    public @NotNull ShaderFile copy() {
        ShaderFile shaderFile = new ShaderFile();
        shaderFile.encoding = encoding;
        shaderFile.version = version;
        shaderFile.paths = paths;
        shaderFile.uris = uris;
        shaderFile.urls = urls;
        return shaderFile;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ShaderFile that)) return false;
        return Objects.equals(encoding, that.encoding) && Objects.equals(version, that.version) && Objects.deepEquals(paths, that.paths) && Objects.deepEquals(uris, that.uris) && Objects.deepEquals(urls, that.urls);
    }

    @Override
    public int hashCode() {
        return Objects.hash(encoding, version, Arrays.hashCode(paths), Arrays.hashCode(uris), Arrays.hashCode(urls));
    }

    @Override
    public String toString() {
        return "ShaderFile{" +
                "encoding=" + encoding +
                ", version='" + version + '\'' +
                ", paths=" + Arrays.toString(paths) +
                ", uris=" + Arrays.toString(uris) +
                ", urls=" + Arrays.toString(urls) +
                '}';
    }
}
