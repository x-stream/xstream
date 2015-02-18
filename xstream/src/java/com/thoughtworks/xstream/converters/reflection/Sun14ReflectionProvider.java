/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2011, 2013, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.reflection;

/**
 * Instantiates a new object on the Sun JVM by bypassing the constructor (meaning code in the constructor will never be
 * executed and parameters do not have to be known). This is the same method used by the internals of standard Java
 * serialization, but relies on internal Sun code that may not be present on all JVMs.
 * 
 * @author Joe Walnes
 * @author Brian Slesinsky
 * @deprecated As of 1.4.7 use {@link SunUnsafeReflectionProvider}
 */
public class Sun14ReflectionProvider extends SunUnsafeReflectionProvider {
    /**
     * @deprecated As of 1.4.7 use {@link SunUnsafeReflectionProvider#SunUnsafeReflectionProvider()}
     */
    public Sun14ReflectionProvider() {
        super();
    }

    /**
     * @deprecated As of 1.4.7 use {@link SunUnsafeReflectionProvider#SunUnsafeReflectionProvider(FieldDictionary)}
     */
    public Sun14ReflectionProvider(FieldDictionary dic) {
        super(dic);
    }
    
    private Object readResolve() {
        init();
        return this;
    }
}
