/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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

    /**
     * @deprecated As of 1.4.11 use {@link #writeCompleteItem(Object, MarshallingContext, HierarchicalStreamWriter)}
     *             instead.
     */
    @Deprecated
    protected void writeItem(final Object item, final MarshallingContext context,
            final HierarchicalStreamWriter writer) {
        // PUBLISHED API METHOD! If changing signature, ensure backwards compatibility.
        if (item == null) {
            // todo: this is duplicated in TreeMarshaller.start()
            writeNullItem(context, writer);
        } else {
            final String name = mapper().serializedClass(item.getClass());
            writer.startNode(name, item.getClass());
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
        writer.startNode(name, Mapper.Null.class);
        writer.endNode();
    }

    /**
     * @deprecated As of 1.4.11 use {@link #readBareItem(HierarchicalStreamReader, UnmarshallingContext, Object)} or
     *             {@link #readCompleteItem(HierarchicalStreamReader, UnmarshallingContext, Object)} instead.
     */
    @Deprecated
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
        final Class<?> type = HierarchicalStreams.readClassType(reader, mapper());
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

    protected Object createCollection(final Class<?> type) {
        ErrorWritingException ex = null;
        final Class<?> defaultType = mapper().defaultImplementationOf(type);
        try {
            return defaultType.newInstance();
        } catch (final InstantiationException e) {
            ex = new ConversionException("Cannot instantiate default collection", e);
        } catch (final IllegalAccessException e) {
            ex = new ObjectAccessException("Cannot instantiate default collection", e);
        }
        ex.add("collection-type", type.getName());
        ex.add("default-type", defaultType.getName());
        throw ex;
    }
}
