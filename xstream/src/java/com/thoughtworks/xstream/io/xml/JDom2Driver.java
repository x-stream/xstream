/*
 * Copyright (C) 2013 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 24. June 2012 by Joerg Schaible 
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
    public JDom2Driver(NameCoder nameCoder) {
        super(nameCoder);
    }

    public HierarchicalStreamReader createReader(Reader reader) {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(reader);
            return new JDom2Reader(document, getNameCoder());
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
            return new JDom2Reader(document, getNameCoder());
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
            return new JDom2Reader(document, getNameCoder());
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
            return new JDom2Reader(document, getNameCoder());
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

