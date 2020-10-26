/*
 * Copyright (C) 2004, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2014 XStream Committers.
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
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.path.Path;
import com.thoughtworks.xstream.mapper.Mapper;


public class ReferenceByXPathMarshaller extends AbstractReferenceMarshaller<Path> {

    private final int mode;

    public ReferenceByXPathMarshaller(
            final HierarchicalStreamWriter writer, final ConverterLookup converterLookup, final Mapper mapper,
            final int mode) {
        super(writer, converterLookup, mapper);
        this.mode = mode;
    }

    @Override
    protected String createReference(final Path currentPath, final Path existingReferenceKey) {
        final Path existingPath = existingReferenceKey;
        final Path referencePath = (mode & ReferenceByXPathMarshallingStrategy.ABSOLUTE) > 0
            ? existingPath
            : currentPath.relativeTo(existingPath);
        return (mode & ReferenceByXPathMarshallingStrategy.SINGLE_NODE) > 0 ? referencePath.explicit() : referencePath
            .toString();
    }

    @Override
    protected Path createReferenceKey(final Path currentPath, final Object item) {
        return currentPath;
    }

    @Override
    protected void fireValidReference(final Path referenceKey) {
        // nothing to do
    }
}
