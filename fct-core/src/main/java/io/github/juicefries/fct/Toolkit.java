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
// Data 2026/08/23 22:05
//

package io.github.juicefries.fct;

import io.github.juicefries.fct.callback.Handler;
import io.github.juicefries.fct.callback.Handlers;
import io.github.juicefries.fct.event.DefaultEventTrigger;
import io.github.juicefries.fct.event.DisposeType;
import io.github.juicefries.fct.event.EventTrigger;
import io.github.juicefries.fct.event.SysEvent;
import io.github.juicefries.fct.event.SystemListener;
import io.github.juicefries.fct.logging.LoggerFactory;
import io.github.juicefries.fct.sign.ApiSign;
import io.github.juicefries.fct.sign.Manager;
import io.github.juicefries.fct.sign.Uninitialized;
import io.github.juicefries.fct.util.Lock;
import io.github.juicefries.fct.util.Resources;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryUtil;

public class Toolkit implements Uninitialized, Manager {

    /**
     * 路径
     * @since 0.0.2
     */
    public final static String FCT_DEFAULT_PATH = "/io/github/juicefries/fct/";

    /**
     * 锁
     * @since 0.0.1
     */
    private final static Lock LOCK = Lock.create();

    /**
     * 日志
     * @since 0.0.3
     */
    private final static Logger logger = LoggerFactory.getLogger(Toolkit.class);

    /**
     * 默认处理者
     * @since 0.0.3
     */
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final static Handlers DEFAULT = new Handlers(true);

    /**
     * 处理者
     * @since 0.0.3
     */
    private final static Handlers handlers = new Handlers();

    /**
     * 默认事件管理器
     * @since 0.0.4
     * @see EventTrigger
     * @see DefaultEventTrigger
     */
    public final static Class<? extends EventTrigger> DEFAULT_TRIGGER = DefaultEventTrigger.class;

    /**
     * 事件管理器
     * @since 0.0.4
     * @see EventTrigger
     */
    private static Class<? extends EventTrigger> trigger = Toolkit.DEFAULT_TRIGGER;

    private final static Map<String,String> FCT_ICON_PATH_MAP = new HashMap<>();
    private final static Map<String,String>      FONT_PATH_MAP = new HashMap<>();
    private final static Map<String,ByteBuffer>  FONT_SOURCES  = new HashMap<>();
    private final static Map<String,STBTTFontinfo> FONT_INFOS  = new HashMap<>();
    private final static Map<FontFace.Key,FontFace> FONT_FACES = new HashMap<>();

    private final static SystemListener listener = SystemListener.createPassiveListener(
            "FCTToolkitSystemListener",
            Toolkit.class,
            e -> {
                if (e == null) return;
                if (e.getType() == SysEvent.SYS_TERMINATE_EVENT) {
                    Toolkit.dispose();
                }
                if (e.getType() == SysEvent.SYS_INIT_EVENT) {
                    logger.log(Level.ALL,"你的意思是你在整个FCT还没初始化的情况下，让窗口先初始化了?");
                }
            }
    );

    static {
        Sys.checkInit();
        registerSystemListener();
        loadFctIcons();
        loadFctFonts();
    }

    /**
     * ToolkitWindow
     * <p>
     *     用于提供全局的弹出窗口用于复用，
     *     <br>
     *     通过从池中取出与存放用以复用。
     *     <br>
     *     自{@code 0.0.5}暂时删除相关API，
     *     <br>
     *     待后续。
     * </p>
     * @deprecated 0.0.5
     */
    @Deprecated(since = "0.0.5")
    public static final class ToolkitWindow extends FWindow {

        {
            super.listener = SystemListener.createPassiveListener(
                    "FCTToolkitWindowSystemListener",
                    ToolkitWindow.this,
                    e -> {
                        if (e == null) return;
                        if (isInit()) {
                            var type = e.getType();
                            if (type == SysEvent.SYS_CANCEL_EVENT) {
                                dispose(DisposeType.CANCEL_INVOKE);
                            }
                            if (type == SysEvent.SYS_TERMINATE_EVENT) {
                                dispose(DisposeType.TERMINATE_INVOKE);
                            }
                            if (type == SysEvent.SYS_INIT_EVENT) {
                                logger.log(Level.ALL,"你的意思是你在整个FCT还没初始化的情况下，让窗口先初始化了?");
                            }
                        }
                    }
            );
        }

        private ToolkitWindow() {
            super();
        }

        private ToolkitWindow(WindowConfig config) {
            super(config);
        }
    }

    // ========================= OTM =========================

    @ApiStatus.Internal
    @ApiSign.Dangerous(since = "0.0.3")
    public static void dispose() {
        logger.trace("dispose() - start");
        synchronized (lock()) {
            FCT_ICON_PATH_MAP.clear();
            FONT_FACES.clear();
            FONT_INFOS.values().forEach(STBTTFontinfo::free);
            FONT_INFOS.clear();
            FONT_SOURCES.clear();
            FONT_PATH_MAP.clear();
        }
        logger.trace("dispose() - complete");
    }

    public static void putFontSource(String name, String path) {
        if (name == null || path == null) throw new NullPointerException();
        synchronized (lock()) { FONT_PATH_MAP.put(name, path); }
    }

    public static void putHandler(String type,Class<? extends Handler> handlerClass) {
        if (handlerClass == null) {
            throw new NullPointerException("handlerClass is null!");
        }
        if (type == null) {
            throw new NullPointerException("type is null!");
        }

        synchronized (lock()) {
            handlers.put(type,handlerClass);
        }
    }

    public static void putHandler(String type,String name) throws ClassNotFoundException {
        if (type == null) {
            throw new NullPointerException("type is null!");
        }
        if (name == null) {
            throw new NullPointerException("name is null!");
        }

        Class<?> handlerClass = Class.forName(name);
        if (!Handler.class.isAssignableFrom(handlerClass)) {
            throw new ClassNotFoundException(
                    String.format("Class [%s] is not of type Handler!", name)
            );
        }

        synchronized (lock()) {
            //noinspection unchecked
            Class<? extends Handler> h = (Class<? extends Handler>) handlerClass;
            Toolkit.putHandler(type, h);
        }
    }

    // ========================= SET =========================

    public static void setEventTrigger(Class<? extends EventTrigger> trigger) {
        if (trigger == null) {
            throw new NullPointerException("trigger is null!");
        }
        Toolkit.trigger = trigger;
    }

    public static void setEventTrigger(String name) throws ClassNotFoundException {
        if (name == null) {
            throw new NullPointerException("name is null!");
        }
        Class<?> aClass = Class.forName(name);
        if (!EventTrigger.class.isAssignableFrom(aClass)) {
            throw new ClassNotFoundException(
                    String.format("Class [%s] is not of type EventTrigger!", name)
            );
        }
        //noinspection unchecked
        trigger = (Class<? extends EventTrigger>) aClass;
    }

    public static void setHandlers(Handlers handlers) {
        if (handlers == null) {
            throw new NullPointerException("handlers is null!");
        }
        synchronized (lock()) {
            handlers.clear();
            Toolkit.handlers.putAll(handlers);
        }
    }

    // ========================= GET =========================

    public static @NotNull Handlers getHandlers() {
        Handlers handlers = new Handlers(false);
        handlers.putAll(Toolkit.handlers);
        return handlers;
    }

    public static @NotNull Handlers getDefaultHandlers() {
        Handlers handlers = new Handlers(false);
        handlers.putAll(Toolkit.DEFAULT);
        return handlers;
    }

    /**
     * 获取窗口句柄
     * @param window 窗口
     * @return 窗口的句柄
     * @throws NullPointerException 窗口不能为null
     * @throws IllegalArgumentException 窗口不能未初始化
     * @since 0.0.2
     * @see Window
     */
    public static long getWindow(Window window) {
        if (window == null) {
            throw new NullPointerException("window is null!");
        }
        if (window.window == MemoryUtil.NULL) {
            throw new IllegalArgumentException("Uninitialized window!");
        }
        return window.window;
    }

    @Contract("null -> fail")
    public static @NotNull Image getIcon(String key) {
        if (key == null) {
            throw new NullPointerException("key is null!");
        }
        if (!FCT_ICON_PATH_MAP.containsKey(key)) {
            var path = FCT_ICON_PATH_MAP.getOrDefault(
                    "fct.blank.image.64",
                    "/io/github/juicefries/fct/icons/blank.png"
            );

            return ImageToolkit.getResourcesImage(Toolkit.class,path);
        }
        var path = FCT_ICON_PATH_MAP.get(key);
        return ImageToolkit.getResourcesImage(Toolkit.class,path);
    }

    @Contract(" -> new")
    public static @NotNull Set<String> getIconKeys() {
        return new HashSet<>(FCT_ICON_PATH_MAP.keySet());
    }

    public static @NotNull Class<? extends EventTrigger> getTrigger() {
        return Objects.requireNonNullElseGet(trigger, Toolkit::getDefaultTrigger);
    }

    public static @NotNull Class<? extends EventTrigger> getDefaultTrigger() {
        return Objects.requireNonNullElse(DEFAULT_TRIGGER, DefaultEventTrigger.class);
    }

    @Contract("null, _, _ -> fail")
    public static @NotNull FontFace getOrCreate(String name, float pixelW, float pixelH) {
        if (name == null) throw new NullPointerException("name is null!");
        synchronized (lock()) {
            var key = new FontFace.Key(name, pixelW, pixelH);
            var face = FONT_FACES.get(key);
            if (face != null) return face;

            var info = FONT_INFOS.get(name);
            if (info == null) {
                var ttf = FONT_SOURCES.get(name);
                if (ttf == null) {
                    var p = FONT_PATH_MAP.get(name);
                    if (p == null) throw new IllegalArgumentException("Unknown font: " + name);
                    ttf = loadFontSource(name, p);
                }
                info = STBTTFontinfo.create();
                if (!STBTruetype.stbtt_InitFont(info, ttf))
                    throw new IllegalStateException("Failed to init font: " + name);
                FONT_INFOS.put(name, info);
            }

            float scaleY = STBTruetype.stbtt_ScaleForPixelHeight(info, pixelH);
            float scaleX = scaleY * (pixelW / pixelH);
            face = new FontFace(key, info, scaleX, scaleY);
            FONT_FACES.put(key, face);
            return face;
        }
    }

    // ========================= EEE =========================

    private static void registerSystemListener() {
        Sys.register(listener);
    }

    private static void loadFctIcons() {
        Properties properties = new Properties();

        var path = "%sicons/icon.properties".formatted(FCT_DEFAULT_PATH);
        try(InputStream in = Resources.getResourceAsStream(Toolkit.class,path)) {
            if (in == null) {
                throw new NullPointerException("in is null!");
            }
            properties.load(in);
        } catch (Exception e) {
            logger.error("An error occurred while loading the default icon set.",e);
            return;
        }

        for (Object key : properties.keySet()) {
            if (!(key instanceof String strKey)) continue;
            FCT_ICON_PATH_MAP.put(strKey,properties.getProperty(strKey));
        }
    }

    private static void loadFctFonts() {
        Properties properties = new Properties();
        var path = "%sfonts/font.properties".formatted(FCT_DEFAULT_PATH);
        try (InputStream in = Resources.getResourceAsStream(Toolkit.class, path)) {
            if (in == null) return;
            properties.load(in);
        } catch (Exception e) {
            logger.error("An error occurred while loading the default font set.", e);
            return;
        }
        for (Object key : properties.keySet())
            if (key instanceof String k) FONT_PATH_MAP.put(k, properties.getProperty(k));
    }

    private static @NotNull ByteBuffer loadFontSource(String name, String path) {
        try (InputStream in = Resources.getResourceAsStream(Toolkit.class, path)) {
            if (in == null) throw new NullPointerException("in is null!");
            byte[] bytes = in.readAllBytes();
            ByteBuffer buf = BufferUtils.createByteBuffer(bytes.length);
            buf.put(bytes).flip();
            FONT_SOURCES.put(name, buf);
            return buf;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load font: " + name, e);
        }
    }

    static @NotNull Glyph ensureGlyph(@NotNull FontFace face, int cp) {
        synchronized (lock()) { return face.ensure(cp); }
    }

    // ========================= UTIL =========================

    public static @NotNull Handlers forMultipleAttempts() {
        Handlers handlers;
        var generalHandlers = Toolkit.getHandlers();
        var defaultHandlers = Toolkit.getDefaultHandlers();
        if (!generalHandlers.isEmpty()) {
            handlers = generalHandlers;
        } else if (!defaultHandlers.isEmpty()) {
            handlers = defaultHandlers;
        } else {
            handlers = new Handlers(true);
        }

        if (handlers.isEmpty()) {
            throw new IllegalArgumentException("Are the handlers empty?");
        }
        return handlers;
    }

    static Lock lock() {
        return LOCK;
    }

    // 用来触发类加载
    public static void initialize() {

    }

    public Toolkit() {

    }

}
