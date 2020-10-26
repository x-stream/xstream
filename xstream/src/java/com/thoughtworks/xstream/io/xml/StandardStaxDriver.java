/*
 * Copyright (C) 2013, 2014, 2015, 2020 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 27. July 2013 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.naming.NameCoder;


/**
 * A driver using the standard JDK StAX implementation provided by the Java runtime.
 * <p>
 * In contrast to XMLInputFactory.newFactory() or XMLOutputFactory.newFactory() this implementation will ignore any
 * implementations provided with the system properties <em>javax.xml.stream.XMLInputFactory</em> and
 * <em>javax.xml.stream.XMLOutputFactory</em>, all implementations configured in <em>lib/stax.properties</em> or
 * registered with the Service API.
 * </p>
 *
 * @author J&ouml;rg Schaible
 * @since 1.4.5
 */
public class StandardStaxDriver extends StaxDriver {

    public StandardStaxDriver() {
        super();
    }

    /**
     * @deprecated As of 1.4.6 use {@link #StandardStaxDriver(QNameMap, NameCoder)}
     */
    @Deprecated
    public StandardStaxDriver(final QNameMap qnameMap, final XmlFriendlyNameCoder nameCoder) {
        super(qnameMap, nameCoder);
    }

    /**
     * @since 1.4.6
     */
    public StandardStaxDriver(final QNameMap qnameMap, final NameCoder nameCoder) {
        super(qnameMap, nameCoder);
    }

    public StandardStaxDriver(final QNameMap qnameMap) {
        super(qnameMap);
    }

    /**
     * @deprecated As of 1.4.6 use {@link #StandardStaxDriver(NameCoder)}
     */
    @Deprecated
    public StandardStaxDriver(final XmlFriendlyNameCoder nameCoder) {
        super(nameCoder);
    }

    /**
     * @since 1.4.6
     */
    public StandardStaxDriver(final NameCoder nameCoder) {
        super(nameCoder);
    }

    @Override
    protected XMLInputFactory createInputFactory() {
        Exception exception = null;
        try {
            final Class<? extends XMLInputFactory> staxInputFactory = JVM.getStaxInputFactory();
            if (staxInputFactory != null) {
                final XMLInputFactory instance = staxInputFactory.newInstance();
                instance.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
                return instance;
            } else {
                throw new StreamException("Java runtime has no standard XMLInputFactory implementation.", exception);
            }
        } catch (final InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            exception = e;
        }
        throw new StreamException("Cannot create standard XMLInputFactory instance of Java runtime.", exception);
    }

    @Override
    protected XMLOutputFactory createOutputFactory() {
        Exception exception = null;
        try {
            final Class<? extends XMLOutputFactory> staxOutputFactory = JVM.getStaxOutputFactory();
            if (staxOutputFactory != null) {
                return staxOutputFactory.newInstance();
            } else {
                throw new StreamException("Java runtime has no standard XMLOutputFactory implementation.", exception);
            }
        } catch (final InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            exception = e;
        }
        throw new StreamException("Cannot create standard XMLOutputFactory instance of Java runtime.", exception);
    }

}
