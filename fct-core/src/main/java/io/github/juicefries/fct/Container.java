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
// Data 2026/08/08 03:40
//

package io.github.juicefries.fct;

import io.github.juicefries.fct.util.Array;
import io.github.juicefries.fct.util.TraverseList;

public abstract class Container extends Component {

    volatile boolean valid = false;

    final TraverseList<Component> components = new TraverseList<>(Component.class);

    volatile Layout layout;

    protected Container() {

    }

    // ========================= OTM =========================

    public void layout() {
        Layout layout = this.layout;
        if (layout != null) {
            layout.layout(this);
        }
    }


    @Override
    public void validate() {
        var parent = this.parent;
        if (parent != null) {
            parent.validate();
        }
        layout();
    }

    // ========================= SET =========================

    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        layout();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            layout();
        }
    }

    @Override
    public void setLocation(float x, float y) {
        super.setLocation(x, y);
        layout();
    }

    @Override
    public void setIdealSize(Size size) {
        super.setIdealSize(size);
        layout();
    }

    @Override
    public void setMaxSize(Size size) {
        super.setMaxSize(size);
        layout();
    }

    @Override
    public void setMinSize(Size size) {
        super.setMinSize(size);
        layout();
    }

    // ========================= GET =========================

    public Layout getLayout() {
        return layout;
    }

    public Component[] getComponents() {
        return components.getArray();
    }

    @Deprecated(since = "0.0.3")
    public boolean isValid() {
        return valid;
    }

    public Component getComponentAt(float x, float y) {
        // 点不在容器自己内
        if (!contains(x, y)) {
            return null;
        }

        Component[] arr = getComponents();
        for (int i = arr.length - 1; i >= 0; i--) {   // 从后往前 = 最上层优先
            Component comp = arr[i];
            if (comp == null || !comp.isVisible()) continue;

            float lx = x - comp.getX();   // 减偏移 → 子组件本地坐标
            float ly = y - comp.getY();

            if (comp instanceof Container container) {
                Component deeper = container.getComponentAt(lx, ly);
                if (deeper != null) return deeper;    // 命中深层子组件
            } else {
                if (comp.contains(lx, ly)) return comp;  // 命中叶子控件
            }
        }
        return this;   // 点在自己内，但没命中任何子组件，返回自己
    }


    public Component getComponentAt(Location location) {
        if (location == null) {
            throw new NullPointerException("location is null!");
        }
        return getComponentAt(location.x,location.y);
    }

    // ========================= ADD =========================

    protected Component addImp(Component comp,Object constraints,int index) {
        if (comp == null) {
            throw new NullPointerException("comp is null!");
        }

        if (comp instanceof Container container) {
            checkContainer(container);
        }

        if (comp instanceof KeyEventComponent component) {
            var rc = getRootContainer();
            if (rc instanceof KeyEventComponent ec) {
                ec.loseFocus();
            }
            for (Component c : rc.getComponents()) {
                if (c == null) continue;
                loseFocus(c);
            }
            component.getFocus();
        }

        if (index <= -1) {
            components.add(comp);
        } else {
            components.add(index,comp);
        }

        comp.parent = this;
        if (layout != null) {
            layout.addLayoutComponent(comp, constraints);
        }

        validate();
        return comp;
    }

    protected void loseFocus(Component comp) {
        if (!(comp instanceof KeyEventComponent ec)) {
            return;
        }
        if (ec.isFocus()) {
            ec.loseFocus();
        }

        if (ec instanceof Container container) {
            for (Component c : container.getComponents()) {
                loseFocus(c);
            }
        }
    }

    public void add(Component component,Object constraints,int index) {
        addImp(component, constraints, index);
    }

    public Component add(Component component) {
        return addImp(component,null,-1);
    }

    public Component add(Component component,Object constraints) {
        return addImp(component,constraints,-1);
    }

    public void remove(Component comp) {
        if (comp == null) {
            throw new NullPointerException("comp is null!");
        }
        if (comp == this) {
            throw new IllegalArgumentException("The container cannot remove itself!");
        }
        var components = getComponents();
        if (!Array.contains(comp,components)) {
            throw new IllegalArgumentException("Component does not exist!");
        }
        this.components.remove(comp);
        validate();
    }

    public void remove(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Index is less than 0!");
        }
        if (index > components.size() - 1) {
            throw new IllegalArgumentException("Index out of bounds!");
        }
        this.components.remove(index);
        validate();
    }

    public void removeAll() {
        Array.forArr(getComponents(),(comp, _) -> {
            if (comp == null) return;
            remove(comp);
        });
        validate();
    }

    // ========================= PAINT =========================

    @Override
    public void paint(Graphics g) {
        paintBackground(g);
        paintComponents(g);
        paintForeground(g);
    }

    public void paintBackground(Graphics g) {
        if (isOpaque()) {
            g.setColor(getBackground());
            g.fillRect(0,0,getWidth(),getHeight());
        }
    }

    public void paintForeground(Graphics g) {

    }

    public void paintComponents(Graphics g) {
        if (g == null) return;

        for (Component comp : getComponents()) {
            if (comp == null) continue;
            if (!comp.isVisible()) continue;
            g.save();
            int cx = (int) Math.floor(comp.getAbsoluteX());
            int cy = (int) Math.floor(comp.getAbsoluteY());
            int cw = (int) Math.ceil(comp.getAbsoluteX() + comp.getWidth())  - cx;
            int ch = (int) Math.ceil(comp.getAbsoluteY() + comp.getHeight()) - cy;
            g.clip(cx, cy, cw, ch);

            g.translate(comp.getX(),comp.getY());
            comp.paint(g);
            g.unclip();
            g.restore();
        }
    }

    // ========================= UTIL =========================

    protected Container getRootContainer() {
        Container p = this;
        while (p.getParent() != null) {
            p = p.getParent();
        }
        return p;
    }


    private void checkContainer(Container container) {
        if (container == this) {
            throw new IllegalArgumentException("A container cannot add itself!");
        }
        if (container == parent) {
            throw new IllegalArgumentException("The container cannot add a parent component!");
        }
        if (checkIsParent(container)) {
            throw new IllegalArgumentException("Circular reference: Cannot add ancestor container "
                    + container.getClass().getCanonicalName()
                    + " as its own child component"
            );
        }

        if (container instanceof Window) {
            throw new IllegalArgumentException("The container cannot add a window!");
        }
    }

    private boolean checkIsParent(Container container) {
        if (container == null) {
            return false;
        }
        for (Component c = parent; c != null; c = c.parent) {
            if (c == container) {
                return true;
            }
        }
        return false;
    }

}
