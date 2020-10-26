/*
 * Copyright (C) 2009, 2011, 2014, 2015, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 29. April 2009 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.thoughtworks.xstream.core.util.XmlHeaderAwareReader;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.naming.NameCoder;


/**
 * An abstract base class for a driver using an XPP implementation.
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public abstract class AbstractXppDriver extends AbstractXmlDriver {

    /**
     * Construct an AbstractXppDriver.
     * 
     * @param nameCoder the replacer for XML friendly tag and attribute names
     * @since 1.4
     */
    public AbstractXppDriver(final NameCoder nameCoder) {
        super(nameCoder);
    }

    @Override
    public HierarchicalStreamReader createReader(final Reader in) {
        try {
            return new XppReader(in, createParser(), getNameCoder());
        } catch (final XmlPullParserException e) {
            throw new StreamException("Cannot create XmlPullParser", e);
        }
    }

    @SuppressWarnings("resource")
    @Override
    public HierarchicalStreamReader createReader(final InputStream in) {
        try {
            return createReader(new XmlHeaderAwareReader(in));
        } catch (final UnsupportedEncodingException e) {
            throw new StreamException(e);
        } catch (final IOException e) {
            throw new StreamException(e);
        }
    }

    @Override
    public HierarchicalStreamWriter createWriter(final Writer out) {
        return new PrettyPrintWriter(out, getNameCoder());
    }

    @Override
    public HierarchicalStreamWriter createWriter(final OutputStream out) {
        return createWriter(new OutputStreamWriter(out));
    }

    /**
     * Create the parser of the XPP implementation.
     * 
     * @throws XmlPullParserException if the parser cannot be created
     * @since 1.4
     */
    protected abstract XmlPullParser createParser() throws XmlPullParserException;
}
