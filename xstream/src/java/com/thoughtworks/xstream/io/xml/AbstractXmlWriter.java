/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 04. June 2006 by Mauro Talevi
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.AbstractWriter;
import com.thoughtworks.xstream.io.naming.NameCoder;

/**
 * Abstract base implementation of HierarchicalStreamWriter that provides common functionality
 * to all XML-based writers.
 * 
 * @author Mauro Talevi
 * @author J&ouml;rg Schaible
 * @since 1.2
 * @deprecated As of 1.4 use {@link AbstractWriter} instead
 */
public abstract class AbstractXmlWriter extends AbstractWriter implements XmlFriendlyWriter {

    protected AbstractXmlWriter(){
        this(new XmlFriendlyNameCoder());
    }

    /**
     * @deprecated As of 1.4
     */
    protected AbstractXmlWriter(XmlFriendlyReplacer replacer) {
        this((NameCoder)replacer);
    }

    protected AbstractXmlWriter(NameCoder nameCoder) {
        super(nameCoder);
    }

    /**
     * Escapes XML name (node or attribute) to be XML-friendly
     * 
     * @param name the unescaped XML name
     * @return An escaped name with original characters replaced
     * @deprecated As of 1.4 use {@link #encodeNode(String)} or {@link #encodeAttribute(String)} instead
     */
    public String escapeXmlName(String name) {
        return super.encodeNode(name);
    }

}
