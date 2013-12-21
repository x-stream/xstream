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
 */
public class BEAStaxDriver extends StaxDriver {

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
     */
    public BEAStaxDriver(QNameMap qnameMap, NameCoder nameCoder) {
        super(qnameMap, nameCoder);
    }

    public BEAStaxDriver(QNameMap qnameMap) {
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
     */
    public BEAStaxDriver(NameCoder nameCoder) {
        super(nameCoder);
    }

    protected XMLInputFactory createInputFactory() {
        return new MXParserFactory();
    }

    protected XMLOutputFactory createOutputFactory() {
        return new XMLOutputFactoryBase();
    }

}
