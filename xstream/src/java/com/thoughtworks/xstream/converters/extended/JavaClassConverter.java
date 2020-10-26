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
