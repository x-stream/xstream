/*
 * Copyright (C) 2009, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 15. August 2009 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml;


/**
 * A XmlFriendlyNameCoder to support backward compatibility with XStream 1.1.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public class XStream11NameCoder extends XmlFriendlyNameCoder {

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
}
