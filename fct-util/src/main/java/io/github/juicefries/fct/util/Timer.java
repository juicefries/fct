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

package io.github.juicefries.fct.util;

/**
 * 抽象定时器基类，提供时间获取与帧率计算的基本契约。
 * <p>
 * 子类需实现具体的时间源（纳秒、毫秒等），并维护帧率与每帧耗时。
 */
public abstract class Timer {

    /**
     * 获取从定时器启动或重置后经过的原始时间单位值。
     *
     * @return 原始时间单位计数值（具体单位由子类 {@link #getResolution()} 定义）
     */
    public abstract long getTime();

    /**
     * 获取经过的秒数（浮点数）。
     * <p>
     * 默认实现通过 {@link #getTime()} / {@link #getResolution()} 计算。
     *
     * @return 秒数，可能为小数
     */public float getTimeInSeconds() {
               return getTime() / (float) getResolution();
           }


    /**
     * 返回定时器的时间分辨率（即 {@link #getTime()} 每个单位对应的真实时间，以纳秒为单位）。
     *
     * @return 分辨率，单位纳秒。例如纳秒定时器返回 1_000_000_000L
     */
    public abstract long getResolution();

    /**
     * 获取当前的实时帧率（FPS）。
     *
     * @return 最近一次 {@link #update()} 计算的帧率，若尚未更新则返回 0
     */
    public abstract float getFrameRate();

    /**
     * 获取每帧耗时（秒）。
     *
     * @return 最近一帧的耗时秒数，若尚未更新则返回 0
     */
    public abstract float getTimePerFrame();

    /**
     * 更新帧率与每帧耗时数据。
     * <p>
     * 应在每一帧开始时调用。
     */
    public abstract void update();

    /**
     * 重置定时器零点，清除历史时间数据。
     * <p>
     * 重置后 {@link #getTime()} 应重新从 0 开始计时。
     */
    public abstract void reset();
}
