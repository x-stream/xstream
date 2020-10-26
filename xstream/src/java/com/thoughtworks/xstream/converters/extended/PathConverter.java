/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
    public boolean canConvert(final Class<?> type) {
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
