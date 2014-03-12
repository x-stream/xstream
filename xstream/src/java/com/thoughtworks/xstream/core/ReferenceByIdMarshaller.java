/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 15. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.path.Path;
import com.thoughtworks.xstream.mapper.Mapper;


public class ReferenceByIdMarshaller extends AbstractReferenceMarshaller<String> {

    private final IDGenerator idGenerator;

    public static interface IDGenerator {
        String next(Object item);
    }

    public ReferenceByIdMarshaller(
            final HierarchicalStreamWriter writer, final ConverterLookup converterLookup, final Mapper mapper,
            final IDGenerator idGenerator) {
        super(writer, converterLookup, mapper);
        this.idGenerator = idGenerator;
    }

    public ReferenceByIdMarshaller(
            final HierarchicalStreamWriter writer, final ConverterLookup converterLookup, final Mapper mapper) {
        this(writer, converterLookup, mapper, new SequenceGenerator(1));
    }

    @Override
    protected String createReference(final Path currentPath, final String existingReferenceKey) {
        return existingReferenceKey.toString();
    }

    @Override
    protected String createReferenceKey(final Path currentPath, final Object item) {
        return idGenerator.next(item);
    }

    @Override
    protected void fireValidReference(final String referenceKey) {
        final String attributeName = getMapper().aliasForSystemAttribute("id");
        if (attributeName != null) {
            writer.addAttribute(attributeName, referenceKey.toString());
        }
    }
}
