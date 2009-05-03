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


import com.thoughtworks.xstream.io.HierarchicalStreamDriver;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;


/**
 * A {@link HierarchicalStreamDriver} using the kXML2 parser.
 * 
 * @author J&ouml;rg Schaible
 * @since upcoming
 */
public class KXml2Driver extends AbstractXppDriver {

    /**
     * Construct a KXml2Driver.
     * 
     * @since upcoming
     */
    public KXml2Driver() {
        super(new XmlFriendlyReplacer());
    }

    /**
     * Construct a KXml2Driver.
     * 
     * @param replacer the replacer for XML friendly names
     * @since upcoming
     */
    public KXml2Driver(XmlFriendlyReplacer replacer) {
        super(replacer);
    }

    /**
     * {@inheritDoc}
     */
    protected XmlPullParser createParser() {
        return new KXmlParser();
    }
}
