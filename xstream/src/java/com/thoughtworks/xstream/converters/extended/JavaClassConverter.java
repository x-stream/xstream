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
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

/**
 * Converts a java.lang.Class to XML.
 * 
 * @author Aslak Helles&oslash;y
 * @author Joe Walnes
 * @author Matthew Sandoz
 * @author J&ouml;rg Schaible
 */
public class JavaClassConverter extends AbstractSingleValueConverter {

    private ClassLoader classLoader;

    /**
     * @deprecated As of 1.1.1 - use other constructor and explicitly supply a ClassLoader.
     */
    public JavaClassConverter() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public JavaClassConverter(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public boolean canConvert(Class clazz) {
        return Class.class.equals(clazz); // :)
    }

    public String toString(Object obj) {
        return ((Class) obj).getName();
    }

    public Object fromString(String str) {
        try {
            return loadClass(str);
        } catch (ClassNotFoundException e) {
            throw new ConversionException("Cannot load java class " + str, e);
        }
    }

    private Class loadClass(String className) throws ClassNotFoundException {
        Class resultingClass = primitiveClassForName(className);
        if( resultingClass != null ){
            return resultingClass;
        }
        int dimension;
        for(dimension = 0; className.charAt(dimension) == '['; ++dimension);
        if (dimension > 0) {
            final ClassLoader classLoaderToUse;
            if (className.charAt(dimension) == 'L') {
                String componentTypeName = className.substring(dimension + 1, className.length() - 1);
                classLoaderToUse = classLoader.loadClass(componentTypeName).getClassLoader();
            } else {
                classLoaderToUse = null;
            }
            return Class.forName(className, false, classLoaderToUse);
        }
        return classLoader.loadClass(className);
    }

    /**
     * Lookup table for primitive types.
     */
    private Class primitiveClassForName(String name) {
        return  name.equals("void") ? Void.TYPE :
                name.equals("boolean") ? Boolean.TYPE :
                name.equals("byte") ? Byte.TYPE :
                name.equals("char") ? Character.TYPE :
                name.equals("short") ? Short.TYPE :
                name.equals("int") ? Integer.TYPE :
                name.equals("long") ? Long.TYPE :
                name.equals("float") ? Float.TYPE :
                name.equals("double") ? Double.TYPE :
                null;
    }

}
