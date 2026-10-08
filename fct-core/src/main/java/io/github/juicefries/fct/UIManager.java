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
// Data 2026/10/04 17:47
//

package io.github.juicefries.fct;

import io.github.juicefries.fct.event.SysEvent;
import io.github.juicefries.fct.event.SystemListener;
import io.github.juicefries.fct.sign.ApiSign;
import io.github.juicefries.fct.sign.Manager;
import io.github.juicefries.fct.util.Lock;
import io.github.juicefries.fct.util.Parameters;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryUtil;

/**
 * <h2>UI 管理器 </h2>
 *
 * <p>
 *     管理 FCT 的界面资源，目前负责光标的创建、获取与销毁，
 *     <br>
 *     内置十种{@link GLFW}标准光标形状，并为每种提供别名，
 *     <br>
 *     同时允许运行期注册自定义光标图像。
 *     <br>
 *     文档让AI写的。
 * </p>
 *
 * @since 1.0.1
 * @version 1.0
 * @author juicefries
 * @author ds(AI)
 * @see SystemListener
 * @see GLFW#glfwCreateStandardCursor(int)
 */
public class UIManager implements Manager {

    /**
     * 普通箭头光标，窗口未指定光标时使用
     * @since 1.0.1
     */
    public final static String DEFAULT_CURSOR = "FCT.DefaultCursor";

    /**
     * 文本 I 形光标
     * @since 1.0.1
     */
    public final static String TEXT_CURSOR = "FCT.TextCursor";

    /**
     * 十字准星光标
     * @since 1.0.1
     */
    public final static String CROSSHAIR_CURSOR = "FCT.CrosshairCursor";

    /**
     * 指向手形光标
     * @since 1.0.1
     */
    public final static String HAND_CURSOR = "FCT.HandCursor";
    /**
     * 水平双向箭头光标
     * @since 1.0.1
     */
    public final static String RESIZE_EW_CURSOR = "FCT.ResizeEWCursor";

    /**
     * 垂直双向箭头光标
     * @since 1.0.1
     */
    public final static String RESIZE_NS_CURSOR = "FCT.ResizeNSCursor";

    /**
     * 左上↔右下对角箭头光标
     * @since 1.0.1
     */
    public final static String RESIZE_NWSE_CURSOR = "FCT.ResizeNWSECursor";

    /**
     * 右上↔左下对角箭头光标
     * @since 1.0.1
     */
    public final static String RESIZE_NESW_CURSOR = "FCT.ResizeNESWCursor";

    /**
     * 全向缩放光标
     * @since 1.0.1
     */
    public final static String RESIZE_ALL_CURSOR = "FCT.ResizeAllCursor";

    /**
     * 禁止操作光标
     * @since 1.0.1
     */
    public final static String NOT_ALLOWED_CURSOR = "FCT.NotAllowedCursor";

    /**
     * 日志
     * @since 1.0.1
     */
    private static final Logger log = LogManager.getLogger(UIManager.class);

    /**
     * 锁
     * @since 1.0.1
     */
    private final static Lock LOCK = Lock.create();

    /**
     * 光标调用锁
     * @since 1.0.1
     */
    private final static Lock CURSOR_LOCK = Lock.create();

    /**
     * 系统监听器
     * @since 1.0.1
     */
    private final static SystemListener listener = SystemListener.createPassiveListener(
            "FCT-UIManagerSystemListener",
            UIManager.class,
            e -> {
                if (e == null) return;
                if (e.getType() == SysEvent.SYS_CANCEL_EVENT || e.getType() == SysEvent.SYS_TERMINATE_EVENT) {
                    dispose();
                }
                if (e.getType() == SysEvent.SYS_INIT_EVENT) {
                    log.log(Level.ALL,"你的意思是你在整个FCT还没初始化的情况下，让窗口先初始化了?");
                    initialize();
                }
            }
    );

    /**
     * 光标存储
     * <p>
     *     缓存全局资源。
     * </p>
     * @since 1.0.1
     */
    private final static Map<String,Long> cursors = new ConcurrentHashMap<>();

    /**
     * 默认光标名 [直接名,别名...]
     * <p>
     *     别名用来在获取时如果没有对应光标时就指向特定光标，
     *     <br>
     *     指向别名返回时不写入map，不占用重复资源
     * </p>
     * @since 1.0.1
     */
    private final static String[][] DEFAULT_CURSOR_NAMES = {
            {DEFAULT_CURSOR,     "default",     "arrow"},
            {TEXT_CURSOR,        "text",        "ibeam"},
            {CROSSHAIR_CURSOR,   "crosshair",   "cross"},
            {HAND_CURSOR,        "pointer",     "hand"},
            {RESIZE_EW_CURSOR,   "ew-resize",   "resizeEW"},
            {RESIZE_NS_CURSOR,   "ns-resize",   "resizeNS"},
            {RESIZE_NWSE_CURSOR, "nwse-resize", "resizeNWSE"},
            {RESIZE_NESW_CURSOR, "nesw-resize", "resizeNESW"},
            {RESIZE_ALL_CURSOR,  "move",        "resizeAll"},
            {NOT_ALLOWED_CURSOR, "not-allowed", "forbidden"}
    };


    /**
     * 默认光标样式映射表
     * @since 1.0.1
     */
    private final static Map<String,Integer> DEFAULT_CURSOR_STYLE_MAPPING = new HashMap<>();

    final static Parameters<String,Object> DEFAULT_UI = new Parameters<>();

    static {
        Toolkit.initialize();
        initialize();
        initDefaultUiValue();
    }

    /**
     * 销毁全部光标
     * <p>
     *     清空样式映射并释放已创建的每一个光标，
     *     <br>
     *     由系统监听器在 FCT 注销或终止时调用，
     *     <br>
     *     除非你知道自己在干什么，否则不要手动调用。
     * </p>
     * @since 1.0.1
     */
    @ApiStatus.Internal
    @ApiSign.Dangerous(since = "1.0.1")
    @ApiSign.InternalApi(since = "1.0.1")
    public static void dispose() {
        synchronized (LOCK) {
            synchronized (CURSOR_LOCK) {
                DEFAULT_CURSOR_STYLE_MAPPING.clear();
            }

            for (long cursor : cursors.values()) {
                try {
                    if (cursor == MemoryUtil.NULL) continue;
                    GLFW.glfwDestroyCursor(cursor);
                    log.trace("Cursor has been deactivated : {}", cursor);
                } catch (Exception e) {
                    log.error("An error occurred while closing cursor : {}.", cursor, e);
                }
            }
        }
    }

    /**
     * 设置窗口光标
     * @param window 窗口
     * @param cursor 光标
     * @since 1.0.1
     */
    @ApiStatus.Experimental
    @Contract("null, _ -> fail")
    public static void setWindowCursor(Window window, long cursor) {
        if (window == null) {
            throw new NullPointerException("window is null!");
        }
        long _window = Toolkit.getWindow(window);
        GLFW.glfwSetCursor(_window,cursor);
    }

    /**
     * 设置窗口光标
     * @param window 窗口
     * @param name 光标名称
     * @since 1.0.1
     */
    @ApiStatus.Experimental
    @Contract("null, _ -> fail")
    public static void setWindowCursor(Window window, String name) {
        if (window == null) {
            throw new NullPointerException("window is null!");
        }
        setWindowCursor(window,getCursor(name));
    }

    @Contract("null -> fail")
    @ApiStatus.Experimental
    public static void resetCursor(Window window) {
        if (window == null) {
            throw new NullPointerException("window is null!");
        }
        setWindowCursor(window,MemoryUtil.NULL);
    }

    /**
     * 创建自定义光标
     * <p>
     *     名称不可与任何默认光标的正名重名，
     *     <br>
     *     也不可用已有的自定义名称，若重名则先销毁旧光标再创建，
     *     <br>
     *     创建失败只记录日志，不抛出异常。
     * </p>
     * @param name 光标名
     * @param image 光标图像
     * @param xhot 热点横向偏移
     * @param yhot 热点纵向偏移
     * @throws NullPointerException 名称或图像为{@code null}
     * @throws IllegalArgumentException 名称被内置正名占用
     * @since 1.0.1
     */
    @Contract("null, _, _, _ -> fail; !null, null, _, _ -> fail")
    public static void createCursor(String name, GLFWImage image, int xhot, int yhot) {
        if (name == null) {
            throw new NullPointerException("name is null!");
        }
        if (image == null) {
            throw new NullPointerException("image is null!");
        }
        int location = getAliasLocationAndCheckIsDefaultCursorName(name);
        if (location != -1) {
            throw new IllegalArgumentException("This cursor name cannot be used! name: %s .".formatted(name));
        }
        try {
            if (cursors.containsKey(name)) {
                Long cursor = cursors.get(name);
                if (cursor == null) {
                    throw new NullPointerException("cursor is null!");
                }
                if (cursor != MemoryUtil.NULL) {
                    GLFW.glfwDestroyCursor(cursor);
                }
            }
            long cursor = GLFW.glfwCreateCursor(image, xhot, yhot);
            if (cursor == MemoryUtil.NULL) {
                throw new IllegalArgumentException("Cursor creation failed! name : %s.".formatted(name));
            }
            cursors.put(name,cursor);
        } catch (Exception e) {
            log.error("An error occurred when creating the cursor, name : {} .", name, e);
        }
    }

    /**
     * 创建自定义光标，热点为{@code (0,0)}
     * @see #createCursor(String, GLFWImage, int, int)
     * @since 1.0.1
     */
    @Contract("null, _ -> fail; !null, null -> fail")
    public static void createCursor(String name, GLFWImage image) {
        createCursor(name,image,0,0);
    }

    /**
     * 创建自定义光标
     * <p>
     *     内部会把{@link Image}转换为{@link GLFWImage}，
     *     <br>
     *     并在创建结束后释放转换出的图像。
     * </p>
     * @throws NullPointerException 名称或图像为{@code null}
     * @see #createCursor(String, GLFWImage, int, int)
     * @since 1.0.1
     */
    @Contract("null, _, _, _ -> fail; !null, null, _, _ -> fail")
    public static void createCursor(String name, Image image, int xhot, int yhot) {
        if (name == null) {
            throw new NullPointerException("name is null!");
        }
        if (image == null) {
            throw new NullPointerException("image is null!");
        }
        GLFWImage glfwImage = Toolkit.getImage(image);
        createCursor(name,glfwImage,xhot,yhot);
        glfwImage.free();
    }

    /**
     * 创建自定义光标，热点为{@code (0,0)}
     * @see #createCursor(String, Image, int, int)
     * @since 1.0.1
     */
    @Contract("null, _ -> fail; !null, null -> fail")
    public static void createCursor(String name, Image image) {
        createCursor(name,image,0,0);
    }

    /**
     * 获取光标句柄
     * <p>
     *     先按名称查表，未命中则视作别名，去默认光标表中找对应的正名再查，
     *     <br>
     *     别名被同名自定义光标覆盖时以自定义为准。
     * </p>
     * @param cursorName 光标名，可为正名或别名
     * @return 光标句柄
     * @throws NullPointerException 名称为{@code null}
     * @throws IllegalArgumentException 没有匹配的光标
     * @since 1.0.1
     */
    @Contract("null -> fail")
    public static long getCursor(String cursorName) {
        if (cursorName == null) {
            throw new NullPointerException("cursorName is null!");
        }

        if (cursors.containsKey(cursorName)) {
            return cursors.get(cursorName);
        }
        for (var names : DEFAULT_CURSOR_NAMES) {
            if (names == null) continue;
            for (var name : names) {
                if (!Objects.equals(name, cursorName)) continue;
                return cursors.get(names[0]);
            }
        }
        throw new IllegalArgumentException("No matching cursor found!");
    }

    /**
     * 获取全部已注册的光标名
     * <p>
     *     不包含别名。
     * </p>
     * @return 光标名数组
     * @since 1.0.1
     */
    public static String @NotNull [] getCursorNames() {
        synchronized (CURSOR_LOCK) {
            return cursors.keySet().toArray(new String[0]);
        }
    }

    /**
     * 获取指定默认光标的全部名称
     * <p>
     *     返回的是副本，改动它不会影响内部表。
     * </p>
     * @param defaultCursorName 默认光标的正名
     * @return [正名, 别名...]
     * @throws NullPointerException 名称为{@code null}
     * @throws IllegalArgumentException 该名称不是任何默认光标的正名
     * @since 1.0.1
     */
    @Contract("null -> fail")
    public static String @NotNull [] getDefaultCursorAlias(String defaultCursorName) {
        if (defaultCursorName == null) {
            throw new NullPointerException("defaultCursorName is null!");
        }
        int location = getAliasLocationAndCheckIsDefaultCursorName(defaultCursorName);
        if (location == -1) {
            throw new IllegalArgumentException("This name is not any of the default cursor names!");
        }
        var arr = DEFAULT_CURSOR_NAMES[location];
        var names = new String[arr.length];
        System.arraycopy(arr, 0, names, 0, arr.length);
        return names;
    }

    /**
     * 获取默认光标名称包括别名
     * <p>
     *     我也是闲得蛋疼，把整个数组完整复制了一份
     * </p>
     * @return 默认光标名
     * @since 1.0.1
     */
    public static String @NotNull [][] getDefaultCursorNames() {
        var arr = DEFAULT_CURSOR_NAMES;
        var copy = new String[arr.length][];
        for (int i = 0; i < arr.length; i++) {
            var names = arr[i];
            var _names_copy = new String[names.length];
            System.arraycopy(names,0,_names_copy,0,names.length);
            copy[i] = _names_copy;
        }
        return copy;
    }

    /**
     * 获取默认光标正名的位置
     * <p>
     *     只比对正名，别名不会被命中，
     *     <br>
     *     不建议用。
     * </p>
     * @param name 名称
     * @return 该正名在默认光标表中的下标，不存在则返回{@code -1}
     * @since 1.0.1
     */
    @Contract(pure = true)
    @ApiSign.InternalApi(since = "1.0.1")
    public static int getAliasLocationAndCheckIsDefaultCursorName(@NotNull String name) {
        var arr = DEFAULT_CURSOR_NAMES;
        for (int i = 0; i < arr.length; i++) {
            var names = arr[i];
            if (name.equals(names[0])) {
                return i;
            }
        }
        return -1;
    }

    @Contract("null -> fail")
    public static Color getColor(String key) {
        if (key == null) {
            throw new NullPointerException("key is null!");
        }
        if (!DEFAULT_UI.containsKey(key)) {
            return Color.NEAR_BLACK;
        }
        return DEFAULT_UI.get(key,Color.NEAR_BLACK);
    }

    @Contract("null -> fail")
    public static int getInteger(String key) {
        if (key == null) {
            throw new NullPointerException("key is null!");
        }
        if (!DEFAULT_UI.containsKey(key)) {
            return 0;
        }
        return DEFAULT_UI.get(key,0);
    }

    @Contract("null -> fail")
    public static long getLong(String key) {
        if (key == null) {
            throw new NullPointerException("key is null!");
        }
        if (!DEFAULT_UI.containsKey(key)) {
            return 0L;
        }
        return DEFAULT_UI.get(key,0L);
    }

    @Contract("null -> fail")
    public static float getFloat(String key) {
        if (key == null) {
            throw new NullPointerException("key is null!");
        }
        if (!DEFAULT_UI.containsKey(key)) {
            return 0.0f;
        }
        return DEFAULT_UI.get(key,0.0f);
    }

    @Contract("null -> fail")
    public static boolean getBoolean(String key) {
        if (key == null) {
            throw new NullPointerException("key is null!");
        }
        if (!DEFAULT_UI.containsKey(key)) {
            return false;
        }
        return DEFAULT_UI.get(key,false);
    }

    @Contract("null -> fail")
    public static char getChar(String key) {
        if (key == null) {
            throw new NullPointerException("key is null!");
        }
        if (!DEFAULT_UI.containsKey(key)) {
            return '\u0000';
        }
        return DEFAULT_UI.get(key,'\u0000');
    }

    @Contract("null -> fail")
    public static Object getObject(String key) {
        if (key == null) {
            throw new NullPointerException("key is null!");
        }
        if (!DEFAULT_UI.containsKey(key)) {
            return new Object();
        }
        return DEFAULT_UI.get(key,new Object());
    }

    @Contract("null -> fail")
    public static Vector2i getVector2i(String key) {
        if (key == null) {
            throw new NullPointerException("key is null!");
        }
        if (!DEFAULT_UI.containsKey(key)) {
            return new Vector2i(0);
        }
        return DEFAULT_UI.get(key,new Vector2i(0));
    }

    // ========================= UTIL =========================

    /**
     * 初始化
     * @since 1.0.1
     */
    private static void initialize() {
        Sys.register(listener);
        initializeDefaultCursorStyleMapping();
        initializeDefaultCursors();
    }

    /**
     * 初始化默认光标样式映射
     * @since 1.0.1
     */
    private static void initializeDefaultCursorStyleMapping() {
        synchronized (LOCK) {
            var map = cursorStyleMapping();
            map.put(DEFAULT_CURSOR, GLFW.GLFW_ARROW_CURSOR);
            map.put(TEXT_CURSOR, GLFW.GLFW_IBEAM_CURSOR);
            map.put(CROSSHAIR_CURSOR, GLFW.GLFW_CROSSHAIR_CURSOR);
            map.put(HAND_CURSOR, GLFW.GLFW_POINTING_HAND_CURSOR);
            map.put(RESIZE_EW_CURSOR, GLFW.GLFW_RESIZE_EW_CURSOR);
            map.put(RESIZE_NS_CURSOR, GLFW.GLFW_RESIZE_NS_CURSOR);
            map.put(RESIZE_NWSE_CURSOR, GLFW.GLFW_RESIZE_NWSE_CURSOR);
            map.put(RESIZE_NESW_CURSOR, GLFW.GLFW_RESIZE_NESW_CURSOR);
            map.put(RESIZE_ALL_CURSOR, GLFW.GLFW_RESIZE_ALL_CURSOR);
            map.put(NOT_ALLOWED_CURSOR, GLFW.GLFW_NOT_ALLOWED_CURSOR);
        }
    }

    static void initDefaultUiValue() {
        var defaultUi = DEFAULT_UI;
        defaultUi.put("fct.window.background",Color.NEAR_BLACK);
        defaultUi.put("fct.window.foreground",Color.NEAR_WHITE);
        defaultUi.put("fct.window.size",new Vector2i(500));
        defaultUi.put("fct.window.point",new Vector2i(GLFW.GLFW_ANY_POSITION));
        defaultUi.put("fct.window.visible",false);
        defaultUi.put("fct.component.background",Color.NEAR_WHITE);
        defaultUi.put("fct.control.background",Color.NEAR_WHITE);
        defaultUi.put("fct.container.background",Color.NEAR_WHITE);
        defaultUi.put("fct.button.background",new Color(0.08f, 0.08f, 0.08f, 1.0f));
        defaultUi.put("fct.button.foreground",Color.NEAR_WHITE);

        // 通用前景，给没有单独定义的类型兜底
        defaultUi.put("fct.component.foreground", Color.NEAR_WHITE);
        defaultUi.put("fct.control.foreground", Color.NEAR_WHITE);
        defaultUi.put("fct.container.foreground", Color.NEAR_WHITE);

        // 标签
        defaultUi.put("fct.label.background", Color.NONE);
        defaultUi.put("fct.label.foreground", Color.NEAR_WHITE);

        // 按钮状态
        defaultUi.put("fct.button.background.hover", new Color(0.13f, 0.13f, 0.13f, 1.0f));
        defaultUi.put("fct.button.background.press", new Color(0.05f, 0.05f, 0.05f, 1.0f));
        defaultUi.put("fct.button.font.size", 14.0f);

        // 全局字体与不透明度
        defaultUi.put("fct.font.size", 16.0f);
        defaultUi.put("fct.component.opaque", true);
    }

    /**
     * 初始化默认光标
     * @since 1.0.1
     */
    private static void initializeDefaultCursors() {
        synchronized (CURSOR_LOCK) {
            var map = cursorStyleMapping();
            map.forEach((name, shape) -> {
                try {
                    if (name == null) {
                        throw new NullPointerException("name is null!");
                    }
                    if (shape == null) {
                        throw new NullPointerException("shape is null!");
                    }
                } catch (Exception e) {
                    log.error("An error occurred while obtaining the default cursor style mapping.",e);
                    return;
                }

                try {
                    long cursor = GLFW.glfwCreateStandardCursor(shape);
                    if (cursor == MemoryUtil.NULL) {
                        throw new IllegalArgumentException("Failed to create default cursor!"
                                + " name: " + name
                                + " shape: " + shape + "."
                        );
                    }
                    cursors.put(name, cursor);
                } catch (Exception e) {
                    log.error(
                            "An error occurred while creating the default cursor, name : {}, shape : {}.",
                            name, shape, e
                    );
                }
            });
        }
    }

    /**
     * 工具方法
     * @return 默认光标样式表
     * @since 1.0.1
     */
    @Contract(pure = true)
    private static Map<String,Integer> cursorStyleMapping() {
        return DEFAULT_CURSOR_STYLE_MAPPING;
    }

}
