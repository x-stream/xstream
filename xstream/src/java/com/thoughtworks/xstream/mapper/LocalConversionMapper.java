/*
 * Copyright (C) 2007, 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 06. November 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.core.util.FastField;

import java.util.HashMap;
import java.util.Map;


/**
 * A Mapper for locally defined converters for a member field.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.3
 */
public class LocalConversionMapper extends MapperWrapper {

    private final Map localConverters = new HashMap();
    private transient AttributeMapper attributeMapper;

    /**
     * Constructs a LocalConversionMapper.
     * 
     * @param wrapped
     * @since 1.3
     */
    public LocalConversionMapper(Mapper wrapped) {
        super(wrapped);
        readResolve();
    }

    public void registerLocalConverter(Class definedIn, String fieldName, Converter converter) {
        localConverters.put(new FastField(definedIn, fieldName), converter);
    }

    public Converter getLocalConverter(Class definedIn, String fieldName) {
        return (Converter)localConverters.get(new FastField(definedIn, fieldName));
    }

    public SingleValueConverter getConverterFromAttribute(Class definedIn, String attribute,
        Class type) {
        SingleValueConverter converter = getLocalSingleValueConverter(
            definedIn, attribute, type);
        return converter == null
            ? super.getConverterFromAttribute(definedIn, attribute, type)
            : converter;
    }

    public SingleValueConverter getConverterFromItemType(String fieldName, Class type,
        Class definedIn) {
        SingleValueConverter converter = getLocalSingleValueConverter(
            definedIn, fieldName, type);
        return converter == null
            ? super.getConverterFromItemType(fieldName, type, definedIn)
            : converter;
    }

    private SingleValueConverter getLocalSingleValueConverter(Class definedIn,
        String fieldName, Class type) {
        if (attributeMapper != null
            && attributeMapper.shouldLookForSingleValueConverter(fieldName, type, definedIn)) {
            Converter converter = getLocalConverter(definedIn, fieldName);
            if (converter != null && converter instanceof SingleValueConverter) {
                return (SingleValueConverter)converter;
            }
        }
        return null;
    }

    private Object readResolve() {
        this.attributeMapper = (AttributeMapper)lookupMapperOfType(AttributeMapper.class);
        return this;
    }
}
