/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2011, 2013, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 04. April 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.thoughtworks.xstream.core.ClassLoaderReference;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import com.thoughtworks.xstream.mapper.DefaultMapper;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Converts a {@link Class} to a string.
 * 
 * @author Aslak Helles&oslash;y
 * @author Joe Walnes
 * @author Matthew Sandoz
 * @author J&ouml;rg Schaible
 */
public class JavaClassConverter extends AbstractSingleValueConverter {

    private final Mapper mapper;

    /**
     * Construct a JavaClassConverter.
     * 
     * @param classLoaderReference the reference to the {@link ClassLoader} of the XStream instance
     * @since 1.4.5
     */
    public JavaClassConverter(final ClassLoaderReference classLoaderReference) {
        this(new DefaultMapper(classLoaderReference));
    }

    /**
     * @deprecated As of 1.4.5 use {@link #JavaClassConverter(ClassLoaderReference)}
     */
    @Deprecated
    public JavaClassConverter(final ClassLoader classLoader) {
        this(new ClassLoaderReference(classLoader));
    }

    /**
     * Construct a JavaClassConverter that uses a provided mapper. Depending on the mapper chain it will not only be
     * used to load classes, but also to support type aliases.
     * 
     * @param mapper to use
     * @since 1.4.5
     */
    protected JavaClassConverter(final Mapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public boolean canConvert(final Class<?> clazz) {
        return Class.class.equals(clazz); // :)
    }

    @Override
    public String toString(final Object obj) {
        return mapper.serializedClass((Class<?>)obj);
    }

    @Override
    public Object fromString(final String str) {
        try {
            return mapper.realClass(str);
        } catch (final CannotResolveClassException e) {
            throw new ConversionException("Cannot load java class " + str, e.getCause());
        }
    }
}
