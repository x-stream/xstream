/*
 * Copyright (C) 2009 XStream Committers.
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

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

/**
 * A driver using the BEA StAX implementation.
 * 
 * @author J&ouml;rg Schaible
 * @since upcoming
 */
public class BEAStaxDriver extends StaxDriver {

    public BEAStaxDriver() {
        super();
    }

    public BEAStaxDriver(QNameMap qnameMap, XmlFriendlyReplacer replacer) {
        super(qnameMap, replacer);
    }

    public BEAStaxDriver(QNameMap qnameMap) {
        super(qnameMap);
    }

    public BEAStaxDriver(XmlFriendlyReplacer replacer) {
        super(replacer);
    }

    protected XMLInputFactory createInputFactory() {
        return new MXParserFactory();
    }

    protected XMLOutputFactory createOutputFactory() {
        return new XMLOutputFactoryBase();
    }

}
