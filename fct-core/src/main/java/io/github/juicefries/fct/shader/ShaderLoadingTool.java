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
// Data 2026/08/19 01:53
//

package io.github.juicefries.fct.shader;

import io.github.juicefries.fct.logging.LoggerFactory;
import io.github.juicefries.fct.util.Resources;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import org.apache.logging.log4j.Logger;

@Deprecated(since = "0.0.1")
public abstract class ShaderLoadingTool {

    private final static Logger log = LoggerFactory.getLogger(ShaderLoadingTool.class);
    protected String version;

    public String getVersion() {
        return version;
    }

    public String version() {
        return version;
    }

    public static SDF getShaderDefinitionFile(Class<?> clazz, String name) {
        if (clazz == null) throw new NullPointerException("clazz is null");
        if (name == null) throw new NullPointerException("name is null");

        try (InputStream in = Resources.getResourceAsStream(clazz, name)) {
            if (in == null) {
                throw new IOException("Resource not found: " + name);
            }
            String content = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            return parseSDF(content, name);
        } catch (IOException e) {
            log.error("Failed to load SDF: {}", name, e);
            throw new RuntimeException("SDF loading failed", e);
        }
    }

    private static SDF parseSDF(String content, String fileName) {
        // 提取纯文件名和期望根名
        String pureFileName = fileName;
        if (pureFileName.contains("/")) {
            pureFileName = pureFileName.substring(pureFileName.lastIndexOf('/') + 1);
        } else if (pureFileName.contains("\\")) {
            pureFileName = pureFileName.substring(pureFileName.lastIndexOf('\\') + 1);
        }
        String expectedName = pureFileName.contains(".") ? pureFileName.substring(0, pureFileName.lastIndexOf('.')) : pureFileName;
        expectedName = expectedName.trim();

        SDF sdf = new SDF();
        sdf.name = expectedName;

        Deque<String> blockStack = new ArrayDeque<>();
        ShaderFile currentShader = null;
        int state = 0; // 0=外层, 1=元数据块内, 2=shader块内

        String[] lines = content.split("\\r?\\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("//")) continue;

            // 处理块开始（以 "{" 结尾的行）
            if (line.endsWith("{")) {
                String blockDecl = line.substring(0, line.length() - 1).trim();
                String[] parts = blockDecl.split("\\s+");
                String blockName = parts[parts.length - 1];
                if (parts[0].equals("sdf")) {
                    if (!blockName.equals(expectedName)) {
                        throw new RuntimeException("SDF root block name must match file name: " + expectedName + " but got " + blockName);
                    }
                    // 根块压栈标记，不改变state
                    blockStack.push("__ROOT__");
                    continue;
                }
                // 其他块压栈
                blockStack.push(blockName);
                if (parts[0].equals("shader")) {
                    currentShader = new ShaderFile();
                    state = 2;
                } else {
                    state = 1;
                }
                continue;
            }

            // 处理块结束
            if (line.equals("}")) {
                if (blockStack.isEmpty()) {
                    throw new RuntimeException("Unexpected closing brace");
                }
                String popped = blockStack.pop();
                if ("__ROOT__".equals(popped)) {
                    // 根块结束，忽略
                    state = 0;
                    continue;
                }
                if (state == 2 && currentShader != null) {
                    if ("vert".equals(popped)) sdf.vert = currentShader;
                    else if ("frag".equals(popped)) sdf.frag = currentShader;
                    currentShader = null;
                }
                state = blockStack.isEmpty() ? 0 : 1;
                continue;
            }

            // 处理属性行
            String key, valuePart;
            if (line.contains(" is ")) {
                int idx = line.indexOf(" is ");
                key = line.substring(0, idx).trim();
                valuePart = line.substring(idx + 4).trim();
            } else {
                int idx = line.indexOf(' ');
                if (idx == -1) continue;
                key = line.substring(0, idx).trim();
                valuePart = line.substring(idx + 1).trim();
            }

            if (state == 1) {
                // 元数据块属性
                if ("sdf version".equals(key)) {
                    sdf.version = stripQuotes(valuePart);
                } else if ("glsl version".equals(key)) {
                    String[] verParts = valuePart.split("\\s+");
                    sdf.glslVersion = GLSLVersion.valueOf(verParts[0]);
                    if (verParts.length > 1) {
                        String modeStr = verParts[1].toUpperCase();
                        if ("CORE".equals(modeStr)) sdf.mode = Mode.CORE;
                        else if ("COMPATIBILITY".equals(modeStr)) sdf.mode = Mode.COMPATIBILITY;
                        else sdf.mode = Mode.NONE;
                    } else {
                        sdf.mode = Mode.NONE;
                    }
                }
            } else if (state == 2 && currentShader != null) {
                // shader块属性
                switch (key) {
                    case "encoding" -> {
                        String enc = stripQuotes(valuePart);
                        enc = enc.replace('_', '-');
                        currentShader.encoding = Charset.forName(enc);
                    }
                    case "version" -> currentShader.version = stripQuotes(valuePart);
                    case "path" -> {
                        String[] paths = valuePart.split("\\s+or\\s+");
                        for (int i = 0; i < paths.length; i++) {
                            paths[i] = stripQuotes(paths[i]);
                        }
                        currentShader.paths = paths;
                    }
                    case "uri" -> {
                        String[] uris = valuePart.split("\\s+or\\s+");
                        URI[] uriArr = new URI[uris.length];
                        for (int i = 0; i < uris.length; i++) {
                            try {
                                uriArr[i] = new URI(stripQuotes(uris[i]));
                            } catch (URISyntaxException e) {
                                throw new RuntimeException("Invalid URI: " + uris[i]);
                            }
                        }
                        currentShader.uris = uriArr;
                    }
                    case "url" -> {
                        String[] urls = valuePart.split("\\s+or\\s+");
                        URL[] urlArr = new URL[urls.length];
                        for (int i = 0; i < urls.length; i++) {
                            try {
                                urlArr[i] = new URL(stripQuotes(urls[i]));
                            } catch (MalformedURLException e) {
                                throw new RuntimeException("Invalid URL: " + urls[i]);
                            }
                        }
                        currentShader.urls = urlArr;
                    }
                }
            }
        }

        if (sdf.glslVersion == null) throw new RuntimeException("Missing glsl version");
        if (sdf.vert == null) throw new RuntimeException("Missing vertex shader");
        if (sdf.frag == null) throw new RuntimeException("Missing fragment shader");
        return sdf;
    }

    private static String stripQuotes(String s) {
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\"")) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    public static SDF getSDF(Class<?> clazz, String name) {
        return getShaderDefinitionFile(clazz, name);
    }
}