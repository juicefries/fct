package io.github.juicefries.fct.layout;

import io.github.juicefries.fct.Component;
import io.github.juicefries.fct.Container;
import io.github.juicefries.fct.Layout;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * 盒式布局
 * <p>
 *     参考 {@link javax.swing.BoxLayout BoxLayout} 实现的单轴线性布局。
 *     <br>
 *     组件沿主轴依次排列：
 *     <ul>
 *         <li>主轴方向：组件有理想尺寸则用理想尺寸，否则按比例分配，默认父容器的 1/5（{@link #DEFAULT_RATIO}）</li>
 *         <li>交叉轴方向：填满父容器</li>
 *     </ul>
 *     布局不做边界裁剪，组件可以超出父容器范围，适配滚动条。
 * </p>
 * @since 0.0.5
 * @author juicefries
 * @see Layout
 */
public class BoxLayout implements Layout {

    /** 水平排列 */
    public static final int X_AXIS = 0;

    /** 垂直排列 */
    public static final int Y_AXIS = 1;

    /** 默认比例：父容器的 1/5 */
    public static final float DEFAULT_RATIO = 0.2f;

    /** 排列方向 */
    private final int axis;

    /**
     * 比例模式开关。
     * <p>
     *     {@code true}：固定模式，所有自动尺寸的组件统一使用 {@link #DEFAULT_RATIO}；
     *     <br>
     *     {@code false}：自由模式，允许组件通过约束指定比例，未指定的用默认值。
     * </p>
     */
    private boolean fixedRatio;

    /** 组件到指定比例的映射 */
    private final Map<Component, Float> ratios = new HashMap<>();

    /** 默认水平排列 */
    public BoxLayout() {
        this(X_AXIS);
    }

    /**
     * 指定排列方向。
     *
     * @param axis 排列方向，{@link #X_AXIS} 或 {@link #Y_AXIS}
     * @throws IllegalArgumentException 如果 axis 不是合法方向
     */
    public BoxLayout(int axis) {
        if (axis != X_AXIS && axis != Y_AXIS) {
            throw new IllegalArgumentException("axis 必须是 X_AXIS 或 Y_AXIS");
        }
        this.axis = axis;
        this.fixedRatio = true;
    }

    // ========================= OTM =========================

    /**
     * 添加组件，约束可以是数值类型，表示该组件在主轴方向的比例。
     * <p>约束为 {@code null} 或非正数时使用 {@link #DEFAULT_RATIO}。</p>
     *
     * @param comp        组件
     * @param constraints 比例约束
     */
    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
        if (comp == null) return;
        float r = DEFAULT_RATIO;
        if (constraints instanceof Number n) {
            float v = n.floatValue();
            if (v > 0) r = v;
        }
        ratios.put(comp, r);
    }

    @Override
    public void removeLayoutComponent(Component comp) {
        if (comp == null) return;
        ratios.remove(comp);
    }

    @Override
    public void layout(Container container) {
        if (container == null) return;
        var size = container.getSize();
        float W = size.width;
        float H = size.height;

        if (axis == X_AXIS) {
            layoutX(container, W, H);
        } else {
            layoutY(container, W, H);
        }
    }

    // ========================= SET =========================

    /**
     * 设置比例模式开关。
     *
     * @param fixedRatio {@code true} 固定用默认比例，{@code false} 允许组件指定比例
     */
    public void setFixedRatio(boolean fixedRatio) {
        this.fixedRatio = fixedRatio;
    }

    // ========================= GET =========================

    /**
     * 返回当前是否处于固定比例模式。
     *
     * @return {@code true} 表示固定比例模式
     */
    public boolean isFixedRatio() {
        return fixedRatio;
    }

    /**
     * 返回排列方向。
     *
     * @return {@link #X_AXIS} 或 {@link #Y_AXIS}
     */
    public int getAxis() {
        return axis;
    }

    // ========================= UTIL =========================

    /** 水平排列 */
    private void layoutX(@NotNull Container container, float W, float H) {
        float x = 0;
        for (Component comp : container.getComponents()) {
            if (comp == null || !comp.isVisible()) continue;
            float w = comp.getIdealSize().width;
            if (w <= 0) {
                w = W * ratio(comp);
            }
            comp.setBounds(x, 0, w, H);
            x += w;
        }
    }

    /** 垂直排列 */
    private void layoutY(@NotNull Container container, float W, float H) {
        float y = 0;
        for (Component comp : container.getComponents()) {
            if (comp == null || !comp.isVisible()) continue;
            float h = comp.getIdealSize().height;
            if (h <= 0) {
                h = H * ratio(comp);
            }
            comp.setBounds(0, y, W, h);
            y += h;
        }
    }

    /** 获取组件的有效比例 */
    private float ratio(Component comp) {
        if (fixedRatio) return DEFAULT_RATIO;
        return ratios.getOrDefault(comp, DEFAULT_RATIO);
    }

}
