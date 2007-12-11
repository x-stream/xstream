/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 02. September 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.io.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * @author Michael Kopp
 */
public class DomWriter extends AbstractDocumentWriter {

    private final Document document;
    private boolean hasRootElement;

    public DomWriter(final Document document) {
        this(document, new XmlFriendlyReplacer());
    }

    public DomWriter(final Element rootElement) {
        this(rootElement, new XmlFriendlyReplacer());
    }

    /**
     * @since 1.2
     */
    public DomWriter(final Document document, final XmlFriendlyReplacer replacer) {
        this(document.getDocumentElement(), document, replacer);
    }

    /**
     * @since 1.2.1
     */
    public DomWriter(final Element element, final Document document, final XmlFriendlyReplacer replacer) {
        super(element, replacer);
        this.document = document;
        hasRootElement = document.getDocumentElement() != null;
    }

    /**
     * @since 1.2
     */
    public DomWriter(final Element rootElement, final XmlFriendlyReplacer replacer) {
        this(rootElement.getOwnerDocument(), replacer);
    }

    protected Object createNode(final String name) {
        final Element child = document.createElement(escapeXmlName(name));
        final Element top = top();
        if (top != null) {
            top().appendChild(child);
        } else if (!hasRootElement) {
            document.appendChild(child);
            hasRootElement = true;
        }
        return child;
    }

    public void addAttribute(final String name, final String value) {
        top().setAttribute(escapeXmlName(name), value);
    }

    public void setValue(final String text) {
        top().appendChild(document.createTextNode(text));
    }

    private Element top() {
        return (Element)getCurrent();
    }
}
