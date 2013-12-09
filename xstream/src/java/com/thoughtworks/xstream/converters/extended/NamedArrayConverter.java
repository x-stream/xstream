/*
 * Copyright (C) 2013 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 03. December 2013 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.extended;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.util.HierarchicalStreams;
import com.thoughtworks.xstream.core.util.Primitives;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * An array converter that uses predefined names for its items.
 * <p>
 * To be used as local converter.
 * </p>
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4.6
 */
public class NamedArrayConverter implements Converter {

    private final Class arrayType;
    private final String itemName;
    private final Mapper mapper;

    /**
     * Construct a NamedArrayConverter.
     * @param arrayType
     * @param mapper
     * @param itemName
     * @since 1.4.6
     */
    public NamedArrayConverter(final Class arrayType, final Mapper mapper, final String itemName) {
        if (!arrayType.isArray()) {
            throw new IllegalArgumentException(arrayType.getName() + " is not an array");
        }
        this.arrayType = arrayType;
        this.mapper = mapper;
        this.itemName = itemName;
    }

    public boolean canConvert(final Class type) {
        return type == arrayType;
    }

    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final int length = Array.getLength(source);
        for (int i = 0; i < length; ++i) {
            final Object item = Array.get(source, i);
            final Class itemType = item == null 
                    ? Mapper.Null.class 
                    : arrayType.getComponentType().isPrimitive()
                        ?  Primitives.unbox(item.getClass())
                        : item.getClass();
            ExtendedHierarchicalStreamWriterHelper.startNode(writer, itemName, itemType);
            if (!itemType.equals(arrayType.getComponentType())) {
                final String attributeName = mapper.aliasForSystemAttribute("class");
                if (attributeName != null) {
                    writer.addAttribute(attributeName, mapper.serializedClass(itemType));
                }
            }
            if (item != null) {
                context.convertAnother(item);
            }
            writer.endNode();
        }
    }

    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final List list = new ArrayList();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            final Object item;
            final String className = HierarchicalStreams.readClassAttribute(reader, mapper);
            final Class itemType = className == null ? arrayType.getComponentType() : mapper.realClass(className);
            if (Mapper.Null.class.equals(itemType)) {
                item = null;
            } else {
                item = context.convertAnother(null, itemType);
            }
            list.add(item);
            reader.moveUp();
        }
        final Object array = Array.newInstance(arrayType.getComponentType(), list.size());
        for (int i = 0; i < list.size(); ++i) {
            Array.set(array, i, list.get(i));
        }
        return array;
    }

}
