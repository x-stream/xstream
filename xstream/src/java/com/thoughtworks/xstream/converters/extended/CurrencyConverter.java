/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 24. July 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.extended;

import java.util.Currency;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

/**
 * Converts a java.util.Currency to String. Despite the name of this class, it has nothing to do with converting
 * currencies between exchange rates! It makes sense in the context of XStream.
 *
 * @author Jose A. Illescas 
 * @author Joe Walnes
 */
public class CurrencyConverter extends AbstractSingleValueConverter {

    public boolean canConvert(Class type) {
        return type.equals(Currency.class);
    }

    public Object fromString(String str) {
        return Currency.getInstance(str);
    }

}
