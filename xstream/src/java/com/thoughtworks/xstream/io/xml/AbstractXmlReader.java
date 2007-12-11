/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 04. June 2006 by Mauro Talevi
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

/**
 * Abstract base implementation of HierarchicalStreamReader that provides common functionality
 * to all XML-based readers.
 * 
 * @author Mauro Talevi
 * @since 1.2
 */
public abstract class AbstractXmlReader implements HierarchicalStreamReader, XmlFriendlyReader {

    private XmlFriendlyReplacer replacer;

    protected AbstractXmlReader(){
        this(new XmlFriendlyReplacer());
    }

    protected AbstractXmlReader(XmlFriendlyReplacer replacer) {
        this.replacer = replacer;
    }

    /**
     * Unescapes XML-friendly name (node or attribute) 
     * 
     * @param name the escaped XML-friendly name
     * @return An unescaped name with original characters
     */
    public String unescapeXmlName(String name) {
        return replacer.unescapeName(name);
    }
    
}
