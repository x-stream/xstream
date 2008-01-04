/*
 * Copyright (C) 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 04. January 2008 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.xstream.converters.ConversionException;

import java.util.UUID;


/**
 * Converts a java.util.UUID to a string.
 * 
 * @author J&ouml;rg Schaible
 */
public class UUIDConverter extends AbstractSingleValueConverter {

    public boolean canConvert(Class type) {
        return type.equals(UUID.class);
    }

    public Object fromString(String str) {
        try {
            return UUID.fromString(str);
        } catch(IllegalArgumentException e) {
            throw new ConversionException("Cannot create UUID instance", e);
        }
    }

}
