/*
 * Copyright (C) 2003, 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2010, 2011, 2013, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 01. October 2003 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Vector;

/**
 * Converts most common Collections (Lists and Sets) to XML, specifying a nested
 * element for each item.
 *
 * <p>Supports java.util.ArrayList, java.util.HashSet,
 * java.util.LinkedList, java.util.Vector and java.util.LinkedHashSet.</p>
 *
 * @author Joe Walnes
 */
public class CollectionConverter extends AbstractCollectionConverter {

    private final Class type;

    public CollectionConverter(Mapper mapper) {
        this(mapper, null);
    }

    /**
     * Construct a CollectionConverter for a special Collection type.
     * @param mapper the mapper
     * @param type the Collection type to handle
     * @since 1.4.5
     */
    public CollectionConverter(Mapper mapper, Class type) {
        super(mapper);
        this.type = type;
        if (type != null && !Collection.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException(type + " not of type " + Collection.class);
        }
    }

    public boolean canConvert(Class type) {
        if (this.type != null) {
            return type.equals(this.type);
        }
        return type.equals(ArrayList.class)
            || type.equals(HashSet.class)
            || type.equals(LinkedList.class)
            || type.equals(Vector.class) 
            || type.equals(LinkedHashSet.class);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Collection collection = (Collection) source;
        for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
            Object item = iterator.next();
            writeCompleteItem(item, context, writer);
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Collection collection = (Collection) createCollection(context.getRequiredType());
        populateCollection(reader, context, collection);
        return collection;
    }

    protected void populateCollection(HierarchicalStreamReader reader, UnmarshallingContext context, Collection collection) {
        populateCollection(reader, context, collection, collection);
    }

    protected void populateCollection(HierarchicalStreamReader reader, UnmarshallingContext context, Collection collection, Collection target) {
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            addCurrentElementToCollection(reader, context, collection, target);
            reader.moveUp();
        }
    }

    protected void addCurrentElementToCollection(HierarchicalStreamReader reader, UnmarshallingContext context,
        Collection collection, Collection target) {
        final Object item = readItem(reader, context, collection); // call readBareItem when deprecated method is removed
        target.add(item);
    }

    protected Object createCollection(Class type) {
        return super.createCollection(this.type != null ? this.type : type);
    }
}
