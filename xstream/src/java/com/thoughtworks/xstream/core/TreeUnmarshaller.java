/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 15. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.core;

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


public class TreeUnmarshaller implements UnmarshallingContext {

    private Object root;
    protected HierarchicalStreamReader reader;
    private ConverterLookup converterLookup;
    private Mapper mapper;
    private FastStack types = new FastStack(16);
    private DataHolder dataHolder;
    private final PrioritizedList validationList = new PrioritizedList();

    public TreeUnmarshaller(
        Object root, HierarchicalStreamReader reader, ConverterLookup converterLookup,
        Mapper mapper) {
        this.root = root;
        this.reader = reader;
        this.converterLookup = converterLookup;
        this.mapper = mapper;
    }

    public Object convertAnother(Object parent, Class type) {
        return convertAnother(parent, type, null);
    }

    public Object convertAnother(Object parent, Class type, Converter converter) {
        type = mapper.defaultImplementationOf(type);
        if (converter == null) {
            converter = converterLookup.lookupConverterForType(type);
        } else {
            if (!converter.canConvert(type)) {
                ConversionException e = new ConversionException(
                    "Explicit selected converter cannot handle type");
                e.add("item-type", type.getName());
                e.add("converter-type", converter.getClass().getName());
                throw e;
            }
        }
        return convert(parent, type, converter);
    }

    protected Object convert(Object parent, Class type, Converter converter) {
        try {
            types.push(type);
            Object result = converter.unmarshal(reader, this);
            types.popSilently();
            return result;
        } catch (ConversionException conversionException) {
            addInformationTo(conversionException, type, converter, parent);
            throw conversionException;
        } catch (RuntimeException e) {
            ConversionException conversionException = new ConversionException(e);
            addInformationTo(conversionException, type, converter, parent);
            throw conversionException;
        }
    }

    private void addInformationTo(ErrorWriter errorWriter, Class type, Converter converter, Object parent) {
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

    public void addCompletionCallback(Runnable work, int priority) {
        validationList.add(work, priority);
    }

    public Object currentObject() {
        return types.size() == 1 ? root : null;
    }

    public Class getRequiredType() {
        return (Class)types.peek();
    }

    public Object get(Object key) {
        lazilyCreateDataHolder();
        return dataHolder.get(key);
    }

    public void put(Object key, Object value) {
        lazilyCreateDataHolder();
        dataHolder.put(key, value);
    }

    public Iterator keys() {
        lazilyCreateDataHolder();
        return dataHolder.keys();
    }

    private void lazilyCreateDataHolder() {
        if (dataHolder == null) {
            dataHolder = new MapBackedDataHolder();
        }
    }

    public Object start(DataHolder dataHolder) {
        this.dataHolder = dataHolder;
        Class type = HierarchicalStreams.readClassType(reader, mapper);
        Object result = convertAnother(null, type);
        Iterator validations = validationList.iterator();
        while (validations.hasNext()) {
            Runnable runnable = (Runnable)validations.next();
            runnable.run();
        }
        return result;
    }

    protected Mapper getMapper() {
        return this.mapper;
    }

}
