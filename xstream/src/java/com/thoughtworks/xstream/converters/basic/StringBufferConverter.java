/*
 * Copyright (C) 2003, 2004 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 29. September 2003 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.basic;

/**
 * Converts the contents of a StringBuffer to XML.
 *
 * @author Joe Walnes
 */
public class StringBufferConverter extends AbstractSingleValueConverter {

    public Object fromString(String str) {
        return new StringBuffer(str);
    }

    public boolean canConvert(Class type) {
        return type.equals(StringBuffer.class);
    }
}
