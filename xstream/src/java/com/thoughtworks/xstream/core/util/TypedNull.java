/*
 * Copyright (c) 2007, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 30. March 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.core.util;

/**
 * A placeholder for a <code>null</code> value of a specific type.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2.2
 */
public class TypedNull<T> {
    private final Class<T> type;

    public TypedNull(final Class<T> type) {
        super();
        this.type = type;
    }

    public Class<T> getType() {
        return this.type;
    }
}
