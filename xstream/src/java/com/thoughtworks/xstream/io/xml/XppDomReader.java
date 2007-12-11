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

import com.thoughtworks.xstream.io.xml.xppdom.Xpp3Dom;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class XppDomReader extends AbstractDocumentReader {

    private Xpp3Dom currentElement;

    public XppDomReader(Xpp3Dom xpp3Dom) {
        super(xpp3Dom);
    }

    /**
     * @since 1.2
     */
    public XppDomReader(Xpp3Dom xpp3Dom, XmlFriendlyReplacer replacer) {
        super(xpp3Dom, replacer);
    }
    
    public String getNodeName() {
        return unescapeXmlName(currentElement.getName());
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
        return currentElement.getAttribute(attributeName);
    }

    public String getAttribute(int index) {
        return currentElement.getAttribute(currentElement.getAttributeNames()[index]);
    }

    public int getAttributeCount() {
        return currentElement.getAttributeNames().length;
    }

    public String getAttributeName(int index) {
        return unescapeXmlName(currentElement.getAttributeNames()[index]);
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
        this.currentElement = (Xpp3Dom) current;
    }

}
