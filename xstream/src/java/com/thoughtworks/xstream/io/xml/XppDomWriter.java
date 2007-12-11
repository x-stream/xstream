/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.xml.xppdom.Xpp3Dom;


public class XppDomWriter extends AbstractDocumentWriter {
    public XppDomWriter() {
        this(null, new XmlFriendlyReplacer());
    }

    /**
     * @since 1.2.1
     */
    public XppDomWriter(final Xpp3Dom parent) {
        this(parent, new XmlFriendlyReplacer());
    }

    /**
     * @since 1.2
     */
    public XppDomWriter(final XmlFriendlyReplacer replacer) {
        this(null, replacer);
    }

    /**
     * @since 1.2.1
     */
    public XppDomWriter(final Xpp3Dom parent, final XmlFriendlyReplacer replacer) {
        super(parent, replacer);
    }

    public Xpp3Dom getConfiguration() {
        return (Xpp3Dom)getTopLevelNodes().get(0);
    }

    protected Object createNode(final String name) {
        final Xpp3Dom newNode = new Xpp3Dom(escapeXmlName(name));
        final Xpp3Dom top = top();
        if (top != null) {
            top().addChild(newNode);
        }
        return newNode;
    }

    public void setValue(final String text) {
        top().setValue(text);
    }

    public void addAttribute(final String key, final String value) {
        top().setAttribute(escapeXmlName(key), value);
    }

    private Xpp3Dom top() {
        return (Xpp3Dom)getCurrent();
    }
}
