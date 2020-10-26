/*
 * Copyright (C) 2013, 2014, 2015, 2020 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 24. June 2013 by Joerg Schaible 
 */
package com.thoughtworks.xstream.io.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.thoughtworks.xstream.io.AbstractDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.naming.NameCoder;


/**
 * @since 1.4.5
 */
public class JDom2Driver extends AbstractDriver {

    public JDom2Driver() {
        super(new XmlFriendlyNameCoder());
    }

    /**
     * @since 1.4.5
     */
    public JDom2Driver(final NameCoder nameCoder) {
        super(nameCoder);
    }

    @Override
    public HierarchicalStreamReader createReader(final Reader reader) {
        try {
            final SAXBuilder builder = createBuilder();
            final Document document = builder.build(reader);
            return new JDom2Reader(document, getNameCoder());
        } catch (final IOException | JDOMException e) {
            throw new StreamException(e);
        }
    }

    @Override
    public HierarchicalStreamReader createReader(final InputStream in) {
        try {
            final SAXBuilder builder = createBuilder();
            final Document document = builder.build(in);
            return new JDom2Reader(document, getNameCoder());
        } catch (final IOException | JDOMException e) {
            throw new StreamException(e);
        }
    }

    @Override
    public HierarchicalStreamReader createReader(final URL in) {
        try {
            final SAXBuilder builder = createBuilder();
            final Document document = builder.build(in);
            return new JDom2Reader(document, getNameCoder());
        } catch (final IOException | JDOMException e) {
            throw new StreamException(e);
        }
    }

    @Override
    public HierarchicalStreamReader createReader(final File in) {
        try {
            final SAXBuilder builder = createBuilder();
            final Document document = builder.build(in);
            return new JDom2Reader(document, getNameCoder());
        } catch (final IOException | JDOMException e) {
            throw new StreamException(e);
        }
    }

    @Override
    public HierarchicalStreamWriter createWriter(final Writer out) {
        return new PrettyPrintWriter(out, getNameCoder());
    }

    @Override
    public HierarchicalStreamWriter createWriter(final OutputStream out) {
        return new PrettyPrintWriter(new OutputStreamWriter(out));
    }

    /**
     * Create and initialize the SAX builder.
     * 
     * @return the SAX builder instance.
     * @since 1.4.9
     */
    protected SAXBuilder createBuilder() {
        final SAXBuilder builder = new SAXBuilder();
        builder.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        return builder;
    }
}
