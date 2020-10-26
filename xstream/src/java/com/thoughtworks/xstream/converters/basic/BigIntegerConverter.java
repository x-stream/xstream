/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2014, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. May 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.basic;

import java.math.BigInteger;


/**
 * Converts a {@link BigInteger} to a string.
 * 
 * @author Joe Walnes
 */
public class BigIntegerConverter extends AbstractSingleValueConverter {

    @Override
    public boolean canConvert(final Class<?> type) {
        return type == BigInteger.class;
    }

    @Override
    public Object fromString(final String str) {
        return new BigInteger(str);
    }

}
