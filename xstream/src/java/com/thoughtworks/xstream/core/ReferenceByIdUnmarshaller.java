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
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;


public class ReferenceByIdUnmarshaller extends AbstractReferenceUnmarshaller<String> {

    public ReferenceByIdUnmarshaller(
            final Object root, final HierarchicalStreamReader reader, final ConverterLookup converterLookup,
            final Mapper mapper) {
        super(root, reader, converterLookup, mapper);
    }

    @Override
    protected String getReferenceKey(final String reference) {
        return reference;
    }

    @Override
    protected String getCurrentReferenceKey() {
        final String attributeName = getMapper().aliasForSystemAttribute("id");
        return attributeName == null ? null : reader.getAttribute(attributeName);
    }
}
