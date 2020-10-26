/*
 * Copyright (C) 2013 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 26. June 2013 by Joerg Schaible
 */
package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.core.util.CompositeClassLoader;

/**
 * Reference to a ClassLoader, allowing a single instance to be passed around the codebase that
 * can later have its destination changed.
 *
 * @author J&ouml;rg Schaible
 * @since 1.4.5
 */
public final class ClassLoaderReference {

    private transient ClassLoader reference;

    public ClassLoaderReference(ClassLoader reference) {
        setReference(reference);
    }

    public ClassLoader getReference() {
        return reference;
    }

    public void setReference(ClassLoader reference) {
        this.reference = reference instanceof com.thoughtworks.xstream.core.util.ClassLoaderReference
                ? ((com.thoughtworks.xstream.core.util.ClassLoaderReference)reference)
                    .getReference() : reference;
    }

    private Object readResolve() {
        this.reference = new CompositeClassLoader();
        return this;
    }
}
