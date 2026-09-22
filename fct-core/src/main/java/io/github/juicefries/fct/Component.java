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
// Data 2026/08/08 02:45
//

package io.github.juicefries.fct;


import io.github.juicefries.fct.sign.ApiSign;

public abstract class Component {


    static {
        Toolkit.initialize();
    }

    float x;
    float y;
    float width;
    float height;
    Container parent;
    Color background = Color.NEAR_BLACK.copy();
    boolean visible = true;
    boolean enabled = true;

    boolean opaque = true;
    Size maxSize = new Size();
    Size minSize = new Size();

    Size idealSize = new Size();

    protected Component() {

    }

    // ========================= OTM =========================

    public void paint(Graphics g) {
        if (g == null) return;
        if (!isOpaque()) return;
        g.setColor(getBackground());
        g.fillRect(0,0,getWidth(),getHeight());
    }

    /**
     * 判断点是否在组件内
     * <p>
     *     坐标相对于组件自身左上角，
     *     <br>
     *     默认按矩形判定，可重写实现非矩形命中。
     * </p>
     * @param x 相对组件左上角的X
     * @param y 相对组件左上角的Y
     * @return 点是否在组件内
     * @since 0.0.4
     */
    public boolean contains(float x, float y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public void validate() {
        var parent = this.parent;
        if (parent != null) {
            parent.validate();
        }
    }

    // ========================= SET =========================

    public void setSize(float width,float height) {
        this.width = width;
        this.height = height;
    }

    public void setSize(Size size) {
        setSize(size.width,size.height);
    }

    public void setMaxSize(Size size) {
        if (size == null) {
            throw new NullPointerException("size is null!");
        }
        this.maxSize = size;
    }

    public void setMaxSize(float width,float height) {
        if (width < 0) {
            throw new IllegalArgumentException("width is less than or equal to 0!");
        }
        if (height < 0) {
            throw new IllegalArgumentException("height is less than or equal to 0!");
        }
        setMaxSize(new Size(width, height));
    }

    public void setMinSize(Size size) {
        if (size == null) {
            throw new NullPointerException("size is null!");
        }
        this.minSize = size;
    }

    public void setMinSize(float width,float height) {
        if (width < 0) {
            throw new IllegalArgumentException("width is less than or equal to 0!");
        }
        if (height < 0) {
            throw new IllegalArgumentException("height is less than or equal to 0!");
        }
        setMinSize(new Size(width, height));
    }

    public void setIdealSize(Size size) {
        if (size == null) {
            throw new NullPointerException("size is null!");
        }
        this.idealSize = size;
    }

    public void setIdealSize(float width,float height) {
        if (width < 0) {
            throw new IllegalArgumentException("width is less than or equal to 0!");
        }
        if (height < 0) {
            throw new IllegalArgumentException("height is less than or equal to 0!");
        }
        setIdealSize(new Size(width, height));
    }

    public void setLocation(float x,float y) {
        this.x = x;
        this.y = y;
    }

    public void setLocation(Location location) {
        setLocation(location.x,location.y);
    }

    public void setBounds(float x,float y,float width,float height) {
        setSize(width, height);
        setLocation(x, y);
    }

    public void setBounds(Bounds bounds) {
        if (bounds == null) {
            throw new NullPointerException("bounds is null!");
        }
        setSize(bounds.getSize());
        setLocation(bounds.getLocation());
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setOpaque(boolean opaque) {
        this.opaque = opaque;
    }

    public void setBackground(Color background) {
        this.background = background;
    }

    // ========================= GET =========================

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Location getLocation() {
        return new Location(x,y);
    }

    public Location getAbsoluteLocation() {
        if (this instanceof Window) {
            return getLocation();
        }

        float absX = this.x;
        float absY = this.y;
        Container parent = getParent();

        while (parent != null) {
            if (parent instanceof Window) {
                break;
            }
            absX += parent.getX();
            absY += parent.getY();
            parent = parent.getParent();
        }

        return new Location(absX, absY);
    }

    public float getAbsoluteX() {
        return getAbsoluteLocation().x;
    }

    public float getAbsoluteY() {
        return getAbsoluteLocation().y;
    }

    /**
     * @return 获取组件的宽
     * @since 0.0.1
     */
    public float getWidth() {
        return width;
    }

    /**
     * @return 获取组件的高
     * @since 0.0.1
     */
    public float getHeight() {
        return height;
    }

    public Size getSize() {
        return new Size(width,height);
    }

    public Size getMaxSize() {
        return maxSize;
    }

    public Size getMinSize() {
        return minSize;
    }

    public Size getIdealSize() {
        return idealSize;
    }

    public Bounds getBounds() {
        Bounds bounds = new Bounds();
        bounds.setLocation(getLocation());
        bounds.setSize(getSize());
        return bounds;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isOpaque() {
        return opaque;
    }

    public Color getBackground() {
        return background;
    }

    public Container getParent() {
        return parent;
    }

    // ========================= UTIL =========================

    @ApiSign.NotRecommended(since = "0.0.4")
    @ApiSign.Dangerous(since = "0.0.4")
    public Container getAncestor() {
        Container p = getParent();
        if (p == null) {
            return null;
        }
        while (p.getParent() != null) {
            p = p.getParent();
        }
        return p;
    }

    @ApiSign.NotRecommended(since = "0.0.4")
    public Container[] getAncestors() {
        int count = 0;
        for (Container p = parent; p != null; p = p.getParent()) count++;

        Container[] arr = new Container[count];
        int i = 0;
        for (Container p = parent; p != null; p = p.getParent()) arr[i++] = p;
        return arr;
    }


}
