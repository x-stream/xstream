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

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class DomReader extends AbstractDocumentReader {

    private Element currentElement;
    private StringBuffer textBuffer;
    private List childElements;

    public DomReader(Element rootElement) {
        this(rootElement, new XmlFriendlyReplacer());
    }

    public DomReader(Document document) {
        this(document.getDocumentElement());
    }

    /**
     * @since 1.2
     */
    public DomReader(Element rootElement, XmlFriendlyReplacer replacer) {
        super(rootElement, replacer);
        textBuffer = new StringBuffer();
    }

    /**
     * @since 1.2
     */
    public DomReader(Document document, XmlFriendlyReplacer replacer) {
        this(document.getDocumentElement(), replacer);
    }
    
    public String getNodeName() {
        return unescapeXmlName(currentElement.getTagName());
    }

    public String getValue() {
        NodeList childNodes = currentElement.getChildNodes();
        textBuffer.setLength(0);
        int length = childNodes.getLength();
        for (int i = 0; i < length; i++) {
            Node childNode = childNodes.item(i);
            if (childNode instanceof Text) {
                Text text = (Text) childNode;
                textBuffer.append(text.getData());
            }
        }
        return textBuffer.toString();
    }

    public String getAttribute(String name) {
        Attr attribute = currentElement.getAttributeNode(name);
        return attribute == null ? null : attribute.getValue();
    }

    public String getAttribute(int index) {
        return ((Attr) currentElement.getAttributes().item(index)).getValue();
    }

    public int getAttributeCount() {
        return currentElement.getAttributes().getLength();
    }

    public String getAttributeName(int index) {
        return unescapeXmlName(((Attr) currentElement.getAttributes().item(index)).getName());
    }

    protected Object getParent() {
        return currentElement.getParentNode();
    }

    protected Object getChild(int index) {
        return childElements.get(index);
    }

    protected int getChildCount() {
        return childElements.size();
    }

    protected void reassignCurrentElement(Object current) {
        currentElement = (Element) current;
        NodeList childNodes = currentElement.getChildNodes();
        childElements = new ArrayList();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node instanceof Element) {
                childElements.add(node);
            }
        }
    }

}
