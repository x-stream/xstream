/*
 * Copyright (C) 2009, 2011, 2014, 2015, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 29. April 2009 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml;

import com.bea.xml.stream.MXParserFactory;
import com.bea.xml.stream.XMLOutputFactoryBase;
import com.thoughtworks.xstream.io.naming.NameCoder;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

/**
 * A driver using the BEA StAX implementation.
 *
 * @author J&ouml;rg Schaible
 * @since 1.4
 * @deprecated As of 1.4.11 use {@link StandardStaxDriver} or {@link WstxDriver} instead. BEA StAX implementation is
 *              outdated, unmaintained and has security issues.
 */
public class BEAStaxDriver extends StaxDriver {

    /**
     * @deprecated As of 1.4.11 use {@link StandardStaxDriver} or {@link WstxDriver} instead.
     */
    public BEAStaxDriver() {
        super();
    }

    /**
     * @deprecated As of 1.4.6 use {@link #BEAStaxDriver(QNameMap, NameCoder)}
     */
    public BEAStaxDriver(QNameMap qnameMap, XmlFriendlyNameCoder nameCoder) {
        super(qnameMap, nameCoder);
    }

    /**
     * @since 1.4.6
     * @deprecated As of 1.4.11 use {@link StandardStaxDriver} or {@link WstxDriver} instead.
     */
    public BEAStaxDriver(QNameMap qnameMap, NameCoder nameCoder) {
        super(qnameMap, nameCoder);
    }

    /**
     * @deprecated As of 1.4.11 use {@link StandardStaxDriver} or {@link WstxDriver} instead.
     */
    public BEAStaxDriver(final QNameMap qnameMap) {
        super(qnameMap);
    }

    /**
     * @deprecated As of 1.4.6 use {@link #BEAStaxDriver(NameCoder)}
     */
    public BEAStaxDriver(XmlFriendlyNameCoder nameCoder) {
        super(nameCoder);
    }

    /**
     * @since 1.4.6
     * @deprecated As of 1.4.11 use {@link StandardStaxDriver} or {@link WstxDriver} instead.
     */
    public BEAStaxDriver(NameCoder nameCoder) {
        super(nameCoder);
    }

    protected XMLInputFactory createInputFactory() {
        final XMLInputFactory instance = new MXParserFactory();
        instance.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
//        if (instance.isPropertySupported(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES))
//            throw new IllegalStateException("Should not support external entities now!");
        return instance;
    }

    protected XMLOutputFactory createOutputFactory() {
        return new XMLOutputFactoryBase();
    }

}
