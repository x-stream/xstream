/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2011 XStream Committers.
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
import com.thoughtworks.xstream.core.util.Primitives;

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
        Class resultingClass = Primitives.primitiveType(className);
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
}
