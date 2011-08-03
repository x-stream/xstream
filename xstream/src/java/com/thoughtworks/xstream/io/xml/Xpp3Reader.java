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

import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;

import com.thoughtworks.xstream.io.naming.NameCoder;

/**
 * XStream reader that pulls directly from the stream using the Xpp3 XmlPullParser implementation.
 *
 * @author J&ouml;rg Schaible
 */
public class Xpp3Reader extends AbstractXppReader {

    /**
     * Construct an Xpp3Reader.
     * 
     * @param reader the reader with the input data
     * @since upcoming
     */
    public Xpp3Reader(Reader reader) {
        this(reader, new XmlFriendlyNameCoder());
    }

    /**
     * Construct an Xpp3Reader.
     * 
     * @param reader the reader with the input data
     * @param nameCoder the coder for XML friendly tag and attribute names
     * @since upcoming
     */
    public Xpp3Reader(Reader reader, NameCoder nameCoder) {
        super(reader, nameCoder);
    }
    
    /**
     * {@inheritDoc}
     */
    protected synchronized XmlPullParser createParser() {
        return new MXParser();
    }
}