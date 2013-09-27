/*
 * Copyright (C) 2013 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 24. June 2012 by Joerg Schaible 
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.naming.NameCoder;
import java.util.List;
import org.jdom2.Document;
import org.jdom2.Element;

/**
 * @since 1.4.5
 */
public class JDom2Reader extends AbstractDocumentReader {

    private Element currentElement;

    /**
     * @since 1.4.5
     */
    public JDom2Reader(Element root) {
        super(root);
    }

    /**
     * @since 1.4.5
     */
    public JDom2Reader(Document document) {
        super(document.getRootElement());
    }

    /**
     * @since 1.4.5
     */
    public JDom2Reader(Element root, NameCoder nameCoder) {
        super(root, nameCoder);
    }

    /**
     * @since 1.4.5
     */
    public JDom2Reader(Document document, NameCoder nameCoder) {
        super(document.getRootElement(), nameCoder);
    }
    
    protected void reassignCurrentElement(Object current) {
        currentElement = (Element) current;
    }

    protected Object getParent() {
        return currentElement.getParentElement();
    }

    protected Object getChild(int index) {
        return currentElement.getChildren().get(index);
    }

    protected int getChildCount() {
        return currentElement.getChildren().size();
    }

    public String getNodeName() {
        return decodeNode(currentElement.getName());
    }

    public String getValue() {
        return currentElement.getText();
    }

    public String getAttribute(String name) {
        return currentElement.getAttributeValue(encodeAttribute(name));
    }

    public String getAttribute(int index) {
        return currentElement.getAttributes().get(index).getValue();
    }

    public int getAttributeCount() {
        return currentElement.getAttributes().size();
    }

    public String getAttributeName(int index) {
        return decodeAttribute(currentElement.getAttributes().get(index).getQualifiedName());
    }

    public String peekNextChild() {
        List list = currentElement.getChildren();
        if (null == list || list.isEmpty()) {
            return null;
        }
        return decodeNode(((Element) list.get(0)).getName());
    }
}

