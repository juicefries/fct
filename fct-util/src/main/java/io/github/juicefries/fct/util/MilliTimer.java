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

public class MilliTimer extends Timer {

    private static final long TIMER_RESOLUTION = 1000L;
    private static final float INVERSE_RESOLUTION = 1f / 1000f;

    private long startTime;
    private long previousTime;
    private float tpf;
    private float fps;

    public MilliTimer() {
        startTime = System.currentTimeMillis();
        previousTime = getTime();
    }

    @Override
    public long getTime() {
        return System.currentTimeMillis() - startTime;
    }

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
        long now = getTime();
        tpf = (now - previousTime) * INVERSE_RESOLUTION;
        if (tpf > 0) fps = 1f / tpf;
        previousTime = now;
    }

    @Override
    public void reset() {
        startTime = System.currentTimeMillis();
        previousTime = getTime();
    }
}