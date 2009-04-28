/*
 * Copyright (C) 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 17. April 2009 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.lang.reflect.Field;

/**
 * Converts a java.lang.reflect.Field to XML.
 * 
 * @author J&ouml;rg Schaible
 */
public class JavaFieldConverter implements Converter {

    private final SingleValueConverter javaClassConverter;

    public JavaFieldConverter(ClassLoader classLoader) {
        this.javaClassConverter = new JavaClassConverter(classLoader);
    }

    public boolean canConvert(Class type) {
        return type.equals(Field.class);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Field field = (Field) source;

        writer.startNode("name");
        writer.setValue(field.getName());
        writer.endNode();

        writer.startNode("clazz");
        writer.setValue(javaClassConverter.toString(field.getDeclaringClass()));
        writer.endNode();
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        String methodName = null;
        String declaringClassName = null;
        
        while((methodName == null || declaringClassName == null) && reader.hasMoreChildren()) {
            reader.moveDown();
            
            if (reader.getNodeName().equals("name")) {
                methodName = reader.getValue();
            } else if (reader.getNodeName().equals("clazz")) {
                declaringClassName = reader.getValue();
            }
            reader.moveUp();
        }
        
        Class declaringClass = (Class)javaClassConverter.fromString(declaringClassName);
        try {
            return declaringClass.getDeclaredField(methodName);
        } catch (NoSuchFieldException e) {
            throw new ConversionException(e);
        }
    }
}
