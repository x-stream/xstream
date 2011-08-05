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


import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.naming.NameCoder;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;


/**
 * A {@link HierarchicalStreamDriver} using the kXML2 parser.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public class KXml2Driver extends AbstractXppDriver {

    /**
     * Construct a KXml2Driver.
     * 
     * @since 1.4
     */
    public KXml2Driver() {
        super(new XmlFriendlyNameCoder());
    }

    /**
     * Construct a KXml2Driver.
     * 
     * @param nameCoder the replacer for XML friendly names
     * @since 1.4
     */
    public KXml2Driver(NameCoder nameCoder) {
        super(nameCoder);
    }

    /**
     * {@inheritDoc}
     */
    protected XmlPullParser createParser() {
        return new KXmlParser();
    }
}
