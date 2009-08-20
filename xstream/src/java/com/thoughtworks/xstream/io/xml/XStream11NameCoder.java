/*
 * Copyright (C) 2009 XStream Committers.
 * All rights reserved.
 *
 * Created on 15. August 2009 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml;


/**
 * A XmlFriendlyNameCoder to support backward compatibility with XStream 1.1.
 * 
 * @author J&ouml;rg Schaible
 * @since upcoming
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
