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

import java.io.IOException;

import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultElement;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.thoughtworks.xstream.core.util.FastStack;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.naming.NameCoder;


public class Dom4JXmlWriter extends AbstractXmlWriter {

    private final XMLWriter writer;
    private final FastStack<String> elementStack;
    private final AttributesImpl attributes;
    private boolean started;
    private boolean children;

    public Dom4JXmlWriter(final XMLWriter writer) {
        this(writer, new XmlFriendlyNameCoder());
    }

    /**
     * @since 1.4
     */
    public Dom4JXmlWriter(final XMLWriter writer, final NameCoder nameCoder) {
        super(nameCoder);
        this.writer = writer;
        elementStack = new FastStack<>(16);
        attributes = new AttributesImpl();
        try {
            writer.startDocument();
        } catch (final SAXException e) {
            throw new StreamException(e);
        }
    }

    /**
     * @since 1.2
     * @deprecated As of 1.4 use {@link Dom4JXmlWriter#Dom4JXmlWriter(XMLWriter, NameCoder)} instead.
     */
    @Deprecated
    public Dom4JXmlWriter(final XMLWriter writer, final XmlFriendlyReplacer replacer) {
        this(writer, (NameCoder)replacer);
    }

    @Override
    public void startNode(final String name) {
        if (elementStack.size() > 0) {
            try {
                startElement();
            } catch (final SAXException e) {
                throw new StreamException(e);
            }
            started = false;
        }
        elementStack.push(encodeNode(name));
        children = false;
    }

    @Override
    public void setValue(final String text) {
        final char[] value = text.toCharArray();
        if (value.length > 0) {
            try {
                startElement();
                writer.characters(value, 0, value.length);
            } catch (final SAXException e) {
                throw new StreamException(e);
            }
            children = true;
        }
    }

    @Override
    public void addAttribute(final String key, final String value) {
        attributes.addAttribute("", "", encodeAttribute(key), "string", value);
    }

    @Override
    public void endNode() {
        try {
            if (!children) {
                final Element element = new DefaultElement(elementStack.pop());
                for (int i = 0; i < attributes.getLength(); ++i) {
                    element.addAttribute(attributes.getQName(i), attributes.getValue(i));
                }
                writer.write(element);
                attributes.clear();
                children = true; // node just closed is child of node on top of stack
                started = true;
            } else {
                startElement();
                writer.endElement("", "", elementStack.pop());
            }
        } catch (final SAXException | IOException e) {
            throw new StreamException(e);
        }
    }

    @Override
    public void flush() {
        try {
            writer.flush();
        } catch (final IOException e) {
            throw new StreamException(e);
        }
    }

    @Override
    public void close() {
        try {
            writer.endDocument();
            writer.flush();
        } catch (final SAXException | IOException e) {
            throw new StreamException(e);
        }
    }

    private void startElement() throws SAXException {
        if (!started) {
            writer.startElement("", "", elementStack.peek(), attributes);
            attributes.clear();
            started = true;
        }
    }
}
