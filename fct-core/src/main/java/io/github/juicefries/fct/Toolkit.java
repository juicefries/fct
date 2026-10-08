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
import io.github.juicefries.fct.event.EventTrigger;
import io.github.juicefries.fct.event.SysEvent;
import io.github.juicefries.fct.event.SystemListener;
import io.github.juicefries.fct.image.Format;
import io.github.juicefries.fct.lwjgl.WindowHint;
import io.github.juicefries.fct.sign.ApiSign;
import io.github.juicefries.fct.sign.Manager;
import io.github.juicefries.fct.sign.Uninitialized;
import io.github.juicefries.fct.util.Lock;
import io.github.juicefries.fct.util.Resources;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import javax.swing.ImageIcon;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

/**
 * 工具包
 * <p>
 *     用于提供部分功能，
 *     懒得写。
 * </p>
 * @since 1.0.1
 * @author juicefries
 */
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
    private final static Logger logger = LogManager.getLogger(Toolkit.class);

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
            "FCT-ToolkitSystemListener",
            Toolkit.class,
            e -> {
                if (e == null) return;
                if (e.getType() == SysEvent.SYS_CANCEL_EVENT || e.getType() == SysEvent.SYS_TERMINATE_EVENT) {
                    Toolkit.dispose();
                }
                if (e.getType() == SysEvent.SYS_INIT_EVENT) {
                    logger.log(Level.ALL,"你的意思是你在整个FCT还没初始化的情况下，让窗口先初始化了?");
                    init();
                }
            }
    );

    static {
        init();
    }

    private static void init() {
        Sys.checkInit();
        WindowHint.initialize();
        registerSystemListener();
        loadFctIcons();
        loadFctFonts();
    }

    // ========================= OTM =========================

    /**
     * 延后任务，名称自动生成且只执行一次
     * @param task 任务本体
     * @see Sys#invokeLater(Runnable)
     * @since 1.0.2
     * @throws NullPointerException 当任务为null时抛出
     */
    @Contract("null -> fail")
    public static void invokeLater(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task is null!");
        }

        Sys.invokeLater(task);
    }

    /**
     * 追加任务，名称自动生成且只执行一次
     * @param task 任务本体
     * @see Sys#invokeAppend(Runnable)
     * @since 1.0.2
     * @throws NullPointerException 当任务为null时抛出
     */
    @Contract("null -> fail")
    public static void invokeAppend(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task is null!");
        }
        Sys.invokeAppend(task);
    }

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

    @Contract("null, _ -> fail; !null, null -> fail")
    public static void putFontSource(String name, String path) {
        if (name == null || path == null) throw new NullPointerException();
        synchronized (lock()) { FONT_PATH_MAP.put(name, path); }
    }

    @Contract("_, null -> fail; null, !null -> fail")
    public static void putHandler(String type, Class<? extends Handler> handlerClass) {
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

    @Contract("null, _ -> fail; !null, null -> fail")
    public static void putHandler(String type, String name) throws ClassNotFoundException {
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

    @Contract("null -> fail")
    public static void setEventTrigger(Class<? extends EventTrigger> trigger) {
        if (trigger == null) {
            throw new NullPointerException("trigger is null!");
        }
        Toolkit.trigger = trigger;
    }

    @Contract("null -> fail")
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

    @Contract("null -> fail")
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

    public static @NonNls ComponentContext getComponentContext() {
        return ComponentContext.FCT_CC_LOCAL.get();
    }

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
    @Contract("null -> fail")
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

            return Toolkit.getResourcesImage(Toolkit.class,path);
        }
        var path = FCT_ICON_PATH_MAP.get(key);
        return Toolkit.getResourcesImage(Toolkit.class,path);
    }

    @Contract(" -> new")
    public static @NotNull Set<String> getIconKeys() {
        return new HashSet<>(FCT_ICON_PATH_MAP.keySet());
    }

    @Contract(pure = true)
    public static @NotNull Class<? extends EventTrigger> getTrigger() {
        return Objects.requireNonNullElseGet(trigger, Toolkit::getDefaultTrigger);
    }

    @Contract(pure = true)
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
        synchronized (lock()) {
            return face.ensure(cp);
        }
    }
    // ========================= IMAGE =========================

    @Contract("null, _ -> fail; !null, null -> fail")
    public static @NotNull Image getResourcesImage(Class<?> clazz, String name) {
        if (clazz == null || name == null) {
            throw new NullPointerException("clazz or name is null");
        }
        try (InputStream is = Resources.getResourceAsStream(clazz, name)) {
            if (is == null) {
                throw new IOException("Resource not found: " + name);
            }
            return loadImageFromStream(is);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load image: " + name, e);
        }
    }

    public static @NotNull Image getResourcesImage(String path) {
        return getResourcesImage(Toolkit.class, path);
    }

    @Contract("null -> fail")
    public static @NotNull Image getImage(byte[] image) {
        if (image == null) {
            throw new NullPointerException("image byte array is null");
        }
        return loadImageFromBytes(image);
    }

    /**
     * 从 GLFWImage 转换。
     * GLFWImage 的像素数据通过 width(), height(), 和 address() 访问。
     */
    @Contract("null -> fail")
    public static @NotNull Image getImage(GLFWImage image) {
        if (image == null) {
            throw new NullPointerException("GLFWImage is null");
        }
        int w = image.width();
        int h = image.height();
        // GLFWImage 的像素数据是一个连续的字节数组，每个像素 RGBA
        // 通过 MemoryUtil.memByteBuffer 将地址转为 ByteBuffer
        long addr = image.address();
        if (addr == 0L) {
            throw new IllegalArgumentException("GLFWImage has no pixel data (null address)");
        }
        // 每个像素 4 字节 (RGBA)
        int capacity = w * h * 4;
        ByteBuffer pixels = MemoryUtil.memByteBuffer(addr, capacity);
        if (pixels == null) {
            throw new IllegalArgumentException("Failed to map GLFWImage pixel data");
        }
        // 复制一份，避免与 GLFW 生命周期绑定
        ByteBuffer copy = MemoryUtil.memAlloc(capacity);
        copy.put(pixels.duplicate().rewind());
        copy.flip();
        return new DefaultImage(w, h, Format.RGBA, copy);
    }

    @ApiStatus.Experimental
    public static @NotNull BufferedImage toAwtImage(Image image) {
        GLFWImage gl = Toolkit.getImage(image);
        int w = gl.width(), h = gl.height();
        ByteBuffer src = gl.pixels(w * h * 4);   // ← 传容量：RGBA，每像素 4 字节

        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        int[] dst = ((DataBufferInt) bi.getRaster().getDataBuffer()).getData();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int si = (y * w + x) * 4;
                int r = src.get(si)     & 0xFF;
                int g = src.get(si + 1) & 0xFF;
                int b = src.get(si + 2) & 0xFF;
                int a = src.get(si + 3) & 0xFF;
                dst[y * w + x] = (a << 24) | (r << 16) | (g << 8) | b;  // ARGB
            }
        }
        return bi;
    }


    @Contract("null -> fail")
    public static @NotNull GLFWImage getImage(Image image) {
        if (image == null) {
            throw new NullPointerException("image is null");
        }
        if (!(image instanceof DefaultImage di)) {
            throw new IllegalArgumentException("Only DefaultImage is supported");
        }
        ByteBuffer data = di.getData();
        Format fmt = di.getFormat();
        ByteBuffer out;
        if (fmt == Format.RGBA) {
            // 直接使用，但需要确保是直接缓冲区
            out = data.duplicate();
        } else if (fmt == Format.RGB) {
            // 转为 RGBA：添加 A=255
            int pixelCount = di.getWidth() * di.getHeight();
            out = MemoryUtil.memAlloc(pixelCount * 4);
            for (int i = 0; i < pixelCount; i++) {
                int r = data.get() & 0xFF;
                int g = data.get() & 0xFF;
                int b = data.get() & 0xFF;
                out.put((byte) r);
                out.put((byte) g);
                out.put((byte) b);
                out.put((byte) 0xFF);
            }
            out.flip();
        } else {
            throw new IllegalArgumentException("Unsupported format for GLFWImage: " + fmt);
        }
        GLFWImage glfwImage = GLFWImage.malloc();
        // 设置宽高和像素数据
        glfwImage.width(di.getWidth());
        glfwImage.height(di.getHeight());
        glfwImage.set(di.getWidth(), di.getHeight(), out);
        return glfwImage;
    }

    @Contract("null -> fail")
    public static @NotNull Image getImage(ImageIcon imageIcon) {
        if (imageIcon == null) {
            throw new NullPointerException("ImageIcon is null");
        }
        return getImage(imageIcon.getImage());
    }

    @Contract("null -> fail")
    public static @NotNull Image getImage(java.awt.Image image) {
        if (image == null) {
            throw new NullPointerException("AWT Image is null");
        }
        BufferedImage bi;
        if (image instanceof BufferedImage) {
            bi = (BufferedImage) image;
        } else {
            bi = toBufferedImage(image);
        }
        return fromBufferedImage(bi);
    }

    // ========================= UTIL =========================

    private static @NotNull Image loadImageFromStream(InputStream is) throws IOException {
        byte[] bytes = readAllBytes(is);
        return loadImageFromBytes(bytes);
    }

    @Contract("null -> fail")
    private static @NotNull Image loadImageFromBytes(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("Image data is empty or null");
        }
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer wBuf = stack.mallocInt(1);
            IntBuffer hBuf = stack.mallocInt(1);
            IntBuffer compBuf = stack.mallocInt(1);

            // --- 分配直接缓冲区并拷贝数据 ---
            ByteBuffer buffer = MemoryUtil.memAlloc(bytes.length);
            buffer.put(bytes);
            buffer.flip();

            ByteBuffer data;
            try {
                data = STBImage.stbi_load_from_memory(buffer, wBuf, hBuf, compBuf, 0);
            } finally {
                MemoryUtil.memFree(buffer); // 用完释放临时缓冲区
            }

            if (data == null) {
                throw new RuntimeException("STBImage failed to load: " + STBImage.stbi_failure_reason());
            }

            int width = wBuf.get(0);
            int height = hBuf.get(0);
            int channels = compBuf.get(0);

            // 确定 Format
            Format format = switch (channels) {
                case 1 -> Format.GRAYSCALE;
                case 2 -> Format.ALPHA;
                case 3 -> Format.RGB;
                case 4 -> Format.RGBA;
                default -> throw new IllegalStateException("Unexpected channels: " + channels);
            };


            ByteBuffer copy = MemoryUtil.memAlloc(data.remaining());
            copy.put(data.duplicate().rewind());
            copy.flip();
            STBImage.stbi_image_free(data);

            return new DefaultImage(width, height, format, copy);
        }
    }

    private static byte @NotNull [] readAllBytes(@NotNull InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int len;
        while ((len = is.read(buf)) != -1) {
            baos.write(buf, 0, len);
        }
        return baos.toByteArray();
    }

    private static @NotNull BufferedImage toBufferedImage(java.awt.Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        BufferedImage bi = new BufferedImage(
                image.getWidth(null),
                image.getHeight(null),
                BufferedImage.TYPE_INT_ARGB
        );
        java.awt.Graphics2D g = bi.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return bi;
    }

    private static @NotNull Image fromBufferedImage(@NotNull BufferedImage bi) {
        int w = bi.getWidth();
        int h = bi.getHeight();
        int type = bi.getType();
        ByteBuffer buffer;
        Format format;

        if (type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB) {
            DataBufferInt db = (DataBufferInt) bi.getRaster().getDataBuffer();
            int[] pixels = db.getData();
            boolean hasAlpha = (type == BufferedImage.TYPE_INT_ARGB);
            format = hasAlpha ? Format.ARGB : Format.RGB;
            buffer = MemoryUtil.memAlloc(pixels.length * 4);
            for (int pixel : pixels) {
                int a = (pixel >> 24) & 0xFF;
                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;
                if (hasAlpha) {
                    buffer.put((byte) a);
                    buffer.put((byte) r);
                    buffer.put((byte) g);
                    buffer.put((byte) b);
                } else {
                    buffer.put((byte) r);
                    buffer.put((byte) g);
                    buffer.put((byte) b);
                }
            }
            buffer.flip();
        } else if (type == BufferedImage.TYPE_3BYTE_BGR) {
            DataBufferByte db = (DataBufferByte) bi.getRaster().getDataBuffer();
            byte[] bgr = db.getData();
            format = Format.RGB;
            buffer = MemoryUtil.memAlloc(bgr.length);
            for (int i = 0; i < bgr.length; i += 3) {
                buffer.put(bgr[i + 2]); // R
                buffer.put(bgr[i + 1]); // G
                buffer.put(bgr[i]);     // B
            }
            buffer.flip();
        } else if (type == BufferedImage.TYPE_BYTE_GRAY) {
            DataBufferByte db = (DataBufferByte) bi.getRaster().getDataBuffer();
            byte[] gray = db.getData();
            format = Format.GRAYSCALE;
            buffer = MemoryUtil.memAlloc(gray.length);
            buffer.put(gray);
            buffer.flip();
        } else {
            // 其他类型先转换为 ARGB
            BufferedImage converted = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            converted.getGraphics().drawImage(bi, 0, 0, null);
            return fromBufferedImage(converted);
        }
        return new DefaultImage(w, h, format, buffer);
    }

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

    @Contract(pure = true)
    static Lock lock() {
        return LOCK;
    }

    // 用来触发类加载
    @Contract(pure = true)
    public static void initialize() {

    }

    @Contract(pure = true)
    public Toolkit() {

    }

}
