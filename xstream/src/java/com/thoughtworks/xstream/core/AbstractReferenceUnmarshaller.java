/*
 * Copyright (C) 2006, 2007, 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 15. March 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.core;

import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.core.util.FastStack;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * Abstract base class for a TreeUnmarshaller, that resolves references.
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 * @since 1.2
 */
public abstract class AbstractReferenceUnmarshaller extends TreeUnmarshaller {

    private Map values = new HashMap();
    private FastStack parentStack = new FastStack(16);

    public AbstractReferenceUnmarshaller(Object root, HierarchicalStreamReader reader,
                                     ConverterLookup converterLookup, Mapper mapper) {
        super(root, reader, converterLookup, mapper);
    }

    protected Object convert(Object parent, Class type, Converter converter) {
        if (parentStack.size() > 0) { // handles circular references
            Object parentReferenceKey = parentStack.peek();
            if (parentReferenceKey != null) {
                if (!values.containsKey(parentReferenceKey)) { // see AbstractCircularReferenceTest.testWeirdCircularReference()
                    values.put(parentReferenceKey, parent);
                }
            }
        }
        final Object result;
        String attributeName = getMapper().aliasForSystemAttribute("reference");
        String reference = attributeName == null ? null : reader.getAttribute(attributeName);
        if (reference != null) {
            result = values.get(getReferenceKey(reference));
            if (result == null) {
                final ConversionException ex = new ConversionException("Invalid reference");
                ex.add("reference", reference);
                throw ex;
            }
        } else {
            Object currentReferenceKey = getCurrentReferenceKey();
            parentStack.push(currentReferenceKey);
            result = super.convert(parent, type, converter);
            if (currentReferenceKey != null && result != null) {
                values.put(currentReferenceKey, result);
            }
            parentStack.popSilently();
        }
        return result;
    }
    
    protected abstract Object getReferenceKey(String reference);
    protected abstract Object getCurrentReferenceKey();
}
