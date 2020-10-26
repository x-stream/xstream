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

package com.thoughtworks.xstream.core;

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
        lazilyCreateDataHolder();
        return dataHolder.get(key);
    }

    @Override
    public void put(final Object key, final Object value) {
        lazilyCreateDataHolder();
        dataHolder.put(key, value);
    }

    @Override
    public Iterator<Object> keys() {
        lazilyCreateDataHolder();
        return dataHolder.keys();
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
