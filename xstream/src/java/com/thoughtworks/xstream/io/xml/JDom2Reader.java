/*
 * Copyright (C) 2013, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 24. June 2013 by Joerg Schaible 
 */
package com.thoughtworks.xstream.io.xml;

import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;

import com.thoughtworks.xstream.io.naming.NameCoder;


/**
 * @since 1.4.5
 */
public class JDom2Reader extends AbstractDocumentReader {

    private Element currentElement;

    /**
     * @since 1.4.5
     */
    public JDom2Reader(final Element root) {
        super(root);
    }

    /**
     * @since 1.4.5
     */
    public JDom2Reader(final Document document) {
        super(document.getRootElement());
    }

    /**
     * @since 1.4.5
     */
    public JDom2Reader(final Element root, final NameCoder nameCoder) {
        super(root, nameCoder);
    }

    /**
     * @since 1.4.5
     */
    public JDom2Reader(final Document document, final NameCoder nameCoder) {
        super(document.getRootElement(), nameCoder);
    }

    @Override
    protected void reassignCurrentElement(final Object current) {
        currentElement = (Element)current;
    }

    @Override
    protected Object getParent() {
        return currentElement.getParentElement();
    }

    @Override
    protected Object getChild(final int index) {
        return currentElement.getChildren().get(index);
    }

    @Override
    protected int getChildCount() {
        return currentElement.getChildren().size();
    }

    @Override
    public String getNodeName() {
        return decodeNode(currentElement.getName());
    }

    @Override
    public String getValue() {
        return currentElement.getText();
    }

    @Override
    public String getAttribute(final String name) {
        return currentElement.getAttributeValue(encodeAttribute(name));
    }

    @Override
    public String getAttribute(final int index) {
        return currentElement.getAttributes().get(index).getValue();
    }

    @Override
    public int getAttributeCount() {
        return currentElement.getAttributes().size();
    }

    @Override
    public String getAttributeName(final int index) {
        return decodeAttribute(currentElement.getAttributes().get(index).getQualifiedName());
    }

    @Override
    public String peekNextChild() {
        final List<Element> list = currentElement.getChildren();
        if (null == list || list.isEmpty()) {
            return null;
        }
        return decodeNode(list.get(0).getName());
    }
}
