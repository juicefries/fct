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
// Data 2026/09/05 19:42
//

package io.github.juicefries.fct.layout;

import io.github.juicefries.fct.Component;
import io.github.juicefries.fct.Container;

public class FPageLayout extends PageLayout {

    float margin = 0.0f;

    public FPageLayout(float margin) {
        this.margin = Math.clamp(margin,0.0f,1.0f);
    }

    public FPageLayout() {

    }

    // ========================= OTM =========================

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

        // ====== 根据 margin 计算 page 的位置和尺寸 ======
        float scale = 1.0f - margin;          // margin∈[0,1] => scale∈[1,0]
        float parentW = size.width;
        float parentH = size.height;
        float pageW = parentW * scale;
        float pageH = parentH * scale;
        float x = (parentW - pageW) * 0.5f;   // 居中
        float y = (parentH - pageH) * 0.5f;

        page.setLocation(x, y);
        page.setSize(pageW, pageH);
    }


    // ========================= SET =========================

    public void setMargin(float margin) {
        this.margin = Math.clamp(margin,0.0f,1.0f);
    }

    // ========================= GET =========================

    public float getMargin() {
        return margin;
    }


    // ========================= EEE =========================

    // ========================= =========================

}
