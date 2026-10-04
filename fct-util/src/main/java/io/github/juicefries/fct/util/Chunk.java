package io.github.juicefries.fct.util;

import io.github.juicefries.fct.sign.ApiSign;
import io.github.juicefries.fct.sign.CopyFailedException;
import io.github.juicefries.fct.sign.Copyable;

/**
 * 按坐标强制定位的稀疏容器
 * <p>
 *     与 {@link java.util.List} 不同,索引不由容器分配,
 *     必须由调用方给出坐标,容器只负责存放,
 *     因为坐标由外部决定,元素之间没有先后关系,
 *     所以 {@link #size()} 返回的是已铺开的槽位容量,
 *     而非元素个数。
 * </p>
 * @param <E> 元素类型
 * @since 1.0.4
 */
@ApiSign.AIGenerated(since = "1.0.4")
public interface Chunk<E> extends Copyable {

    /**
     * 将元素放入指定坐标,返回该坐标原有的元素。
     * @return 被顶替的旧元素,原本为空则返回 {@code null}
     */
    E add(int x, int y, int z, E e);

    /**
     * 取指定坐标的元素。
     * @return 该坐标的元素,未放入过则返回 {@code null}
     */
    E get(int x, int y, int z);

    /**
     * 移除指定坐标的元素。
     * @return 被移除的元素,原本为空则返回 {@code null}
     */
    E remove(int x, int y, int z);

    /**
     * 清空全部元素,坐标容量一并释放。
     */
    void clear();

    /**
     * 当前已铺开的槽位容量,不是元素个数。
     */
    int size();

    default boolean contains(int x, int y, int z) {
        return get(x, y, z) != null;
    }

    default boolean isEmpty() {
        return size() == 0;
    }

    @Override
    Chunk<E> copy() throws CopyFailedException;

}
