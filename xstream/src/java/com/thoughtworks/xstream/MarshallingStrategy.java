/*
 * Copyright (C) 2004, 2006 Joe Walnes.
 * Copyright (C) 2007, 2009, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 16. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream;

import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Core interface for a marshalling strategy.
 * <p>
 * An implementation dictates how an object graph is marshalled and unmarshalled. It is the implementation's
 * responsibility to deal with references between the objects.
 * </p>
 */
public interface MarshallingStrategy {

    /**
     * Marshal an object graph.
     * 
     * @param writer the target for the marshalled data
     * @param obj the object to marshal
     * @param converterLookup the converter store
     * @param mapper the mapper chain
     * @param dataHolder the holder for additional data and state while marshalling
     */
    void marshal(HierarchicalStreamWriter writer, Object obj, ConverterLookup converterLookup, Mapper mapper,
            DataHolder dataHolder);

    /**
     * Unmarshal an object graph.
     * 
     * @param root a possible root object (should be {@code null} in normal cases)
     * @param reader the source for the unmarshalled object data
     * @param dataHolder the holder for additional data and state while marshalling
     * @param converterLookup the converter store
     * @param mapper the mapper chain
     * @return the unmarshalled object
     */
    Object unmarshal(Object root, HierarchicalStreamReader reader, DataHolder dataHolder, ConverterLookup converterLookup,
            Mapper mapper);
}
