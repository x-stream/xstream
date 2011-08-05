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

import com.thoughtworks.xstream.io.naming.NameCoder;

import nu.xom.Attribute;
import nu.xom.Element;


public class XomWriter extends AbstractDocumentWriter {

    /**
     * @since 1.2.1
     */
    public XomWriter() {
        this(null);
    }

    public XomWriter(final Element parentElement) {
        this(parentElement, new XmlFriendlyNameCoder());
    }

    /**
     * @since 1.4
     */
    public XomWriter(final Element parentElement, final NameCoder nameCoder) {
        super(parentElement, nameCoder);
    }

    /**
     * @since 1.2
     * @deprecated As of 1.4 use {@link XomWriter#XomWriter(Element, NameCoder)} instead
     */
    public XomWriter(final Element parentElement, final XmlFriendlyReplacer replacer) {
        this(parentElement, (NameCoder)replacer);
    }

    protected Object createNode(final String name) {
        final Element newNode = new Element(encodeNode(name));
        final Element top = top();
        if (top != null){
            top().appendChild(newNode);
        }
        return newNode;
    }

    public void addAttribute(final String name, final String value) {
        top().addAttribute(new Attribute(encodeAttribute(name), value));
    }

    public void setValue(final String text) {
        top().appendChild(text);
    }

    private Element top() {
        return (Element)getCurrent();
    }
}
