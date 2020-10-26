/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.thoughtworks.xstream.core.util;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;


/**
 * A HashMap implementation with weak references values and by default for the key. When the value is garbage collected,
 * the key will also vanish from the map.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public class WeakCache<K, V> extends AbstractMap<K, V> {

    private final Map<K, Reference<V>> map;

    /**
     * Construct a WeakCache with weak keys.
     * <p>
     * Note, that the internally used WeakHashMap is <b>not</b> thread-safe.
     * </p>
     * 
     * @since 1.4
     */
    public WeakCache() {
        this(new WeakHashMap<K, Reference<V>>());
    }

    /**
     * Construct a WeakCache.
     * 
     * @param map the map to use
     * @since 1.4
     */
    public WeakCache(final Map<K, Reference<V>> map) {
        this.map = map;
    }

    @Override
    public V get(final Object key) {
        final Reference<V> reference = map.get(key);
        return reference != null ? reference.get() : null;
    }

    @Override
    public V put(final K key, final V value) {
        final Reference<V> ref = map.put(key, createReference(value));
        return ref == null ? null : ref.get();
    }

    @Override
    public V remove(final Object key) {
        final Reference<V> ref = map.remove(key);
        return ref == null ? null : ref.get();
    }

    protected Reference<V> createReference(final V value) {
        return new WeakReference<>(value);
    }

    @Override
    public boolean containsValue(final Object value) {
        final Boolean result = (Boolean)iterate(new Visitor() {

            @Override
            public Object visit(final Object element) {
                return element.equals(value) ? Boolean.TRUE : null;
            }

        }, Visitor.Type.value);
        return result == Boolean.TRUE;
    }

    @Override
    public int size() {
        if (map.isEmpty()) {
            return 0;
        }
        final int i[] = new int[1];
        i[0] = 0;
        iterate(new Visitor() {

            @Override
            public Object visit(final Object element) {
                ++i[0];
                return null;
            }

        }, Visitor.Type.key);
        return i[0];
    }

    @Override
    public Collection<V> values() {
        final Collection<V> collection = new ArrayList<>();
        if (!map.isEmpty()) {
            iterate(new Visitor() {

                @Override
                public Object visit(final Object element) {
                    @SuppressWarnings("unchecked")
                    final V value = (V)element;
                    collection.add(value);
                    return null;
                }

            }, Visitor.Type.value);
        }
        return collection;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        final Set<Map.Entry<K, V>> set = new HashSet<>();
        if (!map.isEmpty()) {
            iterate(new Visitor() {

                @Override
                public Object visit(final Object element) {
                    @SuppressWarnings("unchecked")
                    final Map.Entry<K, Reference<V>> entry = (Map.Entry<K, Reference<V>>)element;
                    set.add(new Map.Entry<K, V>() {

                        @Override
                        public K getKey() {
                            return entry.getKey();
                        }

                        @Override
                        public V getValue() {
                            return entry.getValue().get();
                        }

                        @Override
                        public V setValue(final V value) {
                            final Reference<V> reference = entry.setValue(createReference(value));
                            return reference != null ? reference.get() : null;
                        }

                    });
                    return null;
                }

            }, Visitor.Type.entry);
        }
        return set;
    }

    private Object iterate(final Visitor visitor, final Visitor.Type type) {
        Object result = null;
        for (final Iterator<Map.Entry<K, Reference<V>>> iter = map.entrySet().iterator(); result == null
            && iter.hasNext();) {
            final Map.Entry<K, Reference<V>> entry = iter.next();
            final Reference<V> reference = entry.getValue();
            final V element = reference.get();
            if (element == null) {
                iter.remove();
                continue;
            }
            switch (type) {
            case value:
                result = visitor.visit(element);
                break;
            case key:
                result = visitor.visit(entry.getKey());
                break;
            case entry:
                result = visitor.visit(entry);
                break;
            }

        }
        return result;
    }
    
    private interface Visitor {
        enum Type {key, value, entry};
        Object visit(Object element);
    }

    @Override
    public boolean containsKey(final Object key) {
        return map.containsKey(key);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public boolean equals(final Object o) {
        return map.equals(o);
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
