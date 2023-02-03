/*
 * Copyright (C) 2003, 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2010, 2011, 2013, 2014, 2018, 2021 XStream Committers.
 * Copyright (C) 2006, 2007, 2010, 2011, 2013, 2018, 2021 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 01. October 2003 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Vector;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.SecurityUtils;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Converts most common Collections (Lists and Sets), specifying a nested element for each item.
 * <p>
 * Supports {@link ArrayList}, {@link HashSet}, {@link LinkedList}, {@link Vector} and {@link LinkedHashSet}.
 * </p>
 * 
 * @author Joe Walnes
 * @see com.thoughtworks.xstream.converters.extended.NamedCollectionConverter
 */
public class CollectionConverter extends AbstractCollectionConverter {

    private final Class<? extends Collection<?>> type;

    public CollectionConverter(final Mapper mapper) {
        this(mapper, null);
    }

    /**
     * Construct a CollectionConverter for a special Collection type.
     * 
     * @param mapper the mapper
     * @param type the Collection type to handle
     * @since 1.4.5
     */
    public CollectionConverter(final Mapper mapper, @SuppressWarnings("rawtypes") final Class<? extends Collection> type) {
        super(mapper);
        @SuppressWarnings("unchecked")
        final Class<? extends Collection<?>> checkedType = (Class<? extends Collection<?>>)type;
        this.type = checkedType;
        if (type != null && !Collection.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException(type + " not of type " + Collection.class);
        }
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        if (this.type != null) {
            return type.equals(this.type);
        }
        return type.equals(ArrayList.class)
            || type.equals(HashSet.class)
            || type.equals(LinkedList.class)
            || type.equals(Vector.class)
            || type.equals(LinkedHashSet.class);
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final Collection<?> collection = (Collection<?>)source;
        for (final Object item : collection) {
            writeCompleteItem(item, context, writer);
        }
    }

    @Override
    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final Class<?> collectionType = context.getRequiredType();
        final Collection<?> collection = createCollection(collectionType);
        populateCollection(reader, context, collection);
        return collection;
    }

    protected void populateCollection(final HierarchicalStreamReader reader, final UnmarshallingContext context,
            final Collection<?> collection) {
        populateCollection(reader, context, collection, collection);
    }

    protected void populateCollection(final HierarchicalStreamReader reader, final UnmarshallingContext context,
            final Collection<?> collection, final Collection<?> target) {
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            addCurrentElementToCollection(reader, context, collection, target);
            reader.moveUp();
        }
    }

    protected void addCurrentElementToCollection(final HierarchicalStreamReader reader,
            final UnmarshallingContext context, final Collection<?> collection, final Collection<?> target) {
        @SuppressWarnings("deprecation")
        final Object item = readItem(reader, context, collection); // call readBareItem when deprecated method is removed
        @SuppressWarnings("unchecked")
        final Collection<Object> targetCollection = (Collection<Object>)target;
        final long now = System.currentTimeMillis();
        targetCollection.add(item);
        SecurityUtils.checkForCollectionDoSAttack(context, now);
    }

    @Override
    protected Collection<?> createCollection(final Class<?> type) {
        return (Collection<?>)super.createCollection(this.type != null ? this.type : type);
    }
}
