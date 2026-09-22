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

public final class MouseData implements InputData {

    public final static long MOUSE_BUTTON_EVENT         = Util.turn("MouseEvent-Button");
    public final static long MOUSE_BUTTON_PRESS_EVENT   = Util.turn("MouseEvent-ButtonPress");
    public final static long MOUSE_BUTTON_RELEASE_EVENT = Util.turn("MouseEvent-ButtonRelease");
    public final static long MOUSE_SCROLL_EVENT         = Util.turn("MouseEvent-Scroll");
    public final static long MOUSE_CURSOR_POS_EVENT     = Util.turn("MouseEvent-CursorPos");
    public final static long MOUSE_CURSOR_ENTER_EVENT   = Util.turn("MouseEvent-CursorEnter");
    public final static long MOUSE_CURSOR_LEAVE_EVENT   = Util.turn("MouseEvent-CursorLeave");
    public final static long MOUSE_DROP_EVENT           = Util.turn("MouseEvent-Drop");
    public final static long MOUSE_CURSOR_POS_OR_SCROLL_EVENT = MOUSE_CURSOR_POS_EVENT | MOUSE_SCROLL_EVENT;

    private long window;
    private long state;

    private long names = NOT_MATCH_EVENT_FIELD_LONG_VALUE;

    private int button = NOT_MATCH_EVENT_FIELD_INT_VALUE;
    private int mods = NOT_MATCH_EVENT_FIELD_INT_VALUE;
    private int count = NOT_MATCH_EVENT_FIELD_INT_VALUE;

    private boolean action = false;

    double x = NOT_MATCH_EVENT_FIELD_DOUBLE_VALUE;
    double y = NOT_MATCH_EVENT_FIELD_DOUBLE_VALUE;

    MouseData(long value0, int value1, int value2, boolean value3, long value4) {
        window = value0;
        button = value1;
        mods = value2;
        action = value3;
        state = value4;
    }

    MouseData(long value0, double value1, double value2, long value3) {
        window = value0;
        x = value1;
        y = value2;
        state = value3;
    }

    MouseData(long value0, long value1) {
        window = value0;
        state = value1;
    }

    MouseData(long value0, int value1, long value2, long value3) {
        window = value0;
        count = value1;
        names = value2;
        state = value3;
    }

    public MouseData() {

    }

    public MouseData(double x,double y) {
        this.x = x;
        this.y = y;
        state = MOUSE_CURSOR_POS_OR_SCROLL_EVENT;
    }

    public MouseData(int count,long names) {
        this.count = count;
        this.names = names;

    }

    public long getWindow() {
        return window;
    }

    @Override
    public long getState() {
        return state;
    }

    public long getNames() {
        return names;
    }

    public int getMods() {
        return mods;
    }

    public int getButton() {
        return button;
    }

    public int getCount() {
        return count;
    }

    public boolean isAction() {
        return action;
    }

    public double getXPos() {
        if (getState() == MOUSE_CURSOR_POS_EVENT) {
            return x;
        }
        return NOT_MATCH_EVENT_FIELD_DOUBLE_VALUE;
    }

    public double getYPos() {
        if (getState() == MOUSE_CURSOR_POS_EVENT) {
            return y;
        }
        return NOT_MATCH_EVENT_FIELD_DOUBLE_VALUE;
    }

    public double getXOffset() {
        if (getState() == MOUSE_SCROLL_EVENT) {
            return x;
        }
        return NOT_MATCH_EVENT_FIELD_DOUBLE_VALUE;
    }

    public double getYOffset() {
        if (getState() == MOUSE_SCROLL_EVENT) {
            return y;
        }
        return NOT_MATCH_EVENT_FIELD_DOUBLE_VALUE;
    }

    MouseData copy() {
        return null;
    }

}
