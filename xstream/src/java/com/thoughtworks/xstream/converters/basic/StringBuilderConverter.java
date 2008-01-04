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

/**
 * Converts the contents of a StringBuilder to XML.
 *
 * @author J&ouml;rg Schaible
 */
public class StringBuilderConverter extends AbstractSingleValueConverter {

    public Object fromString(String str) {
        return new StringBuilder(str);
    }

    public boolean canConvert(Class type) {
        return type.equals(StringBuilder.class);
    }
}
