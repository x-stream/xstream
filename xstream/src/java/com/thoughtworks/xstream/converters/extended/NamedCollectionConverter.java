/*
 * Copyright (C) 2013 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 19. September 2013 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.core.util.HierarchicalStreams;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * A collection converter that uses predefined names for its items.
 * <p>
 * To be used as local converter. Note, suppress the usage of the implicit type argument, if
 * registered with annotation.
 * </p>
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4.5
 */
public class NamedCollectionConverter extends CollectionConverter {

    private final String name;
    private final Class type;

    /**
     * Constructs a NamedCollectionConverter.
     * 
     * @param mapper the mapper
     * @param itemName the name of the items
     * @param itemType the base type of the items
     * @since 1.4.5
     */
    public NamedCollectionConverter(Mapper mapper, String itemName, Class itemType) {
        this(null, mapper, itemName, itemType);
    }

    /**
     * Constructs a NamedCollectionConverter handling an explicit Collection type.
     * 
     * @param type the Collection type to handle
     * @param mapper the mapper
     * @param itemName the name of the items
     * @param itemType the base type of the items
     * @since 1.4.5
     */
    public NamedCollectionConverter(Class type, Mapper mapper, String itemName, Class itemType) {
        super(mapper, type);
        this.name = itemName;
        this.type = itemType;
    }

    protected void writeItem(Object item, MarshallingContext context, HierarchicalStreamWriter writer) {
        Class itemType = item == null ? Mapper.Null.class : item.getClass();
        ExtendedHierarchicalStreamWriterHelper.startNode(writer, name, itemType);
        if (!itemType.equals(type)) {
            String attributeName = mapper().aliasForSystemAttribute("class");
            if (attributeName != null) {
                writer.addAttribute(attributeName, mapper().serializedClass(itemType));
            }
        }
        if (item != null) {
            context.convertAnother(item);
        }
        writer.endNode();
    }

    protected Object readItem(HierarchicalStreamReader reader, UnmarshallingContext context, Object current) {
        String className = HierarchicalStreams.readClassAttribute(reader, mapper());
        Class itemType = className == null ? type : mapper().realClass(className);
        if (Mapper.Null.class.equals(itemType)) {
            return null;
        } else {
            return context.convertAnother(current, itemType);
        }
    }
}
