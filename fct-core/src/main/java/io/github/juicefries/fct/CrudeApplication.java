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
// Data 2026/09/15 21:59
//

package io.github.juicefries.fct;

import io.github.juicefries.fct.logging.LoggerFactory;
import io.github.juicefries.fct.util.Lock;
import java.util.Objects;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryUtil;

/**
 * <h2>简陋的应用</h2>
 *
 * <p>
 *     不怎么建议使用，只是一个简单的抽象类，没加多少功能。
 *     <br>
 * <blockquote><pre>
 *     CrudeApplication app = new CrudeApplication() {
 *         {@code @Override}
 *         public void simpleInit() {
 *             // 逻辑
 *         }
 *     }
 *     app.start();
 * </pre></blockquote>
 * </p>
 * @since 0.0.4
 * @see Frame
 * @author juicefries
 */
public abstract class CrudeApplication extends Frame implements Application {

    private final static Logger log = LoggerFactory.getLogger(CrudeApplication.class);

    protected CrudeApplication() {
        this((Lock) null);
        WindowConfig config = new WindowConfig();
        config.setInitialize(false);
        parameters.put("Config",true,config);
    }

    CrudeApplication(Lock lock) {
        super(lock);
    }

    // ========================= OTM =========================

    @Override
    public void start() {
        if (isInit() || window != MemoryUtil.NULL) return;
        log.trace("start()");
        try {
            initialize();
        } catch (Exception e) {
            log.error("An error occurred in start() while calling initialize().",e);
        }
    }

    @Override
    public void stop() {
        log.trace("stop()");
        dispose();
    }

    protected void close() {

    }

    /**
     * 参数设置
     * @param config 用于调整的参数
     * @since 0.0.4
     */
    protected void parameterSet(WindowConfig config) {

    }

    /**
     * 完成设置，此时窗口已创建完成，但还在初始化。
     * <br>
     * 不建议使用。
     * @since 0.0.4
     */
    protected void completedSet() {

    }

    /**
     * 简单初始化
     * <p>
     *     可以参考{@link WindowConfig#setNotice}的作用，但在之后。
     * </p>
     * @since 0.0.4
     */
    public abstract void simpleInit();

    // ========================= SET =========================

    /**
     * 在初始化后设置配置
     * @param config 配置
     */
    public void setConfig(WindowConfig config) {
        WindowConfig val = Objects.requireNonNullElseGet(config, WindowConfig::build);
        parameters.put("Config",true,val);
    }

    // ========================= GET =========================

    // ========================= EEE =========================

    @Override
    protected void initComplete() {
        super.initComplete();
        simpleInit();
    }

    @Override
    void _window_sets() {
        super._window_sets();
        completedSet();
    }

    @Override
    public void dispose() {
        log.trace("dispose()");
        close();
        super.dispose();
    }
}
