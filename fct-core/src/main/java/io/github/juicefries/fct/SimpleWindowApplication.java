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
// Data 2026/11/05 02:16
//

package io.github.juicefries.fct;

import io.github.juicefries.fct.event.CursorEnterListener;
import io.github.juicefries.fct.event.KeyCharInputListener;
import io.github.juicefries.fct.event.KeyInputListener;
import io.github.juicefries.fct.event.MouseButtonListener;
import io.github.juicefries.fct.lwjgl.Hint;
import io.github.juicefries.fct.lwjgl.WindowHint;
import io.github.juicefries.fct.sign.ApiSign;
import io.github.juicefries.fct.sign.Initializable;
import io.github.juicefries.fct.util.*;
import io.github.juicefries.fct.util.Timer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 简单窗口应用。
 *
 * <p>提供窗口创建、帧循环、输入监听转发和常用窗口操作；默认不渲染，绘制由子类完成。</p>
 *
 * <p>生命周期：{@link #start()} → {@link #simpleInit()} → 每帧 {@link #simpleUpdate(float)} →
 * {@link #stop()} / 窗口关闭 → {@link #simpleDispose()}。</p>
 *
 * @since 1.0.6
 * @author juicefries
 */
@Deprecated(since = "1.0.7.2")
public abstract class SimpleWindowApplication implements Initializable,Application {

    private static final Logger log = LogManager.getLogger(SimpleWindowApplication.class);

    private final Lock asl = Lock.create("AppStateLock");

    final AtomicBoolean initialize = new AtomicBoolean(false);
    String name;
    protected AppSettings settings;
    protected Frame frame;
    protected long windowHandle = MemoryUtil.NULL;

    /** 帧计时器，可用 {@link #setTimer(Timer)} 替换。 */
    protected Timer timer = new NanoTimer();

    /** 每帧回调，由 FCT 的 ThreadManager 调用。 */
    Runnable update = this::update;

    private volatile boolean paused = false;
    private volatile boolean renderEnabled = false;
    private volatile boolean stopping = false;
    private volatile boolean initDone = false;

    private float fps = 0f;
    private float fpsAccum = 0f;
    private int fpsFrames = 0;
    private float elapsed = 0f;
    private float lastTpf = 0f;

    protected SimpleWindowApplication() {

    }

    /**
     * 启动应用：创建窗口并开始帧循环。
     *
     * @throws IllegalStateException 重复启动
     */
    @Override
    public void start() {
        synchronized (asl) {
            if (frame != null && !isInitialize()) {
                throw new IllegalStateException("Called repeatedly during initialization!");
            }
            if (isInitialize()) {
                throw new IllegalStateException("Repeated initialization call!");
            }
        }

        if (settings == null || settings.isEmpty()) {
            settings = new AppSettings(true);
        }
        log.trace("Launch the application {}.", name);
        log.log(Level.ALL, "Build window basic configuration.");

        WindowConfig config = new WindowConfig(false);
        config.setInitialize(false);
        buildWindowBasicConfiguration(config, settings);

        log.trace("Build Window.");
        frame = new SimpleApplicationWindow(config);
        log.trace("Initialize window settings.");
        initializeWindowSettings(frame, settings);
        log.trace("Initialize window");
        frame.initialize();
    }

    /**
     * 停止应用：调用 {@link #simpleDispose()} 后销毁窗口。
     *
     * <p>可在任意线程调用，内部转发到窗口线程；重复调用无副作用。</p>
     */
    @Override
    public void stop() {
        if (stopping) {
            return;
        }
        stopping = true;

        Frame current = frame;
        if (current == null) {
            return;
        }
        if (current.isWorkerThread()) {
            disposeImp();
        } else {
            try {
                current.invoke(this::disposeImp);
            } catch (Throwable throwable) {
                log.warn("Failed to destroy task at the delivery window.: {}", throwable.toString());
            }
        }
    }

    private void disposeImp() {
        try {
            if (isInitialize()) {
                simpleDispose();
            }
        } catch (Throwable throwable) {
            log.error("simpleDispose Throw an exception.", throwable);
        }
        try {
            var threadMgr = frame.getThreadManager();
            threadMgr.removeEvery(update);
        } catch (Throwable throwable) {
            log.warn("Failed to remove frame callback: {}", throwable.toString());
        }
        try {
            frame.dispose();
        } catch (Throwable throwable) {
            log.error("Failed to destroy window.", throwable);
        }
    }

    private void buildWindowBasicConfiguration(@NotNull WindowConfig config, @NotNull AppSettings settings)
    {
        Hint samples = Hint.samples(settings.getSamples());
        config.addHint(samples);
        Array.forArr(settings.getHints(), config::addHint);
    }

    private void initializeWindowSettings(@NotNull Frame frame, @NotNull AppSettings settings)
    {
        frame.setAutomaticallyUpdateViewport(false);
        frame.setEnableDefaultRefreshBehavior(false);

        int width = settings.getWidth();
        int height = settings.getHeight();
        int x = settings.getX();
        int y = settings.getY();
        boolean center = settings.isCenter();
        Object icon = settings.getObject("Icon", null);

        if (icon instanceof Image image) {
            frame.setIconImage(image);
        }

        frame.setSize(width, height);
        if (center) {
            frame.setLocation(null);
        } else {
            frame.setLocation(x, y);
        }
        frame.setTitle(settings.getTitle());


        frame.setDefaultCloseOperation(Frame.DO_NOTHING_ON_CLOSE);
        frame.addWindowLifeListener(_ -> {
            if (!stopping) {
                stop();
            }
        });

        frame.setVisible(true);
    }

    /**
     * 由 FCT 在窗口线程调用，外部不要手动调用。
     *
     * @since 1.0.6
     */
    @Override
    @ApiSign.Dangerous(since = "1.0.6")
    public void initialize() {
        if (isInitialize()) {
            return;
        }
        windowHandle = frame.window;
        //noinspection deprecation
        frame.setVSync(settings.isVSync());


        initialize.set(true);

        var threadMgr = frame.getThreadManager();
        threadMgr.every(update);

        try {
            simpleInit();
        } catch (Exception e) {
            log.error("An error occurred while calling the simple initialization method.",e);
        }
    }

    @Override
    public void update() {
        if (!initDone || !isInitialize() || stopping) {
            return;
        }
        timer.update();
        lastTpf = timer.getTimePerFrame();
        elapsed += lastTpf;

        fpsAccum += lastTpf;
        fpsFrames++;
        if (fpsAccum >= 0.5f) {
            fps = fpsFrames / fpsAccum;
            fpsAccum = 0f;
            fpsFrames = 0;
        }

        if (!paused) {
            simpleUpdate(lastTpf);
        }

        if (renderEnabled) {
            frame.update();
        }
    }

    /** 窗口创建、GL 上下文可用后调用一次。 */
    protected abstract void simpleInit();

    /**
     * 每帧调用。
     *
     * @param tpf 上一帧耗时（秒）
     */
    protected void simpleUpdate(float tpf) {
    }

    /** 停止或关闭窗口前调用一次。 */
    protected void simpleDispose() {
    }

    /**
     * FCT 绘制入口；仅在 {@link #setRenderEnabled(boolean)} 为 true 时调用。
     *
     * @param graphics FCT 绘制上下文
     */
    protected void render(Graphics graphics) {
    }

    /**
     * 添加键盘按下/释放监听。
     *
     * @param listener 监听器
     */
    public void addKeyListener(@NotNull KeyInputListener listener) {
        frame.addKeyInputListener(listener);
    }

    /**
     * 添加字符输入监听。
     *
     * @param listener 监听器
     */
    public void addCharListener(@NotNull KeyCharInputListener listener) {
        frame.addKeyCharInputListener(listener);
    }

    /**
     * 添加鼠标按键监听。
     *
     * @param listener 监听器
     */
    public void addMouseListener(@NotNull MouseButtonListener listener) {
        frame.addMouseButtonListener(listener);
    }

    /**
     * 添加鼠标进出窗口监听。
     *
     * @param listener 监听器
     */
    public void addCursorEnterListener(@NotNull CursorEnterListener listener) {
        frame.addCursorEnterListener(listener);
    }

    /** 锁定光标到窗口。 */
    public void lockCursor() {
        if (windowHandle != MemoryUtil.NULL) {
            GLFW.glfwSetInputMode(windowHandle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
        }
    }

    /** 释放光标。 */
    public void unlockCursor() {
        if (windowHandle != MemoryUtil.NULL) {
            GLFW.glfwSetInputMode(windowHandle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        }
    }

    /**
     * 光标是否锁定。
     *
     * @return 锁定返回 true
     */
    public boolean isCursorLocked() {
        return windowHandle != MemoryUtil.NULL
                && GLFW.glfwGetInputMode(windowHandle, GLFW.GLFW_CURSOR) == GLFW.GLFW_CURSOR_DISABLED;
    }

    /**
     * 设置窗口标题。
     *
     * @param title 标题
     */
    public void setTitle(@NotNull String title) {
        frame.setTitle(title);
    }

    /**
     * 设置窗口尺寸（像素）。
     *
     * @param width  宽
     * @param height 高
     */
    public void setWindowSize(int width, int height) {
        frame.setSize(width, height);
    }

    /**
     * 设置窗口位置（像素）。
     *
     * @param x 横坐标
     * @param y 纵坐标
     */
    public void setWindowLocation(int x, int y) {
        frame.setLocation(x, y);
    }

    /**
     * 设置垂直同步。
     *
     * @param sync 是否开启
     */
    public void setVSync(boolean sync) {
        frame.setVSync(sync);
    }

    /**
     * 设置窗口可见性。
     *
     * @param visible 是否可见
     */
    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }

    /** 请求关闭窗口，等价于 {@link #stop()}。 */
    public void requestClose() {
        stop();
    }

    /**
     * 把任务投递到窗口线程执行。
     *
     * @param task 任务
     */
    public void runOnWindowThread(@NotNull Runnable task) {
        frame.invoke(task);
    }

    /**
     * 当前线程是否为窗口线程。
     *
     * @return 是返回 true
     */
    public boolean isWindowThread() {
        return frame != null && frame.isWorkerThread();
    }

    /**
     * 当前帧率。
     *
     * @return 帧/秒
     */
    public float fps() {
        return fps;
    }

    /**
     * 上一帧耗时。
     *
     * @return 秒
     */
    public float tpf() {
        return lastTpf;
    }

    /**
     * 启动至今的秒数。
     *
     * @return 秒
     */
    public float elapsedSeconds() {
        return elapsed;
    }

    /**
     * 暂停帧逻辑（帧循环继续，fps 仍统计）。
     *
     * @param paused 是否暂停
     */
    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    /**
     * 是否暂停。
     *
     * @return 暂停返回 true
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * 是否启用 FCT 绘制。
     *
     * @param enabled true 时每帧调用 {@code frame.update()} 并回调 {@link #render(Graphics)}
     */
    public void setRenderEnabled(boolean enabled) {
        this.renderEnabled = enabled;
    }

    /**
     * 是否启用 FCT 绘制。
     *
     * @return 启用返回 true
     */
    public boolean isRenderEnabled() {
        return renderEnabled;
    }

    /**
     * 替换帧计时器并重置。
     *
     * @param timer 计时器
     */
    public void setTimer(@NotNull Timer timer) {
        this.timer = timer;
        timer.reset();
    }

    /**
     * 设置应用名。
     *
     * @param name 名称
     */
    @Override
    public void setName(String name) {
        if (name == null) {
            throw new NullPointerException("name is null!");
        }
        this.name = name;
    }

    @Override
    public boolean isInitialize() {
        synchronized (asl) {
            return initialize.get();
        }
    }

    /**
     * 窗口对象；未启动时返回 null。
     *
     * @return 窗口
     */
    public Frame getFrame() {
        return frame;
    }

    /**
     * 原生 GLFW 窗口句柄。
     *
     * @return 句柄；未创建时为 {@link MemoryUtil#NULL}
     */
    public long getWindowHandle() {
        return windowHandle;
    }

    /**
     * 窗口设置。
     *
     * @return 设置对象
     */
    public AppSettings getSettings() {
        return settings;
    }

    /**
     * 应用名。
     *
     * @return 名称
     */
    @Override
    public String getName() {
        return name;
    }

    private final class SimpleApplicationWindow extends Frame {

        private SimpleApplicationWindow(@NotNull WindowConfig config) {
            super(config);
        }

        @Override
        protected void initComplete() {
            super.initComplete();
            try {
                SimpleWindowApplication.this.initialize();
            } catch (Throwable throwable) {
                log.error("An error occurred during initialization.", throwable);
                stop();
                return;
            }
            initDone = true;
        }

        @Override
        public void paint(Graphics g) {
            render(g);
        }
    }

    /**
     * 窗口设置。
     */
    public static class AppSettings extends Settings {

        private static final Logger log = LogManager.getLogger(AppSettings.class);
        private static final AppSettings defaults = new AppSettings(false);

        static {
            defaults.put("Width", 800);
            defaults.put("Height", 600);
            defaults.put("V-Sync", true);
            defaults.put("Center", true);
            defaults.put("X", GLFW.GLFW_ANY_POSITION);
            defaults.put("Y", GLFW.GLFW_ANY_POSITION);
            defaults.put("Title", "SimpleWindowApplication");
            defaults.put("Icon", null);
            defaults.put("Samples", 4);
        }

        /**
         * @param loadDefs 是否加载默认值
         */
        public AppSettings(boolean loadDefs) {
            if (loadDefs) {
                putAll(defaults);
            }
        }

        /**
         * @param width  宽
         * @param height 高
         */
        public void setWindowSize(int width, int height) {
            putInteger("Width", width);
            putInteger("Height", height);
        }

        /**
         * @param x 横坐标
         * @param y 纵坐标
         */
        public void setLocation(int x, int y) {
            putInteger("X", x);
            putInteger("Y", y);
        }

        /**
         * @param center 是否居中
         */
        public void setCenter(boolean center) {
            putBoolean("Center", center);
        }

        /**
         * @param samples 采样数
         */
        public void setSamples(int samples) {
            putInteger("Samples", samples);
        }

        /**
         * @param title 标题
         */
        public void setTitle(String title) {
            putString("Title", title);
        }

        /**
         * @param vSync 是否开启垂直同步
         */
        public void setVSync(boolean vSync) {
            putBoolean("V-Sync", vSync);
        }

        /**
         * @param icon 图标
         */
        public void setIcon(Image icon) {
            put("Icon", icon);
        }

        public void setHint(String hint,int value) {
            putInteger("Hint-%s".formatted(hint),value);
        }

        public Hint getHint(String hint) {
            if (hint == null) {
                throw new NullPointerException("hint is null!");
            }

            var hintKey = "Hint-%s".formatted(hint);
            if (!containsKey(hintKey)) {
                throw new IllegalArgumentException("Non-existent window hint, Hint: %s.".formatted(hintKey));
            }
            var hintVal = getInteger(hintKey,0);

            return WindowHint.hint(hintKey,hintVal);
        }

        public Hint[] getHints() {
            if (isEmpty()) {
                return new Hint[0];
            }

            final String prefix = "Hint-";
            java.util.List<Hint> hints = new java.util.ArrayList<>();
            for (String key : keySet()) {
                if (key == null || !key.startsWith(prefix)) {
                    continue;
                }
                int value = getInteger(key, 0);
                hints.add(WindowHint.hint(key, value));
            }
            return hints.toArray(Hint[]::new);
        }


        /**
         * @return 是否开启垂直同步
         */
        public boolean isVSync() {
            return getBoolean("V-Sync", true);
        }

        /**
         * @return 标题
         */
        public String getTitle() {
            return getString("Title", "SimpleWindowApplication");
        }

        /**
         * @return 横坐标
         */
        public int getX() {
            return getInteger("X", GLFW.GLFW_ANY_POSITION);
        }

        /**
         * @return 纵坐标
         */
        public int getY() {
            return getInteger("Y", GLFW.GLFW_ANY_POSITION);
        }

        /**
         * @return 位置
         */
        public Vector2i getLocation() {
            return new Vector2i(getX(), getY());
        }

        /**
         * @return 采样数
         */
        public int getSamples() {
            return getInteger("Samples", 4);
        }

        /**
         * @return 宽
         */
        public int getWidth() {
            return getInteger("Width", 800);
        }

        /**
         * @return 高
         */
        public int getHeight() {
            return getInteger("Height", 600);
        }

        /**
         * @return 尺寸
         */
        public Vector2i getSize() {
            return new Vector2i(getWidth(), getHeight());
        }

        /**
         * @return 是否居中
         */
        public boolean isCenter() {
            return getBoolean("Center", true);
        }
    }

    /**
     * 简化名称的抽象类。
     *
     * @since 1.0.6
     * @author juicefries
     * @see SimpleWindowApplication
     */
    public abstract static class SimpleApplication extends SimpleWindowApplication {

        protected SimpleApplication() {
        }
    }

}
