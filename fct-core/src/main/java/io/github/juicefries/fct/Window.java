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
// Data 2026/08/08 04:30
//

package io.github.juicefries.fct;

import io.github.juicefries.fct.callback.CallbackManager;
import io.github.juicefries.fct.event.CursorEnterListener;
import io.github.juicefries.fct.event.DisposeType;
import io.github.juicefries.fct.event.EventTrigger;
import io.github.juicefries.fct.event.SysEvent;
import io.github.juicefries.fct.event.SystemListener;
import io.github.juicefries.fct.event.WindowContentScaleListener;
import io.github.juicefries.fct.event.WindowFocusListener;
import io.github.juicefries.fct.event.WindowFrameBufferSizeListener;
import io.github.juicefries.fct.event.WindowLifeListener;
import io.github.juicefries.fct.event.WindowMaximizedListener;
import io.github.juicefries.fct.event.WindowMinimizedListener;
import io.github.juicefries.fct.event.WindowMoveListener;
import io.github.juicefries.fct.event.WindowRefreshListener;
import io.github.juicefries.fct.event.WindowRestoredListener;
import io.github.juicefries.fct.event.WindowSizeListener;
import io.github.juicefries.fct.glfw.GLFWUtil;
import io.github.juicefries.fct.glfw.GLUtil;
import io.github.juicefries.fct.glfw.Hint;
import io.github.juicefries.fct.glfw._GLFW_API;
import io.github.juicefries.fct.glfw._GL_API;
import io.github.juicefries.fct.layout.PageLayout;
import io.github.juicefries.fct.logging.LoggerFactory;
import io.github.juicefries.fct.sign.ApiSign;
import io.github.juicefries.fct.sign.Initializable;
import io.github.juicefries.fct.util.Array;
import io.github.juicefries.fct.util.Lock;
import io.github.juicefries.fct.util.Math;
import io.github.juicefries.fct.util.Parameters;
import io.github.juicefries.fct.util.ThreadManager;
import io.github.juicefries.fct.util.TraverseList;
import io.github.juicefries.fct.util.Util;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryUtil;

/**
 * <h2>窗口 </h2>
 *
 * <p>
 *     FCT 的顶层窗口，封装{@link GLFW}窗口，
 *     <br>
 *     负责窗口的创建、显示、渲染与销毁，
 *     <br>
 *     以及事件分发与窗口线程的管理。
 * </p>
 *
 * @since 0.0.1
 * @author juicefries
 * @see EventContainer
 * @see Graphics
 * @see ThreadManager
 * @see CallbackManager
 */
public class Window extends EventContainer implements
        EventComponent,
        WindowEventComponent,
        KeyEventComponent,
        MouseEventComponent,
        Initializable
{

    final static Logger logger = LoggerFactory.getLogger(Window.class);
    final Lock invokeLock = Lock.create();

    final AtomicBoolean initialize = new AtomicBoolean(false);
    ThreadManager threadMgr;
    CallbackManager callbackMgr;
    EventTrigger eventTrigger;
    Graphics graphics;

    Parameters<String,Object> parameters = new Parameters<>();

    SystemListener listener = SystemListener.createProactiveListener(
            "FCTWindowSystemListener",
            Window.this,
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

    volatile long window = MemoryUtil.NULL;

    {
        parameters.put("Point",new Vector2i(GLFW.GLFW_ANY_POSITION));
        parameters.put("Point",new Vector2i(GLFW.GLFW_ANY_POSITION));
        parameters.put("Size",new Vector2i(800,600));
        parameters.put("Title","");
        parameters.put("Config",new WindowConfig());
        parameters.put("EventTrigger",(EventTrigger) null);
        parameters.put("Visible",false);
        parameters.put("ReuseGraphics",false);
        layout = new PageLayout();
        visible = false;
        background = Color.NEAR_BLACK.copy();
        parent = null;
    }

    @SuppressWarnings("unused")
    protected Window(Lock lock) {

    }

    public Window(WindowConfig configuration) {
        this((Lock) null);
        var config = Objects.requireNonNullElseGet(configuration, WindowConfig::new);
        parameters.put("Config",true,config);
        if (config.isInitialize()) {
            initialize();
        }
    }

    public Window() {
        this((WindowConfig) null);
    }

    // ==================== LOGIC =========================

    @Override
    @ApiSign.Dangerous(since = "0.0.4")
    public void initialize() {
        if (isInit()) {
            throw new IllegalStateException("The window has been initialized!");
        }
        Sys.checkInit(true);
        threadMgr = new ThreadManager();
        threadMgr.initialize();
        threadMgr.invoke(() -> {
            if (!_create_window()) {
                logger.error("Failed to create window.");
                threadMgr.cleanup();
                return;
            }
            _window_sets();
        });
    }

    /**
     * 创建窗口
     * <p>
     *     从参数表中读取{@code Point}、{@code Size}、{@code Title}与{@code Config}，
     *     <br>
     *     添加默认提示，再调用{@link #additionalPrompt(List)}收集子类追加的提示，
     *     <br>
     *     最终通过{@link _GLFW_API}创建窗口。
     * </p>
     * @return 创建成功返回{@code true}，失败返回{@code false}
     * @since 0.0.5
     * @see #additionalPrompt(List)
     * @see _GLFW_API
     */
    @ApiStatus.Internal
    boolean _create_window() {
        try {
            // 获取参数
            var point  = parameters.get("Point" , new Vector2i(GLFW.GLFW_ANY_POSITION));
            var size   = parameters.get("Size"  , new Vector2i(500));
            var title  = parameters.get("Title" , "");
            var config = parameters.get("Config", new WindowConfig());
            var hints  = config.getHints();

            // 添加默认的提示
            hints.add(Hint.create(GLFW.GLFW_POSITION_X,point.x));
            hints.add(Hint.create(GLFW.GLFW_POSITION_Y,point.y));
            hints.add(Hint.visible(false));

            // 构造hint list
            var hs = new TraverseList<>(Hint.class);

            // 调用追加方法
            try {
                additionalPrompt(hs);
            } catch (Exception exception) {
                logger.error(
                        "An error occurred while appending the hint, but it does not affect the build.",
                        exception
                );
            }

            // 追加
            hints.addAll(hs);

            // 正式创建窗口
            window = _GLFW_API._create_window(size.x,size.y,title,hints.getArray());
            // 完成
            return true;
        } catch (Exception e) {
            logger.error("An error occurred while creating the window.",e);
            return false;
        }
    }

    /**
     * 追加窗口提示
     * <p>
     *     子类可重写此方法，向{@code hints}中追加额外的提示，
     *     <br>
     *     例如{@link Hint#decorated(boolean)}等。
     * </p>
     * @param hints 提示列表
     * @since 0.0.5
     */
    protected void additionalPrompt(List<Hint> hints) {

    }

    void _window_sets() {
        threadMgr.setThreadName("FCT-WindowThread[%d]".formatted(window));
        // 绑定
        _GL_API._bind(window);
        // 初始化输入
        _init_input_sets();
        // 注册系统监听器
        Sys.register(getListener());
        // 设置垂直同步
        _GL_API._swap_interval(true);

        GLGraphics2D.setGlClearColor(getBackground());
        threadMgr.screen(callbackMgr);
        initialize.set(true);
        initComplete();

        var _visible__ = parameters.get("Visible",false);
        setVisible(_visible__);
    }

    void _init_input_sets() {
        callbackMgr = new CallbackManager();
        callbackMgr.bind(window);
        callbackMgr.initialize();

        try {
            var tb = Array.createB(1);

            var trigger = parameters.get("EventTrigger",(EventTrigger) null,tb);
            eti : if (tb[0]) {
                if (trigger == null) break eti;
                eventTrigger = trigger;
            }

            if (eventTrigger == null) {
                eventTrigger = Util.newInstance(Toolkit.getTrigger());
            }

            eventTrigger.bind(this);
            eventTrigger.initialize();
        } catch (Exception e) {
            logger.error("An error occurred while initializing EventTrigger.",e);
            return;
        }

        defaultInputSet();
    }

    /**
     * 对于初始化完成但还未显示时做出的操作中，应当重写此方法。
     * @since 0.0.3
     */
    protected void initComplete() {
        var config = parameters.get("Config", new WindowConfig());
        var pb = Array.createB(1);
        var point = parameters.get("Point",new Vector2i(GLFW.GLFW_ANY_POSITION),pb);
        if (pb[0]) setLocation(point.x,point.y);
        var sb = Array.createB(1);
        var size = parameters.get("Size",new Vector2i(800,600),sb);
        if (sb[0]) setSize(size.x,size.y);

        var notice = config.getNotice();
        if (notice != null) {
            notice.accept(this);
        }
    }

    protected void defaultInputSet() {
        addWindowRefreshListener(_ -> validate());
        addWindowFocusListener(e -> {
            if (!e.isFocused()) return;
            validate();
        });
        addWindowMoveListener(e -> {
            x = e.getX();
            y = e.getY();
        });
        addWindowRestoredListener(_ -> validate());
        addWindowFBSListener(e -> {
            width = e.getWidth();
            height = e.getHeight();
        });
    }

    public Graphics getGraphics() {
        if (!isWorkerThread() || !isInit()) return null;

        var reuse = isReuseGraphics();

        if (reuse) {
            if (graphics == null) {
                graphics = createGraphics();
            }
            return graphics;
        } else {
            if (graphics != null) {
                graphics.dispose();
                graphics = null;
            }
        }

        return createGraphics();
    }

    Graphics createGraphics() {
        var size = GLFWUtil.getFrameBufferSize(window);

        GLUtil.viewport(size);

        var g2d = GLGraphics2D.build();

        g2d.setAttribute(Attribute.create());
        g2d.setMatrix4f(Math.create(size));

        g2d.update();
        return g2d;
    }

    // ==================== OTM =========================

    @ApiStatus.Internal
    void repaintImpI(int x, int y, int width, int height) {
        Graphics2D g = (Graphics2D) getGraphics();
        if (g == null) return;

        g.clip(x, y, width, height);

        Color color = g.getColor();
        g.setColor(getBackground());
        g.clear();
        g.setColor(color);

        g.unclip();
        paint(g);

        var reuse = isReuseGraphics();
        if (reuse) {
            if (g != graphics) {
                g.dispose();
            }
        } else {
            g.dispose();
        }


        GLFW.glfwSwapBuffers(window);
    }

    public void repaintImp(int x, int y, int width, int height) {
        if (!isInit()) return;
        if (!isVisible()) return;

        if (isWorkerThread()) {
            repaintImpI(x, y, width, height);
        } else {
            invoke(() -> repaintImpI(x, y, width, height));
        }
    }

    public void repaint(int x,int y,int width,int height) {
        repaintImp(x, y, width, height);
    }

    public void repaint() {
        repaintImp(0,0, (int) getWidth(), (int) getHeight());
    }

    @Override
    public void paint(Graphics g) {
        if (g == null || !isWorkerThread()) return;
        super.paint(g);
    }

    @Override
    public void paintForeground(Graphics g) {
        super.paintForeground(g);
    }

    @Override
    public void paintBackground(Graphics g) {
        super.paintBackground(g);
    }

    void disposeImp() {
        synchronized (invokeLock) {
            if (!isInit()) return;
            setVisible(false);
            if (graphics != null) {
                graphics.dispose();
                graphics = null;
            }
            parameters.clear();
            threadMgr.cleanup();
            threadMgr = null;
            eventTrigger.dispose();
            eventTrigger = null;
            callbackMgr.dispose();
            callbackMgr = null;
            initialize.set(false);
            GLFW.glfwDestroyWindow(window);
        }
    }

    void dispose(DisposeType type) {
        if (type == null) {
            throw new NullPointerException("type is null!");
        }
        logger.trace("dispose()[type:{}]",type);
        if (!isWorkerThread()) {
            invoke(() -> {
                disposeImp();
                if (type != DisposeType.CANCEL_INVOKE && type != DisposeType.TERMINATE_INVOKE) {
                    Sys.cancel(getListener());
                }
            });
            return;
        }
        disposeImp();
        if (type != DisposeType.CANCEL_INVOKE && type != DisposeType.TERMINATE_INVOKE) {
            Sys.cancel(getListener());
        }
    }

    /**
     * 销毁窗口
     * @since 0.0.1
     * @see #dispose(DisposeType)
     */
    @Override
    public void dispose() {
        dispose(DisposeType.MANUAL_INVOKE);
    }

    /**
     * 将任务包裹从而传递到渲染线程，<br>
     * 对于部分操作建议调用此方法。<br>
     * 自{@code 0.0.5}后对于绝大部分操作不必在意此方法。
     * @param invoke 任务
     * @since 0.0.1
     * @throws NullPointerException 任务不能为null
     */
    public void invoke(Runnable invoke) {
        if (invoke == null) {
            throw new NullPointerException("invoke is null!");
        }
        threadMgr.invoke(invoke);
    }


    @Override
    public void validate() {
        eventTrigger.update();
        layout();
        repaint();
    }

    // ========================= ADD =========================

    @Override
    public void addWindowLifeListener(WindowLifeListener l) {
        listenerList.add(WindowLifeListener.class,l);
    }

    @Override
    public void addWindowRefreshListener(WindowRefreshListener l) {
        listenerList.add(WindowRefreshListener.class,l);
    }

    @Override
    public void addWindowFocusListener(WindowFocusListener l) {
        listenerList.add(WindowFocusListener.class,l);
    }

    @Override
    public void addWindowMoveListener(WindowMoveListener l) {
        listenerList.add(WindowMoveListener.class,l);
    }

    @Override
    public void addWindowMaximizedListener(WindowMaximizedListener l) {
        listenerList.add(WindowMaximizedListener.class,l);
    }

    @Override
    public void addWindowMinimizedListener(WindowMinimizedListener l) {
        listenerList.add(WindowMinimizedListener.class,l);
    }

    @Override
    public void addWindowRestoredListener(WindowRestoredListener l) {
        listenerList.add(WindowRestoredListener.class,l);
    }

    @Override
    public void addWindowSizeListener(WindowSizeListener l) {
        listenerList.add(WindowSizeListener.class,l);
    }

    @Override
    public void addWindowFrameBufferSizeListener(WindowFrameBufferSizeListener l) {
        listenerList.add(WindowFrameBufferSizeListener.class,l);
    }

    @Override
    public void addWindowFBSListener(WindowFrameBufferSizeListener l) {
        addWindowFrameBufferSizeListener(l);
    }

    @Override
    public void addWindowContentScaleListener(WindowContentScaleListener l) {
        listenerList.add(WindowContentScaleListener.class,l);
    }

    @Override
    public void addWindowCSListener(WindowContentScaleListener l) {
        addWindowContentScaleListener(l);
    }

    @Override
    public void addCursorEnterListener(CursorEnterListener l) {
        listenerList.add(CursorEnterListener.class,l);
    }

    // ========================= REMOVE =========================

    @Override
    public void removeWindowLifeListener(WindowLifeListener l) {
        listenerList.remove(WindowLifeListener.class,l);
    }

    @Override
    public void removeWindowRefreshListener(WindowRefreshListener l) {
        listenerList.remove(WindowRefreshListener.class,l);
    }

    @Override
    public void removeWindowFocusListener(WindowFocusListener l) {
        listenerList.remove(WindowFocusListener.class,l);
    }

    @Override
    public void removeWindowMoveListener(WindowMoveListener l) {
        listenerList.remove(WindowMoveListener.class,l);
    }

    @Override
    public void removeWindowRestoredListener(WindowRestoredListener l) {
        listenerList.remove(WindowRestoredListener.class,l);
    }

    @Override
    public void removeWindowMaximizedListener(WindowMaximizedListener l) {
        listenerList.remove(WindowMaximizedListener.class,l);
    }

    @Override
    public void removeWindowMinimizedListener(WindowMinimizedListener l) {
        listenerList.remove(WindowMinimizedListener.class,l);
    }

    @Override
    public void removeWindowSizeListener(WindowSizeListener l) {
        listenerList.remove(WindowSizeListener.class,l);
    }

    @Override
    public void removeWindowFrameBufferSizeListener(WindowFrameBufferSizeListener l) {
        listenerList.remove(WindowFrameBufferSizeListener.class,l);
    }

    @Override
    public void removeWindowContentScaleListener(WindowContentScaleListener l) {
        listenerList.remove(WindowContentScaleListener.class,l);
    }

    @Override
    public void removeCursorEnterListener(CursorEnterListener l) {
        listenerList.remove(CursorEnterListener.class,l);
    }

    // ==================== SET =========================

    /**
     * 设置窗口的尺寸，
     *
     * @param width 窗口的宽度
     * @param height 窗口的高度
     */
    public void setSize(int width,int height) {
        if (width < 0) {
            throw new IllegalArgumentException("width is less than or equal to 0!");
        }
        if (height < 0) {
            throw new IllegalArgumentException("height is less than or equal to 0!");
        }
        this.width = width;
        this.height = height;
        if (initialize.get() || window != MemoryUtil.NULL) {
            GLFW.glfwSetWindowSize(window, width, height);
        } else {
            parameters.put("Size",true,new Vector2i(width,height));
        }
        layout();
    }

    /**
     * 设置窗口的占据屏幕的比例
     * <p>
     *     该方法在{@link Window}被重写为了占窗口比例的设置方法。<br>
     *     设置窗口的像素大小应当转到{@link #setSize(int, int)}方法。
     * </p>
     * @param width 占宽度比例
     * @param height 占高度比例
     * @since 0.0.1
     * @apiNote 该方法应当被invoke方法包裹
     */
    @Override
    @ApiStatus.Experimental
    public void setSize(float width, float height) {
        Sys.checkInit(true);
        long monitor = Util.match(isInit(),
                GLFW.glfwGetWindowMonitor(window),
                GLFW.glfwGetPrimaryMonitor()
        );
        GLFWVidMode vidMode = GLFWUtil.getVideoMode(monitor);
        if (vidMode == null) {
            throw new NullPointerException("vidMode is null!");
        }
        int w = (int)(vidMode.width() * width);
        int h = (int)(vidMode.height() * height);
        setSize(w,h);
    }

    /**
     * 设置大小
     * <p>
     *     设置窗口占屏幕比例，
     *     <br>
     *     若{@code align}为{@code true}则将窗口高度的比例对齐为宽度的比例，
     *     <br>
     *     该方法应当在{@link #invoke(Runnable)}中调用
     *     <br>
     *     不推荐。
     * </p>
     * @param ratio 比例
     * @param align 对齐
     * @since 0.0.1
     */
    @ApiStatus.Experimental
    public void setSize(float ratio,boolean align) {
        Sys.checkInit(true);
        long monitor = Util.match(isInit(),
                GLFW.glfwGetWindowMonitor(window),
                GLFW.glfwGetPrimaryMonitor()
        );
        GLFWVidMode vidMode = GLFWUtil.getVideoMode(monitor);
        if (vidMode == null) {
            throw new NullPointerException("vidMode is null!");
        }
        int w = (int)(vidMode.width() * ratio);
        int h = Util.match(align,w,(int)(vidMode.height() * ratio));
        setSize(w,h);
    }

    /**
     * 设置大小
     * <p>
     *     设置窗口占屏幕比例，
     *     <br>
     *     参考{@link #setSize(float, boolean)}的{@code align}参数，
     *     <br>
     *     该方法为重载方法，{@code align}为{@code false}。<br>
     *     该方法应当在{@link #invoke(Runnable)}中调用
     * </p>
     * @param ratio 比例
     * @since 0.0.1
     */
    public void setSize(float ratio) {
        setSize(ratio,false);
    }

    @Override
    public void setSize(Size size) {
        if (size == null) {
            throw new NullPointerException("size is null!");
        }
        if (width < 0) {
            throw new IllegalArgumentException("width is less than or equal to 0!");
        }
        if (height < 0) {
            throw new IllegalArgumentException("height is less than or equal to 0!");
        }
        setSize((int) size.width,(int) size.height);
    }

    @Override
    public void setBounds(float x, float y, float width, float height) {
        setSize((int) width,(int) height);
        setLocation((int) x,(int) y);
    }

    /**
     * 设置窗口垂直同步。
     * @param sync 同步
     * @since 0.0.1
     */
    public void setVSync(boolean sync) {
        if (!isInit()) return;

        if (isWorkerThread()) {
            _GL_API._swap_interval(sync);
        } else {
            invoke(() -> _GL_API._swap_interval(sync));
        }
    }

    /**
     * 设置窗口的位置
     *
     * <p>
     *     方法建议在{@link #invoke(Runnable)}中调用。
     * </p>
     *
     * @param x 位置X
     * @param y 位置Y
     * @since 0.0.1
     */
    public void setLocation(int x, int y) {
        if (isInit() || window != MemoryUtil.NULL) {
            Sys.checkInit(true);
            GLFW.glfwSetWindowPos(window, x, y);
        } else {
            parameters.put("Point",true,new Vector2i(x, y));
        }
        super.setLocation(x, y); // 这里更新字段
    }

    /**
     * 设置窗口的位置
     *
     * <p>
     *     方法建议在{@link #invoke(Runnable)}中调用。
     * </p>
     *
     * @param x 位置X
     * @param y 位置Y
     * @since 0.0.1
     */
    @Override
    public void setLocation(float x, float y) {
        setLocation((int) x, (int) y);
    }

    /**
     * 设置窗口的位置
     *
     * <p>
     *     若参数为{@code null}则按照当前能获取到的屏幕宽高进行居中。
     *     <br>
     *     该方法应当在设置尺寸之后调用。
     *     <br>
     *     目前在初始化前设置可能会因为初始化过快导致窗口闪现到指定位置。
     *     <br>
     *     目前还并不稳定。
     *     <br>
     *     方法建议在{@link #invoke(Runnable)}中调用。
     * </p>
     *
     * @param location 位置
     * @since 0.0.1
     */
    @Override
    @ApiStatus.Experimental
    public void setLocation(Location location) {
        int x;
        int y;
        if (location == null) {
            Sys.checkInit(true);
            long monitor;

            if (isInit() || window != MemoryUtil.NULL) {
                monitor = GLFW.glfwGetWindowMonitor(window);
            } else {
                monitor = GLFW.glfwGetPrimaryMonitor();
            }

            GLFWVidMode vidMode = GLFWUtil.getVideoMode(monitor);
            if (vidMode == null) {
                throw new NullPointerException("vidMode is null!");
            }

            x = (int) ((vidMode.width() - width) / 2);
            y = (int) ((vidMode.height() - height) / 2);
        } else {
            x = (int) location.x;
            y = (int) location.y;
        }
        setLocation(x,y);
    }

    /**
     * 设置窗口可见性
     * <p>
     *     该方法应当在{@link #invoke(Runnable)}中调用。
     * </p>
     * @param visible 可见
     * @since 0.0.1
     */
    @Override
    public void setVisible(boolean visible) {
        parameters.put("Visible",true,visible);
        if (window == MemoryUtil.NULL) {
            this.visible = visible;
            return;
        }

        if (visible == isVisible()) {
            return;
        }

        var attrib = GLFWUtil.isVisible(window);
        if (visible) {
            if (!attrib) {
                GLFW.glfwShowWindow(window);
                validate();
            }
        } else {
            if (attrib) {
                GLFW.glfwHideWindow(window);
            }
        }
        this.visible = visible;
    }

    /**
     * 设置窗口可见
     * @since 0.0.1
     */
    public void show() {
        setVisible(true);
    }

    /**
     * 设置窗口不可见
     * @since 0.0.1
     */
    public void hide() {
        setVisible(false);
    }

    public void setEventTrigger(EventTrigger trigger) {
        if (trigger == null) {
            throw new NullPointerException("trigger is null!");
        }
        synchronized (invokeLock) {
            parameters.put("EventTrigger",true,trigger);
        }
    }

    @ApiStatus.Experimental
    public void setWindowAttrib(Hint hint) {
        if (hint == null) {
            throw new NullPointerException("hint is null!");
        }

        GLFW.glfwSetWindowAttrib(window,hint.hint(),hint.value());
    }

    @ApiStatus.Experimental
    public void setWindowAttrib(int attrib, int value) {
        GLFW.glfwSetWindowAttrib(window,attrib,value);
    }

    /**
     * 复用画笔
     *
     * <p>
     *     允许{@link Window}在渲染时复用{@link Graphics}，
     *     <br>
     *     启用后将对画笔进行缓存，但不推荐。
     *     <br>
     *     若状态未设置好及其容易污染画面。
     *     <br>
     *     默认关闭,不会立即生效，在渲染新的画面时生效。
     *     <br>
     *     即在执行{@link #getGraphics()}时检查。
     * </p>
     * @param reuse 状态
     * @since 0.0.5
     * @see Graphics
     * @see #getGraphics()
     */
    @ApiStatus.Experimental
    public void setReuseGraphics(boolean reuse) {
        parameters.put("ReuseGraphics",true,reuse);
    }

    // ==================== GET =========================

    SystemListener getListener() {
        return listener;
    }

    public boolean isReuseGraphics() {
        return parameters.get("ReuseGraphics",false);
    }

    @Override
    @Contract(pure = true)
    public final @Nullable Container getParent() {
        return null;
    }

    public boolean isWorkerThread() {
        return threadMgr.isWorkerThread();
    }

    @ApiSign.Dangerous(since = "0.0.4")
    public int getAttribI(int attrib) {
        return GLFWUtil.getWindowAttribI(window,attrib);
    }

    @ApiSign.Dangerous(since = "0.0.4")
    public boolean getAttrib(int attrib) {
        return GLFWUtil.getWindowAttrib(window,attrib);
    }

    /**
     * 获取窗口的可见性，<br>
     * 在初始化未完成前获取到的为缓存值，<br>
     * 该方法应当在{@link #invoke(Runnable)}中调用
     * @return 获取窗口可见性
     * @since 0.0.1
     */
    @Override
    public boolean isVisible() {
        if (window == MemoryUtil.NULL) return visible;
        boolean attrib = GLFWUtil.isVisible(window);
        if (visible != attrib) visible = attrib;
        return attrib;
    }

    /**
     * 获取窗口的尺寸，获取到的值应当转为int<br>
     * {@link #isInit()}为false时返回的为缓存的值，<br>
     * 该方法应当在{@link #invoke(Runnable)}中调用.
     * @return 获取窗口的尺寸
     * @since 0.0.1
     */
    @Override
    public Size getSize() {
        if (!isInit() || window == MemoryUtil.NULL) return super.getSize();

        var size = GLFWUtil.getFrameBufferSize(window);

        if (width != size.x) {
            width = size.x;
        }
        if (height != size.y) {
            height = size.y;
        }
        return new Size(size.x,size.y);
    }

    public Vector2i getContentSize() {
        if (!isInit() || window == MemoryUtil.NULL) return super.getSize().toVector2i();
        var size = GLFWUtil.getSize(window);

        if (width != size.x) {
            width = size.x;
        }
        if (height != size.y) {
            height = size.y;
        }
        return new Vector2i(size.x,size.y);
    }

    public Vector2i getFramebufferSize() {
        if (!isInit() || window == MemoryUtil.NULL) return super.getSize().toVector2i();
        var size = GLFWUtil.getFrameBufferSize(window);

        if (width != size.x) {
            width = size.x;
        }
        if (height != size.y) {
            height = size.y;
        }
        return size;
    }

    /**
     * 获取窗口内容区宽度，<br>
     * 该方法在使用时应当转换为int.
     * @return 获取窗口宽度
     * @since 0.0.1
     * @see #getSize()
     */
    @Override
    public float getWidth() {
        return getSize().width;
    }

    /**
     * 获取窗口内容区高度，<br>
     * 该方法在使用时应当转换为int.
     * @return 获取窗口高度
     * @since 0.0.1
     * @see #getSize()
     */
    @Override
    public float getHeight() {
        return getSize().height;
    }

    public int getContentWidth() {
        return getContentSize().x;
    }

    public int getContentHeight() {
        return getContentSize().y;
    }

    public int getFramebufferWidth() {
        return getFramebufferSize().x;
    }

    public int getFramebufferHeight() {
        return getFramebufferSize().y;
    }

    /**
     * 获取到的值应当转换为int,<br>
     * 此方法建议在{@link #invoke(Runnable)}调用。
     * @return 获取窗口的位置
     * @since 0.0.1
     */
    @Override
    public Location getLocation() {
        if (!isInit() || window == MemoryUtil.NULL) return super.getLocation();
        Location location = new Location();
        int[] xPos = Array.createI(1);
        int[] yPos = Array.createI(1);
        GLFW.glfwGetWindowPos(window,xPos,yPos);
        location.x = xPos[0];
        location.y = yPos[0];
        if (x != location.x) {
            x = location.x;
        }
        if (y != location.y) {
            y = location.y;
        }
        return location;
    }

    /**
     *
     * @return 窗口的绝对位置
     * @since 0.0.4
     * @deprecated 对于{@link #getLocation()}此方法不建议用
     */
    @Override
    @Deprecated(since = "0.0.4")
    @ApiSign.NotRecommended(since = "0.0.4")
    public Location getAbsoluteLocation() {
        return getLocation();
    }

    /**
     * 获取到的值应当转换为int,<br>
     * 此方法应当在{@link #invoke(Runnable)}调用。
     * @return 获取窗口的位置X
     * @since 0.0.1
     */
    @Override
    public float getX() {
        return getLocation().x;
    }

    /**
     * 获取到的值应当转换为int,<br>
     * 此方法应当在{@link #invoke(Runnable)}调用。
     * @return 获取窗口的位置Y
     * @since 0.0.1
     */
    @Override
    public float getY() {
        return getLocation().y;
    }

    /**
     *
     * @return 窗口的绝对位置
     * @since 0.0.4
     * @deprecated 对于{@link #getLocation()}此方法不建议用
     */
    @Override
    @Deprecated(since = "0.0.4")
    @ApiSign.NotRecommended(since = "0.0.4")
    public float getAbsoluteX() {
        return super.getAbsoluteX();
    }

    /**
     *
     * @return 窗口的绝对位置
     * @since 0.0.4
     * @deprecated 对于{@link #getLocation()}此方法不建议用
     */
    @Override
    @Deprecated(since = "0.0.4")
    @ApiSign.NotRecommended(since = "0.0.4")
    public float getAbsoluteY() {
        return super.getAbsoluteY();
    }

    @ApiStatus.Experimental
    @ApiSign.Dangerous(since = "0.0.3")
    public CallbackManager getCallbackManager() {
        return callbackMgr;
    }

    @ApiStatus.Experimental
    public ThreadManager getThreadManager() {
        return threadMgr;
    }

    public EventTrigger getEventTrigger() {
        return eventTrigger;
    }

    @Override
    public boolean isInitialize() {
        return initialize.get();
    }

    // ==================== EEE =========================

    // ==================== RRR =========================

}
