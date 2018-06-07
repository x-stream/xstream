/*
 * Copyright (C) 2016, 2017, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 7. February 2016 by Aaron Johnson
 */
package com.thoughtworks.xstream.converters.extended;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.thoughtworks.xstream.converters.ConversionException;
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
        return type != null && Path.class.isAssignableFrom(type);
    }

    @Override
    public Path fromString(final String str) {
        try {
            try {
                final URI uri = new URI(str);
                if (uri.getScheme() == null || uri.getScheme().length() == 1) {
                    return Paths.get(File.separatorChar != '/' ? str.replace('/', File.separatorChar) : str);
                } else {
                    return Paths.get(uri);
                }
            } catch (final URISyntaxException e) {
                return Paths.get(str);
            }
        } catch (final InvalidPathException e) {
            throw new ConversionException(e);
        }
    }

    @Override
    public String toString(final Object obj) {
        final Path path = (Path)obj;
        if (path.getFileSystem() == FileSystems.getDefault()) {
            final String localPath = path.toString();
            if (File.separatorChar != '/') {
                return localPath.replace(File.separatorChar, '/');
            } else {
                return localPath;
            }
        } else {
            return path.toUri().toString();
        }
    }
}
