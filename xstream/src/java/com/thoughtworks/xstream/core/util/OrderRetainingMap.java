/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2013, 2014, 2015 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 06. February 2005 by Joe Walnes
 */
package com.thoughtworks.xstream.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @deprecated As of 1.4.8 use {@link java.util.LinkedHashMap}
 */
@Deprecated
public class OrderRetainingMap<K, V> extends HashMap<K, V> {
    private static final long serialVersionUID = 20151010L;
    private final ArraySet<K> keyOrder = new ArraySet<>();
    private final List<V> valueOrder = new ArrayList<>();

    public OrderRetainingMap() {
        super();
    }

    public OrderRetainingMap(final Map<K, V> m) {
        super();
        putAll(m);
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        for (final Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public V put(final K key, final V value) {
        final int idx = keyOrder.lastIndexOf(key);
        if (idx < 0) {
            keyOrder.add(key);
            valueOrder.add(value);
        } else {
            valueOrder.set(idx, value);
        }
        return super.put(key, value);
    }

    @Override
    public V remove(final Object key) {
        final int idx = keyOrder.lastIndexOf(key);
        if (idx != 0) {
            keyOrder.remove(idx);
            valueOrder.remove(idx);
        }
        return super.remove(key);
    }

    @Override
    public void clear() {
        keyOrder.clear();
        valueOrder.clear();
        super.clear();
    }

    @Override
    public Collection<V> values() {
        return Collections.unmodifiableList(valueOrder);
    }

    @Override
    public Set<K> keySet() {
        return Collections.unmodifiableSet(keyOrder);
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        @SuppressWarnings("unchecked")
        final Map.Entry<K, V>[] entries = new Map.Entry[size()];
        for (final Map.Entry<K, V> entry : super.entrySet()) {
            entries[keyOrder.indexOf(entry.getKey())] = entry;
        }
        final Set<Map.Entry<K, V>> set = new ArraySet<>();
        set.addAll(Arrays.asList(entries));
        return Collections.unmodifiableSet(set);
    }

    private static class ArraySet<K> extends ArrayList<K> implements Set<K> {
        private static final long serialVersionUID = 20151010L;
    }
}
