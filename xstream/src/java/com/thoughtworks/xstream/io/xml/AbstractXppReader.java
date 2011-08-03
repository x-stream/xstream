/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 02. August 2011 by Joerg Schaible as copy of XppReader
 */
package com.thoughtworks.xstream.io.xml;

import java.io.IOException;
import java.io.Reader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.thoughtworks.xstream.converters.ErrorWriter;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.naming.NameCoder;

/**
 * XStream reader that pulls directly from the stream using the XmlPullParser API.
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public abstract class AbstractXppReader extends AbstractPullReader {

    private final XmlPullParser parser;
    private final Reader reader;

    /**
     * Construct an XppReader.
     * 
     * @param reader the reader with the input data
     * @since upcoming
     */
    public AbstractXppReader(Reader reader) {
        this(reader, new XmlFriendlyNameCoder());
    }

    /**
     * Construct an XppReader.
     * 
     * @param reader the reader with the input data
     * @param nameCoder the coder for XML friendly tag and attribute names
     * @since upcoming
     */
    public AbstractXppReader(Reader reader, NameCoder nameCoder) {
        super(nameCoder);
        this.reader = reader;
        try {
            this.parser = createParser();
            parser.setInput(this.reader);
        } catch (XmlPullParserException e) {
            throw new StreamException("Cannot create XmlPullParser", e);
        }
        moveDown();
    }
    
    /**
     * Create the parser of the XPP implementation.

     * @throws XmlPullParserException if the parser cannot be created
     * @since upcoming
     */
  abstract protected XmlPullParser createParser() throws XmlPullParserException;

    protected int pullNextEvent() {
        try {
            switch(parser.next()) {
                case XmlPullParser.START_DOCUMENT:
                case XmlPullParser.START_TAG:
                    return START_NODE;
                case XmlPullParser.END_DOCUMENT:
                case XmlPullParser.END_TAG:
                    return END_NODE;
                case XmlPullParser.TEXT:
                    return TEXT;
                case XmlPullParser.COMMENT:
                    return COMMENT;
                default:
                    return OTHER;
            }
        } catch (XmlPullParserException e) {
            throw new StreamException(e);
        } catch (IOException e) {
            throw new StreamException(e);
        }
    }

    protected String pullElementName() {
        return parser.getName();
    }

    protected String pullText() {
        return parser.getText();
    }

    public String getAttribute(String name) {
        return parser.getAttributeValue(null, encodeAttribute(name));
    }

    public String getAttribute(int index) {
        return parser.getAttributeValue(index);
    }

    public int getAttributeCount() {
        return parser.getAttributeCount();
    }

    public String getAttributeName(int index) {
        return decodeAttribute(parser.getAttributeName(index));
    }

    public void appendErrors(ErrorWriter errorWriter) {
        errorWriter.add("line number", String.valueOf(parser.getLineNumber()));
    }

    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            throw new StreamException(e);
        }
    }

}