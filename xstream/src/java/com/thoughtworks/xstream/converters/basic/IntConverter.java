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
 * Converts an int primitive or java.lang.Integer wrapper to
 * a String.
 *
 * @author Joe Walnes
 */
public class IntConverter extends AbstractSingleValueConverter {

    public boolean canConvert(Class type) {
        return type.equals(int.class) || type.equals(Integer.class);
    }

    public Object fromString(String str) {
    	long value = Long.decode(str).longValue();
    	if(value < Integer.MIN_VALUE || value > 0xFFFFFFFFl) {
    		throw new NumberFormatException("For input string: \"" + str + '"');
    	}
        return new Integer((int)value);
    }

}
