/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2013, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.mapper.Mapper;

public class ReflectionConverter extends AbstractReflectionConverter {

    // Might be missing in Android
    private final static Class eventHandlerType = JVM.loadClassForName("java.beans.EventHandler");
    private Class type;

    public ReflectionConverter(Mapper mapper, ReflectionProvider reflectionProvider) {
        super(mapper, reflectionProvider);
    }

    /**
     * Construct a ReflectionConverter for an explicit type.
     * 
     * @param mapper the mapper in use
     * @param reflectionProvider the reflection provider in use
     * @param type the explicit type to handle
     * @since 1.4.7
     */
    public ReflectionConverter(Mapper mapper, ReflectionProvider reflectionProvider, Class type) {
        this(mapper, reflectionProvider);
        this.type = type;
    }

    public boolean canConvert(Class type) {
        return ((this.type != null && this.type == type) || (this.type == null && type != null && type != eventHandlerType))
            && canAccess(type);
    }
}
