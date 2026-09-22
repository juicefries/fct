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

package io.github.juicefries.fct.shader;

import io.github.juicefries.fct.logging.LoggerFactory;
import io.github.juicefries.fct.sign.Uninitialized;
import io.github.juicefries.fct.util.Resources;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus.Experimental;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Experimental
public final class ShaderLoader implements Uninitialized {

    private static final Logger log = LoggerFactory.getLogger(ShaderLoader.class);

    @Deprecated(since = "0.0.2")
    public static String loadVert(String name, String defaultVertSrc) {
        return loadShader(name, defaultVertSrc);
    }

    @Deprecated(since = "0.0.2")
    public static String loadVert(String name) {
        return loadVert(name,"");
    }

    @Deprecated(since = "0.0.2")
    public static String loadFrag(String name, String defaultFragSrc) {
        return loadShader(name, defaultFragSrc);
    }

    @Deprecated(since = "0.0.2")
    public static String loadFrag(String name) {
        return loadFrag(name,"");
    }

    @Deprecated(since = "0.0.2")
    public static String loadShader(String name, String defaultSrc) {
        InputStream in = Resources.getResourceAsStream(ShaderLoader.class, name);
        if (in == null) {
            return defaultSrc;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            log.warn("Loading error.",e);
            return defaultSrc;
        }
    }

    public static String loadShader(Class<?> clazz,String name, String defaultSrc,Charset fileCharset) throws NullPointerException {
        if (clazz == null) throw new NullPointerException("clazz is null!");
        if (name == null) throw new NullPointerException("name is null!");
        if (defaultSrc == null) throw new NullPointerException("defaultSrc is null!");
        if (fileCharset == null) throw new NullPointerException("fileCharset is null!");

        try (InputStream in = Resources.getResourceAsStream(clazz, name)) {
            if (in == null) {
                return defaultSrc;
            }
            return new String(in.readAllBytes(), fileCharset);
        } catch (IOException e) {
            log.warn("Loading error.", e);
            return defaultSrc;
        }
    }

    public static String loadShader(Class<?> clazz,String name, Charset fileCharset) throws NullPointerException {
        return loadShader(clazz,name,"",fileCharset);
    }

    public static String _fct_load_shader(String name, String defaultSrc,Charset fileCharset) throws NullPointerException {
        return loadShader(ShaderLoader.class,name,defaultSrc,fileCharset);
    }

    public static String loadShader(String name,Charset fileCharset) throws NullPointerException {
        return _fct_load_shader(name,"",fileCharset);
    }

    public static String load_shader_utf_8(Class<?> clazz, String name, String defaultSrc) throws NullPointerException {
        return loadShader(clazz,name,defaultSrc,StandardCharsets.UTF_8);
    }

    public static String load_shader_utf_8(Class<?> clazz, String name) throws NullPointerException {
        return load_shader_utf_8(clazz,name,"");
    }

    public static String _fct_load_shader_utf_8(String name, String defaultSrc) throws NullPointerException {
        return load_shader_utf_8(ShaderLoader.class,name,defaultSrc);
    }

    public static String _fct_load_shader_utf_8(String name) throws NullPointerException {
        return _fct_load_shader_utf_8(name,"");
    }


    private ShaderLoader() {

    }
}
