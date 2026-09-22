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

import java.util.Objects;

/**
 * 纳秒级定时器，基于 {@link System#nanoTime()} 实现。
 * <p>
 * 分辨率固定为 1_000_000_000（即每秒十亿个单位，对应纳秒）。
 * </p>
 *
 */
public class NanoTimer extends Timer {

    private static final long TIMER_RESOLUTION = 1000000000L;
    private static final float INVERSE_TIMER_RESOLUTION = 1f/1000000000L;

    long startTime;
    long previousTime;
    float tpf;
    float fps;

    /**
     * 构造新定时器，记录启动时的纳秒时间。
     */
    public NanoTimer() {
        startTime = System.nanoTime();
    }

    @Override
    public float getTimeInSeconds() {
        return getTime() * INVERSE_TIMER_RESOLUTION;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 返回从启动或重置后经过的纳秒数。
     *
     * @return 纳秒计数值
     */
    @Override
    public long getTime() {
        return System.nanoTime() - startTime;
    }

    /**
     * {@inheritDoc}
     *
     * @return 固定值 1_000_000_000L
     */
    @Override
    public long getResolution() {
        return TIMER_RESOLUTION;
    }

    @Override
    public float getFrameRate() {
        return fps;
    }

    @Override
    public float getTimePerFrame() {
        return tpf;
    }

    @Override
    public void update() {
        tpf = (getTime() - previousTime) * (1.0f / TIMER_RESOLUTION);
        fps = 1.0f / tpf;
        previousTime = getTime();
    }

    @Override
    public void reset() {
        startTime = System.nanoTime();
        previousTime = getTime();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof NanoTimer nanoTimer)) return false;
        return startTime == nanoTimer.startTime && previousTime == nanoTimer.previousTime && Float.compare(tpf, nanoTimer.tpf) == 0 && Float.compare(fps, nanoTimer.fps) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, previousTime, tpf, fps);
    }

    @Override
    public String toString() {
        return "NanoTimer[" +
                "startTime=" + startTime +
                ", previousTime=" + previousTime +
                ", tpf=" + tpf +
                ", fps=" + fps +
                ']';
    }
}

