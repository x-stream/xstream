/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2014, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 25. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.basic;

import java.net.MalformedURLException;
import java.net.URL;

import com.thoughtworks.xstream.converters.ConversionException;


/**
 * Converts a {@link URL} to a string.
 * 
 * @author J. Matthew Pryor
 */
public class URLConverter extends AbstractSingleValueConverter {

    @Override
    public boolean canConvert(final Class<?> type) {
        return type == URL.class;
    }

    @Override
    public Object fromString(final String str) {
        try {
            return new URL(str);
        } catch (final MalformedURLException e) {
            throw new ConversionException(e);
        }
    }

}
