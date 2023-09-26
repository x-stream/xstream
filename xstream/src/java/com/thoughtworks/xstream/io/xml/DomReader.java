/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2011, 2014, 2015, 2023 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 07. March 2004 by Joe Walnes
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

import com.thoughtworks.xstream.core.util.FastStack;
import com.thoughtworks.xstream.io.naming.NameCoder;


public class DomReader extends AbstractDocumentReader {

    private Element currentElement;
    private final StringBuilder textBuffer;
    private List<Element> childElements;
    private final FastStack<List<Element>> childrenStack;

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
        childrenStack = new FastStack<>(16);
        collectChildElements();
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
    }

    private void collectChildElements() {
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
    public void moveDown() {
        super.moveDown();
        childrenStack.push(childElements);
        collectChildElements();
    }

    @Override
    public void moveUp() {
        childElements = childrenStack.pop();
        super.moveUp();
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
