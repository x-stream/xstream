/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2013, 2014, 2015, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 04. April 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.extended;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.InitializationException;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.ClassLoaderReference;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


/**
 * Converts a {@link Method}.
 * 
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 */
public class JavaMethodConverter implements Converter {

    private final SingleValueConverter javaClassConverter;

    /**
     * Construct a JavaMethodConverter.
     * 
     * @param classLoaderReference the reference to the {@link ClassLoader} of the XStream instance
     * @since 1.4.5
     */
    public JavaMethodConverter(final ClassLoaderReference classLoaderReference) {
        this(new JavaClassConverter(classLoaderReference));
    }

    /**
     * @deprecated As of 1.4.5 use {@link #JavaMethodConverter(ClassLoaderReference)}
     */
    @Deprecated
    public JavaMethodConverter(final ClassLoader classLoader) {
        this(new ClassLoaderReference(classLoader));
    }

    /**
     * Construct a JavaMethodConverter.
     * 
     * @param javaClassConverter the converter to use
     * @since 1.4.5
     */
    protected JavaMethodConverter(final SingleValueConverter javaClassConverter) {
        if (!javaClassConverter.canConvert(Class.class)) {
            throw new InitializationException("Java Class Converter cannot handle Class types");
        }
        this.javaClassConverter = javaClassConverter;
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return type == Method.class || type == Constructor.class;
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        if (source instanceof Method) {
            final Method method = (Method)source;
            final String declaringClassName = javaClassConverter.toString(method.getDeclaringClass());
            marshalMethod(writer, declaringClassName, method.getName(), method.getParameterTypes());
        } else {
            final Constructor<?> method = (Constructor<?>)source;
            final String declaringClassName = javaClassConverter.toString(method.getDeclaringClass());
            marshalMethod(writer, declaringClassName, null, method.getParameterTypes());
        }
    }

    private void marshalMethod(final HierarchicalStreamWriter writer, final String declaringClassName,
            final String methodName, final Class<?>[] parameterTypes) {

        writer.startNode("class");
        writer.setValue(declaringClassName);
        writer.endNode();

        if (methodName != null) {
            // it's a method and not a ctor
            writer.startNode("name");
            writer.setValue(methodName);
            writer.endNode();
        }

        writer.startNode("parameter-types");
        for (final Class<?> parameterType : parameterTypes) {
            writer.startNode("class");
            writer.setValue(javaClassConverter.toString(parameterType));
            writer.endNode();
        }
        writer.endNode();
    }

    @Override
    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        try {
            final boolean isMethodNotConstructor = context.getRequiredType().equals(Method.class);

            reader.moveDown();
            final String declaringClassName = reader.getValue();
            final Class<?> declaringClass = (Class<?>)javaClassConverter.fromString(declaringClassName);
            reader.moveUp();

            String methodName = null;
            if (isMethodNotConstructor) {
                reader.moveDown();
                methodName = reader.getValue();
                reader.moveUp();
            }

            reader.moveDown();
            final List<Class<?>> parameterTypeList = new ArrayList<>();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                final String parameterTypeName = reader.getValue();
                parameterTypeList.add((Class<?>)javaClassConverter.fromString(parameterTypeName));
                reader.moveUp();
            }
            final Class<?>[] parameterTypes = parameterTypeList.toArray(new Class[parameterTypeList.size()]);
            reader.moveUp();

            if (isMethodNotConstructor) {
                return declaringClass.getDeclaredMethod(methodName, parameterTypes);
            } else {
                return declaringClass.getDeclaredConstructor(parameterTypes);
            }
        } catch (final NoSuchMethodException e) {
            throw new ConversionException(e);
        }
    }
}
