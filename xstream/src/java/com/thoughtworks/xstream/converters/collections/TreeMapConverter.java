/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2010 XStream Committers.
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
import com.thoughtworks.xstream.core.util.PresortedMap;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Converts a java.util.TreeMap to XML, and serializes
 * the associated java.util.Comparator. The converter
 * assumes that the entries in the XML are already sorted 
 * according the comparator.
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class TreeMapConverter extends MapConverter {
    
    private final static Field comparatorField;
    static {
        Field cmpField = null;
        try {
            Field[] fields = TreeMap.class.getDeclaredFields();
            for (int i = 0; i < fields.length; i++ ) {
                if (fields[i].getType() == Comparator.class) {
                    // take the fist member of type "Comparator"
                    cmpField = fields[i];
                    cmpField.setAccessible(true);
                    break;
                }
            }
            if (cmpField == null) {
                throw new ExceptionInInitializerError("Cannot detect comparator field of TreeMap");
            }

        } catch (SecurityException ex) {
            // ignore, no access possible with current SecurityManager
        }
        comparatorField = cmpField;
    }

    public TreeMapConverter(Mapper mapper) {
        super(mapper);
    }

    public boolean canConvert(Class type) {
        return type.equals(TreeMap.class);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        TreeMap treeMap = (TreeMap) source;
        Comparator comparator = treeMap.comparator();
        if (comparator == null) {
            writer.startNode("no-comparator");
            writer.endNode();
        } else {
            writer.startNode("comparator");
            writer.addAttribute("class", mapper().serializedClass(comparator.getClass()));
            context.convertAnother(comparator);
            writer.endNode();
        }
        super.marshal(source, writer, context);
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        reader.moveDown();
        SortedMap sortedMap;
        TreeMap result;
        final Comparator comparator;
        if (reader.getNodeName().equals("comparator")) {
            String comparatorClass = reader.getAttribute("class");
            sortedMap = comparatorField != null ? new PresortedMap() :  null;
            comparator = (Comparator) context.convertAnother(sortedMap, mapper().realClass(comparatorClass));
            if (sortedMap == null) {
                sortedMap = new PresortedMap(comparator);
            }
        } else if (reader.getNodeName().equals("no-comparator")) {
            comparator = null;
            sortedMap = new PresortedMap();
        } else {
            throw new ConversionException("TreeMap does not contain <comparator> element");
        }
        reader.moveUp();
        populateMap(reader, context, sortedMap);
        if (comparator == null || comparator == sortedMap.comparator()) {
            result = comparator == null ? new TreeMap() : new TreeMap(comparator);
            result.putAll(sortedMap); // internal optimization in *Sun* JDK will not call comparator
        } else {
            result = new TreeMap(sortedMap.comparator());
            result.putAll(sortedMap); // "sort" by index
            try {
                comparatorField.set(result, comparator); 
            } catch (final IllegalAccessException e) {
                throw new ConversionException("Cannot set comparator of TreeMap", e);
            }
        }
        return result;
    }
}
