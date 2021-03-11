/*
 * Copyright (C) 2021 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 02. January 2021 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.naming.NameCoder;

import io.github.xstream.mxparser.MXParser;

import org.xmlpull.v1.XmlPullParser;

/**
 * A {@link HierarchicalStreamDriver} for XPP DOM using the MXParser fork.
 *
 * @author J&ouml;rg Schaible
 * @since 1.4.16
 */
public class MXParserDomDriver extends AbstractXppDomDriver {

    /**
     * Construct an MXParserDomDriver.
     *
     * @since 1.4.16
     */
    public MXParserDomDriver() {
        super(new XmlFriendlyNameCoder());
    }

    /**
     * Construct an Xpp3DomDriver.
     *
     * @param nameCoder the replacer for XML friendly names
     * @since 1.4
     */
    public MXParserDomDriver(NameCoder nameCoder) {
        super(nameCoder);
    }

    /**
     * {@inheritDoc}
     */
    protected XmlPullParser createParser() {
        return new MXParser();
    }
}
