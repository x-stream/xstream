/*
 * Copyright (C) 2009, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 29. April 2009 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml;

import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import com.thoughtworks.xstream.io.naming.NameCoder;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

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
    public WstxDriver(QNameMap qnameMap, XmlFriendlyNameCoder nameCoder) {
        super(qnameMap, nameCoder);
    }

    /**
     * @since 1.4.6
     */
    public WstxDriver(QNameMap qnameMap, NameCoder nameCoder) {
        super(qnameMap, nameCoder);
    }

    public WstxDriver(QNameMap qnameMap) {
        super(qnameMap);
    }

    /**
     * @deprecated As of 1.4.6 use {@link #WstxDriver(NameCoder)}
     */
    public WstxDriver(XmlFriendlyNameCoder nameCoder) {
        super(nameCoder);
    }

    /**
     * @since 1.4.6
     */
    public WstxDriver(NameCoder nameCoder) {
        super(nameCoder);
    }

    protected XMLInputFactory createInputFactory() {
        return new WstxInputFactory();
    }

    protected XMLOutputFactory createOutputFactory() {
        return new WstxOutputFactory();
    }

}
