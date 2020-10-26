/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2014, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 13. January 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.extended;

import java.io.File;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;


/**
 * Converts a {@link File}.
 * 
 * <p>This converter will take care of storing and retrieving {@link File} with either an absolute path OR a relative path
 * depending on how they were created.</p>
 * 
 * @author Joe Walnes
 */
public class FileConverter extends AbstractSingleValueConverter {

    @Override
    public boolean canConvert(final Class<?> type) {
        return type == File.class;
    }

    @Override
    public Object fromString(final String str) {
        return new File(str);
    }

    @Override
    public String toString(final Object obj) {
        return ((File)obj).getPath();
    }

}
