/*
 * Copyright (C) 2013, 2014, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 19. September 2013 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.extended;

import java.util.Collection;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.core.util.HierarchicalStreams;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * A collection converter that uses predefined names for its items.
 * <p>
 * To be used as local converter. Note, suppress the usage of the implicit type argument, if registered with annotation.
 * </p>
 *
 * @author J&ouml;rg Schaible
 * @since 1.4.5
 */
public class NamedCollectionConverter extends CollectionConverter {

    private final String name;
    private final Class<?> type;

    /**
     * Constructs a NamedCollectionConverter.
     *
     * @param mapper the mapper
     * @param itemName the name of the items
     * @param itemType the base type of the items
     * @since 1.4.5
     */
    public NamedCollectionConverter(final Mapper mapper, final String itemName, final Class<?> itemType) {
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
    public NamedCollectionConverter(
            @SuppressWarnings("rawtypes") final Class<? extends Collection> type, final Mapper mapper,
            final String itemName, final Class<?> itemType) {
        super(mapper, type);
        name = itemName;
        this.type = itemType;
    }

    @Override
    protected void writeCompleteItem(final Object item, final MarshallingContext context,
            final HierarchicalStreamWriter writer) {
        writeItem(item, context, writer);
    }

    /**
     * @deprecated As of 1.4.11 use {@link #writeCompleteItem(Object, MarshallingContext, HierarchicalStreamWriter)}
     *             instead.
     */
    @Deprecated
    @Override
    protected void writeItem(final Object item, final MarshallingContext context,
            final HierarchicalStreamWriter writer) {
        final Class<?> itemType = item == null ? Mapper.Null.class : item.getClass();
        writer.startNode(name, itemType);
        if (!itemType.equals(type)) {
            final String attributeName = mapper().aliasForSystemAttribute("class");
            if (attributeName != null) {
                writer.addAttribute(attributeName, mapper().serializedClass(itemType));
            }
        }
        if (item != null) {
            context.convertAnother(item);
        }
        writer.endNode();
    }

    @Override
    protected Object readBareItem(final HierarchicalStreamReader reader, final UnmarshallingContext context,
            final Object current) {
        final String className = HierarchicalStreams.readClassAttribute(reader, mapper());
        final Class<?> itemType = className == null ? type : mapper().realClass(className);
        if (Mapper.Null.class.equals(itemType)) {
            return null;
        } else {
            return context.convertAnother(current, itemType);
        }
    }
}
