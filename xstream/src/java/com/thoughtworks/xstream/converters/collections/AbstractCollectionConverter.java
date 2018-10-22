/*
 * Copyright (C) 2003, 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2013, 2016, 2018 XStream Committers.
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
import com.thoughtworks.xstream.converters.ErrorWritingException;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.core.util.HierarchicalStreams;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * Base helper class for converters that need to handle
 * collections of items (arrays, Lists, Maps, etc).
 *
 * <p>Typically, subclasses of this will converter the outer
 * structure of the collection, loop through the contents and
 * call readItem() or writeItem() for each item.</p>
 *
 * @author Joe Walnes
 */
public abstract class AbstractCollectionConverter implements Converter {

    private final Mapper mapper;

    public abstract boolean canConvert(Class type);

    public AbstractCollectionConverter(Mapper mapper) {
        this.mapper = mapper;
    }

    protected Mapper mapper() {
        return mapper;
    }

    public abstract void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context);

    public abstract Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context);

    /**
     * @deprecated As of 1.4.11 use {@link #writeCompleteItem(Object, MarshallingContext, HierarchicalStreamWriter)}
     *             instead.
     */
    protected void writeItem(Object item, MarshallingContext context, HierarchicalStreamWriter writer) {
        // PUBLISHED API METHOD! If changing signature, ensure backwards compatibility.
        if (item == null) {
            // todo: this is duplicated in TreeMarshaller.start()
            writeNullItem(context, writer);
        } else {
            String name = mapper().serializedClass(item.getClass());
            ExtendedHierarchicalStreamWriterHelper.startNode(writer, name, item.getClass());
            writeBareItem(item, context, writer);
            writer.endNode();
        }
    }

    /**
     * Write an item of the collection into the writer including surrounding tags.
     *
     * @param item the item to write
     * @param context the current marshalling context
     * @param writer the target writer
     * @since 1.4.11
     */
    protected void writeCompleteItem(final Object item, final MarshallingContext context,
            final HierarchicalStreamWriter writer) {
        writeItem(item, context, writer);
    }

    /**
     * Write the bare item of the collection into the writer.
     *
     * @param item the item to write
     * @param context the current marshalling context
     * @param writer the target writer
     * @since 1.4.11
     */
    protected void writeBareItem(final Object item, final MarshallingContext context,
            final HierarchicalStreamWriter writer) {
        context.convertAnother(item);
    }

    /**
     * Write a null item of the collection into the writer. The method readItem should be able to process the written
     * data i.e. it has to write the tags or may not write anything at all.
     *
     * @param context the current marshalling context
     * @param writer the target writer
     * @since 1.4.11
     */
    protected void writeNullItem(final MarshallingContext context, final HierarchicalStreamWriter writer) {
        final String name = mapper().serializedClass(null);
        ExtendedHierarchicalStreamWriterHelper.startNode(writer, name, Mapper.Null.class);
        writer.endNode();
    }

    /**
     * @deprecated As of 1.4.11 use {@link #readBareItem(HierarchicalStreamReader, UnmarshallingContext, Object)} or
     *             {@link #readCompleteItem(HierarchicalStreamReader, UnmarshallingContext, Object)} instead.
     */
    protected Object readItem(final HierarchicalStreamReader reader, final UnmarshallingContext context,
            final Object current) {
        return readBareItem(reader, context, current);
    }

    /**
     * Read a bare item of the collection from the reader.
     *
     * @param reader the source reader
     * @param context the unmarshalling context
     * @param current the target collection (if already available)
     * @return the read item
     * @since 1.4.11
     */
    protected Object readBareItem(final HierarchicalStreamReader reader, final UnmarshallingContext context,
            final Object current) {
        Class type = HierarchicalStreams.readClassType(reader, mapper());
        return context.convertAnother(current, type);
    }

    /**
     * Read an item of the collection including the tags from the reader.
     *
     * @param reader the source reader
     * @param context the unmarshalling context
     * @param current the target collection (if already available)
     * @return the read item
     * @since 1.4.11
     */
    protected Object readCompleteItem(final HierarchicalStreamReader reader, final UnmarshallingContext context,
            final Object current) {
        reader.moveDown();
        final Object result = readItem(reader, context, current);
        reader.moveUp();
        return result;
    }

    protected Object createCollection(Class type) {
        ErrorWritingException ex = null;
        Class defaultType = mapper().defaultImplementationOf(type);
        try {
            return defaultType.newInstance();
        } catch (InstantiationException e) {
            ex =  new ConversionException("Cannot instantiate default collection", e);
        } catch (IllegalAccessException e) {
            ex = new ObjectAccessException("Cannot instantiate default collection", e);
        }
        ex.add("collection-type", type.getName());
        ex.add("default-type", defaultType.getName());
        throw ex;
    }
}
