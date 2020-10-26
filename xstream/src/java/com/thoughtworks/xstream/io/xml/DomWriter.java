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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.thoughtworks.xstream.io.naming.NameCoder;


/**
 * @author Michael Kopp
 */
public class DomWriter extends AbstractDocumentWriter<Element, Element> {

    private final Document document;
    private boolean hasRootElement;

    public DomWriter(final Document document) {
        this(document, new XmlFriendlyNameCoder());
    }

    public DomWriter(final Element rootElement) {
        this(rootElement, new XmlFriendlyNameCoder());
    }

    /**
     * @since 1.4
     */
    public DomWriter(final Document document, final NameCoder nameCoder) {
        this(document.getDocumentElement(), document, nameCoder);
    }

    /**
     * @since 1.4
     */
    public DomWriter(final Element element, final Document document, final NameCoder nameCoder) {
        super(element, nameCoder);
        this.document = document;
        hasRootElement = document.getDocumentElement() != null;
    }

    /**
     * @since 1.4
     */
    public DomWriter(final Element rootElement, final NameCoder nameCoder) {
        this(rootElement, rootElement.getOwnerDocument(), nameCoder);
    }

    /**
     * @since 1.2
     * @deprecated As of 1.4 use {@link DomWriter#DomWriter(Document, NameCoder)} instead.
     */
    @Deprecated
    public DomWriter(final Document document, final XmlFriendlyReplacer replacer) {
        this(document.getDocumentElement(), document, (NameCoder)replacer);
    }

    /**
     * @since 1.2.1
     * @deprecated As of 1.4 use {@link DomWriter#DomWriter(Element, Document, NameCoder)} instead.
     */
    @Deprecated
    public DomWriter(final Element element, final Document document, final XmlFriendlyReplacer replacer) {
        this(element, document, (NameCoder)replacer);
    }

    /**
     * @since 1.2
     * @deprecated As of 1.4 use {@link DomWriter#DomWriter(Element, NameCoder)} instead.
     */
    @Deprecated
    public DomWriter(final Element rootElement, final XmlFriendlyReplacer replacer) {
        this(rootElement, rootElement.getOwnerDocument(), (NameCoder)replacer);
    }

    @Override
    protected Element createNode(final String name) {
        final Element child = document.createElement(encodeNode(name));
        final Element top = top();
        if (top != null) {
            top().appendChild(child);
        } else if (!hasRootElement) {
            document.appendChild(child);
            hasRootElement = true;
        }
        return child;
    }

    @Override
    public void addAttribute(final String name, final String value) {
        top().setAttribute(encodeAttribute(name), value);
    }

    @Override
    public void setValue(final String text) {
        top().appendChild(document.createTextNode(text));
    }

    private Element top() {
        return getCurrent();
    }
}
