/*
 * Copyright (C) 2009, 2011, 2013 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 29. April 2009 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.StreamException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

/**
 * A driver using the JDK 6 StAX implementation of Sun.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 * @deprecated As of 1.4.5 use {@link StandardStaxDriver}
 */
public class SjsxpDriver extends StaxDriver {

    /**
     * @deprecated As of 1.4.5 use {@link StandardStaxDriver#StandardStaxDriver()}
     */
    public SjsxpDriver() {
        super();
    }

    /**
     * @deprecated As of 1.4.5 use {@link StandardStaxDriver#StandardStaxDriver(QNameMap, XmlFriendlyNameCoder)}
     */
    public SjsxpDriver(QNameMap qnameMap, XmlFriendlyNameCoder nameCoder) {
        super(qnameMap, nameCoder);
    }

    /**
     * @deprecated As of 1.4.5 use {@link StandardStaxDriver#StandardStaxDriver(QNameMap)}
     */
    public SjsxpDriver(QNameMap qnameMap) {
        super(qnameMap);
    }

    /**
     * @deprecated As of 1.4.5 use {@link StandardStaxDriver#StandardStaxDriver(XmlFriendlyNameCoder)}
     */
    public SjsxpDriver(XmlFriendlyNameCoder nameCoder) {
        super(nameCoder);
    }

    /**
     * @deprecated As of 1.4.5 use {@link StandardStaxDriver#createInputFactory()}
     */
    protected XMLInputFactory createInputFactory() {
        Exception exception = null;
        try {
            return (XMLInputFactory)Class.forName("com.sun.xml.internal.stream.XMLInputFactoryImpl").newInstance();
        } catch (InstantiationException e) {
            exception = e;
        } catch (IllegalAccessException e) {
            exception = e;
        } catch (ClassNotFoundException e) {
            exception = e;
        }
        throw new StreamException("Cannot create SJSXP (Sun JDK 6 StAX) XMLInputFaqctory instance.", exception);
    }

    /**
     * @deprecated As of 1.4.5 use {@link StandardStaxDriver#createOutputFactory()}
     */
    protected XMLOutputFactory createOutputFactory() {
        Exception exception = null;
        try {
            return (XMLOutputFactory)Class.forName("com.sun.xml.internal.stream.XMLOutputFactoryImpl").newInstance();
        } catch (InstantiationException e) {
            exception = e;
        } catch (IllegalAccessException e) {
            exception = e;
        } catch (ClassNotFoundException e) {
            exception = e;
        }
        throw new StreamException("Cannot create SJSXP (Sun JDK 6 StAX) XMLOutputFaqctory instance.", exception);
    }

}
