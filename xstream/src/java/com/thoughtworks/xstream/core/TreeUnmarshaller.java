/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2011, 2014, 2015, 2018, 2021 XStream Committers.
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
import com.thoughtworks.xstream.converters.ErrorReporter;
import com.thoughtworks.xstream.converters.ErrorWriter;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.util.FastStack;
import com.thoughtworks.xstream.core.util.HierarchicalStreams;
import com.thoughtworks.xstream.core.util.PrioritizedList;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.security.AbstractSecurityException;


public class TreeUnmarshaller implements UnmarshallingContext {

    private final Object root;
    protected HierarchicalStreamReader reader;
    private final ConverterLookup converterLookup;
    private final Mapper mapper;
    private final FastStack<Class<?>> types = new FastStack<>(16);
    private DataHolder dataHolder;
    private final PrioritizedList<Runnable> validationList = new PrioritizedList<>();

    public TreeUnmarshaller(
            final Object root, final HierarchicalStreamReader reader, final ConverterLookup converterLookup,
            final Mapper mapper) {
        this.root = root;
        this.reader = reader;
        this.converterLookup = converterLookup;
        this.mapper = mapper;
    }

    @Override
    public Object convertAnother(final Object parent, final Class<?> type) {
        return convertAnother(parent, type, null);
    }

    @Override
    public Object convertAnother(final Object parent, Class<?> type, Converter converter) {
        type = mapper.defaultImplementationOf(type);
        if (converter == null) {
            converter = converterLookup.lookupConverterForType(type);
        } else {
            if (!converter.canConvert(type)) {
                final ConversionException e = new ConversionException("Explicitly selected converter cannot handle type");
                e.add("item-type", type.getName());
                e.add("converter-type", converter.getClass().getName());
                throw e;
            }
        }
        return convert(parent, type, converter);
    }

    protected Object convert(final Object parent, final Class<?> type, final Converter converter) {
        types.push(type);
        try {
            return converter.unmarshal(reader, this);
        } catch (final ConversionException conversionException) {
            addInformationTo(conversionException, type, converter, parent);
            throw conversionException;
        } catch (AbstractSecurityException e) {
            throw e;
        } catch (RuntimeException e) {
            final ConversionException conversionException = new ConversionException(e);
            addInformationTo(conversionException, type, converter, parent);
            throw conversionException;
        } finally {
            types.popSilently();
        }
    }

    private void addInformationTo(final ErrorWriter errorWriter, final Class<?> type, final Converter converter,
            final Object parent) {
        errorWriter.add("class", type.getName());
        errorWriter.add("required-type", getRequiredType().getName());
        errorWriter.add("converter-type", converter.getClass().getName());
        if (converter instanceof ErrorReporter) {
            ((ErrorReporter)converter).appendErrors(errorWriter);
        }
        if (parent instanceof ErrorReporter) {
            ((ErrorReporter)parent).appendErrors(errorWriter);
        }
        reader.appendErrors(errorWriter);
    }

    @Override
    public void addCompletionCallback(final Runnable work, final int priority) {
        validationList.add(work, priority);
    }

    @Override
    public Object currentObject() {
        return types.size() == 1 ? root : null;
    }

    @Override
    public Class<?> getRequiredType() {
        return types.peek();
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

    public Object start(final DataHolder dataHolder) {
        this.dataHolder = dataHolder;
        final Class<?> type = HierarchicalStreams.readClassType(reader, mapper);
        final Object result = convertAnother(null, type);
        for (final Runnable runnable : validationList) {
            runnable.run();
        }
        return result;
    }

    protected Mapper getMapper() {
        return mapper;
    }

}
