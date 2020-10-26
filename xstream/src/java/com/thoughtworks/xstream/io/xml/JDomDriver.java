/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2011, 2014, 2015, 2020 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 03. September 2004 by Joe Walnes
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

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.naming.NameCoder;


/**
 * @author Laurent Bihanic
 */
public class JDomDriver extends AbstractXmlDriver {

    public JDomDriver() {
        super(new XmlFriendlyNameCoder());
    }

    /**
     * @since 1.4
     */
    public JDomDriver(final NameCoder nameCoder) {
        super(nameCoder);
    }

    /**
     * @since 1.2
     * @deprecated As of 1.4, use {@link JDomDriver#JDomDriver(NameCoder)} instead.
     */
    @Deprecated
    public JDomDriver(final XmlFriendlyReplacer replacer) {
        this((NameCoder)replacer);
    }

    @Override
    public HierarchicalStreamReader createReader(final Reader reader) {
        try {
            final SAXBuilder builder = createBuilder();
            final Document document = builder.build(reader);
            return new JDomReader(document, getNameCoder());
        } catch (final IOException | JDOMException e) {
            throw new StreamException(e);
        }
    }

    @Override
    public HierarchicalStreamReader createReader(final InputStream in) {
        try {
            final SAXBuilder builder = createBuilder();
            final Document document = builder.build(in);
            return new JDomReader(document, getNameCoder());
        } catch (final IOException | JDOMException e) {
            throw new StreamException(e);
        }
    }

    @Override
    public HierarchicalStreamReader createReader(final URL in) {
        try {
            final SAXBuilder builder = createBuilder();
            final Document document = builder.build(in);
            return new JDomReader(document, getNameCoder());
        } catch (final IOException | JDOMException e) {
            throw new StreamException(e);
        }
    }

    @Override
    public HierarchicalStreamReader createReader(final File in) {
        try {
            final SAXBuilder builder = createBuilder();
            final Document document = builder.build(in);
            return new JDomReader(document, getNameCoder());
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
