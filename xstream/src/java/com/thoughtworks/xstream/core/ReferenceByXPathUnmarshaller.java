/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2011, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 03. April 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.io.AbstractReader;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.path.Path;
import com.thoughtworks.xstream.io.path.PathTracker;
import com.thoughtworks.xstream.io.path.PathTrackingReader;
import com.thoughtworks.xstream.mapper.Mapper;


public class ReferenceByXPathUnmarshaller extends AbstractReferenceUnmarshaller<Path> {

    private final PathTracker pathTracker = new PathTracker();
    protected boolean isNameEncoding;

    public ReferenceByXPathUnmarshaller(
            final Object root, final HierarchicalStreamReader reader, final ConverterLookup converterLookup,
            final Mapper mapper) {
        super(root, reader, converterLookup, mapper);
        this.reader = new PathTrackingReader(reader, pathTracker);
        isNameEncoding = reader.underlyingReader() instanceof AbstractReader;
    }

    @Override
    protected Path getReferenceKey(final String reference) {
        final Path path = new Path(isNameEncoding
            ? ((AbstractReader)reader.underlyingReader()).decodeNode(reference)
            : reference);
        // We have absolute references, if path starts with '/'
        return reference.charAt(0) != '/' ? pathTracker.getPath().apply(path) : path;
    }

    @Override
    protected Path getCurrentReferenceKey() {
        return pathTracker.getPath();
    }

}
