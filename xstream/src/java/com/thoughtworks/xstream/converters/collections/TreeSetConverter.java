/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2010, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 08. May 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.core.util.PresortedSet;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import java.lang.reflect.Field;
import java.util.AbstractList;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Converts a java.util.TreeSet to XML, and serializes
 * the associated java.util.Comparator. The converter
 * assumes that the elements in the XML are already sorted 
 * according the comparator.
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class TreeSetConverter extends CollectionConverter {
    private transient TreeMapConverter treeMapConverter;  
    private final static Field sortedMapField;
    static {
        Field smField = null;
        if (!JVM.hasOptimizedTreeSetAddAll()) {
            try {
                Field[] fields = TreeSet.class.getDeclaredFields();
                for (int i = 0; i < fields.length; i++ ) {
                    if (SortedMap.class.isAssignableFrom(fields[i].getType())) {
                        // take the fist member assignable to type "SortedMap"
                        smField = fields[i];
                        smField.setAccessible(true);
                        break;
                    }
                }
                if (smField == null) {
                    throw new ExceptionInInitializerError("Cannot detect field of backing map of TreeSet");
                }
    
            } catch (SecurityException ex) {
                // ignore, no access possible with current SecurityManager
            }
        }
        sortedMapField = smField;
    }

    public TreeSetConverter(Mapper mapper) {
        super(mapper);
        readResolve();
    }

    public boolean canConvert(Class type) {
        return type.equals(TreeSet.class);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        TreeSet treeSet = (TreeSet) source;
        treeMapConverter.marshalComparator(treeSet.comparator(), writer, context);
        super.marshal(source, writer, context);
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        TreeSet result = null;
        final TreeMap treeMap;
        Comparator unmarshalledComparator = treeMapConverter.unmarshalComparator(reader, context, null);
        boolean inFirstElement = unmarshalledComparator instanceof Mapper.Null;
        Comparator comparator = inFirstElement ? null : unmarshalledComparator;
        if (sortedMapField != null) {
            TreeSet possibleResult = comparator == null ? new TreeSet() : new TreeSet(comparator);
            Object backingMap = null;
            try {
                backingMap = sortedMapField.get(possibleResult);
            } catch (IllegalAccessException e) {
                throw new ConversionException("Cannot get backing map of TreeSet", e);
            }
            if (backingMap instanceof TreeMap) {
                treeMap = (TreeMap)backingMap;
                result = possibleResult;
            } else {
                treeMap = null;
            }
        } else {
            treeMap = null;
        }
        if (treeMap == null) {
            final PresortedSet set = new PresortedSet(comparator);
            result = comparator == null ? new TreeSet() : new TreeSet(comparator);
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

            protected void populateMap(HierarchicalStreamReader reader,
                UnmarshallingContext context, Map map, final Map target) {
                populateCollection(reader, context, new AbstractList() {
                    public boolean add(Object object) {
                        return target.put(object, object) != null;
                    }

                    public Object get(int location) {
                        return null;
                    }

                    public int size() {
                        return target.size();
                    }
                });
            }

            protected void putCurrentEntryIntoMap(HierarchicalStreamReader reader, UnmarshallingContext context,
                Map map, Map target) {
                Object key = readItem(reader, context, map);
                target.put(key, key);
            }
        };
        return this;
    }
}
