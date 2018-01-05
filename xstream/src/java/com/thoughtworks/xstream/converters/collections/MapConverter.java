/*
 * Copyright (C) 2003, 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2010, 2011, 2012, 2013, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 26. September 2003 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 * Converts a java.util.Map to XML, specifying an 'entry'
 * element with 'key' and 'value' children.
 * <p>Note: 'key' and 'value' is not the name of the generated tag. The
 * children are serialized as normal elements and the implementation expects
 * them in the order 'key'/'value'.</p>
 * <p>Supports java.util.HashMap, java.util.Hashtable,
 * java.util.LinkedHashMap and java.util.concurrent.ConcurrentHashMap.</p>
 *
 * @author Joe Walnes
 */
public class MapConverter extends AbstractCollectionConverter {

    private final Class type;

    public MapConverter(Mapper mapper) {
        this(mapper, null);
    }

    /**
     * Construct a MapConverter for a special Map type.
     * @param mapper the mapper
     * @param type the type to handle
     * @since 1.4.5
     */
    public MapConverter(Mapper mapper, Class type) {
        super(mapper);
        this.type = type;
        if (type != null && !Map.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException(type + " not of type " + Map.class);
        }
    }

    public boolean canConvert(Class type) {
        if (this.type != null) {
            return type.equals(this.type);
        }
        return type.equals(HashMap.class)
            || type.equals(Hashtable.class)
            || type.getName().equals("java.util.LinkedHashMap")
            || type.getName().equals("java.util.concurrent.ConcurrentHashMap")
            || type.getName().equals("sun.font.AttributeMap") // Used by java.awt.Font in JDK 6
            ;
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Map map = (Map) source;
        String entryName = mapper().serializedClass(Map.Entry.class);
        for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            ExtendedHierarchicalStreamWriterHelper.startNode(writer, entryName, entry.getClass());

            writeCompleteItem(entry.getKey(), context, writer);
            writeCompleteItem(entry.getValue(), context, writer);

            writer.endNode();
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Map map = (Map) createCollection(context.getRequiredType());
        populateMap(reader, context, map);
        return map;
    }

    protected void populateMap(HierarchicalStreamReader reader, UnmarshallingContext context, Map map) {
        populateMap(reader, context, map, map);
    }

    protected void populateMap(HierarchicalStreamReader reader, UnmarshallingContext context, Map map, Map target) {
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            putCurrentEntryIntoMap(reader, context, map, target);
            reader.moveUp();
        }
    }

    protected void putCurrentEntryIntoMap(HierarchicalStreamReader reader, UnmarshallingContext context,
        Map map, Map target) {
        final Object key = readCompleteItem(reader, context, map);
        final Object value = readCompleteItem(reader, context, map);
        target.put(key, value);
    }

    protected Object createCollection(Class type) {
        return super.createCollection(this.type != null ? this.type : type);
    }
}
