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

import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;


/**
 * A {@link HierarchicalStreamDriver} using the Xpp3 parser.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public class Xpp3Driver extends AbstractXppDriver {

    /**
     * Construct an Xpp3Driver.
     * 
     * @since 1.4
     */
    public Xpp3Driver() {
        super(new XmlFriendlyNameCoder());
    }

    /**
     * Construct an Xpp3Driver.
     * 
     * @param nameCoder the replacer for XML friendly names
     * @since 1.4
     */
    public Xpp3Driver(NameCoder nameCoder) {
        super(nameCoder);
    }

    /**
     * {@inheritDoc}
     */
    protected XmlPullParser createParser() {
        return new MXParser();
    }
}
