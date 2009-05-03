/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamDriver;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * A {@link HierarchicalStreamDriver} for XPP DOM using the XmlPullParserFactory to locate an parser.
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class XppDomDriver extends AbstractXppDomDriver {

    private static XmlPullParserFactory factory;
    
    public XppDomDriver() {
        super(new XmlFriendlyReplacer());
    }

    /**
     * @since 1.2
     */
    public XppDomDriver(XmlFriendlyReplacer replacer) {
        super(replacer);
    }

    /**
     * {@inheritDoc}
     */
    protected synchronized XmlPullParser createParser() throws XmlPullParserException {
        if (factory == null) {
            factory = XmlPullParserFactory.newInstance(null, XppDriver.class);
        }
        return factory.newPullParser();
    }
}
