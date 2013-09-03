/*
 * Copyright (C) 2011, 2012, 2013 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 19. April 2011 by Joerg Schaible
 */
package com.thoughtworks.xstream.hibernate.converter;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.TreeMapConverter;
import com.thoughtworks.xstream.hibernate.util.Hibernate;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * A converter for Hibernate's PersistentSortedMap and for the SortedMapProxy from Hibernate's
 * Envers add-on. The converter will drop any reference to the Hibernate collection and emit at
 * serialization time an equivalent JDK collection instead.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public class HibernatePersistentSortedMapConverter extends TreeMapConverter {

    /**
     * Construct a HibernatePersistentSortedMapConverter.
     * 
     * @param mapper
     * @since 1.4
     */
    public HibernatePersistentSortedMapConverter(final Mapper mapper) {
        super(mapper);
    }

    public boolean canConvert(final Class type) {
        return type != null && (type == Hibernate.PersistentSortedMap || type == Hibernate.EnversSortedMap);
    }

    public Object unmarshal(final HierarchicalStreamReader reader,
        final UnmarshallingContext context) {
        throw new ConversionException("Cannot deserialize Hibernate collection");
    }
}
