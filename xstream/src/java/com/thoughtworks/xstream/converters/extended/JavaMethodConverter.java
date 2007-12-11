/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
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
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Converts a java.lang.reflect.Method to XML.
 * 
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 */
public class JavaMethodConverter implements Converter {

    private final SingleValueConverter javaClassConverter;

    /**
     * @deprecated As of 1.2 - use other constructor and explicitly supply a ClassLoader.
     */
    public JavaMethodConverter() {
        this(JavaMethodConverter.class.getClassLoader());
    }

    public JavaMethodConverter(ClassLoader classLoader) {
        this.javaClassConverter = new JavaClassConverter(classLoader);
    }

    public boolean canConvert(Class type) {
        return type.equals(Method.class) || type.equals(Constructor.class);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        if (source instanceof Method) {
            Method method = (Method) source;
            String declaringClassName = javaClassConverter.toString(method.getDeclaringClass());
            marshalMethod(writer, declaringClassName, method.getName(), method.getParameterTypes());
        } else {
            Constructor method = (Constructor) source;
            String declaringClassName = javaClassConverter.toString(method.getDeclaringClass());
            marshalMethod(writer, declaringClassName, null, method.getParameterTypes());
        }
    }

    private void marshalMethod(HierarchicalStreamWriter writer, String declaringClassName, String methodName, Class[] parameterTypes) {

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
        for (int i = 0; i < parameterTypes.length; i++) {
            writer.startNode("class");
            writer.setValue(javaClassConverter.toString(parameterTypes[i]));
            writer.endNode();
        }
        writer.endNode();
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        try {
            boolean isMethodNotConstructor = context.getRequiredType().equals(Method.class);

            reader.moveDown();
            String declaringClassName = reader.getValue();
            Class declaringClass = (Class)javaClassConverter.fromString(declaringClassName);
            reader.moveUp();

            String methodName = null;
            if (isMethodNotConstructor) {
                reader.moveDown();
                methodName = reader.getValue();
                reader.moveUp();
            }

            reader.moveDown();
            List parameterTypeList = new ArrayList();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                String parameterTypeName = reader.getValue();
                parameterTypeList.add(javaClassConverter.fromString(parameterTypeName));
                reader.moveUp();
            }
            Class[] parameterTypes = (Class[]) parameterTypeList.toArray(new Class[parameterTypeList.size()]);
            reader.moveUp();

            if (isMethodNotConstructor) {
                return declaringClass.getDeclaredMethod(methodName, parameterTypes);
            } else {
                return declaringClass.getDeclaredConstructor(parameterTypes);
            }
        } catch (NoSuchMethodException e) {
            throw new ConversionException(e);
        }
    }
}
