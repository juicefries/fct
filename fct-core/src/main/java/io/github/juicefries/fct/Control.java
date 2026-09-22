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

import io.github.juicefries.fct.event.EventListenerList;
import io.github.juicefries.fct.event.KeyCharInputListener;
import io.github.juicefries.fct.event.KeyInputListener;
import io.github.juicefries.fct.event.MouseButtonListener;
import io.github.juicefries.fct.event.MouseCursorListener;
import io.github.juicefries.fct.event.MouseDropListener;
import io.github.juicefries.fct.event.MouseEntersListener;
import io.github.juicefries.fct.event.MouseWheelListener;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public abstract class Control extends Component implements
        EventComponent,
        KeyEventComponent,
        MouseEventComponent
{

    protected EventListenerList listenerList = new EventListenerList();
    boolean focus = true;
    Color foreground;
    Font font;

    protected Control() {

    }

    // ========================= OTM =========================

    @Override
    public void getFocus() {
        if (focus) return;
        focus = true;
    }

    @Override
    public void loseFocus() {
        if (!focus) return;
        focus = false;
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

    @Override
    public boolean isFocus() {
        return focus;
    }

    @Override
    public EventListenerList getListenerList() {
        return listenerList;
    }

    // ========================= ADD =========================

    @Override
    public void addMouseButtonListener(MouseButtonListener l) {
        listenerList.add(MouseButtonListener.class,l);
    }

    @Override
    public void addMouseWheelListener(MouseWheelListener l) {
        listenerList.add(MouseWheelListener.class,l);
    }

    @Override
    public void addMouseCursorListener(MouseCursorListener l) {
        listenerList.add(MouseCursorListener.class,l);
    }

    @Override
    public void addMouseDropListener(MouseDropListener l) {
        listenerList.add(MouseDropListener.class,l);
    }

    @Override
    public void addKeyCharInputListener(KeyCharInputListener l) {
        listenerList.add(KeyCharInputListener.class,l);
    }

    @Override
    public void addKeyInputListener(KeyInputListener l) {
        listenerList.add(KeyInputListener.class,l);
    }

    @Override
    public void addMouseEntersListener(MouseEntersListener l) {
        listenerList.add(MouseEntersListener.class,l);
    }

    // ========================= REMOVE =========================

    @Override
    public void removeKeyInputListener(KeyInputListener l) {
        listenerList.remove(KeyInputListener.class,l);
    }

    @Override
    public void removeKeyCharInputListener(KeyCharInputListener l) {
        listenerList.remove(KeyCharInputListener.class,l);
    }

    @Override
    public void removeMouseButtonListener(MouseButtonListener l) {
        listenerList.remove(MouseButtonListener.class,l);
    }

    @Override
    public void removeMouseWheelListener(MouseWheelListener l) {
        listenerList.remove(MouseWheelListener.class,l);
    }

    @Override
    public void removeMouseCursorListener(MouseCursorListener l) {
        listenerList.remove(MouseCursorListener.class,l);
    }

    @Override
    public void removeMouseDropListener(MouseDropListener l) {
        listenerList.remove(MouseDropListener.class,l);
    }

    @Override
    public void removeMouseEntersListener(MouseEntersListener l) {
        listenerList.remove(MouseEntersListener.class,l);
    }

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
