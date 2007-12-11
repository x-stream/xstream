/*
 * Copyright (C) 2003, 2004 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 26. September 2003 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.basic;

/**
 * Converts a long primitive or java.lang.Long wrapper to
 * a String.
 *
 * @author Joe Walnes
 */
public class LongConverter extends AbstractSingleValueConverter {

    public boolean canConvert(Class type) {
        return type.equals(long.class) || type.equals(Long.class);
    }

    public Object fromString(String str) {
        return Long.decode(str);
    }

}
