/*
 * Copyright (C) 2007, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 11. January 2007 by Konstantin Pribluda
 */
package com.thoughtworks.xstream.hibernate.converter;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import org.hibernate.proxy.HibernateProxy;


/**
 * Converter for Hibernate proxy instances. The converter will effectively remove any trace of
 * the proxy.
 * 
 * @author Konstantin Pribluda
 * @author J&ouml;rg Schaible
 */
public class HibernateProxyConverter implements Converter {
    public boolean canConvert(final Class clazz) {
        // be responsible for Hibernate proxy.
        return HibernateProxy.class.isAssignableFrom(clazz);
    }

    public void marshal(final Object object, final HierarchicalStreamWriter writer,
        final MarshallingContext context) {
        final Object item = ((HibernateProxy)object)
            .getHibernateLazyInitializer()
            .getImplementation();
        context.convertAnother(item);
    }

    public Object unmarshal(final HierarchicalStreamReader reader,
        final UnmarshallingContext context) {
        throw new ConversionException("Cannot deserialize Hibernate proxy");
    }
}
