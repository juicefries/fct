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
// Data 2026/09/17 16:51
//

package io.github.juicefries.fct.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 参数表
 * <p>
 *     一个带{@code valid}标志的键值表封装，用于缓存初始化参数等
 *     <br>
 *     需要临时独立存储、避免被运行时状态覆盖的值。
 *     <br>
 *     每个值附带一个有效性标志，配合重载的{@link #get(Object, boolean[])}
 *     <br>
 *     可判断该值是否为显式设置而非默认值。
 * </p>
 * @param <K> 键类型
 * @param <P> 值类型
 * @since 0.0.4
 * @author ds(AI)
 * @see Parameter
 */
public class Parameters<K, P> {

    private final Map<K, Parameter<P>> map;

    public Parameters() {
        this.map = new HashMap<>();
    }

    // ========================= PUT =========================

    /**
     * 存入一个参数
     * @param key 键
     * @param valid 有效性标志
     * @param val 值
     * @throws NullPointerException 键不能为{@code null}
     * @since 0.0.4
     */
    public void put(K key, boolean valid, P val) {
        requireKey(key);
        map.put(key, Parameter.create(valid, val));
    }

    /**
     * 存入一个参数，默认标志为无效
     * @param key 键
     * @param val 值
     * @throws NullPointerException 键不能为{@code null}
     * @since 0.0.4
     */
    public void put(K key, P val) {
        put(key, false, val);
    }

    /**
     * 存入一个参数，并标记为有效
     * @param key 键
     * @param val 值
     * @throws NullPointerException 键不能为{@code null}
     * @since 0.0.4
     */
    public void putValid(K key, P val) {
        put(key, true, val);
    }

    // ========================= GET =========================

    /**
     * 获取参数值，不存在时返回{@code null}
     * @param key 键
     * @return 参数值，可能为{@code null}
     * @throws NullPointerException 键不能为{@code null}
     * @since 0.0.4
     */
    public P get(K key) {
        requireKey(key);
        Parameter<P> p = map.get(key);
        return p == null ? null : p.p();
    }

    /**
     * {@link #get(Object, Object)}的别名
     * @param key 键
     * @param defaultValue 默认值
     * @return 参数值或默认值
     * @since 0.0.4
     */
    public P getOrDefault(K key, P defaultValue) {
        return get(key, defaultValue);
    }

    /**
     * 获取参数值，并回填有效性标志
     * @param key 键
     * @param valid 长度为1的数组，用于接收有效性标志
     * @return 参数值，不存在时为{@code null}
     * @throws NullPointerException 键或数组不能为{@code null}
     * @throws IllegalArgumentException 数组长度不能小于1
     * @since 0.0.4
     */
    public P get(K key, boolean[] valid) {
        requireKey(key);
        Array.requireNotLessThan(valid, 1);
        Parameter<P> p = map.get(key);
        if (p == null) {
            valid[0] = false;
            return null;
        }
        valid[0] = p.valid();
        return p.p();
    }

    /**
     * 获取参数值并做类型检查，类型不符时返回默认值
     * <p>
     *     若参数值不是默认值类型的实例，则回退为默认值，
     *     <br>
     *     避免存储值与期望类型不一致时抛出{@link ClassCastException}。
     * </p>
     * @param key 键
     * @param defaultValue 默认值，同时用于类型校验
     * @param <T> 返回值类型
     * @return 参数值或默认值
     * @throws NullPointerException 键不能为{@code null}
     * @since 0.0.4
     */
    public <T> T get(K key, T defaultValue) {
        requireKey(key);
        P val = get(key);
        if (val == null) return defaultValue;
        if (defaultValue != null && !defaultValue.getClass().isInstance(val)) {
            return defaultValue;
        }
        @SuppressWarnings("unchecked")
        T result = (T) val;
        return result;
    }

    /**
     * 获取参数值并做类型检查，同时回填有效性标志
     * @param key 键
     * @param defaultValue 默认值，同时用于类型校验
     * @param valid 长度为1的数组，用于接收有效性标志
     * @param <T> 返回值类型
     * @return 参数值或默认值
     * @throws NullPointerException 键或数组不能为{@code null}
     * @throws IllegalArgumentException 数组长度不能小于1
     * @since 0.0.4
     */
    public <T> T get(K key, T defaultValue, boolean[] valid) {
        requireKey(key);
        if (valid == null) {
            throw new NullPointerException("valid is null!");
        }
        Array.requireNotLessThan(valid, 1);

        boolean[] flag = Array.createB(1);
        P val = get(key, flag);
        valid[0] = flag[0];

        if (val == null) return defaultValue;
        if (defaultValue != null && !defaultValue.getClass().isInstance(val)) {
            return defaultValue;
        }
        @SuppressWarnings("unchecked")
        T result = (T) val;
        return result;
    }


    // ========================= QUERY =========================

    /**
     * 判断是否包含指定键
     * @param key 键
     * @return 是否包含
     * @since 0.0.4
     */
    public boolean containsKey(K key) {
        requireKey(key);
        return map.containsKey(key);
    }

    /**
     * 判断指定键是否有效
     * @param key 键
     * @return 键存在且有效时返回{@code true}
     * @since 0.0.4
     */
    public boolean isValid(K key) {
        requireKey(key);
        Parameter<P> p = map.get(key);
        return p != null && p.valid();
    }

    /**
     * @return 参数数量
     * @since 0.0.4
     */
    public int size() {
        return map.size();
    }

    /**
     * @return 是否为空
     * @since 0.0.4
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    // ========================= REMOVE =========================

    /**
     * 移除并返回参数值
     * @param key 键
     * @return 被移除的参数值，不存在时为{@code null}
     * @throws NullPointerException 键不能为{@code null}
     * @since 0.0.4
     */
    public P remove(K key) {
        requireKey(key);
        Parameter<P> p = map.remove(key);
        return p == null ? null : p.p();
    }

    /**
     * 清空所有参数
     * @since 0.0.4
     */
    public void clear() {
        map.clear();
    }

    // ========================= VIEW =========================

    /**
     * @return 所有键的集合
     * @since 0.0.4
     */
    public Set<K> keySet() {
        return map.keySet();
    }

    /**
     * @return 所有参数的集合
     * @since 0.0.4
     */
    public Collection<Parameter<P>> values() {
        return map.values();
    }

    /**
     * @return 所有键值对的集合
     * @since 0.0.4
     */
    public Set<Map.Entry<K, Parameter<P>>> entrySet() {
        return map.entrySet();
    }

    // ========================= OTM =========================

    private void requireKey(K key) {
        if (key == null) {
            throw new NullPointerException("key is null!");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Parameters<?, ?> that)) return false;
        return map.equals(that.map);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public String toString() {
        return map.toString();
    }
}
