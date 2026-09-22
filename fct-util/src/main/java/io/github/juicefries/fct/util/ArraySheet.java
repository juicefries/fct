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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ArraySheet<G, K, V> implements Sheet<G, K, V> {

    private Group<G, K, V>[] groupTable = new Group[16];
    private Group<G, K, V> head, tail;
    private int groupCount, totalSize;
    private final Object lock = new Object();


    private static class Group<G, K, V> {
        G group;
        EntryItem<K, V>[] entryTable = new EntryItem[8];
        EntryItem<K, V> entryHead, entryTail;
        Group<G, K, V> next;
        Group<G, K, V> prev;
        int size;
    }

    // 改名避免冲突
    private static class EntryItem<K, V> {
        K key;
        V value;
        EntryItem<K, V> next;
        EntryItem<K, V> prev;

        EntryItem(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private int hash(Object obj, int capacity) {
        return obj == null ? 0 : (obj.hashCode() & 0x7FFFFFFF) % capacity;
    }

    private void ensureGroupCapacity() {
        if (groupCount >= groupTable.length * 0.75) {
            Group<G, K, V>[] newTable = new Group[groupTable.length * 2];
            Group<G, K, V> current = head;
            while (current != null) {
                int newIndex = hash(current.group, newTable.length);
                current.next = newTable[newIndex];
                newTable[newIndex] = current;
                current = current.next;
            }
            groupTable = newTable;
        }
    }

    @Contract(pure = true)
    private @Nullable Group<G, K, V> findGroup(G group, int index) {
        Group<G, K, V> current = groupTable[index];
        while (current != null) {
            if (Objects.equals(current.group, group)) {
                return current;
            }
            current = current.next;
        }
        return null;
    }

    private void addGroupToList(Group<G, K, V> grp) {
        if (head == null) {
            head = tail = grp;
        } else {
            tail.next = grp;
            grp.prev = tail;
            tail = grp;
        }
    }

    private void removeGroupFromList(@NotNull Group<G, K, V> grp) {
        if (grp.prev != null) grp.prev.next = grp.next;
        if (grp.next != null) grp.next.prev = grp.prev;
        if (grp == head) head = grp.next;
        if (grp == tail) tail = grp.prev;
    }

    @Contract(pure = true)
    private @Nullable EntryItem<K, V> findEntryInGroup(@NotNull Group<G, K, V> grp, K key, int index) {
        EntryItem<K, V> current = grp.entryTable[index];
        while (current != null) {
            if (Objects.equals(current.key, key)) {
                return current;
            }
            current = current.next;
        }
        return null;
    }

    private void addEntryToGroupTail(@NotNull Group<G, K, V> grp, EntryItem<K, V> entry) {
        if (grp.entryHead == null) {
            grp.entryHead = grp.entryTail = entry;
        } else {
            grp.entryTail.next = entry;
            entry.prev = grp.entryTail;
            grp.entryTail = entry;
        }
    }

    private void removeEntryFromGroup(@NotNull Group<G, K, V> grp, EntryItem<K, V> entry, int index) {
        if (grp.entryTable[index] == entry) {
            grp.entryTable[index] = entry.next;
        } else {
            EntryItem<K, V> current = grp.entryTable[index];
            while (current != null && current.next != entry) {
                current = current.next;
            }
            if (current != null) current.next = entry.next;
        }

        if (entry.prev != null) entry.prev.next = entry.next;
        if (entry.next != null) entry.next.prev = entry.prev;
        if (grp.entryHead == entry) grp.entryHead = entry.next;
        if (grp.entryTail == entry) grp.entryTail = entry.prev;
    }

    private void ensureEntryCapacity(@NotNull Group<G, K, V> grp) {
        if (grp.size >= grp.entryTable.length * 0.75) {
            EntryItem<K, V>[] newTable = new EntryItem[grp.entryTable.length * 2];
            EntryItem<K, V> current = grp.entryHead;
            while (current != null) {
                int newIndex = hash(current.key, newTable.length);
                current.next = newTable[newIndex];
                newTable[newIndex] = current;
                current = current.next;
            }
            grp.entryTable = newTable;
        }
    }

    @Override
    public void put(G group, K key, V value) {
        synchronized (lock) {
            int index = hash(group, groupTable.length);
            Group<G, K, V> grp = findGroup(group, index);

            if (grp == null) {
                grp = new Group<>();
                grp.group = group;
                grp.next = groupTable[index];
                groupTable[index] = grp;
                addGroupToList(grp);
                groupCount++;
                ensureGroupCapacity();
            }

            putInGroup(grp, key, value);
        }
    }

    private void putInGroup(@NotNull Group<G, K, V> grp, K key, V value) {
        int index = hash(key, grp.entryTable.length);
        EntryItem<K, V> entry = findEntryInGroup(grp, key, index);

        if (entry != null) {
            entry.value = value;
        } else {
            entry = new EntryItem<>(key, value);
            entry.next = grp.entryTable[index];
            grp.entryTable[index] = entry;
            addEntryToGroupTail(grp, entry);
            grp.size++;
            totalSize++;
            ensureEntryCapacity(grp);
        }
    }

    @Override
    public V get(G group, K key) {
        synchronized (lock) {
            int index = hash(group, groupTable.length);
            Group<G, K, V> grp = findGroup(group, index);
            if (grp == null) return null;

            int entryIndex = hash(key, grp.entryTable.length);
            EntryItem<K, V> entry = findEntryInGroup(grp, key, entryIndex);
            return entry != null ? entry.value : null;
        }
    }

    @Override
    public V getOrDefault(G group, K key, V defaultValue) {
        V value = get(group, key);
        return value != null ? value : defaultValue;
    }

    @Override
    public boolean contains(G group, K key) {
        return get(group, key) != null;
    }

    @Override
    public boolean containsGroup(G group) {
        synchronized (lock) {
            int index = hash(group, groupTable.length);
            return findGroup(group, index) != null;
        }
    }

    @Override
    public V remove(G group, K key) {
        synchronized (lock) {
            int index = hash(group, groupTable.length);
            Group<G, K, V> grp = findGroup(group, index);
            if (grp == null) return null;

            int entryIndex = hash(key, grp.entryTable.length);
            EntryItem<K, V> entry = findEntryInGroup(grp, key, entryIndex);
            if (entry == null) return null;

            V oldValue = entry.value;
            removeEntryFromGroup(grp, entry, entryIndex);
            grp.size--;
            totalSize--;

            if (grp.size == 0) {
                removeGroupInternal(group); // 这里也要改
            }

            return oldValue;
        }
    }

    private void removeGroupInternal(G group) {
        int index = hash(group, groupTable.length);
        Group<G, K, V> grp = findGroup(group, index);
        if (grp == null) return;

        if (groupTable[index] == grp) {
            groupTable[index] = grp.next;
        } else {
            Group<G, K, V> current = groupTable[index];
            while (current != null && current.next != grp) {
                current = current.next;
            }
            if (current != null) current.next = grp.next;
        }

        removeGroupFromList(grp);
        groupCount--;
    }

    @Override
    public Collection<K> getKeys(G group) {
        synchronized (lock) {
            int index = hash(group, groupTable.length);
            Group<G, K, V> grp = findGroup(group, index);
            if (grp == null) return Collections.emptyList();

            List<K> keys = new ArrayList<>();
            EntryItem<K, V> current = grp.entryHead;
            while (current != null) {
                keys.add(current.key);
                current = current.next;
            }
            return Collections.unmodifiableList(keys);
        }
    }

    @Override
    public Collection<V> getValues(G group) {
        synchronized (lock) {
            int index = hash(group, groupTable.length);
            Group<G, K, V> grp = findGroup(group, index);
            if (grp == null) return Collections.emptyList();

            List<V> values = new ArrayList<>();
            EntryItem<K, V> current = grp.entryHead;
            while (current != null) {
                values.add(current.value);
                current = current.next;
            }
            return Collections.unmodifiableList(values);
        }
    }

    @Override
    public Collection<V> removeGroup(G group) {
        synchronized (lock) {
            int index = hash(group, groupTable.length);
            Group<G, K, V> grp = findGroup(group, index);
            if (grp == null) return Collections.emptyList();

            List<V> values = new ArrayList<>();
            EntryItem<K, V> current = grp.entryHead;
            while (current != null) {
                values.add(current.value);
                current = current.next;
            }

            // 改名避免冲突
            removeGroupInternal(group);
            totalSize -= grp.size;
            return Collections.unmodifiableList(values);
        }
    }

    @Override
    public Collection<G> groups() {
        synchronized (lock) {
            List<G> groups = new ArrayList<>();
            Group<G, K, V> current = head;
            while (current != null) {
                groups.add(current.group);
                current = current.next;
            }
            return Collections.unmodifiableList(groups);
        }
    }

    @Override
    public Collection<K> keys() {
        synchronized (lock) {
            List<K> keys = new ArrayList<>();
            Group<G, K, V> current = head;
            while (current != null) {
                EntryItem<K, V> entry = current.entryHead;
                while (entry != null) {
                    keys.add(entry.key);
                    entry = entry.next;
                }
                current = current.next;
            }
            return Collections.unmodifiableList(keys);
        }
    }

    @Override
    public Collection<V> values() {
        synchronized (lock) {
            List<V> values = new ArrayList<>();
            Group<G, K, V> current = head;
            while (current != null) {
                EntryItem<K, V> entry = current.entryHead;
                while (entry != null) {
                    values.add(entry.value);
                    entry = entry.next;
                }
                current = current.next;
            }
            return Collections.unmodifiableList(values);
        }
    }

    @Override
    public Collection<Entry<G, K, V>> entries() {
        synchronized (lock) {
            List<Entry<G, K, V>> entries = new ArrayList<>();
            Group<G, K, V> current = head;
            while (current != null) {
                EntryItem<K, V> entry = current.entryHead;
                while (entry != null) {
                    entries.add(new Entry<>(current.group, entry.key, entry.value));
                    entry = entry.next;
                }
                current = current.next;
            }
            return Collections.unmodifiableList(entries);
        }
    }

    @Override
    public int size() {
        return totalSize;
    }

    @Override
    public int groupCount() {
        return groupCount;
    }

    @Override
    public int groupSize(G group) {
        synchronized (lock) {
            int index = hash(group, groupTable.length);
            Group<G, K, V> grp = findGroup(group, index);
            return grp != null ? grp.size : 0;
        }
    }

    @Override
    public boolean isEmpty() {
        return totalSize == 0;
    }

    @Override
    public boolean isGroupEmpty(G group) {
        return groupSize(group) == 0;
    }

    @Override
    public void putAll(@NotNull Sheet<G, K, V> other) {
        other.forEach(this::put);
    }

    @Override
    public void putAll(G group, @NotNull Iterable<Pair<K, V>> entries) {
        for (Pair<K, V> pair : entries) {
            put(group, pair.first(), pair.second());
        }
    }

    @Override
    public void forEach(SheetConsumer<G, K, V> action) {
        synchronized (lock) {
            Group<G, K, V> current = head;
            while (current != null) {
                EntryItem<K, V> entry = current.entryHead;
                while (entry != null) {
                    action.accept(current.group, entry.key, entry.value);
                    entry = entry.next;
                }
                current = current.next;
            }
        }
    }

    @Override
    public Iterator<Entry<G, K, V>> iterator() {
        return entries().iterator();
    }

    @Override
    public Sheet<G, K, V> copy() {
        synchronized (lock) {
            ArraySheet<G, K, V> copy = new ArraySheet<>();
            this.forEach(copy::put);
            return copy;
        }
    }

    @Override
    public void clear() {
        synchronized (lock) {
            Arrays.fill(groupTable, null);
            head = tail = null;
            groupCount = totalSize = 0;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Sheet<?, ?, ?> other)) return false;
        return this.entries().equals(other.entries());
    }

    @Override
    public int hashCode() {
        return entries().hashCode();
    }

    @Override
    public String toString() {
        return "Chain{groups=" + groupCount + ", totalSize=" + totalSize + "}";
    }
}