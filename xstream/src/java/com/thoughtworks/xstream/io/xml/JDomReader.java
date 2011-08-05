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

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;

/**
 * @author Laurent Bihanic
 */
public class JDomReader extends AbstractDocumentReader {

    private Element currentElement;

    public JDomReader(Element root) {
        super(root);
    }

    public JDomReader(Document document) {
        super(document.getRootElement());
    }

    /**
     * @since 1.4
     */
    public JDomReader(Element root, NameCoder nameCoder) {
        super(root, nameCoder);
    }

    /**
     * @since 1.4
     */
    public JDomReader(Document document, NameCoder nameCoder) {
        super(document.getRootElement(), nameCoder);
    }

    /**
     * @since 1.2
     * @deprecated As of 1.4, use {@link JDomReader#JDomReader(Element, NameCoder)} instead.
     */
    public JDomReader(Element root, XmlFriendlyReplacer replacer) {
        this(root, (NameCoder)replacer);
    }

    /**
     * @since 1.2
     * @deprecated As of 1.4, use {@link JDomReader#JDomReader(Document, NameCoder)} instead.
     */
    public JDomReader(Document document, XmlFriendlyReplacer replacer) {
        this(document.getRootElement(), (NameCoder)replacer);
    }
    
    protected void reassignCurrentElement(Object current) {
        currentElement = (Element) current;
    }

    protected Object getParent() {
        // JDOM 1.0:
        return currentElement.getParentElement();

        // JDOM b10:
        // Parent parent = currentElement.getParent();
        // return (parent instanceof Element) ? (Element)parent : null;

        // JDOM b9 and earlier:
        // return currentElement.getParent();
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
        return ((Attribute) currentElement.getAttributes().get(index)).getValue();
    }

    public int getAttributeCount() {
        return currentElement.getAttributes().size();
    }

    public String getAttributeName(int index) {
        return decodeAttribute(((Attribute) currentElement.getAttributes().get(index)).getQualifiedName());
    }

}

