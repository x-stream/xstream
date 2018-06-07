/*
 * Copyright (C) 2009, 2013, 2014, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 17. April 2009 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.extended;

import java.lang.reflect.Field;

import com.thoughtworks.xstream.InitializationException;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.ClassLoaderReference;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.DefaultMapper;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Converts a {@link Field}.
 * 
 * @author J&ouml;rg Schaible
 */
public class JavaFieldConverter implements Converter {

    private final SingleValueConverter javaClassConverter;
    private final Mapper mapper;

    /**
     * Construct a JavaFieldConverter.
     * 
     * @param classLoaderReference the reference to the {@link ClassLoader} of the XStream instance
     * @since 1.4.5
     */
    public JavaFieldConverter(final ClassLoaderReference classLoaderReference) {
        this(new JavaClassConverter(classLoaderReference), new DefaultMapper(classLoaderReference));
    }

    /**
     * @deprecated As of 1.4.5 use {@link #JavaFieldConverter(ClassLoaderReference)}
     */
    @Deprecated
    public JavaFieldConverter(final ClassLoader classLoader) {
        this(new ClassLoaderReference(classLoader));
    }

    /**
     * Construct a JavaFieldConverter. Depending on the mapper chain the converter will also respect aliases.
     * 
     * @param javaClassConverter the converter to use
     * @param mapper to use
     * @since 1.4.5
     */
    protected JavaFieldConverter(final SingleValueConverter javaClassConverter, final Mapper mapper) {
        if (!javaClassConverter.canConvert(Class.class)) {
            throw new InitializationException("Java Class Converter cannot handle Class types");
        }
        this.javaClassConverter = javaClassConverter;
        this.mapper = mapper;
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return type == Field.class;
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final Field field = (Field)source;
        final Class<?> type = field.getDeclaringClass();

        writer.startNode("name");
        writer.setValue(mapper.serializedMember(type, field.getName()));
        writer.endNode();

        writer.startNode("clazz");
        writer.setValue(javaClassConverter.toString(type));
        writer.endNode();
    }

    @Override
    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        String methodName = null;
        String declaringClassName = null;

        while ((methodName == null || declaringClassName == null) && reader.hasMoreChildren()) {
            reader.moveDown();

            if (reader.getNodeName().equals("name")) {
                methodName = reader.getValue();
            } else if (reader.getNodeName().equals("clazz")) {
                declaringClassName = reader.getValue();
            }
            reader.moveUp();
        }

        final Class<?> declaringClass = (Class<?>)javaClassConverter.fromString(declaringClassName);
        try {
            return declaringClass.getDeclaredField(mapper.realMember(declaringClass, methodName));
        } catch (final NoSuchFieldException e) {
            throw new ConversionException(e);
        }
    }
}
