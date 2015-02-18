/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2010, 2011, 2014, 2015 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 23. August 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.reflection;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.thoughtworks.xstream.core.Caching;
import com.thoughtworks.xstream.core.util.SerializationMembers;

/**
 * Convenience wrapper to invoke special serialization methods on objects (and perform
 * reflection caching).
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @deprecated As of 1.4.8, moved into internal util package.
 */
public class SerializationMethodInvoker implements Caching {

    SerializationMembers serializationMembers = new SerializationMembers();

    /**
     * Resolves an object as native serialization does by calling readResolve(), if available.
     * 
     * @deprecated As of 1.4.8, moved into internal util package.
     */
    public Object callReadResolve(Object result) {
        return serializationMembers.callReadResolve(result);
    }

    /**
     * @deprecated As of 1.4.8, moved into internal util package.
     */
    public Object callWriteReplace(Object object) {
        return serializationMembers.callWriteReplace(object);
    }

    /**
     * @deprecated As of 1.4.8, moved into internal util package.
     */
    public boolean supportsReadObject(Class type, boolean includeBaseClasses) {
        return serializationMembers.supportsReadObject(type, includeBaseClasses);
    }

    /**
     * @deprecated As of 1.4.8, moved into internal util package.
     */
    public void callReadObject(Class type, Object object, ObjectInputStream stream) {
        serializationMembers.callReadObject(type, object, stream);
    }

    /**
     * @deprecated As of 1.4.8, moved into internal util package.
     */
    public boolean supportsWriteObject(Class type, boolean includeBaseClasses) {
        return serializationMembers.supportsWriteObject(type, includeBaseClasses);
    }

    /**
     * @deprecated As of 1.4.8, moved into internal util package.
     */
    public void callWriteObject(Class type, Object instance, ObjectOutputStream stream) {
        serializationMembers.callWriteObject(type, instance, stream);
    }

    /**
     * @deprecated As of 1.4.8, moved into internal util package.
     */
    public void flushCache() {
        serializationMembers.flushCache();
    }
}
