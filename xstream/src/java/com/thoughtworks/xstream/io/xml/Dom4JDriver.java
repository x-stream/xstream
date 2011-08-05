/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2011 XStream Committers.
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

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

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
            SAXReader reader = new SAXReader();
            Document document = reader.read(text);
            return new Dom4JReader(document, getNameCoder());
        } catch (DocumentException e) {
            throw new StreamException(e);
        }
    }

    public HierarchicalStreamReader createReader(InputStream in) {
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(in);
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
            SAXReader reader = new SAXReader();
            Document document = reader.read(in);
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
            SAXReader reader = new SAXReader();
            Document document = reader.read(in);
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
        final Writer writer = new OutputStreamWriter(out);
        return createWriter(writer);
    }
}
