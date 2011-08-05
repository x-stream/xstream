/*
 * Copyright (C) 2008, 2009, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 01. January 2008 by Joerg Schaible
 */
package com.thoughtworks.xstream.tools.benchmark.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Serializable class containing one basic types.
 * 
 * @since 1.4
 */
public class SerializableOne implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private String one;
    
    public SerializableOne(String one) {
        this.one = one;
    }

    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(final ObjectInputStream in)
        throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

    public boolean equals(Object obj) {
        return one.equals(((SerializableOne)obj).one);
    }

    public int hashCode() {
        return one.hashCode() >>> 1;
    }
}