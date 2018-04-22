/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2011, 2014, 2015, 2018 XStream Committers.
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
    public Dom4JDriver(NameCoder nameCoder) {
        this(new DocumentFactory(), OutputFormat.createPrettyPrint(), nameCoder);
        outputFormat.setTrimText(false);
    }

    public Dom4JDriver(DocumentFactory documentFactory, OutputFormat outputFormat) {
        this(documentFactory, outputFormat, new XmlFriendlyNameCoder());
    }

    /**
     * @since 1.4
     */
    public Dom4JDriver(DocumentFactory documentFactory, OutputFormat outputFormat, NameCoder nameCoder) {
        super(nameCoder);
        this.documentFactory = documentFactory;
        this.outputFormat = outputFormat;
    }

    /**
     * @since 1.2
     * @deprecated As of 1.4, use {@link Dom4JDriver#Dom4JDriver(DocumentFactory, OutputFormat, NameCoder)} instead.
     */
    public Dom4JDriver(DocumentFactory documentFactory, OutputFormat outputFormat, XmlFriendlyReplacer replacer) {
        this(documentFactory, outputFormat, (NameCoder)replacer);
    }


    public DocumentFactory getDocumentFactory() {
        return documentFactory;
    }

    public void setDocumentFactory(DocumentFactory documentFactory) {
        this.documentFactory = documentFactory;
    }

    public OutputFormat getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(OutputFormat outputFormat) {
        this.outputFormat = outputFormat;
    }

    public HierarchicalStreamReader createReader(Reader text) {
        try {
            final Document document = createReader().read(text);
            return new Dom4JReader(document, getNameCoder());
        } catch (DocumentException e) {
            throw new StreamException(e);
        }
    }

    public HierarchicalStreamReader createReader(InputStream in) {
        try {
            final Document document = createReader().read(in);
            return new Dom4JReader(document, getNameCoder());
        } catch (DocumentException e) {
            throw new StreamException(e);
        }
    }

    /**
     * @since 1.4
     */
    public HierarchicalStreamReader createReader(URL in) {
        try {
            final Document document = createReader().read(in);
            return new Dom4JReader(document, getNameCoder());
        } catch (DocumentException e) {
            throw new StreamException(e);
        }
    }

    /**
     * @since 1.4
     */
    public HierarchicalStreamReader createReader(File in) {
        try {
            final Document document = createReader().read(in);
            return new Dom4JReader(document, getNameCoder());
        } catch (DocumentException e) {
            throw new StreamException(e);
        }
    }

    public HierarchicalStreamWriter createWriter(final Writer out) {
        final HierarchicalStreamWriter[] writer = new HierarchicalStreamWriter[1];
        final FilterWriter filter = new FilterWriter(out){
            public void close() {
                writer[0].close();
            }
        };
        writer[0] = new Dom4JXmlWriter(new XMLWriter(filter,  outputFormat), getNameCoder());
        return writer[0];
    }

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
