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

package io.github.juicefries.fct.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * 链表
 * @param <G> 组
 * @param <K> 键
 * @param <V> 值
 */
public interface Sheet<G, K, V> {
    // 核心操作
    void put(G group, K key, V value);
    V get(G group, K key);
    V getOrDefault(G group, K key, V defaultValue);
    boolean contains(G group, K key);
    boolean containsGroup(G group);
    V remove(G group, K key);

    // 组操作
    Collection<K> getKeys(G group);
    Collection<V> getValues(G group);
    Collection<V> removeGroup(G group);

    // 全局视图
    Collection<G> groups();
    Collection<K> keys();
    Collection<V> values();
    Collection<Entry<G, K, V>> entries();

    // 容量查询
    int size();
    int groupCount();
    int groupSize(G group);
    boolean isEmpty();
    boolean isGroupEmpty(G group);

    // 批量操作
    void putAll(Sheet<G, K, V> other);
    void putAll(G group, Iterable<Pair<K, V>> entries);

    // 遍历
    void forEach(SheetConsumer<G, K, V> action);
    Iterator<Entry<G, K, V>> iterator();

    // 工具方法
    boolean equals(Object obj);
    int hashCode();
    Sheet<G, K, V> copy();
    void clear();

    record Entry<G, K, V>(G group, K key, V value) {}

    record Pair<A, B>(A first, B second) {}

    @FunctionalInterface
    interface SheetConsumer<G, K, V> {
        void accept(G group, K key, V value);
    }

}
