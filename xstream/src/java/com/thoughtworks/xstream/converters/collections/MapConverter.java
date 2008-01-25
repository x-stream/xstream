/*
 * Copyright (C) 2003, 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008 XStream Committers.
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
 * <p>Supports java.util.HashMap, java.util.Hashtable and
 * java.util.LinkedHashMap.</p>
 *
 * @author Joe Walnes
 */
public class MapConverter extends AbstractCollectionConverter {

    public MapConverter(Mapper mapper) {
        super(mapper);
    }

    public boolean canConvert(Class type) {
        return type.equals(HashMap.class)
                || type.equals(Hashtable.class)
                || type.getName().equals("java.util.LinkedHashMap")
                || type.getName().equals("sun.font.AttributeMap") // Used by java.awt.Font in JDK 6
                ;
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Map map = (Map) source;
        for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            ExtendedHierarchicalStreamWriterHelper.startNode(writer, mapper().serializedClass(Map.Entry.class), Map.Entry.class);

            writeItem(entry.getKey(), context, writer);
            writeItem(entry.getValue(), context, writer);

            writer.endNode();
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Map map = (Map) createCollection(context.getRequiredType());
        populateMap(reader, context, map);
        return map;
    }

    protected void populateMap(HierarchicalStreamReader reader, UnmarshallingContext context, Map map) {
        while (reader.hasMoreChildren()) {
            reader.moveDown();

            reader.moveDown();
            Object key = readItem(reader, context, map);
            reader.moveUp();

            reader.moveDown();
            Object value = readItem(reader, context, map);
            reader.moveUp();

            map.put(key, value);

            reader.moveUp();
        }
    }

}
