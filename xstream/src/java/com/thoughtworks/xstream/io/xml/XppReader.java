/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 08. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.io.xml;

import java.io.Reader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.thoughtworks.xstream.io.naming.NameCoder;

/**
 * XStream reader that pulls directly from the stream using the XmlPullParser API.
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class XppReader extends AbstractXppReader {

    private static XmlPullParserFactory factory;

    /**
     * Construct an XppReader.
     * 
     * @param reader the reader with the input data
     * @since upcoming
     */
    public XppReader(Reader reader) {
        this(reader, new XmlFriendlyNameCoder());
    }

    /**
     * Construct an XppReader.
     * 
     * @param reader the reader with the input data
     * @param nameCoder the coder for XML friendly tag and attribute names
     * @since upcoming
     */
    public XppReader(Reader reader, NameCoder nameCoder) {
        super(reader, nameCoder);
    }

    /**
     * @since 1.2
     * @deprecated As of upcoming, use {@link #XppReader(Reader, NameCoder)}  instead
     */
    public XppReader(Reader reader, XmlFriendlyReplacer replacer) {
        super(reader, replacer);
    }
    
    /**
     * {@inheritDoc}
     */
    protected synchronized XmlPullParser createParser() throws XmlPullParserException {
        if (factory == null) {
            factory = XmlPullParserFactory.newInstance(null, XppReader.class);
        }
        return factory.newPullParser();
    }
}