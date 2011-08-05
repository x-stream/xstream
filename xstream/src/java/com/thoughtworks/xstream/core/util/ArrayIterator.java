/*
 * Copyright (C) 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 29.07.2011 by Joerg Schaible
 */
package com.thoughtworks.xstream.core.util;

import java.lang.reflect.Array;
import java.util.Iterator;

/**
 * @author J&ouml;rg Schaible
 *
 * @since 1.4
 */
public class ArrayIterator implements Iterator {
    private final Object array;
    private int idx;
    private int length;
    public ArrayIterator(Object array) {
        this.array = array;
        length = Array.getLength(array);
    }

    public boolean hasNext() {
        return idx < length;
    }

    public Object next() {
        return Array.get(array, idx++);
    }

    public void remove() {
        throw new UnsupportedOperationException("Remove from array"); 
    }
}
