/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.thoughtworks.xstream.io.xml;

import java.io.File;
import java.io.FilterWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.xml.sax.SAXException;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.naming.NameCoder;


public class Dom4JDriver extends AbstractXmlDriver {

    private DocumentFactory documentFactory;
    private OutputFormat outputFormat;

    public Dom4JDriver() {
        this(new XmlFriendlyNameCoder());
    }

    /**
     * @since 1.4
     */
    public Dom4JDriver(final NameCoder nameCoder) {
        this(new DocumentFactory(), OutputFormat.createPrettyPrint(), nameCoder);
        outputFormat.setTrimText(false);
    }

    public Dom4JDriver(final DocumentFactory documentFactory, final OutputFormat outputFormat) {
        this(documentFactory, outputFormat, new XmlFriendlyNameCoder());
    }

    /**
     * @since 1.4
     */
    public Dom4JDriver(
            final DocumentFactory documentFactory, final OutputFormat outputFormat, final NameCoder nameCoder) {
        super(nameCoder);
        this.documentFactory = documentFactory;
        this.outputFormat = outputFormat;
    }

    /**
     * @since 1.2
     * @deprecated As of 1.4, use {@link Dom4JDriver#Dom4JDriver(DocumentFactory, OutputFormat, NameCoder)} instead.
     */
    @Deprecated
    public Dom4JDriver(
            final DocumentFactory documentFactory, final OutputFormat outputFormat,
            final XmlFriendlyReplacer replacer) {
        this(documentFactory, outputFormat, (NameCoder)replacer);
    }

    public DocumentFactory getDocumentFactory() {
        return documentFactory;
    }

    public void setDocumentFactory(final DocumentFactory documentFactory) {
        this.documentFactory = documentFactory;
    }

    public OutputFormat getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(final OutputFormat outputFormat) {
        this.outputFormat = outputFormat;
    }

    @Override
    public HierarchicalStreamReader createReader(final Reader text) {
        try {
            final Document document = createReader().read(text);
            return new Dom4JReader(document, getNameCoder());
        } catch (final DocumentException e) {
            throw new StreamException(e);
        }
    }

    @Override
    public HierarchicalStreamReader createReader(final InputStream in) {
        try {
            final Document document = createReader().read(in);
            return new Dom4JReader(document, getNameCoder());
        } catch (final DocumentException e) {
            throw new StreamException(e);
        }
    }

    /**
     * @since 1.4
     */
    @Override
    public HierarchicalStreamReader createReader(final URL in) {
        try {
            final Document document = createReader().read(in);
            return new Dom4JReader(document, getNameCoder());
        } catch (final DocumentException e) {
            throw new StreamException(e);
        }
    }

    /**
     * @since 1.4
     */
    @Override
    public HierarchicalStreamReader createReader(final File in) {
        try {
            final Document document = createReader().read(in);
            return new Dom4JReader(document, getNameCoder());
        } catch (final DocumentException e) {
            throw new StreamException(e);
        }
    }

    @Override
    public HierarchicalStreamWriter createWriter(final Writer out) {
        final HierarchicalStreamWriter[] writer = new HierarchicalStreamWriter[1];
        final FilterWriter filter = new FilterWriter(out) {
            @Override
            public void close() {
                writer[0].close();
            }
        };
        writer[0] = new Dom4JXmlWriter(new XMLWriter(filter, outputFormat), getNameCoder());
        return writer[0];
    }

    @SuppressWarnings("resource")
    @Override
    public HierarchicalStreamWriter createWriter(final OutputStream out) {
        final String encoding = getOutputFormat() != null ? getOutputFormat().getEncoding() : null;
        final Charset charset = encoding != null && Charset.isSupported(encoding) ? Charset.forName(encoding) : null;
        final Writer writer = charset != null ? new OutputStreamWriter(out, charset) : new OutputStreamWriter(out);
        return createWriter(writer);
    }

    /**
     * Create and initialize the SAX reader.
     * 
     * @return the SAX reader instance.
     * @throws DocumentException if DOCTYPE processing cannot be disabled
     * @since 1.4.9
     */
    protected SAXReader createReader() throws DocumentException {
        final SAXReader reader = new SAXReader();
        try {
            reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        } catch (SAXException e) {
            throw new DocumentException("Cannot disable DOCTYPE processing", e);
        }
        return reader;
    }
}
