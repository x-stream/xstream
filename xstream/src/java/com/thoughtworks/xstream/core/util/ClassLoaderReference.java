/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2013 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. March 2005 by Joe Walnes
 */
package com.thoughtworks.xstream.core.util;

/**
 * ClassLoader that refers to another ClassLoader, allowing a single instance to be passed around the codebase that
 * can later have its destination changed.
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @since 1.1.1
 * @deprecated As of 1.4.5 use {@link com.thoughtworks.xstream.core.ClassLoaderReference} instead
 */
public class ClassLoaderReference extends ClassLoader {

    private transient ClassLoader reference;

    /**
     * @deprecated As of 1.4.5 use
     *             {@link com.thoughtworks.xstream.core.ClassLoaderReference#ClassLoaderReference(ClassLoader)}
     *             instead
     */
    public ClassLoaderReference(ClassLoader reference) {
        this.reference = reference;
    }

    /**
     * @deprecated As of 1.4.5 use
     *             {@link com.thoughtworks.xstream.core.ClassLoaderReference#getReference()}
     *             .loadClass(String) instead
     */
    public Class loadClass(String name) throws ClassNotFoundException {
        return reference.loadClass(name);
    }

    /**
     * @deprecated As of 1.4.5 use
     *             {@link com.thoughtworks.xstream.core.ClassLoaderReference#getReference()}
     *             instead
     */
    public ClassLoader getReference() {
        return reference;
    }

    /**
     * @deprecated As of 1.4.5 use
     *             {@link com.thoughtworks.xstream.core.ClassLoaderReference#setReference(ClassLoader)}
     *             instead
     */
    public void setReference(ClassLoader reference) {
        this.reference = reference;
    }
    
    private Object writeReplace() {
        return new Replacement();
    }
    
    static class Replacement {
        
        private Object readResolve() {
            return new ClassLoaderReference(new CompositeClassLoader());
        }
        
    };
}
