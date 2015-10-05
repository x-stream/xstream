/*
 * Copyright (C) 2009, 2011, 2014, 2015 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 29. April 2009 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import com.bea.xml.stream.MXParserFactory;
import com.bea.xml.stream.XMLOutputFactoryBase;
import com.thoughtworks.xstream.io.naming.NameCoder;


/**
 * A driver using the BEA StAX implementation.
 *
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public class BEAStaxDriver extends StaxDriver {

    public BEAStaxDriver() {
        super();
    }

    /**
     * @deprecated As of 1.4.6 use {@link #BEAStaxDriver(QNameMap, NameCoder)}
     */
    @Deprecated
    public BEAStaxDriver(final QNameMap qnameMap, final XmlFriendlyNameCoder nameCoder) {
        super(qnameMap, nameCoder);
    }

    /**
     * @since 1.4.6
     */
    public BEAStaxDriver(final QNameMap qnameMap, final NameCoder nameCoder) {
        super(qnameMap, nameCoder);
    }

    public BEAStaxDriver(final QNameMap qnameMap) {
        super(qnameMap);
    }

    /**
     * @deprecated As of 1.4.6 use {@link #BEAStaxDriver(NameCoder)}
     */
    @Deprecated
    public BEAStaxDriver(final XmlFriendlyNameCoder nameCoder) {
        super(nameCoder);
    }

    /**
     * @since 1.4.6
     */
    public BEAStaxDriver(final NameCoder nameCoder) {
        super(nameCoder);
    }

    @Override
    protected XMLInputFactory createInputFactory() {
        final XMLInputFactory instance = new MXParserFactory();
        instance.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
//        if (instance.isPropertySupported(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES))
//            throw new IllegalStateException("Should not support external entities now!");
        return instance;
    }

    @Override
    protected XMLOutputFactory createOutputFactory() {
        return new XMLOutputFactoryBase();
    }

}
