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

package io.github.juicefries.fct.input;

import io.github.juicefries.fct.util.Util;

public class WindowData implements InputData {

    public final static long WINDOW_CLOSE_EVENT            = 0X001FL;
    public final static long WINDOW_SIZE_EVENT             = 0X002FFL;
    public final static long WINDOW_POS_EVENT              = 0X003FFFL;
    public final static long WINDOW_FOCUS_EVENT            = 0X004FFFFL;
    public final static long WINDOW_REFRESH_EVENT          = 0X005FFFFFL;
    public final static long WINDOW_CONTENT_SCALE_EVENT    = 0X006FFFFFFL;
    public final static long WINDOW_MAXIMIZED_EVENT        = 0X007FFFFFFFL;
    public final static long WINDOW_MINIMIZED_EVENT        = 0X008FFFFFFFFL;
    public final static long WINDOW_NORMAL_EVENT           = 0X009FFFFFFFFFL;
    public final static long WINDOW_FRAMEBUFFER_SIZE_EVENT = 0X0010FFFFFFFFFFL;
    public final static long WINDOW_MAXIMIZED              = 1;
    public final static long WINDOW_NORMAL                 = 0;
    public final static long WINDOW_MINIMIZED              = -1;

    private final long state;
    private final long window;
    private final int xPos;
    private final int yPos;
    private final int width;
    private final int height;
    private final int framebufferWidth;
    private final int framebufferHeight;
    private final float xScale;
    private final float yScale;
    private final boolean focused;
    private final long normal;


    public WindowData(long value0, long value1) {
        window = value0;
        xPos = 0;
        yPos = 0;
        width = 0;
        height = 0;
        framebufferWidth = 0;
        framebufferHeight = 0;
        xScale = 0;
        yScale = 0;
        focused = false;
        normal = 0;
        state = value1;
    }

    public WindowData(long value0, long value1, long value2) {
        window = value0;
        xPos = 0;
        yPos = 0;
        width = 0;
        height = 0;
        framebufferWidth = 0;
        framebufferHeight = 0;
        xScale = 0;
        yScale = 0;
        focused = false;
        normal = value1;
        state = value2;
    }

    public WindowData(long value0, boolean value1, long value2) {
        window = value0;
        xPos = 0;
        yPos = 0;
        width = 0;
        height = 0;
        framebufferWidth = 0;
        framebufferHeight = 0;
        xScale = 0;
        yScale = 0;
        focused = value1;
        normal = 0;
        state = value2;
    }

    public WindowData(long value, int value1, int value2, long value3) {
        window = value;
        xPos = Util.cond(value1,0,value3,WINDOW_POS_EVENT);
        yPos = Util.cond(value2,0,value3,WINDOW_POS_EVENT);
        width = Util.cond(value1,0,value3,WINDOW_SIZE_EVENT);
        height = Util.cond(value2,0,value3,WINDOW_SIZE_EVENT);
        framebufferWidth = Util.cond(value1,0,value3,WINDOW_FRAMEBUFFER_SIZE_EVENT);
        framebufferHeight = Util.cond(value2,0,value3,WINDOW_FRAMEBUFFER_SIZE_EVENT);
        xScale = 0;
        yScale = 0;
        focused = false;
        normal = 0;
        state = value3;
    }

    public WindowData(long value0, float value1, float value2, long value3) {
        window = value0;
        xPos = 0;
        yPos = 0;
        width = 0;
        height = 0;
        framebufferWidth = 0;
        framebufferHeight = 0;
        xScale = value1;
        yScale = value2;
        focused = false;
        normal = 0;
        state = value3;
    }


    public long getWindow() {
        return window;
    }

    @Override
    public long getState() {
        return state;
    }

    public long getNormal() {
        return normal;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public int getFramebufferWidth() {
        return framebufferWidth;
    }

    public int getFramebufferHeight() {
        return framebufferHeight;
    }

    public float getxScale() {
        return xScale;
    }

    public float getyScale() {
        return yScale;
    }

    public boolean isFocused() {
        return focused;
    }


}
