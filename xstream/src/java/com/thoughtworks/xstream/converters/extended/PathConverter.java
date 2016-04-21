/*
 * Copyright (C) 2016 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 7. February 2016 by Aaron Johnson
 */
package com.thoughtworks.xstream.converters.extended;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;


/**
 * Converts a {@link Path} to string.
 *
 * @author Aaron Johnson
 * @author J&ouml;rg Schaible
 */
public class PathConverter extends AbstractSingleValueConverter {

    @Override
    public boolean canConvert(@SuppressWarnings("rawtypes") final Class type) {
        return Path.class.isAssignableFrom(type);
    }

    @Override
    public Path fromString(final String str) {
        try {
            final URI uri = new URI(str);
            if (uri.getScheme() == null) {
                return Paths.get(str);
            } else {
                return Paths.get(uri);
            }
        } catch (final URISyntaxException e) {
            return Paths.get(str);
        }
    }

    @Override
    public String toString(final Object obj) {
        final Path path = (Path)obj;
        if (path.getFileSystem() == FileSystems.getDefault()) {
            return path.toString();
        } else {
            return path.toUri().toString();
        }
    }
}
