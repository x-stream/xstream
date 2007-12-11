/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 03. April 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.path.Path;
import com.thoughtworks.xstream.io.path.PathTracker;
import com.thoughtworks.xstream.io.path.PathTrackingReader;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReader;
import com.thoughtworks.xstream.mapper.Mapper;

public class ReferenceByXPathUnmarshaller extends AbstractReferenceUnmarshaller {

    private PathTracker pathTracker = new PathTracker();
    protected boolean isXmlFriendly;

    public ReferenceByXPathUnmarshaller(Object root, HierarchicalStreamReader reader,
                                        ConverterLookup converterLookup, Mapper mapper) {
        super(root, reader, converterLookup, mapper);
        this.reader = new PathTrackingReader(reader, pathTracker);
        isXmlFriendly = reader.underlyingReader() instanceof XmlFriendlyReader;
    }

    /**
     * @deprecated As of 1.2, use {@link #ReferenceByXPathUnmarshaller(Object, HierarchicalStreamReader, ConverterLookup, Mapper)}
     */
    public ReferenceByXPathUnmarshaller(Object root, HierarchicalStreamReader reader,
                                        ConverterLookup converterLookup, ClassMapper classMapper) {
        this(root, reader, converterLookup, (Mapper)classMapper);
    }

    protected Object getReferenceKey(String reference) {
        final Path path = new Path(isXmlFriendly ? ((XmlFriendlyReader)reader.underlyingReader()).unescapeXmlName(reference) : reference);
        // We have absolute references, if path starts with '/'
        return reference.charAt(0) != '/' ? pathTracker.getPath().apply(path) : path;
    }

    protected Object getCurrentReferenceKey() {
        return pathTracker.getPath();
    }

}
