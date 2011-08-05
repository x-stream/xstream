/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2011 XStream Committers.
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
    public JDomDriver(NameCoder nameCoder) {
        super(nameCoder);
    }

    /**
     * @since 1.2
     * @deprecated As of 1.4, use {@link JDomDriver#JDomDriver(NameCoder)} instead.
     */
    public JDomDriver(XmlFriendlyReplacer replacer) {
        this((NameCoder)replacer);
    }

    public HierarchicalStreamReader createReader(Reader reader) {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(reader);
            return new JDomReader(document, getNameCoder());
        } catch (IOException e) {
            throw new StreamException(e);
        } catch (JDOMException e) {
            throw new StreamException(e);
        }
    }

    public HierarchicalStreamReader createReader(InputStream in) {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(in);
            return new JDomReader(document, getNameCoder());
        } catch (IOException e) {
            throw new StreamException(e);
        } catch (JDOMException e) {
            throw new StreamException(e);
        }
    }

    public HierarchicalStreamReader createReader(URL in) {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(in);
            return new JDomReader(document, getNameCoder());
        } catch (IOException e) {
            throw new StreamException(e);
        } catch (JDOMException e) {
            throw new StreamException(e);
        }
    }

    public HierarchicalStreamReader createReader(File in) {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(in);
            return new JDomReader(document, getNameCoder());
        } catch (IOException e) {
            throw new StreamException(e);
        } catch (JDOMException e) {
            throw new StreamException(e);
        }
    }

    public HierarchicalStreamWriter createWriter(Writer out) {
        return new PrettyPrintWriter(out, getNameCoder());
    }

    public HierarchicalStreamWriter createWriter(OutputStream out) {
        return new PrettyPrintWriter(new OutputStreamWriter(out));
    }

}

