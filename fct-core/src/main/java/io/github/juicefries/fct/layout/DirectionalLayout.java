//
// Created by juicefries
// The project name is fct
// Data 2026/09/17 19:14
//

package io.github.juicefries.fct.layout;

import io.github.juicefries.fct.Component;
import io.github.juicefries.fct.Container;
import io.github.juicefries.fct.Layout;
import io.github.juicefries.fct.sign.ApiSign;
import org.jetbrains.annotations.NotNull;

/**
 * 方向布局
 * <p>
 *     参考{@code java.awt.BorderLayout}实现的五向布局。
 *     <br>
 *     北/南占据顶部与底部，东/西占据左右，中央占据剩余空间。
 *     <br>
 *     有理想尺寸则优先使用；
 *     <br>
 *     无理想尺寸时按权重比例分配——垂直（北:中:南 = 1:2:1），
 *     <br>
 *     水平（西:中:东 = 1:2:1），中心占大头。
 * </p>
 * @since 0.0.4
 * @author ds(AI)
 * @see Layout
 */
public class DirectionalLayout implements Layout {

    /**
     * 北
     * @since 0.0.4
     */
    public final static String NORTH = "North";

    /**
     * 南
     * @since 0.0.4
     */
    public final static String SOUTH = "South";

    /**
     * 东
     * @since 0.0.4
     */
    public final static String EAST = "East";

    /**
     * 西
     * @since 0.0.4
     */
    public final static String WEST = "West";

    /**
     * 中
     * @since 0.0.4
     */
    public final static String CENTER = "Center";

    /**
     * 北
     * @since 0.0.4
     */
    @Deprecated(since = "0.0.4")
    @ApiSign.NotRecommended(since = "0.0.4")
    public final static String 北 = NORTH;

    /**
     * 南
     * @since 0.0.4
     */
    @Deprecated(since = "0.0.4")
    @ApiSign.NotRecommended(since = "0.0.4")
    public final static String 南 = SOUTH;

    /**
     * 东
     * @since 0.0.4
     */
    @Deprecated(since = "0.0.4")
    @ApiSign.NotRecommended(since = "0.0.4")
    public final static String 东 = EAST;

    /**
     * 西
     * @since 0.0.4
     */
    @Deprecated(since = "0.0.4")
    @ApiSign.NotRecommended(since = "0.0.4")
    public final static String 西 = WEST;

    /**
     * 中
     * @since 0.0.4
     */
    @Deprecated(since = "0.0.4")
    @ApiSign.NotRecommended(since = "0.0.4")
    public final static String 中 = CENTER;

    private Component north;
    private Component south;
    private Component east;
    private Component west;
    private Component center;

    private float hgap;
    private float vgap;

    /** 垂直方向权重（北 / 中间行 / 南） */
    private final float[] verticalWeight = {1f, 2f, 1f};
    /** 水平方向权重（西 / 中央 / 东） */
    private final float[] horizontalWeight = {1f, 2f, 1f};

    public DirectionalLayout() {

    }

    public DirectionalLayout(float hgap, float vgap) {
        this.hgap = hgap;
        this.vgap = vgap;
    }

    // ========================= OTM =========================

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
        if (comp == null) return;
        String c = constraints == null ? CENTER : constraints.toString();
        switch (c) {
            case NORTH -> north = comp;
            case SOUTH -> south = comp;
            case EAST -> east = comp;
            case WEST -> west = comp;
            default -> center = comp;
        }
    }

    @Override
    public void removeLayoutComponent(Component comp) {
        if (comp == null) return;
        if (comp == north) north = null;
        else if (comp == south) south = null;
        else if (comp == east) east = null;
        else if (comp == west) west = null;
        else if (comp == center) center = null;
    }

    @Override
    public void layout(Container container) {
        if (container == null) return;
        var size = container.getSize();
        float W = size.width, H = size.height;

        Component n = visible(north);
        Component s = visible(south);
        Component e = visible(east);
        Component w = visible(west);
        Component c = visible(center);

        boolean hasMid = (w != null || c != null || e != null);

        // 垂直：NORTH / 中间行 / SOUTH（-1 不存在，0 自动，>0 指定）
        float[] v = distribute(
                n != null ? idealHeight(n) : -1,
                hasMid ? 0 : -1,
                s != null ? idealHeight(s) : -1,
                verticalWeight, H, vgap
        );
        float nH = v[0], midH = v[1], sH = v[2];

        // 水平：WEST / CENTER / EAST
        float[] hz = distribute(
                w != null ? idealWidth(w) : -1,
                c != null ? 0 : -1,
                e != null ? idealWidth(e) : -1,
                horizontalWeight, W, hgap
        );
        float wW = hz[0], eW = hz[2];

        // 放置：北
        float y = 0;
        if (n != null) {
            n.setBounds(0, 0, W, nH);
            y = nH + vgap;
        }

        // 中间行：西 / 中 / 东
        float x = 0, right = W;
        if (w != null) { w.setBounds(x, y, wW, midH); x += wW + hgap; }
        if (e != null) { e.setBounds(right - eW, y, eW, midH); right -= eW + hgap; }
        if (c != null) { c.setBounds(x, y, Math.max(0, right - x), midH); }

        // 南
        if (s != null) {
            s.setBounds(0, H - sH, W, sH);
        }
    }

    // ========================= SET =========================

    public void setHgap(float hgap) { this.hgap = hgap; }
    public void setVgap(float vgap) { this.vgap = vgap; }

    // ========================= GET =========================

    public float getHgap() { return hgap; }
    public float getVgap() { return vgap; }

    // ========================= UTIL =========================

    private float idealWidth(@NotNull Component c) {
        float w = c.getIdealSize().width;
        return w > 0 ? w : 0;
    }

    private float idealHeight(@NotNull Component c) {
        float h = c.getIdealSize().height;
        return h > 0 ? h : 0;
    }

    private Component visible(Component c) {
        return (c != null && c.isVisible()) ? c : null;
    }

    /**
     * 分配三个槽位：指定尺寸（>0）保留，自动槽位（0）按权重比例分剩余
     * @param a/b/c 槽位：-1 不存在，0 自动，>0 指定
     * @param weight 各槽位权重
     * @param total 总尺寸
     * @param gap 槽位间距
     */
    private static float[] distribute(float a, float b, float c,
                                      float[] weight, float total, float gap) {
        float[] arr = {a, b, c};
        int count = 0;
        float used = 0;
        for (float v : arr) {
            if (v >= 0) count++;
            if (v > 0) used += v;
        }
        float totalGap = gap * Math.max(0, count - 1);
        float remaining = Math.max(0, total - used - totalGap);

        float autoWeight = 0;
        for (int i = 0; i < arr.length; i++)
            if (arr[i] == 0) autoWeight += weight[i];

        if (autoWeight > 0) {
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] == 0) arr[i] = remaining * weight[i] / autoWeight;
            }
        }
        return arr;
    }

}
