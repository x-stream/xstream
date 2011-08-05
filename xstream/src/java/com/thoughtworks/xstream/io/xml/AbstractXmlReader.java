/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 04. June 2006 by Mauro Talevi
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.AbstractReader;
import com.thoughtworks.xstream.io.naming.NameCoder;


/**
 * Abstract base implementation of HierarchicalStreamReader that provides common functionality
 * to all XML-based readers.
 * 
 * @author Mauro Talevi
 * @author J&ouml;rg Schaible
 * @since 1.2
 * @deprecated As of 1.4, use {@link AbstractReader} instead.
 */
public abstract class AbstractXmlReader extends AbstractReader /* implements XmlFriendlyReader */ {

    protected AbstractXmlReader() {
        this(new XmlFriendlyNameCoder());
    }

    /**
    * @deprecated As of 1.4, use {@link AbstractReader} instead.
    */
    protected AbstractXmlReader(XmlFriendlyReplacer replacer) {
        this((NameCoder)replacer);
    }

    protected AbstractXmlReader(NameCoder nameCoder) {
        super(nameCoder);
    }

    /**
     * Unescapes XML-friendly name (node or attribute)
     * 
     * @param name the escaped XML-friendly name
     * @return An unescaped name with original characters
     * @deprecated As of 1.4, use {@link #decodeNode(String)} or {@link #decodeAttribute(String)} instead.
     */
    public String unescapeXmlName(String name) {
        return decodeNode(name);
    }

    /**
     * Escapes XML-unfriendly name (node or attribute)
     * 
     * @param name the unescaped XML-unfriendly name
     * @return An escaped name with original characters
     * @deprecated As of 1.4, use {@link AbstractReader} instead.
     */
    protected String escapeXmlName(String name) {
        return encodeNode(name);
    }

}
