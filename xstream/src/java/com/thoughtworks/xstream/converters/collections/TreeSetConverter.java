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

package com.thoughtworks.xstream.converters.collections;

import java.lang.reflect.Field;
import java.util.AbstractList;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.core.util.Fields;
import com.thoughtworks.xstream.core.util.PresortedSet;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Converts a {@link TreeSet} to XML, and serializes the associated {@link Comparator}.
 * <p>
 * The converter assumes that the elements in the XML are already sorted according the comparator.
 * </p>
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class TreeSetConverter extends CollectionConverter {
    private transient TreeMapConverter treeMapConverter;

    public TreeSetConverter(final Mapper mapper) {
        super(mapper, TreeSet.class);
        readResolve();
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final SortedSet<?> sortedSet = (SortedSet<?>)source;
        treeMapConverter.marshalComparator(sortedSet.comparator(), writer, context);
        super.marshal(source, writer, context);
    }

    @Override
    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        TreeSet<Object> result = null;
        final TreeMap<?, ?> treeMap;
        final Comparator<?> unmarshalledComparator = treeMapConverter.unmarshalComparator(reader, context, null);
        final boolean inFirstElement = unmarshalledComparator instanceof Mapper.Null;
        @SuppressWarnings("unchecked")
        final Comparator<Object> comparator = inFirstElement ? null : (Comparator<Object>)unmarshalledComparator;
        if (Reflections.sortedMapField != null) {
            final TreeSet<Object> possibleResult = comparator == null ? new TreeSet<>() : new TreeSet<>(comparator);
            Object backingMap = null;
            try {
                backingMap = Reflections.sortedMapField.get(possibleResult);
            } catch (final IllegalAccessException e) {
                throw new ObjectAccessException("Cannot get backing map of TreeSet", e);
            }
            if (backingMap instanceof TreeMap) {
                treeMap = (TreeMap<?, ?>)backingMap;
                result = possibleResult;
            } else {
                treeMap = null;
            }
        } else {
            treeMap = null;
        }
        if (treeMap == null) {
            final PresortedSet<Object> set = new PresortedSet<>(comparator);
            result = comparator == null ? new TreeSet<>() : new TreeSet<>(comparator);
            if (inFirstElement) {
                // we are already within the first element
                addCurrentElementToCollection(reader, context, result, set);
                reader.moveUp();
            }
            populateCollection(reader, context, result, set);
            if (set.size() > 0) {
                result.addAll(set); // comparator will not be called if internally optimized
            }
        } else {
            treeMapConverter.populateTreeMap(reader, context, treeMap, unmarshalledComparator);
        }
        return result;
    }

    private Object readResolve() {
        treeMapConverter = new TreeMapConverter(mapper()) {

            @Override
            protected void populateMap(final HierarchicalStreamReader reader, final UnmarshallingContext context,
                    final Map<?, ?> map, final Map<?, ?> target) {
                populateCollection(reader, context, new AbstractList<Object>() {
                    @Override
                    public boolean add(final Object object) {
                        @SuppressWarnings("unchecked")
                        final Map<Object, Object> collectionTarget = (Map<Object, Object>)target;
                        return collectionTarget
                            .put(object, Reflections.constantValue != null
                                ? Reflections.constantValue
                                : object) != null;
                    }

                    @Override
                    public Object get(final int location) {
                        return null;
                    }

                    @Override
                    public int size() {
                        return target.size();
                    }
                });
            }

            @Override
            protected void putCurrentEntryIntoMap(final HierarchicalStreamReader reader,
                    final UnmarshallingContext context, final Map<?, ?> map, final Map<?, ?> target) {
                // call readBareItem when deprecated method is removed
                @SuppressWarnings("deprecation")
                final Object key = readItem(reader, context, map);
                @SuppressWarnings("unchecked")
                final Map<Object, Object> checkedTarget = (Map<Object, Object>)target;
                checkedTarget.put(key, key);
            }
        };
        return this;
    }

    private static class Reflections {

        private final static Field sortedMapField;
        private final static Object constantValue;

        static {
            Object value = null;
            sortedMapField = JVM.hasOptimizedTreeSetAddAll()
                ? Fields.locate(TreeSet.class, SortedMap.class, false)
                : null;
            if (sortedMapField != null) {
                final TreeSet<String> set = new TreeSet<>();
                set.add("1");
                set.add("2");

                Map<String, Object> backingMap = null;
                try {
                    @SuppressWarnings("unchecked")
                    final Map<String, Object> map = (Map<String, Object>)sortedMapField.get(set);
                    backingMap = map;
                } catch (final IllegalAccessException e) {
                    // give up;
                }
                if (backingMap != null) {
                    final Object[] values = backingMap.values().toArray();
                    if (values[0] == values[1]) {
                        value = values[0];
                    }
                }
            } else {
                final Field valueField = Fields.locate(TreeSet.class, Object.class, true);
                if (valueField != null) {
                    try {
                        value = valueField.get(null);
                    } catch (final IllegalAccessException e) {
                        // give up;
                    }
                }
            }
            constantValue = value;
        }
    }
}
