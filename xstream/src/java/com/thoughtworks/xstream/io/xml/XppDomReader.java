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

import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.xml.xppdom.XppDom;

/**
 * @author Jason van Zyl
 */
public class XppDomReader extends AbstractDocumentReader {

    private XppDom currentElement;

    public XppDomReader(XppDom xppDom) {
        super(xppDom);
    }

    /**
     * @since 1.4
     */
    public XppDomReader(XppDom xppDom, NameCoder nameCoder) {
        super(xppDom, nameCoder);
    }

    /**
     * @since 1.2
     * @deprecated As of 1.4 use {@link XppDomReader#XppDomReader(XppDom, NameCoder)} instead.
     */
    public XppDomReader(XppDom xppDom, XmlFriendlyReplacer replacer) {
        this(xppDom, (NameCoder)replacer);
    }
    
    public String getNodeName() {
        return decodeNode(currentElement.getName());
    }

    public String getValue() {
        String text = null;

        try {
            text = currentElement.getValue();
        } catch (Exception e) {
            // do nothing.
        }

        return text == null ? "" : text;
    }

    public String getAttribute(String attributeName) {
        return currentElement.getAttribute(encodeAttribute(attributeName));
    }

    public String getAttribute(int index) {
        return currentElement.getAttribute(currentElement.getAttributeNames()[index]);
    }

    public int getAttributeCount() {
        return currentElement.getAttributeNames().length;
    }

    public String getAttributeName(int index) {
        return decodeAttribute(currentElement.getAttributeNames()[index]);
    }

    protected Object getParent() {
        return currentElement.getParent();
    }

    protected Object getChild(int index) {
        return currentElement.getChild(index);
    }

    protected int getChildCount() {
        return currentElement.getChildCount();
    }

    protected void reassignCurrentElement(Object current) {
        this.currentElement = (XppDom) current;
    }

}
