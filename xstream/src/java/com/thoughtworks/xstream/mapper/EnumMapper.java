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
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.enums.EnumSingleValueConverter;
import com.thoughtworks.xstream.core.JVM;


/**
 * Mapper that handles the special case of polymorphic enums in Java 1.5. This renames MyEnum$1
 * to MyEnum making it less bloaty in the XML and avoiding the need for an alias per enum value
 * to be specified. Additionally every enum is treated automatically as immutable type.
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class EnumMapper extends MapperWrapper {

    // Dynamically try to load Enum class. Pre JDK1.5 will silently fail.
    private static JVM jvm = new JVM();
    private static final Class enumClass = jvm.loadClass("java.lang.Enum");

    private static final boolean active = enumClass != null;

    private static final Class enumSetClass = active
        ? jvm.loadClass("java.util.EnumSet")
        : null;

    public EnumMapper(Mapper wrapped) {
        super(wrapped);
    }

    /**
     * @deprecated As of 1.2, use {@link #EnumMapper(Mapper)}
     */
    public EnumMapper(ClassMapper wrapped) {
        this((Mapper)wrapped);
    }

    public String serializedClass(Class type) {
        if (!active || type == null) {
            return super.serializedClass(type);
        } else {
            if (enumClass.isAssignableFrom(type) && type.getSuperclass() != enumClass) {
                return super.serializedClass(type.getSuperclass());
            } else if (enumSetClass.isAssignableFrom(type)) {
                return super.serializedClass(enumSetClass);
            } else {
                return super.serializedClass(type);
            }
        }
    }

    public boolean isImmutableValueType(Class type) {
        return (active && enumClass.isAssignableFrom(type)) || super.isImmutableValueType(type);
    }

    public SingleValueConverter getConverterFromItemType(Class type) {
        if (active && enumClass.isAssignableFrom(type)) {
            return new EnumSingleValueConverter(type);
        }
        return super.getConverterFromItemType(type);
    }
}
