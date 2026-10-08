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

import io.github.juicefries.fct.util.Lock;
import java.util.ArrayList;
import java.util.List;

/**
 * <h2>容器 </h2>
 *
 * <p>
 *     可以容纳子组件的组件，负责子组件列表的维护与布局的下发，
 *     <br>
 *     子组件列表由{@link #cul}保护，所有结构性修改都在锁内完成，
 *     <br>
 *     读取一律返回快照，因此遍历过程不会受并发修改影响，
 *     <br>
 *     锁内不调用任何外部代码，避免与其他锁串成环。
 *     <br>
 *     文档由AI生成。
 * </p>
 *
 * @since 0.0.1
 * @author juicefries
 * @see Component
 * @see Layout
 */
public abstract class Container extends Component {

    /**
     * 子组件列表的更新锁
     * <p>
     *     所有对{@link #components}的读写都必须持有它，
     *     <br>
     *     组件上下文相关的操作（聚焦、失焦）一律放在锁外进行。
     * </p>
     * @since 1.0.3
     */
    final Lock cul = Lock.create("FCT.ComponentsUpdateLock");

    /**
     * 布局有效标记
     * @since 0.0.1
     * @deprecated 已无实际作用
     */
    @Deprecated(since = "0.0.3")
    volatile boolean valid = false;

    /**
     * 子组件列表
     * <p>
     *     读写都需要持有{@link #cul}。
     * </p>
     * @since 0.0.1
     */
    final List<Component> components = new ArrayList<>();

    /**
     * 当前布局
     * @since 0.0.1
     */
    volatile Layout layout;

    protected Container() {

    }

    // ========================= OTM =========================

    /**
     * 执行一次布局
     * <p>
     *     未设置布局时不做任何事。
     * </p>
     * @since 0.0.1
     */
    public void layout() {
        Layout layout = this.layout;
        if (layout != null) {
            layout.layout(this);
        }
    }

    /**
     * 向上校验并执行本容器的布局
     * <p>
     *     先让父容器校验，再对本容器布局，
     *     <br>
     *     不持有{@link #cul}，布局期间读取子组件拿的是快照。
     * </p>
     * @since 0.0.1
     */
    @Override
    public void validate() {
        var parent = this.parent;
        if (parent != null) {
            parent.validate();
        }
        layout();
    }

    // ========================= SET =========================

    /**
     * 设置布局
     * @param layout 布局
     * @since 0.0.1
     */
    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    /**
     * 设置尺寸并重新布局
     * @param width 宽
     * @param height 高
     * @since 0.0.1
     */
    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        layout();
    }

    /**
     * 设置可见性，转为可见时重新布局
     * @param visible 可见
     * @since 0.0.1
     */
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            layout();
        }
    }

    /**
     * 设置位置并重新布局
     * @param x X
     * @param y Y
     * @since 0.0.1
     */
    @Override
    public void setLocation(float x, float y) {
        super.setLocation(x, y);
        layout();
    }

    /**
     * 设置理想尺寸并重新布局
     * @param size 尺寸
     * @since 0.0.1
     */
    @Override
    public void setIdealSize(Size size) {
        super.setIdealSize(size);
        layout();
    }

    /**
     * 设置最大尺寸并重新布局
     * @param size 尺寸
     * @since 0.0.1
     */
    @Override
    public void setMaxSize(Size size) {
        super.setMaxSize(size);
        layout();
    }

    /**
     * 设置最小尺寸并重新布局
     * @param size 尺寸
     * @since 0.0.1
     */
    @Override
    public void setMinSize(Size size) {
        super.setMinSize(size);
        layout();
    }

    // ========================= GET =========================

    /**
     * 获取布局
     * @return 布局，未设置时为{@code null}
     * @since 0.0.1
     */
    public Layout getLayout() {
        return layout;
    }

    /**
     * 获取子组件快照
     * <p>
     *     返回的是调用这一刻的副本，遍历它不会受并发修改影响，
     *     <br>
     *     改动它也不会影响容器内部。
     * </p>
     * @return 子组件快照
     * @since 0.0.1
     */
    public Component[] getComponents() {
        synchronized (cul) {
            Component[] array = new Component[components.size()];
            return components.toArray(array);
        }
    }

    /**
     * 获取布局有效标记
     * @return 是否有效
     * @since 0.0.1
     * @deprecated 已无实际作用
     */
    @Deprecated(since = "0.0.3")
    public boolean isValid() {
        return valid;
    }

    /**
     * 获取坐标所在的子组件
     * <p>
     *     坐标相对于本容器左上角，从最上层开始判定，
     *     <br>
     *     命中子容器时会继续向下查找，
     *     <br>
     *     点在本容器内但没有命中任何子组件时返回本容器。
     * </p>
     * @param x X
     * @param y Y
     * @return 命中的组件，点不在容器内时为{@code null}
     * @since 0.0.4
     */
    public Component getComponentAt(float x, float y) {
        if (!contains(x, y)) {
            return null;
        }

        Component[] arr = getComponents();
        for (int i = arr.length - 1; i >= 0; i--) {   // 从后往前 = 最上层优先
            Component comp = arr[i];
            if (comp == null || !comp.isVisible()) continue;

            float lx = x - comp.getX();
            float ly = y - comp.getY();

            if (comp instanceof Container container) {
                Component deeper = container.getComponentAt(lx, ly);
                if (deeper != null) return deeper;
            } else {
                if (comp.contains(lx, ly)) return comp;
            }
        }
        return this;
    }

    /**
     * 获取坐标所在的子组件
     * @param location 坐标
     * @return 命中的组件，点不在容器内时为{@code null}
     * @throws NullPointerException 坐标为{@code null}
     * @since 0.0.4
     */
    public Component getComponentAt(Location location) {
        if (location == null) {
            throw new NullPointerException("location is null!");
        }
        return getComponentAt(location.x, location.y);
    }

    // ========================= ADD =========================

    /**
     * 添加子组件的实际实现
     * <p>
     *     组件若已有父容器，会先从旧容器中摘除，
     *     <br>
     *     这一句会在本容器加锁之前完成，避免同时持有两把容器锁，
     *     <br>
     *     加入后会注入当前上下文，并让组件获取键盘焦点。
     * </p>
     * @param comp 组件
     * @param constraints 布局约束
     * @param index 插入位置，小于{@code 0}表示追加到末尾
     * @return 被添加的组件
     * @throws NullPointerException 组件为{@code null}
     * @throws IllegalArgumentException 容器自身、父容器、祖先容器或窗口
     * @since 0.0.1
     */
    protected Component addImp(Component comp, Object constraints, int index) {
        if (comp == null) {
            throw new NullPointerException("comp is null!");
        }

        if (comp instanceof Container container) {
            checkContainer(container);
        }

        // 先从旧容器摘除：这一步会拿对方容器的锁，
        // 必须在拿本容器的锁之前做完，否则两把锁可能互等
        var oldParent = comp.getParent();
        if (oldParent != null) {
            oldParent.remove(comp);
        }

        var context = getComponentContext();

        synchronized (cul) {
            if (index <= -1) {
                components.add(comp);
            } else {
                components.add(index, comp);
            }
        }

        comp.componentContext = context;
        comp.parent = this;

        if (comp instanceof KeyEventComponent kec && context != null) {
            context.gainFocus(kec);
        }

        var layout = this.layout;
        if (layout != null) {
            layout.addLayoutComponent(comp, constraints);
        }

        validate();
        return comp;
    }

    /**
     * 添加子组件
     * @param component 组件
     * @param constraints 布局约束
     * @param index 插入位置，小于{@code 0}表示追加到末尾
     * @since 0.0.1
     */
    public void add(Component component, Object constraints, int index) {
        addImp(component, constraints, index);
    }

    /**
     * 添加子组件到末尾
     * @param component 组件
     * @return 被添加的组件
     * @since 0.0.1
     */
    public Component add(Component component) {
        return addImp(component, null, -1);
    }

    /**
     * 添加子组件到末尾
     * @param component 组件
     * @param constraints 布局约束
     * @return 被添加的组件
     * @since 0.0.1
     */
    public Component add(Component component, Object constraints) {
        return addImp(component, constraints, -1);
    }

    /**
     * 移除子组件
     * <p>
     *     只在锁内摘除列表项，父引用、上下文引用与失焦处理都放在锁外，
     *     <br>
     *     若该组件正持有键盘焦点，会先让它失去焦点。
     * </p>
     * @param comp 组件
     * @throws NullPointerException 组件为{@code null}
     * @throws IllegalArgumentException 组件为容器自身，或不在本容器中
     * @since 0.0.1
     */
    public void remove(Component comp) {
        if (comp == null) {
            throw new NullPointerException("comp is null!");
        }
        if (comp == this) {
            throw new IllegalArgumentException("The container cannot remove itself!");
        }

        synchronized (cul) {
            if (!components.contains(comp)) {
                throw new IllegalArgumentException("Component does not exist!");
            }
            components.remove(comp);
        }

        comp.parent = null;

        var context = comp.getComponentContext();
        if (context != null && comp instanceof KeyEventComponent kec && context.isFocused(kec)) {
            kec.loseFocus();
        }

        comp.componentContext = null;

        validate();
    }

    /**
     * 按索引移除子组件
     * <p>
     *     先取出引用，再走{@link #remove(Component)}，
     *     <br>
     *     因此清理流程与按引用移除完全一致。
     * </p>
     * @param index 索引
     * @throws IllegalArgumentException 索引越界
     * @since 0.0.1
     */
    public void remove(int index) {
        Component comp;
        synchronized (cul) {
            if (index < 0 || index >= components.size()) {
                throw new IllegalArgumentException("Index out of bounds!");
            }
            comp = components.get(index);
        }
        remove(comp);
    }

    /**
     * 移除全部子组件
     * @since 0.0.1
     */
    public void removeAll() {
        for (Component comp : getComponents()) {
            if (comp == null) continue;
            remove(comp);
        }
        validate();
    }

    // ========================= PAINT =========================

    /**
     * 绘制本容器与全部子组件
     * @param g 画笔
     * @since 0.0.1
     */
    @Override
    public void paint(Graphics g) {
        paintBackground(g);
        paintComponents(g);
        paintForeground(g);
    }

    /**
     * 绘制背景
     * @param g 画笔
     * @since 0.0.1
     */
    public void paintBackground(Graphics g) {
        if (isOpaque()) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    /**
     * 绘制前景
     * @param g 画笔
     * @since 0.0.1
     */
    public void paintForeground(Graphics g) {

    }

    /**
     * 绘制全部子组件
     * <p>
     *     遍历的是子组件快照，绘制期间发生增删不影响本轮。
     * </p>
     * @param g 画笔
     * @since 0.0.1
     */
    public void paintComponents(Graphics g) {
        if (g == null) return;

        for (Component comp : getComponents()) {
            if (comp == null) continue;
            if (!comp.isVisible()) continue;
            g.save();
            int cx = (int) Math.floor(comp.getAbsoluteX());
            int cy = (int) Math.floor(comp.getAbsoluteY());
            int cw = (int) Math.ceil(comp.getAbsoluteX() + comp.getWidth()) - cx;
            int ch = (int) Math.ceil(comp.getAbsoluteY() + comp.getHeight()) - cy;
            g.clip(cx, cy, cw, ch);

            g.translate(comp.getX(), comp.getY());
            comp.paint(g);
            g.unclip();
            g.restore();
        }
    }

    // ========================= UTIL =========================

    /**
     * 获取根容器
     * @return 顺着父引用走到的最顶层容器
     * @since 0.0.1
     */
    protected Container getRootContainer() {
        Container p = this;
        while (p.getParent() != null) {
            p = p.getParent();
        }
        return p;
    }

    /**
     * 检查待加入的容器是否合法
     * @param container 待加入的容器
     * @throws IllegalArgumentException 自身、父容器、祖先容器或窗口
     * @since 0.0.1
     */
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

    /**
     * 判断给定容器是否为当前容器的祖先
     * @param container 容器
     * @return 是否为祖先
     * @since 0.0.1
     */
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
