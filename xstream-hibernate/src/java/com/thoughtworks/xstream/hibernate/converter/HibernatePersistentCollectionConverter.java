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
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.hibernate.util.Hibernate;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * A converter for Hibernate's PersistentBag, PersistentList and PersistentSet and for ListProxy
 * and SetProxy from Hibernate's Envers add-on. The converter will drop any reference to the
 * Hibernate collection and emit at serialization time an equivalent JDK collection instead.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public class HibernatePersistentCollectionConverter extends CollectionConverter {

    /**
     * Construct a HibernatePersistentCollectionConverter.
     * 
     * @param mapper
     * @since 1.4
     */
    public HibernatePersistentCollectionConverter(final Mapper mapper) {
        super(mapper);
    }

    public boolean canConvert(final Class type) {
        return type != null
            && (
                   type == Hibernate.PersistentBag
                || type == Hibernate.PersistentList
                || type == Hibernate.PersistentSet
                || type == Hibernate.EnversList
                || type == Hibernate.EnversSet
            );
    }

    public Object unmarshal(final HierarchicalStreamReader reader,
        final UnmarshallingContext context) {
        throw new ConversionException("Cannot deserialize Hibernate collection");
    }
}
