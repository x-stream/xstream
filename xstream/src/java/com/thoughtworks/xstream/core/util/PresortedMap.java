/*
 * Copyright (C) 2006, 2007, 2010, 2014, 2015, 2022 XStream Committers.
 * All rights reserved.
 *
 * Created on 12.10.2010 by Joerg Schaible, extracted from TreeMapConverter.
 */
package com.thoughtworks.xstream.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;


/**
 * @author J&ouml;rg Schaible
 */
public class PresortedMap<K, V> implements SortedMap<K, V> {

    private static class ArraySet<T> extends ArrayList<T> implements Set<T> {
        private static final long serialVersionUID = 20151010L;
    }

    private final PresortedMap.ArraySet<Map.Entry<K, V>> set;
    private final Comparator<K> comparator;

    public PresortedMap() {
        this(null, new ArraySet<>());
    }

    public PresortedMap(final Comparator<K> comparator) {
        this(comparator, new ArraySet<>());
    }

    private PresortedMap(final Comparator<K> comparator, final PresortedMap.ArraySet<Map.Entry<K, V>> set) {
        this.comparator = comparator != null ? comparator : new ArraySetComparator<>(set);
        this.set = set;
    }

    @Override
    public Comparator<K> comparator() {
        return comparator;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return set;
    }

    @Override
    public K firstKey() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SortedMap<K, V> headMap(final Object toKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<K> keySet() {
        final Set<K> keySet = new ArraySet<>();
        for (final Map.Entry<K, V> entry : set) {
            keySet.add(entry.getKey());
        }
        return keySet;
    }

    @Override
    public K lastKey() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SortedMap<K, V> subMap(final Object fromKey, final Object toKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SortedMap<K, V> tailMap(final Object fromKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<V> values() {
        final Set<V> values = new ArraySet<>();
        for (final Map.Entry<K, V> entry : set) {
            values.add(entry.getValue());
        }
        return values;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsKey(final Object key) {
        return false;
    }

    @Override
    public boolean containsValue(final Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V get(final Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        return set.isEmpty();
    }

    @Override
    public V put(final K key, final V value) {
        set.add(new Map.Entry<K, V>() {

            @Override
            public K getKey() {
                return key;
            }

            @Override
            public V getValue() {
                return value;
            }

            @Override
            public V setValue(final V value) {
                throw new UnsupportedOperationException();
            }
        });
        return null;
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        for (final Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            @SuppressWarnings("unchecked")
            final Map.Entry<K, V> e = (Map.Entry<K, V>)entry;
            set.add(e);
        }
    }

    @Override
    public V remove(final Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return set.size();
    }

    private static class ArraySetComparator<K, V> implements Comparator<K> {

        private final ArrayList<Map.Entry<K, V>> list;
        private Map.Entry<K, V>[] array;

        ArraySetComparator(final ArrayList<Map.Entry<K, V>> list) {
            this.list = list;
        }

        @Override
        public int compare(final K object1, final K object2) {
            if (array == null || list.size() != array.length) {
                @SuppressWarnings("unchecked")
                final Map.Entry<K, V>[] a = new Map.Entry[list.size()];
                if (array != null) {
                    System.arraycopy(array, 0, a, 0, array.length);
                }
                for (int i = array == null ? 0 : array.length; i < list.size(); ++i) {
                    a[i] = list.get(i);
                }
                array = a;
            }
            int idx1 = Integer.MAX_VALUE, idx2 = Integer.MAX_VALUE;
            for (int i = 0; i < array.length && !(idx1 < Integer.MAX_VALUE && idx2 < Integer.MAX_VALUE); ++i) {
                if (idx1 == Integer.MAX_VALUE && object1 == array[i].getKey()) {
                    idx1 = i;
                }
                if (idx2 == Integer.MAX_VALUE && object2 == array[i].getKey()) {
                    idx2 = i;
                }
            }
            return idx1 - idx2;
        }
    }
}
