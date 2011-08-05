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

import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.xml.xppdom.XppDom;


public class XppDomWriter extends AbstractDocumentWriter {
    public XppDomWriter() {
        this(null, new XmlFriendlyNameCoder());
    }

    /**
     * @since 1.2.1
     */
    public XppDomWriter(final XppDom parent) {
        this(parent, new XmlFriendlyNameCoder());
    }

    /**
     * @since 1.4
     */
    public XppDomWriter(final NameCoder nameCoder) {
        this(null, nameCoder);
    }

    /**
     * @since 1.4
     */
    public XppDomWriter(final XppDom parent, final NameCoder nameCoder) {
        super(parent, nameCoder);
    }

    /**
     * @since 1.2
     * @deprecated As of 1.4 use {@link XppDomWriter#XppDomWriter(NameCoder)} instead
     */
    public XppDomWriter(final XmlFriendlyReplacer replacer) {
        this(null, replacer);
    }

    /**
     * @since 1.2.1
     * @deprecated As of 1.4 use {@link XppDomWriter#XppDomWriter(XppDom, NameCoder)} instead.
     */
    public XppDomWriter(final XppDom parent, final XmlFriendlyReplacer replacer) {
        this(parent, (NameCoder)replacer);
    }

    public XppDom getConfiguration() {
        return (XppDom)getTopLevelNodes().get(0);
    }

    protected Object createNode(final String name) {
        final XppDom newNode = new XppDom(encodeNode(name));
        final XppDom top = top();
        if (top != null) {
            top().addChild(newNode);
        }
        return newNode;
    }

    public void setValue(final String text) {
        top().setValue(text);
    }

    public void addAttribute(final String key, final String value) {
        top().setAttribute(encodeAttribute(key), value);
    }

    private XppDom top() {
        return (XppDom)getCurrent();
    }
}
