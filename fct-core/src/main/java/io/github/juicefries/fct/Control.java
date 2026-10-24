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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public abstract class Control extends Component {
//        implements EventComponent
//        , MouseEventComponent
//        ,KeyEventComponent
//{

    Color foreground;
    Font font;

    protected Control() {

    }

    // ========================= OTM =========================

//    @Override
//    public void gainFocus() {
//        var context = getComponentContext();
//        if (context != null) {
//            context.gainFocus(this);
//        }
//        validate();
//    }

//    @Override
//    public void loseFocus() {
//        var context = getComponentContext();
//        if (context != null) {
//            context.loseFocus(this);
//        }
//        validate();
//    }

    @Override
    protected void update() {

    }


    // ========================= SET =========================

    public void setForeground(Color foreground) {
        this.foreground = foreground;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    // ========================= GET =========================

    public Color getForeground() {
        return foreground;
    }

    public Font getFont() {
        return font;
    }

//    @Override
//    public boolean isFocus() {
//        var context = getComponentContext();
//        if (context != null) {
//            return context.isFocused(this);
//        }
//        return false;
//    }

    // ========================= ADD =========================

//    @Override
//    public void addKeyCharInputListener(KeyCharInputListener l) {
//        listenerList.add(KeyCharInputListener.class,l);
//    }
//
//    @Override
//    public void addKeyInputListener(KeyInputListener l) {
//        listenerList.add(KeyInputListener.class,l);
//    }
//
//    @Override
//    public void addKeyboardFocusListener(KeyboardFocusListener l) {
//        listenerList.add(KeyboardFocusListener.class,l);
//    }

    // ========================= REMOVE =========================

//    @Override
//    public void removeKeyInputListener(KeyInputListener l) {
//        listenerList.remove(KeyInputListener.class,l);
//    }
//
//    @Override
//    public void removeKeyCharInputListener(KeyCharInputListener l) {
//        listenerList.remove(KeyCharInputListener.class,l);
//    }
//
//    @Override
//    public void removeKeyboardFocusListener(KeyboardFocusListener l) {
//        listenerList.remove(KeyboardFocusListener.class,l);
//    }

    // ========================= UTIL =========================

    @Contract(" -> new")
    public static @NotNull Control blank() {
        return new Blank();
    }

    public static @NotNull Control blank(float width, float height) {
        var control = blank();
        control.setSize(width, height);
        return control;
    }

}
