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
 * Converts a boolean primitive or java.lang.Boolean wrapper to
 * a String.
 *
 * @author Joe Walnes
 * @author David Blevins
 */
public class BooleanConverter extends AbstractSingleValueConverter {

    public static final BooleanConverter TRUE_FALSE = new BooleanConverter("true", "false", false);

    public static final BooleanConverter YES_NO = new BooleanConverter("yes", "no", false);

    public static final BooleanConverter BINARY = new BooleanConverter("1", "0", true);

    private final String positive;
    private final String negative;
    private final boolean caseSensitive;

    public BooleanConverter(final String positive, final String negative, final boolean caseSensitive) {
        this.positive = positive;
        this.negative = negative;
        this.caseSensitive = caseSensitive;
    }

    public BooleanConverter() {
        this("true", "false", false);
    }

    public boolean shouldConvert(final Class type, final Object value) {
        return true;
    }

    public boolean canConvert(final Class type) {
        return type.equals(boolean.class) || type.equals(Boolean.class);
    }

    public Object fromString(final String str) {
        if (caseSensitive) {
            return positive.equals(str) ? Boolean.TRUE : Boolean.FALSE;
        } else {
            return positive.equalsIgnoreCase(str) ? Boolean.TRUE : Boolean.FALSE;
        }
    }

    public String toString(final Object obj) {
        final Boolean value = (Boolean) obj;
        return obj == null ? null : value.booleanValue() ? positive : negative;
    }
}
