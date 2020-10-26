/*
 * Copyright (C) 2003, 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2014, 2015, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 03. October 2003 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.collections;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Converts an array of objects or primitives, using a nested child element for each item.
 * 
 * @author Joe Walnes
 * @see com.thoughtworks.xstream.converters.extended.NamedArrayConverter
 */
public class ArrayConverter extends AbstractCollectionConverter {

    public ArrayConverter(final Mapper mapper) {
        super(mapper);
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return type != null && type.isArray();
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final int length = Array.getLength(source);
        for (int i = 0; i < length; i++) {
            final Object item = Array.get(source, i);
            writeCompleteItem(item, context, writer);
        }

    }

    @Override
    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        // read the items from xml into a list (the array size is not known until all items have been read)
        final List<Object> items = new ArrayList<>();
        while (reader.hasMoreChildren()) {
            final Object item = readCompleteItem(reader, context, null); // TODO: arg, what should replace null?
            items.add(item);
        }
        // now convertAnother the list into an array
        final Object array = Array.newInstance(context.getRequiredType().getComponentType(), items.size());
        int i = 0;
        for (final Object item : items) {
            Array.set(array, i++, item);
        }
        return array;
    }
}
