/*
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. April 2006 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

import java.nio.charset.Charset;

/**
 * Converts a java.nio.charset.Carset to a string.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2
 */
public class CharsetConverter extends AbstractSingleValueConverter {

    public boolean canConvert(Class type) {
        return Charset.class.isAssignableFrom(type);
    }

    public String toString(Object obj) {
        return obj == null ? null : ((Charset)obj).name();
    }


    public Object fromString(String str) {
        return Charset.forName(str);
    }
}