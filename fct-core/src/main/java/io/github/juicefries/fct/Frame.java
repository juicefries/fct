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
// Data 2026/08/15 15:42
//

package io.github.juicefries.fct;

import io.github.juicefries.fct.sign.ApiSign;
import io.github.juicefries.fct.util.Array;
import io.github.juicefries.fct.util.Lock;
import io.github.juicefries.fct.util.Util;
import java.util.Objects;
import java.util.function.Consumer;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryUtil;

/**
 * <h2>Frame</h2>
 * @since 0.0.2
 */
public class Frame extends Window implements WindowConstants {

    private Consumer<Graphics> backgroundPaint = Util.emptyConsumer();
    private Consumer<Graphics> foregroundPaint = Util.emptyConsumer();

    @MagicConstant(intValues = {
            WindowConstants.DISPOSE_ON_CLOSE,
            WindowConstants.DO_NOTHING_ON_CLOSE,
            WindowConstants.HIDE_ON_CLOSE,
            WindowConstants.EXIT_ON_CLOSE
    })
    private int defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE;

    {
        parameters.put("Icon",false,null);
        parameters.put("Notice",false,Util.emptyConsumer((Consumer<Window>) null));
    }

    public Frame() {
        this(null,null);
    }

    public Frame(String title) {
        this(title,null);
    }

    public Frame(WindowConfig configuration) {
        this(null,configuration);
    }

    public Frame(String title, WindowConfig configuration) {
        super((Lock) null);
        var config = Objects.requireNonNullElseGet(configuration, WindowConfig::new);
        parameters.put("Config",true,config);
        parameters.put("Title",true,Objects.requireNonNullElse(title, ""));
        if (config.isInitialize()) {
            initialize();
        }
    }

    protected Frame(Lock lock) {
        super(lock);
    }

    // ========================= OTM =========================


    @Override
    protected void initComplete() {
        boolean[] ib = Array.createB(1);
        var icon = parameters.get("Icon",(Image) null,ib);
        boolean[] nb = Array.createB(1);
        var notice = parameters.get("Notice",(Consumer<Window>) null,nb);

        boolean[] tb = Array.createB(1);
        var title  = parameters.get("Title" , "",tb);
        if (tb[0]) {
            setTitle(title);
        }

        checkI : if (ib[0]) {
            if (icon == null) break checkI;
            var glfwImage = ImageToolkit.getImage(icon);
            GLFWImage.Buffer buffer = GLFWImage.malloc(1).put(0, glfwImage);
            GLFW.glfwSetWindowIcon(window, buffer);
            buffer.free();
            glfwImage.free();
        }
        checkN : if (nb[0]) {
            if (notice == null) break checkN;
            notice.accept(this);
        }

        super.initComplete();
    }

    @Override
    public void paint(Graphics g) {
        if (g == null || !isWorkerThread()) return;

        { // 绘制背景
            if (backgroundPaint != null) backgroundPaint.accept(g);
        }

        super.paint(g);

        { // 绘制前景
            if (foregroundPaint != null) foregroundPaint.accept(g);
        }
    }

    @Override
    public void paintBackground(Graphics g) {

    }

    @Override
    public void paintForeground(Graphics g) {

    }

    // ========================= SET =========================

    /**
     * 设置标题
     * <p>
     *     设置窗口的标题
     * </p>
     *
     * @param title 标题
     * @since 0.0.1
     * @throws NullPointerException 标题不能为{@code null}
     */
    public void setTitle(String title) {
        if (title == null) {
            throw new NullPointerException("title is null!");
        }
        if (check()) {
            invoke(() -> GLFW.glfwSetWindowTitle(window,title));
        } else {
            parameters.put("Title",true,title);
        }
    }

    /**
     * 设置默认关闭选项
     *
     * <p>
     *     设置Frame在默认窗口生命输入接收到关闭输入时的操作。
     * </p>
     *
     * @param operation 选项
     * @since 0.0.1
     * @see WindowConstants#DISPOSE_ON_CLOSE
     * @see WindowConstants#DO_NOTHING_ON_CLOSE
     * @see WindowConstants#HIDE_ON_CLOSE
     * @see WindowConstants#EXIT_ON_CLOSE
     * @throws IllegalArgumentException 选项不符合任意一个关闭选项值时抛出
     */
    public void setDefaultCloseOperation(@MagicConstant(
            intValues = {
                    WindowConstants.DISPOSE_ON_CLOSE,
                    WindowConstants.DO_NOTHING_ON_CLOSE,
                    WindowConstants.HIDE_ON_CLOSE,
                    WindowConstants.EXIT_ON_CLOSE
            }
    ) int operation) {
        if (!Array.contains(operation,
                WindowConstants.DISPOSE_ON_CLOSE,
                WindowConstants.DO_NOTHING_ON_CLOSE,
                WindowConstants.HIDE_ON_CLOSE,
                WindowConstants.EXIT_ON_CLOSE
        )) {
            throw new IllegalArgumentException("Nonexistent default off option value! value:" + operation);
        }

        if (this.defaultCloseOperation != operation) {
            this.defaultCloseOperation = operation;
        }
    }

    /**
     * 设置背景paint
     *
     * <p>
     *     设置在绘制时调用此方法设置的val,
     *     <br>
     *     若使用时请不要重写{@link #paint(Graphics)}。
     * </p>
     *
     * @param val 绘制逻辑
     * @since 0.0.3
     */
    public void setBackgroundPaint(Consumer<Graphics> val) {
        if (val == null) {
            throw new NullPointerException("val is null!");
        }
        this.backgroundPaint = val;
    }


    /**
     * 设置前景paint
     *
     * <p>
     *     设置在绘制时调用此方法设置的val.
     *     <br>
     *     若使用时请不要重写{@link #paint(Graphics)}。
     * </p>
     *
     * @param val 绘制逻辑
     * @since 0.0.3
     */
    public void setForegroundPaint(Consumer<Graphics> val) {
        if (val == null) {
            throw new NullPointerException("val is null!");
        }
        this.foregroundPaint = val;
    }

    public void setIconImage(Image icon) {
        if (icon == null) {
            throw new NullPointerException("icon is null!");
        }

        if (isInit()) {
            var glfwImage = ImageToolkit.getImage(icon);
            GLFWImage.Buffer buffer = GLFWImage.malloc(1).put(0, glfwImage);
            GLFW.glfwSetWindowIcon(window, buffer);
            buffer.free();
            glfwImage.free();
        } else {
            parameters.put("Icon", true, icon);
        }
    }

    /**
     * 设置通知
     *
     * <p>
     *     设置在窗口初始化完成时的通知，
     *     <br>
     *     具体流程参考{@link #initComplete},
     *     <br>
     *     在Frame的{@link #initComplete}流程中将调用此设置的值，
     *     <br>
     *     仅在初始化时有效，若初始化时错过时机可能会导致不被调用，不建议使用，
     *     <br>
     *     若需要更稳定的可使用{@link WindowConfig}的{@link WindowConfig#setNotice}设置更稳妥的通知。
     * </p>
     *
     * @param notice 回调
     * @since 0.0.1
     * @see WindowConfig#setNotice
     * @see #initComplete()
     * @throws NullPointerException 设置的通知不能未null
     */
    @ApiSign.NotRecommended(since = "0.0.4")
    public void setNotice(Consumer<Window> notice) {
        if (notice == null) {
            throw new NullPointerException("notice is null!");
        }
        parameters.put("Notice", true, notice);
    }

    // ========================= GET =========================

    /**
     * 获取窗口的标题
     * <p>
     *     {@link #check()}若返回{@code false}方法将返回{@code ""}，<br>
     *     此方法应当在{@link #invoke(Runnable)}中调用。
     * </p>
     * @return 窗口标题
     * @since 0.0.1
     */
    public String getTitle() {
        if (!check()) return "";
        return GLFW.glfwGetWindowTitle(window);
    }

    @MagicConstant(intValues = {
            WindowConstants.DISPOSE_ON_CLOSE,
            WindowConstants.DO_NOTHING_ON_CLOSE,
            WindowConstants.HIDE_ON_CLOSE,
            WindowConstants.EXIT_ON_CLOSE
    })
    public int getDefaultCloseOperation() {
        return defaultCloseOperation;
    }

    /**
     * 在未设置图标的情况下会返回null
     * @return frame的icon
     * @since 0.0.3
     */
    public Image getImageIcon() {
        return parameters.get("Icon",(Image) null);
    }

    // ========================= EEE =========================


    @Override
    protected void defaultInputSet() {
        super.defaultInputSet();
        addWindowLifeListener(_ -> {
            switch (defaultCloseOperation) {
                case WindowConstants.DISPOSE_ON_CLOSE -> dispose();
                case WindowConstants.HIDE_ON_CLOSE -> setVisible(false);
                case WindowConstants.DO_NOTHING_ON_CLOSE -> {}
                case WindowConstants.EXIT_ON_CLOSE -> {
                    dispose();
                    if (Sys.isInitialize()) {
                        Sys.terminate();
                    }
                    System.exit(0);
                }
            }
        });
    }

    /**
     * 检查
     * <p>
     *     检查{@link #isInit()}与{@link #window}，<br>
     *     用于检查窗口是否初始化完成。
     * </p>
     * @return 返回结果
     * @since 0.0.1
     */
    public boolean check() {
        return isInit() || window != MemoryUtil.NULL;
    }

    /**
     * 创建一个新的 Frame
     * <p>
     *     创建 Frame，在初始化完成后调用 {@code notice} 回调，
     *     <br>
     *     回调时机为 {@link #initComplete()} 流程执行完毕后。
     * </p>
     *
     * @param config 窗口配置，可为 {@code null}
     * @param notice 初始化完成后的回调，可为 {@code null}
     * @return 新的 Frame 实例
     * @since 0.0.5
     */
    @Contract("_, _ -> new")
    public static @NotNull Frame frame(WindowConfig config, Consumer<Frame> notice) {
        return new Frame(config) {
            @Override
            protected void initComplete() {
                super.initComplete();
                if (notice != null) {
                    notice.accept(this);
                }
            }
        };
    }

    /**
     * 创建一个新的 Frame
     * <p>
     *     {@link #frame(WindowConfig, Consumer)} 的无配置版本。
     * </p>
     *
     * @param notice 初始化完成后的回调，可为 {@code null}
     * @return 新的 Frame 实例
     * @since 0.0.5
     * @see #frame(WindowConfig, Consumer)
     */
    @Contract("_ -> new")
    public static @NotNull Frame frame(Consumer<Frame> notice) {
        return frame(null, notice);
    }

    /**
     * 创建一个 800×600 的 Frame 并显示
     * <p>
     *     内部 API。
     *     <br>
     *     创建 Frame 后设置大小为 800×600，
     *     <br>
     *     位置传 {@code null}，并设为可见。
     * </p>
     *
     * @param config 窗口配置，可为 {@code null}
     * @param notice 初始化完成后的回调，可为 {@code null}
     * @return 新的 Frame 实例
     * @since 0.0.5
     * @see #frame(WindowConfig, Consumer)
     */
    @ApiSign.InternalApi(since = "0.0.5")
    public static @NotNull Frame _frame_8x6size_null_visible(WindowConfig config, Consumer<Frame> notice) {
        var frame = frame(config, notice);
        frame.setSize((int) 800, (int) 600);
        frame.setLocation(null);
        frame.setVisible(true);
        return frame;
    }

    /**
     * 创建一个 800×600 的 Frame 并显示
     * <p>
     *     {@link #_frame_8x6size_null_visible(WindowConfig, Consumer)} 的无配置版本。
     * </p>
     *
     * @param notice 初始化完成后的回调，可为 {@code null}
     * @return 新的 Frame 实例
     * @since 0.0.5
     * @see #_frame_8x6size_null_visible(WindowConfig, Consumer)
     */
    @ApiSign.InternalApi(since = "0.0.5")
    public static @NotNull Frame _frame_8x6size_null_visible(Consumer<Frame> notice) {
        return _frame_8x6size_null_visible(null, notice);
    }

    /**
     * 创建一个 800×600 的 Frame 并显示
     * <p>
     *     {@link #_frame_8x6size_null_visible(WindowConfig, Consumer)} 的简写。
     * </p>
     *
     * @param config 窗口配置，可为 {@code null}
     * @param notice 初始化完成后的回调，可为 {@code null}
     * @return 新的 Frame 实例
     * @since 0.0.5
     * @see #_frame_8x6size_null_visible(WindowConfig, Consumer)
     */
    @ApiSign.InternalApi(since = "0.0.5")
    public static @NotNull Frame _f86snv(WindowConfig config, Consumer<Frame> notice) {
        return _frame_8x6size_null_visible(config, notice);
    }

    /**
     * 创建一个 800×600 的 Frame 并显示
     * <p>
     *     {@link #_f86snv(WindowConfig, Consumer)} 的无配置版本。
     * </p>
     *
     * @param notice 初始化完成后的回调，可为 {@code null}
     * @return 新的 Frame 实例
     * @since 0.0.5
     * @see #_f86snv(WindowConfig, Consumer)
     */
    @SuppressWarnings("UnusedReturnValue")
    @ApiSign.InternalApi(since = "0.0.5")
    public static @NotNull Frame _f86snv(Consumer<Frame> notice) {
        return _f86snv(null, notice);
    }

}
