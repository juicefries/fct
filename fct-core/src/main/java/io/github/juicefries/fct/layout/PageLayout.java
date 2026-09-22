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
// Data 2026/09/03 15:15
//

package io.github.juicefries.fct.layout;

import io.github.juicefries.fct.Component;
import io.github.juicefries.fct.Container;
import io.github.juicefries.fct.Layout;

/**
 * 一个简单的页面布局，他会将当前页在布局时直接填充满布局。
 * @since 0.0.3
 * @author juicefries
 */
public class PageLayout implements Layout {

    Component page;

    public PageLayout() {

    }

    public void setPage(Component page) {
        if (page == null) {
            throw new NullPointerException("page is null!");
        }

        this.page = page;
    }

    public Component getPage() {
        return page;
    }

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
        if (comp == null) return;
        page = comp;
    }

    @Override
    public void removeLayoutComponent(Component comp) {
        if (page == comp) {
            page = null;
        }
    }

    @Override
    public void layout(Container container) {
        if (container == null) return;
        if (page == null) return;

        var size = container.getSize();
        if (!page.isVisible()) return;
        for (Component comp : container.getComponents()) {
            comp.setVisible(false);
        }
        page.setVisible(true);
        page.setLocation(0.0f,0.0f);
        page.setSize(size);
    }

}
