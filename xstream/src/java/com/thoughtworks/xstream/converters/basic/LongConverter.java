/*
 * Copyright (C) 2003, 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2013 XStream Committers.
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
 * Converts a long primitive or java.lang.Long wrapper to a String.
 * 
 * @author Joe Walnes
 */
public class LongConverter extends AbstractSingleValueConverter {

    public boolean canConvert(Class type) {
        return type.equals(long.class) || type.equals(Long.class);
    }

    public Object fromString(String str) {
        int len = str.length();
        if (len == 0) {
            throw new NumberFormatException("For input string: \"\"");
        }
        if (len < 17) {
            return Long.decode(str);
        }
        char c0 = str.charAt(0);
        if (c0 != '0' && c0 != '#') {
            return Long.decode(str);
        }
        char c1 = str.charAt(1);
        final long high;
        final long low;
        if (c0 == '#' && len == 17) {
            high = Long.parseLong(str.substring(1, 9), 16) << 32;
            low = Long.parseLong(str.substring(9, 17), 16);
        } else if ((c1 == 'x' || c1 == 'X') && len == 18) {
            high = Long.parseLong(str.substring(2, 10), 16) << 32;
            low = Long.parseLong(str.substring(10, 18), 16);
        } else if (len == 23 && c1 == '1') {
            high = Long.parseLong(str.substring(1, 12), 8) << 33;
            low = Long.parseLong(str.substring(12, 23), 8);
        } else {
            return Long.decode(str);
        }
        final long num = high | low;
        return new Long(num);
    }

}
