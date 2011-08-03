/*
 * Copyright (C) 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 03. August 2011 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml;

import java.io.Reader;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;

import com.thoughtworks.xstream.io.naming.NameCoder;

/**
 * XStream reader that pulls directly from the stream using the kXML2 XmlPullParser implementation.
 *
 * @author J&ouml;rg Schaible
 */
public class KXml2Reader extends AbstractXppReader {

    /**
     * Construct an KXml2Reader.
     * 
     * @param reader the reader with the input data
     * @since upcoming
     */
    public KXml2Reader(Reader reader) {
        this(reader, new XmlFriendlyNameCoder());
    }

    /**
     * Construct an KXml2Reader.
     * 
     * @param reader the reader with the input data
     * @param nameCoder the coder for XML friendly tag and attribute names
     * @since upcoming
     */
    public KXml2Reader(Reader reader, NameCoder nameCoder) {
        super(reader, nameCoder);
    }
    
    /**
     * {@inheritDoc}
     */
    protected synchronized XmlPullParser createParser() {
        return new KXmlParser();
    }
}