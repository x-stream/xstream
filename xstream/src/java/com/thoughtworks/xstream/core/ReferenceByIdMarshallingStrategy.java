/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 16. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;


public class ReferenceByIdMarshallingStrategy extends AbstractTreeMarshallingStrategy {

    @Override
    protected TreeUnmarshaller createUnmarshallingContext(final Object root, final HierarchicalStreamReader reader,
            final ConverterLookup converterLookup, final Mapper mapper) {
        return new ReferenceByIdUnmarshaller(root, reader, converterLookup, mapper);
    }

    @Override
    protected TreeMarshaller createMarshallingContext(final HierarchicalStreamWriter writer,
            final ConverterLookup converterLookup, final Mapper mapper) {
        return new ReferenceByIdMarshaller(writer, converterLookup, mapper);
    }
}
