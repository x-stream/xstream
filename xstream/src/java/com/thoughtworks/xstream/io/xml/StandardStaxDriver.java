/*
 * Copyright (C) 2013 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 27. July 2013 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.naming.NameCoder;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;


/**
 * A driver using the standard JDK StAX implementation provided by the Java runtime (since Java
 * 6).
 * <p>
 * In contrast to XMLInputFactory.newFactory() or XMLOutputFactory.newFactory() this
 * implementation will ignore any implementations provided with the system properties
 * <em>javax.xml.stream.XMLInputFactory</em> and <em>javax.xml.stream.XMLOutputFactory</em>, all
 * implementations configured in <em>lib/stax.properties</em> or registered with the Service
 * API.
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
    public StandardStaxDriver(QNameMap qnameMap, XmlFriendlyNameCoder nameCoder) {
        super(qnameMap, nameCoder);
    }

    /**
     * @since 1.4.6
     */
    public StandardStaxDriver(QNameMap qnameMap, NameCoder nameCoder) {
        super(qnameMap, nameCoder);
    }

    public StandardStaxDriver(QNameMap qnameMap) {
        super(qnameMap);
    }

    /**
     * @deprecated As of 1.4.6 use {@link #StandardStaxDriver(NameCoder)}
     */
    public StandardStaxDriver(XmlFriendlyNameCoder nameCoder) {
        super(nameCoder);
    }

    /**
     * @since 1.4.6
     */
    public StandardStaxDriver(NameCoder nameCoder) {
        super(nameCoder);
    }

    protected XMLInputFactory createInputFactory() {
        Exception exception = null;
        try {
            Class staxInputFactory = JVM.getStaxInputFactory();
            if (staxInputFactory != null) {
                return (XMLInputFactory)staxInputFactory.newInstance();
            } else {
                throw new StreamException("Java runtime has no standard XMLInputFactory implementation.", exception);
            }
        } catch (InstantiationException e) {
            exception = e;
        } catch (IllegalAccessException e) {
            exception = e;
        } catch (ClassNotFoundException e) {
            exception = e;
        }
        throw new StreamException("Cannot create standard XMLInputFactory instance of Java runtime.", exception);
    }

    protected XMLOutputFactory createOutputFactory() {
        Exception exception = null;
        try {
            Class staxOutputFactory = JVM.getStaxOutputFactory();
            if (staxOutputFactory != null) {
                return (XMLOutputFactory)staxOutputFactory.newInstance();
            } else {
                throw new StreamException("Java runtime has no standard XMLOutputFactory implementation.", exception);
            }
        } catch (InstantiationException e) {
            exception = e;
        } catch (IllegalAccessException e) {
            exception = e;
        } catch (ClassNotFoundException e) {
            exception = e;
        }
        throw new StreamException("Cannot create standard XMLOutputFactory instance of Java runtime.", exception);
    }

}
