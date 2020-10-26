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

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.thoughtworks.xstream.io.naming.NameCoder;


public class DomReader extends AbstractDocumentReader {

    private Element currentElement;
    private final StringBuilder textBuffer;
    private List<Element> childElements;

    public DomReader(final Element rootElement) {
        this(rootElement, new XmlFriendlyNameCoder());
    }

    public DomReader(final Document document) {
        this(document.getDocumentElement());
    }

    /**
     * @since 1.4
     */
    public DomReader(final Element rootElement, final NameCoder nameCoder) {
        super(rootElement, nameCoder);
        textBuffer = new StringBuilder();
    }

    /**
     * @since 1.4
     */
    public DomReader(final Document document, final NameCoder nameCoder) {
        this(document.getDocumentElement(), nameCoder);
    }

    /**
     * @since 1.2
     * @deprecated As of 1.4, use {@link DomReader#DomReader(Element, NameCoder)} instead.
     */
    @Deprecated
    public DomReader(final Element rootElement, final XmlFriendlyReplacer replacer) {
        this(rootElement, (NameCoder)replacer);
    }

    /**
     * @since 1.2
     * @deprecated As of 1.4, use {@link DomReader#DomReader(Document, NameCoder)} instead.
     */
    @Deprecated
    public DomReader(final Document document, final XmlFriendlyReplacer replacer) {
        this(document.getDocumentElement(), (NameCoder)replacer);
    }

    @Override
    public String getNodeName() {
        return decodeNode(currentElement.getTagName());
    }

    @Override
    public String getValue() {
        final NodeList childNodes = currentElement.getChildNodes();
        textBuffer.setLength(0);
        final int length = childNodes.getLength();
        for (int i = 0; i < length; i++) {
            final Node childNode = childNodes.item(i);
            if (childNode instanceof Text) {
                final Text text = (Text)childNode;
                textBuffer.append(text.getData());
            }
        }
        return textBuffer.toString();
    }

    @Override
    public String getAttribute(final String name) {
        final Attr attribute = currentElement.getAttributeNode(encodeAttribute(name));
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public String getAttribute(final int index) {
        return ((Attr)currentElement.getAttributes().item(index)).getValue();
    }

    @Override
    public int getAttributeCount() {
        return currentElement.getAttributes().getLength();
    }

    @Override
    public String getAttributeName(final int index) {
        return decodeAttribute(((Attr)currentElement.getAttributes().item(index)).getName());
    }

    @Override
    protected Object getParent() {
        return currentElement.getParentNode();
    }

    @Override
    protected Object getChild(final int index) {
        return childElements.get(index);
    }

    @Override
    protected int getChildCount() {
        return childElements.size();
    }

    @Override
    protected void reassignCurrentElement(final Object current) {
        currentElement = (Element)current;
        final NodeList childNodes = currentElement.getChildNodes();
        childElements = new ArrayList<>();
        for (int i = 0; i < childNodes.getLength(); i++) {
            final Node node = childNodes.item(i);
            if (node instanceof Element) {
                childElements.add((Element)node);
            }
        }
    }

    @Override
    public String peekNextChild() {
        final NodeList childNodes = currentElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            final Node node = childNodes.item(i);
            if (node instanceof Element) {
                return decodeNode(((Element)node).getTagName());
            }
        }
        return null;
    }
}
