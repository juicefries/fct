package io.github.juicefries.fct.util;

import io.github.juicefries.fct.sign.ApiSign;
import io.github.juicefries.fct.sign.CopyFailedException;
import java.util.Arrays;

/**
 * {@link Chunk} 的数组实现
 * @param <E> 元素类型
 * @since 1.0.4
 */
@ApiSign.AIGenerated(since = "1.0.4")
public class ArrayChunk<E> implements Chunk<E> {

    /** 不限制坐标容量 */
    public final static int UNLIMITED = -1;

    private final static int X_BITS = 4;
    private final static int Z_BITS = 4;
    private final static int Y_BITS = 8;

    private final static int Z_SHIFT = X_BITS;              // 4
    private final static int Y_SHIFT = X_BITS + Z_BITS;     // 8

    public final static int MIN_X = 0, MAX_X = (1 << X_BITS) - 1;   // 0..15
    public final static int MIN_Z = 0, MAX_Z = (1 << Z_BITS) - 1;   // 0..15
    public final static int MIN_Y = 0, MAX_Y = (1 << Y_BITS) - 1;   // 0..255

    /** 各轴偏移量,用于把负数抬进无符号区间 */
    private final static int X_OFFSET = 1 << (X_BITS - 1);
    private final static int Z_OFFSET = 1 << (Z_BITS - 1);
    private final static int Y_OFFSET = 1 << (Y_BITS - 1);


    private Object[] es = Array.create(0);
    private final int maximumCapacity;

    public ArrayChunk() {
        this(UNLIMITED);
    }

    public ArrayChunk(int maximumCapacity) {
        if (maximumCapacity != UNLIMITED && maximumCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + maximumCapacity);
        }
        this.maximumCapacity = maximumCapacity;
    }

    public ArrayChunk(Chunk<E> chunk) {
        if (!(chunk instanceof ArrayChunk<?> other)) {
            throw new IllegalArgumentException("Unsupported chunk: " + chunk.getClass());
        }
        this.maximumCapacity = other.maximumCapacity;
        this.es = Arrays.copyOf(other.es, other.es.length);
    }

    /**
     * 把三维坐标压成一个槽位下标,压不下就说明该轴超出本实现能表达的范围。
     */
    private int position(int x, int y, int z) {
        if (x < MIN_X || x > MAX_X) {
            throw new IllegalArgumentException("x out of range: " + x);
        }
        if (y < MIN_Y || y > MAX_Y) {
            throw new IllegalArgumentException("y out of range: " + y);
        }
        if (z < MIN_Z || z > MAX_Z) {
            throw new IllegalArgumentException("z out of range: " + z);
        }
        int position = (y << Y_SHIFT) | (z << Z_SHIFT) | x;
        if (maximumCapacity != UNLIMITED && position >= maximumCapacity) {
            throw new IllegalArgumentException("The pointer position exceeds capacity!");
        }
        return position;
    }

    @Override
    public E add(int x, int y, int z, E e) {
        int position = position(x, y, z);
        if (position >= es.length) {
            es = Arrays.copyOf(es, position + 1);
        }
        @SuppressWarnings("unchecked")
        E old = (E) es[position];
        es[position] = e;
        return old;
    }

    @Override
    public E get(int x, int y, int z) {
        int position = position(x, y, z);
        if (position >= es.length) {
            return null;
        }
        @SuppressWarnings("unchecked")
        E e = (E) es[position];
        return e;
    }

    @Override
    public E remove(int x, int y, int z) {
        int position = position(x, y, z);
        if (position >= es.length) {
            return null;
        }
        @SuppressWarnings("unchecked")
        E old = (E) es[position];
        es[position] = null;
        return old;
    }

    @Override
    public void clear() {
        es = Array.create(0);
    }

    @Override
    public int size() {
        return es.length;
    }

    public int maximumCapacity() {
        return maximumCapacity;
    }

    @Override
    public ArrayChunk<E> copy() throws CopyFailedException {
        return new ArrayChunk<>(this);
    }

}
