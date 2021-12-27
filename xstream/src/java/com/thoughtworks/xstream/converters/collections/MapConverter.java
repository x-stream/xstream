/*
 * Copyright (C) 2003, 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2010, 2011, 2012, 2013, 2014, 2018, 2021 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 26. September 2003 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.collections;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.SecurityUtils;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Converts a {@link Map}, specifying an 'entry' element with 'key' and 'value' children.
 * <p>
 * Note: 'key' and 'value' is not the name of the generated tag. The children are serialized as normal elements and the
 * implementation expects them in the order 'key'/'value'.
 * </p>
 * <p>
 * Supports {@link HashMap}, {@link Hashtable}, {@link LinkedHashMap}, {@link ConcurrentHashMap} and
 * sun.font.AttributeMap.
 * </p>
 *
 * @see com.thoughtworks.xstream.converters.extended.NamedMapConverter
 * @author Joe Walnes
 */
public class MapConverter extends AbstractCollectionConverter {

    private final Class<? extends Map<?, ?>> type;

    public MapConverter(final Mapper mapper) {
        this(mapper, null);
    }

    /**
     * Construct a MapConverter for a special Map type.
     *
     * @param mapper the mapper
     * @param type the type to handle
     * @since 1.4.5
     */
    public MapConverter(final Mapper mapper, @SuppressWarnings("rawtypes") final Class<? extends Map> type) {
        super(mapper);
        @SuppressWarnings("unchecked")
        final Class<? extends Map<?, ?>> checkedType = (Class<? extends Map<?, ?>>)type;
        this.type = checkedType;
        if (type != null && !Map.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException(type + " not of type " + Map.class);
        }
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        if (this.type != null) {
            return type.equals(this.type);
        }
        return type.equals(HashMap.class)
            || type.equals(Hashtable.class)
            || type.equals(LinkedHashMap.class)
            || type.equals(ConcurrentHashMap.class)
            || type.getName().equals("sun.font.AttributeMap") // Used by java.awt.Font since JDK 6
        ;
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final Map<?, ?> map = (Map<?, ?>)source;
        final String entryName = mapper().serializedClass(Map.Entry.class);
        for (final Map.Entry<?, ?> entry : map.entrySet()) {
            writer.startNode(entryName, entry.getClass());

            writeCompleteItem(entry.getKey(), context, writer);
            writeCompleteItem(entry.getValue(), context, writer);

            writer.endNode();
        }
    }

    @Override
    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final Class<?> requiredType = context.getRequiredType();
        final Map<?, ?> map = createCollection(requiredType);
        populateMap(reader, context, map);
        return map;
    }

    protected void populateMap(final HierarchicalStreamReader reader, final UnmarshallingContext context,
            final Map<?, ?> map) {
        populateMap(reader, context, map, map);
    }

    protected void populateMap(final HierarchicalStreamReader reader, final UnmarshallingContext context,
            final Map<?, ?> map, final Map<?, ?> target) {
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            putCurrentEntryIntoMap(reader, context, map, target);
            reader.moveUp();
        }
    }

    protected void putCurrentEntryIntoMap(final HierarchicalStreamReader reader, final UnmarshallingContext context,
            final Map<?, ?> map, final Map<?, ?> target) {
        final Object key = readCompleteItem(reader, context, map);
        final Object value = readCompleteItem(reader, context, map);

        @SuppressWarnings("unchecked")
        final Map<Object, Object> targetMap = (Map<Object, Object>)target;
        final long now = System.currentTimeMillis();
        targetMap.put(key, value);
        SecurityUtils.checkForCollectionDoSAttack(context, now);
    }

    @Override
    protected Map<?, ?> createCollection(final Class<?> type) {
        return (Map<?, ?>)super.createCollection(this.type != null ? this.type : type);
    }
}
