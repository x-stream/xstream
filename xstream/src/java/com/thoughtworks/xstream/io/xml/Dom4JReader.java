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

import com.thoughtworks.xstream.converters.ErrorWriter;
import com.thoughtworks.xstream.io.naming.NameCoder;

import org.dom4j.Document;
import org.dom4j.Element;

public class Dom4JReader extends AbstractDocumentReader {

    private Element currentElement;

    public Dom4JReader(Element rootElement) {
        this(rootElement, new XmlFriendlyNameCoder());
    }

    public Dom4JReader(Document document) {
        this(document.getRootElement());
    }

    /**
     * @since 1.4
     */
    public Dom4JReader(Element rootElement, NameCoder nameCoder) {
        super(rootElement, nameCoder);
    }

    /**
     * @since 1.4
     */
    public Dom4JReader(Document document, NameCoder nameCoder) {
        this(document.getRootElement(), nameCoder);
    }

    /**
     * @since 1.2
     * @deprecated As of 1.4, use {@link Dom4JReader#Dom4JReader(Element, NameCoder)} instead
     */
    public Dom4JReader(Element rootElement, XmlFriendlyReplacer replacer) {
        this(rootElement, (NameCoder)replacer);
    }

    /**
     * @since 1.2
     * @deprecated As of 1.4, use {@link Dom4JReader#Dom4JReader(Document, NameCoder)} instead
     */
    public Dom4JReader(Document document, XmlFriendlyReplacer replacer) {
        this(document.getRootElement(), (NameCoder)replacer);
    }
    
    public String getNodeName() {
        return decodeNode(currentElement.getName());
    }

    public String getValue() {
        return currentElement.getText();
    }

    public String getAttribute(String name) {
        return currentElement.attributeValue(encodeAttribute(name));
    }

    public String getAttribute(int index) {
        return currentElement.attribute(index).getValue();
    }

    public int getAttributeCount() {
        return currentElement.attributeCount();
    }

    public String getAttributeName(int index) {
        return decodeAttribute(currentElement.attribute(index).getQualifiedName());
    }

    protected Object getParent() {
        return currentElement.getParent();
    }

    protected Object getChild(int index) {
        return currentElement.elements().get(index);
    }

    protected int getChildCount() {
        return currentElement.elements().size();
    }

    protected void reassignCurrentElement(Object current) {
        currentElement = (Element) current;
    }

    public void appendErrors(ErrorWriter errorWriter) {
        errorWriter.add("xpath", currentElement.getPath());
    }

}
