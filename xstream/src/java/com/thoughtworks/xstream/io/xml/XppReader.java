/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2011, 2014, 2020 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 08. March 2004 by Joe Walnes
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
public class XppReader extends AbstractPullReader {

    private final XmlPullParser parser;
    private final Reader reader;

    /**
     * Construct an XppReader.
     * 
     * @param reader the reader with the input data
     * @param parser the XPP parser to use
     * @since 1.4
     */
    public XppReader(final Reader reader, final XmlPullParser parser) {
        this(reader, parser, new XmlFriendlyNameCoder());
    }

    /**
     * Construct an XppReader.
     * 
     * @param reader the reader with the input data
     * @param parser the XPP parser to use
     * @param nameCoder the coder for XML friendly tag and attribute names
     * @since 1.4
     */
    public XppReader(final Reader reader, final XmlPullParser parser, final NameCoder nameCoder) {
        super(nameCoder);
        this.parser = parser;
        this.reader = reader;
        try {
            parser.setInput(this.reader);
        } catch (final XmlPullParserException e) {
            throw new StreamException(e);
        }
        moveDown();
    }

    /**
     * @deprecated As of 1.4, use {@link #XppReader(Reader, XmlPullParser)} instead
     */
    @Deprecated
    public XppReader(final Reader reader) {
        this(reader, new XmlFriendlyReplacer());
    }

    /**
     * @since 1.2
     * @deprecated As of 1.4, use {@link #XppReader(Reader, XmlPullParser, NameCoder)} instead
     */
    @Deprecated
    public XppReader(final Reader reader, final XmlFriendlyReplacer replacer) {
        super(replacer);
        try {
            parser = createParser();
            this.reader = reader;
            parser.setInput(this.reader);
            moveDown();
        } catch (final XmlPullParserException e) {
            throw new StreamException(e);
        }
    }

    /**
     * To use another implementation of org.xmlpull.v1.XmlPullParser, override this method.
     * 
     * @deprecated As of 1.4, use {@link #XppReader(Reader, XmlPullParser)} instead
     */
    @Deprecated
    protected XmlPullParser createParser() {
        Exception exception = null;
        try {
            return (XmlPullParser)Class
                .forName("org.xmlpull.mxp1.MXParser", true, XmlPullParser.class.getClassLoader())
                .newInstance();
        } catch (final InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            exception = e;
        }
        throw new StreamException("Cannot create Xpp3 parser instance.", exception);
    }

    @Override
    protected int pullNextEvent() {
        try {
            switch (parser.next()) {
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
        } catch (final XmlPullParserException | IOException e) {
            throw new StreamException(e);
        }
    }

    @Override
    protected String pullElementName() {
        return parser.getName();
    }

    @Override
    protected String pullText() {
        return parser.getText();
    }

    @Override
    public String getAttribute(final String name) {
        return parser.getAttributeValue(null, encodeAttribute(name));
    }

    @Override
    public String getAttribute(final int index) {
        return parser.getAttributeValue(index);
    }

    @Override
    public int getAttributeCount() {
        return parser.getAttributeCount();
    }

    @Override
    public String getAttributeName(final int index) {
        return decodeAttribute(parser.getAttributeName(index));
    }

    @Override
    public void appendErrors(final ErrorWriter errorWriter) {
        errorWriter.add("line number", String.valueOf(parser.getLineNumber()));
    }

    @Override
    public void close() {
        try {
            reader.close();
        } catch (final IOException e) {
            throw new StreamException(e);
        }
    }

}
