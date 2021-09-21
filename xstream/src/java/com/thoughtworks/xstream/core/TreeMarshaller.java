/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2011, 2014, 2015, 2018, 2021 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 15. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.core;

import java.util.Collections;
import java.util.Iterator;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.core.util.ObjectIdDictionary;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;


public class TreeMarshaller implements MarshallingContext {

    protected HierarchicalStreamWriter writer;
    protected ConverterLookup converterLookup;
    private final Mapper mapper;
    private final ObjectIdDictionary<Object> parentObjects = new ObjectIdDictionary<>();
    private DataHolder dataHolder;

    public TreeMarshaller(
            final HierarchicalStreamWriter writer, final ConverterLookup converterLookup, final Mapper mapper) {
        this.writer = writer;
        this.converterLookup = converterLookup;
        this.mapper = mapper;
    }

    @Override
    public void convertAnother(final Object item) {
        convertAnother(item, null);
    }

    @Override
    public void convertAnother(final Object item, Converter converter) {
        if (converter == null) {
            converter = converterLookup.lookupConverterForType(item.getClass());
        } else {
            if (!converter.canConvert(item.getClass())) {
                final ConversionException e = new ConversionException("Explicit selected converter cannot handle item");
                e.add("item-type", item.getClass().getName());
                e.add("converter-type", converter.getClass().getName());
                throw e;
            }
        }
        convert(item, converter);
    }

    protected void convert(final Object item, final Converter converter) {
        if (parentObjects.containsId(item)) {
            final ConversionException e = new CircularReferenceException("Recursive reference to parent object");
            e.add("item-type", item.getClass().getName());
            e.add("converter-type", converter.getClass().getName());
            throw e;
        }
        parentObjects.associateId(item, "");
        converter.marshal(item, writer, this);
        parentObjects.removeId(item);
    }

    public void start(final Object item, final DataHolder dataHolder) {
        this.dataHolder = dataHolder;
        if (item == null) {
            writer.startNode(mapper.serializedClass(null));
            writer.endNode();
        } else {
            writer.startNode(mapper.serializedClass(item.getClass()), item.getClass());
            convertAnother(item);
            writer.endNode();
        }
    }

    @Override
    public Object get(final Object key) {
        return dataHolder != null ? dataHolder.get(key) : null;
    }

    @Override
    public void put(final Object key, final Object value) {
        lazilyCreateDataHolder();
        dataHolder.put(key, value);
    }

    @Override
    public Iterator<Object> keys() {
        return dataHolder != null ? dataHolder.keys() : Collections.EMPTY_MAP.keySet().iterator();
    }

    private void lazilyCreateDataHolder() {
        if (dataHolder == null) {
            dataHolder = new MapBackedDataHolder();
        }
    }

    protected Mapper getMapper() {
        return mapper;
    }

    public static class CircularReferenceException extends ConversionException {
        private static final long serialVersionUID = 20151010L;

        public CircularReferenceException(final String msg) {
            super(msg);
        }
    }
}
