/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 20. March 2005 by Joe Walnes
 */
package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.enums.EnumSingleValueConverter;

import java.util.EnumSet;
import java.util.Map;
import java.util.WeakHashMap;


/**
 * Mapper that handles the special case of polymorphic enums in Java 1.5. This renames MyEnum$1
 * to MyEnum making it less bloaty in the XML and avoiding the need for an alias per enum value
 * to be specified. Additionally every enum is treated automatically as immutable type and can
 * be written as attribute.
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class EnumMapper extends MapperWrapper {

    private transient AttributeMapper attributeMapper;
    private transient Map enumConverterMap;
    private final ConverterLookup converterLookup;

    /**
     * @deprecated since 1.3.1, use {@link #EnumMapper(Mapper)}
     */
    public EnumMapper(Mapper wrapped, ConverterLookup lookup) {
        super(wrapped);
        this.converterLookup = lookup;
        readResolve();
    }

    @Deprecated
    public EnumMapper(Mapper wrapped) {
        super(wrapped);
        this.converterLookup = null;
        readResolve();
    }

    /**
     * @deprecated since 1.2, use {@link #EnumMapper(Mapper))}
     */
    @Deprecated
    public EnumMapper(ClassMapper wrapped) {
        this((Mapper)wrapped);
    }

    @Override
    public String serializedClass(Class type) {
        if (type == null) {
            return super.serializedClass(type);
        }
        if (Enum.class.isAssignableFrom(type) && type.getSuperclass() != Enum.class) {
            return super.serializedClass(type.getSuperclass());
        } else if (EnumSet.class.isAssignableFrom(type)) {
            return super.serializedClass(EnumSet.class);
        } else {
            return super.serializedClass(type);
        }
    }

    @Override
    public boolean isImmutableValueType(Class type) {
        return (Enum.class.isAssignableFrom(type)) || super.isImmutableValueType(type);
    }

    @Override
    public SingleValueConverter getConverterFromItemType(String fieldName, Class type,
        Class definedIn) {
        SingleValueConverter converter = getLocalConverter(fieldName, type, definedIn);
        return converter == null
            ? super.getConverterFromItemType(fieldName, type, definedIn)
            : converter;
    }

    @Override
    public SingleValueConverter getConverterFromAttribute(Class definedIn, String attribute,
        Class type) {
        SingleValueConverter converter = getLocalConverter(attribute, type, definedIn);
        return converter == null
            ? super.getConverterFromAttribute(definedIn, attribute, type)
            : converter;
    }

    private SingleValueConverter getLocalConverter(String fieldName, Class type, Class definedIn) {
        if (attributeMapper != null
            && Enum.class.isAssignableFrom(type)
            && attributeMapper.shouldLookForSingleValueConverter(fieldName, type, definedIn)) {
            synchronized (enumConverterMap) {
                SingleValueConverter singleValueConverter = (SingleValueConverter)enumConverterMap
                    .get(type);
                if (singleValueConverter == null) {
                    singleValueConverter = super.getConverterFromItemType(fieldName, type, definedIn);
                    if (singleValueConverter == null) {
                        singleValueConverter = new EnumSingleValueConverter(type);
                    }
                    enumConverterMap.put(type, singleValueConverter);
                }
                return singleValueConverter;
            }
        }
        return null;
    }

    private Object readResolve() {
        this.enumConverterMap = new WeakHashMap();
        this.attributeMapper = (AttributeMapper)lookupMapperOfType(AttributeMapper.class);
        return this;
    }
}
