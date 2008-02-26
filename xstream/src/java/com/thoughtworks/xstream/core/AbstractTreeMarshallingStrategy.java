/*
 * Copyright (C) 2006, 2007, 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 26.09.2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.MarshallingStrategy;
import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Basic functionality of a tree based marshalling strategy.
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @since 1.3
 */
public abstract class AbstractTreeMarshallingStrategy implements MarshallingStrategy {

    public Object unmarshal(Object root, HierarchicalStreamReader reader, DataHolder dataHolder, ConverterLookup converterLookup, Mapper mapper) {
        TreeUnmarshaller context = createUnmarshallingContext(root, reader, converterLookup, mapper);
        return context.start(dataHolder);
    }

    public void marshal(HierarchicalStreamWriter writer, Object obj, ConverterLookup converterLookup, Mapper mapper, DataHolder dataHolder) {
        TreeMarshaller context = createMarshallingContext(writer, converterLookup, mapper);
        context.start(obj, dataHolder);
    }
    
    protected abstract TreeUnmarshaller createUnmarshallingContext(Object root,
        HierarchicalStreamReader reader, ConverterLookup converterLookup, Mapper mapper);

    protected abstract TreeMarshaller createMarshallingContext(
        HierarchicalStreamWriter writer, ConverterLookup converterLookup, Mapper mapper);

    /**
     * @deprecated As of 1.2, use {@link #unmarshal(Object, HierarchicalStreamReader, DataHolder, ConverterLookup, Mapper)}
     */
    public Object unmarshal(Object root, HierarchicalStreamReader reader, DataHolder dataHolder, DefaultConverterLookup converterLookup, ClassMapper classMapper) {
        return unmarshal(root, reader, dataHolder, (ConverterLookup)converterLookup, (Mapper)classMapper);
    }

    /**
     * @deprecated As of 1.2, use {@link #marshal(HierarchicalStreamWriter, Object, ConverterLookup, Mapper, DataHolder)}
     */
    public void marshal(HierarchicalStreamWriter writer, Object obj, DefaultConverterLookup converterLookup, ClassMapper classMapper, DataHolder dataHolder) {
        marshal(writer, obj, converterLookup, (Mapper)classMapper, dataHolder);
    }

}
