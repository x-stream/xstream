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

import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import com.thoughtworks.xstream.io.naming.NameCoder;


/**
 * A driver using the Woodstox StAX implementation.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public class WstxDriver extends StaxDriver {

    public WstxDriver() {
        super();
    }

    /**
     * @deprecated As of 1.4.6 use {@link #WstxDriver(QNameMap, NameCoder)}
     */
    @Deprecated
    public WstxDriver(final QNameMap qnameMap, final XmlFriendlyNameCoder nameCoder) {
        super(qnameMap, nameCoder);
    }

    /**
     * @since 1.4.6
     */
    public WstxDriver(final QNameMap qnameMap, final NameCoder nameCoder) {
        super(qnameMap, nameCoder);
    }

    public WstxDriver(final QNameMap qnameMap) {
        super(qnameMap);
    }

    /**
     * @deprecated As of 1.4.6 use {@link #WstxDriver(NameCoder)}
     */
    @Deprecated
    public WstxDriver(final XmlFriendlyNameCoder nameCoder) {
        super(nameCoder);
    }

    /**
     * @since 1.4.6
     */
    public WstxDriver(final NameCoder nameCoder) {
        super(nameCoder);
    }

    @Override
    protected XMLInputFactory createInputFactory() {
        final XMLInputFactory instance = new WstxInputFactory();
        instance.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        return instance;
    }

    @Override
    protected XMLOutputFactory createOutputFactory() {
        return new WstxOutputFactory();
    }

}
