/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2011, 2014, 2015, 2020 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 07. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.io.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.naming.NameCoder;


public class DomDriver extends AbstractXmlDriver {

    private final String encoding;
    private DocumentBuilderFactory documentBuilderFactory;

    /**
     * Construct a DomDriver.
     */
    public DomDriver() {
        this(null);
    }

    /**
     * Construct a DomDriver with a specified encoding. The created DomReader will ignore any encoding attribute of the
     * XML header though.
     */
    public DomDriver(final String encoding) {
        this(encoding, new XmlFriendlyNameCoder());
    }

    /**
     * @since 1.4
     */
    public DomDriver(final String encoding, final NameCoder nameCoder) {
        super(nameCoder);
        this.encoding = encoding;
    }

    /**
     * @since 1.2
     * @deprecated As of 1.4, use {@link #DomDriver(String, NameCoder)} instead.
     */
    @Deprecated
    public DomDriver(final String encoding, final XmlFriendlyReplacer replacer) {
        this(encoding, (NameCoder)replacer);
    }

    @Override
    public HierarchicalStreamReader createReader(final Reader in) {
        return createReader(new InputSource(in));
    }

    @Override
    public HierarchicalStreamReader createReader(final InputStream in) {
        return createReader(new InputSource(in));
    }

    @Override
    public HierarchicalStreamReader createReader(final URL in) {
        return createReader(new InputSource(in.toExternalForm()));
    }

    @Override
    public HierarchicalStreamReader createReader(final File in) {
        return createReader(new InputSource(in.toURI().toASCIIString()));
    }

    private HierarchicalStreamReader createReader(final InputSource source) {
        try {
            if (documentBuilderFactory == null) {
                synchronized (this) {
                    if (documentBuilderFactory == null) {
                        documentBuilderFactory = createDocumentBuilderFactory();
                    }
                }
            }
            final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            if (encoding != null) {
                source.setEncoding(encoding);
            }
            final Document document = documentBuilder.parse(source);
            return new DomReader(document, getNameCoder());
        } catch (final FactoryConfigurationError | ParserConfigurationException | SAXException | IOException e) {
            throw new StreamException(e);
        }
    }

    @Override
    public HierarchicalStreamWriter createWriter(final Writer out) {
        return new PrettyPrintWriter(out, getNameCoder());
    }

    @Override
    public HierarchicalStreamWriter createWriter(final OutputStream out) {
        try {
            return createWriter(encoding != null ? new OutputStreamWriter(out, encoding) : new OutputStreamWriter(out));
        } catch (final UnsupportedEncodingException e) {
            throw new StreamException(e);
        }
    }

    /**
     * Create the DocumentBuilderFactory instance.
     * 
     * @return the new instance
     * @since 1.4.9
     */
    protected DocumentBuilderFactory createDocumentBuilderFactory() {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        } catch (final ParserConfigurationException e) {
            throw new StreamException(e);
        }
        return factory;
    }
}
