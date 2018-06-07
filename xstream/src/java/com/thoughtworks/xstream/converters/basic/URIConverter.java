/*
 * Copyright (C) 2010, 2014, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 3. August 2010 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.basic;

import java.net.URI;
import java.net.URISyntaxException;

import com.thoughtworks.xstream.converters.ConversionException;


/**
 * Converts a {@link URI} to a string.
 * 
 * @author Carlos Roman
 */
public class URIConverter extends AbstractSingleValueConverter {

    @Override
    public boolean canConvert(final Class<?> type) {
        return type == URI.class;
    }

    @Override
    public Object fromString(final String str) {
        try {
            return new URI(str);
        } catch (final URISyntaxException e) {
            throw new ConversionException(e);
        }
    }
}
