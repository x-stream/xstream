/*
 * Copyright (C) 2003, 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2013, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 26. September 2003 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.util.HierarchicalStreams;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Base helper class for converters that need to handle collections of items (arrays, Lists, Maps, etc).
 * <p>
 * Typically, subclasses of this will converter the outer structure of the collection, loop through the contents and
 * call readItem() or writeItem() for each item.
 * </p>
 * 
 * @author Joe Walnes
 */
public abstract class AbstractCollectionConverter implements Converter {

    private final Mapper mapper;

    @Override
    public abstract boolean canConvert(Class<?> type);

    public AbstractCollectionConverter(final Mapper mapper) {
        this.mapper = mapper;
    }

    protected Mapper mapper() {
        return mapper;
    }

    @Override
    public abstract void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context);

    @Override
    public abstract Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context);

    protected void writeItem(final Object item, final MarshallingContext context, final HierarchicalStreamWriter writer) {
        // PUBLISHED API METHOD! If changing signature, ensure backwards compatibility.
        if (item == null) {
            // todo: this is duplicated in TreeMarshaller.start()
            final String name = mapper().serializedClass(null);
            ExtendedHierarchicalStreamWriterHelper.startNode(writer, name, Mapper.Null.class);
            writer.endNode();
        } else {
            final String name = mapper().serializedClass(item.getClass());
            ExtendedHierarchicalStreamWriterHelper.startNode(writer, name, item.getClass());
            context.convertAnother(item);
            writer.endNode();
        }
    }

    protected Object readItem(final HierarchicalStreamReader reader, final UnmarshallingContext context,
            final Object current) {
        final Class<?> type = HierarchicalStreams.readClassType(reader, mapper());
        return context.convertAnother(current, type);
    }

    protected Object createCollection(final Class<?> type) {
        final Class<?> defaultType = mapper().defaultImplementationOf(type);
        try {
            return defaultType.newInstance();
        } catch (final InstantiationException e) {
            throw new ConversionException("Cannot instantiate " + defaultType.getName(), e);
        } catch (final IllegalAccessException e) {
            throw new ConversionException("Cannot instantiate " + defaultType.getName(), e);
        }
    }
}
