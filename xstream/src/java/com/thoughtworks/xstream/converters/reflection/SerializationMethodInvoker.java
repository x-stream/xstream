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
 * Convenience wrapper to invoke special serialization methods on objects (and perform reflection caching).
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @deprecated As of 1.4.8, moved into internal util package.
 */
@Deprecated
public class SerializationMethodInvoker implements Caching {

    SerializationMembers serializationMembers = new SerializationMembers();

    /**
     * Resolves an object as native serialization does by calling readResolve(), if available.
     * 
     * @deprecated As of 1.4.8, moved into internal util package.
     */
    @Deprecated
    public Object callReadResolve(final Object result) {
        return serializationMembers.callReadResolve(result);
    }

    /**
     * @deprecated As of 1.4.8, moved into internal util package.
     */
    @Deprecated
    public Object callWriteReplace(final Object object) {
        return serializationMembers.callWriteReplace(object);
    }

    /**
     * @deprecated As of 1.4.8, moved into internal util package.
     */
    @Deprecated
    public boolean supportsReadObject(final Class<?> type, final boolean includeBaseClasses) {
        return serializationMembers.supportsReadObject(type, includeBaseClasses);
    }

    /**
     * @deprecated As of 1.4.8, moved into internal util package.
     */
    @Deprecated
    public void callReadObject(final Class<?> type, final Object object, final ObjectInputStream stream) {
        serializationMembers.callReadObject(type, object, stream);
    }

    /**
     * @deprecated As of 1.4.8, moved into internal util package.
     */
    @Deprecated
    public boolean supportsWriteObject(final Class<?> type, final boolean includeBaseClasses) {
        return serializationMembers.supportsWriteObject(type, includeBaseClasses);
    }

    /**
     * @deprecated As of 1.4.8, moved into internal util package.
     */
    @Deprecated
    public void callWriteObject(final Class<?> type, final Object instance, final ObjectOutputStream stream) {
        serializationMembers.callWriteObject(type, instance, stream);
    }

    /**
     * @deprecated As of 1.4.8, moved into internal util package.
     */
    @Override
    @Deprecated
    public void flushCache() {
        serializationMembers.flushCache();
    }
}
