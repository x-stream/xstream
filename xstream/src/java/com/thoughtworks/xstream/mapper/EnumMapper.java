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
public class EnumMapper extends AttributeMapper {

    private transient Map enumConverterMap = new WeakHashMap();

    public EnumMapper(Mapper wrapped, ConverterLookup converterLookup) {
        super(wrapped, converterLookup);
    }

    /**
     * @deprecated since 1.3, use {@link #EnumMapper(Mapper, ConverterLookup)}
     */
    @Deprecated
    public EnumMapper(Mapper wrapped) {
        super(wrapped, null);
    }

    /**
     * @deprecated since 1.2, use {@link #EnumMapper(Mapper, ConverterLookup))}
     */
    @Deprecated
    public EnumMapper(ClassMapper wrapped) {
        this((Mapper)wrapped, null);
    }

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

    public boolean isImmutableValueType(Class type) {
        return (Enum.class.isAssignableFrom(type)) || super.isImmutableValueType(type);
    }

    protected SingleValueConverter getLocalConverterFromItemType(Class type) {
        if (Enum.class.isAssignableFrom(type)) {
            synchronized (enumConverterMap) {
                SingleValueConverter singleValueConverter = (SingleValueConverter)enumConverterMap.get(type);
                if (singleValueConverter == null) {
                    singleValueConverter = new EnumSingleValueConverter(type);
                    enumConverterMap.put(type, singleValueConverter);
                }
                return singleValueConverter;
            }
        }
        return super.getLocalConverterFromItemType(type);
    }

    private Object readResolve() {
        this.enumConverterMap = new WeakHashMap();
        return this;
    }
}
