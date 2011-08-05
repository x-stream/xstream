/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 22. April 2006 by Mauro Talevi
 */
package com.thoughtworks.xstream.io.xml;

/**
 * Allows replacement of Strings in xml-friendly drivers to provide compatibility with XStream
 * 1.1 format
 * 
 * @author Mauro Talevi
 * @since 1.2
 * @deprecated As of 1.4, use {@link XStream11NameCoder} instead
 */
public class XStream11XmlFriendlyReplacer extends XmlFriendlyReplacer {

    /**
     * Default constructor.
     * 
     * @deprecated As of 1.4, use {@link XStream11NameCoder} instead
     */
    public XStream11XmlFriendlyReplacer() {
    }

    /**
     * {@inheritDoc} Noop implementation that does not decode. Used for XStream 1.1
     * compatibility.
     */
    public String decodeAttribute(String attributeName) {
        return attributeName;
    }

    /**
     * {@inheritDoc} Noop implementation that does not decode. Used for XStream 1.1
     * compatibility.
     */
    public String decodeNode(String elementName) {
        return elementName;
    }

    /**
     * Noop implementation that does not unescape name. Used for XStream 1.1 compatibility.
     * 
     * @param name the name of attribute or node
     * @return The String with unescaped name
     */
    public String unescapeName(String name) {
        return name;
    }

}
