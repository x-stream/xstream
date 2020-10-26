/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2011, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 02. September 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.naming.NameCoder;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Text;


public class XomReader extends AbstractDocumentReader {

    private Element currentElement;

    public XomReader(final Element rootElement) {
        super(rootElement);
    }

    public XomReader(final Document document) {
        super(document.getRootElement());
    }

    /**
     * @since 1.4
     */
    public XomReader(final Element rootElement, final NameCoder nameCoder) {
        super(rootElement, nameCoder);
    }

    /**
     * @since 1.4
     */
    public XomReader(final Document document, final NameCoder nameCoder) {
        super(document.getRootElement(), nameCoder);
    }

    /**
     * @since 1.2
     * @deprecated As of 1.4 use {@link XomReader#XomReader(Element, NameCoder)} instead.
     */
    @Deprecated
    public XomReader(final Element rootElement, final XmlFriendlyReplacer replacer) {
        this(rootElement, (NameCoder)replacer);
    }

    /**
     * @since 1.2
     * @deprecated As of 1.4 use {@link XomReader#XomReader(Element, NameCoder)} instead.
     */
    @Deprecated
    public XomReader(final Document document, final XmlFriendlyReplacer replacer) {
        this(document.getRootElement(), (NameCoder)replacer);
    }

    @Override
    public String getNodeName() {
        return decodeNode(currentElement.getLocalName());
    }

    @Override
    public String getValue() {
        // currentElement.getValue() not used as this includes text of child elements, which we don't want.
        final StringBuffer result = new StringBuffer();
        final int childCount = currentElement.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final Node child = currentElement.getChild(i);
            if (child instanceof Text) {
                final Text text = (Text)child;
                result.append(text.getValue());
            }
        }
        return result.toString();
    }

    @Override
    public String getAttribute(final String name) {
        return currentElement.getAttributeValue(encodeAttribute(name));
    }

    @Override
    public String getAttribute(final int index) {
        return currentElement.getAttribute(index).getValue();
    }

    @Override
    public int getAttributeCount() {
        return currentElement.getAttributeCount();
    }

    @Override
    public String getAttributeName(final int index) {
        return decodeAttribute(currentElement.getAttribute(index).getQualifiedName());
    }

    @Override
    protected int getChildCount() {
        return currentElement.getChildElements().size();
    }

    @Override
    protected Object getParent() {
        return currentElement.getParent();
    }

    @Override
    protected Object getChild(final int index) {
        return currentElement.getChildElements().get(index);
    }

    @Override
    protected void reassignCurrentElement(final Object current) {
        currentElement = (Element)current;
    }

    @Override
    public String peekNextChild() {
        final Elements children = currentElement.getChildElements();
        if (null == children || children.size() == 0) {
            return null;
        }
        return decodeNode(children.get(0).getLocalName());
    }
}
