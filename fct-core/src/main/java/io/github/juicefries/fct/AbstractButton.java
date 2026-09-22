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
// Data 2026/09/16 19:35
//

package io.github.juicefries.fct;

import io.github.juicefries.fct.event.ActionListener;
import io.github.juicefries.fct.event.MouseButtonAdapter;
import io.github.juicefries.fct.event.MouseEntersListener;
import io.github.juicefries.fct.event.MouseEvent;
import io.github.juicefries.fct.util.Array;

public abstract class AbstractButton extends Control {

    {
        addMouseEntersListener(new MouseEntersListener() {
            @Override
            public void entered(MouseEvent e) {
                setRollover(true);
                validate();
            }
            @Override
            public void exited(MouseEvent e) {
                setRollover(false);
                validate();
            }
        });
        addMouseButtonListener(new MouseButtonAdapter() {
            @Override
            public void press(MouseEvent e) {
                if (e.getButton() == MouseEvent.MK_LEFT) {
                    setSelect(true);
                    validate();
                }
            }
            @Override
            public void release(MouseEvent e) {
                if (e.getButton() == MouseEvent.MK_LEFT) {
                    setSelect(false);
                    validate();
                    if (contains(e.getX(), e.getY())) {
                        var listeners = getListeners(ActionListener.class);
                        Array.forArr(listeners, action -> action.action(e));
                    }
                }
            }
        });
    }


    boolean select = false;
    boolean rollover = false;



    protected AbstractButton() {

    }

    // ========================= OTM =========================




    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class,l);
    }

    public void removeActionListener(ActionListener l) {
        listenerList.remove(ActionListener.class,l);
    }

    // ========================= SET =========================

    public void setSelect(boolean select) {
        this.select = select;
    }

    public void setRollover(boolean rollover) {
        this.rollover = rollover;
    }

    // ========================= GET =========================

    public boolean isSelect() {
        return select;
    }

    public boolean isRollover() {
        return rollover;
    }
}
