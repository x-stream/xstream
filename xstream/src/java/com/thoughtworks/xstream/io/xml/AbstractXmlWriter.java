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

import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Abstract base implementation of HierarchicalStreamWriter that provides common functionality
 * to all XML-based writers.
 * 
 * @author Mauro Talevi
 * @since 1.2
 */
public abstract class AbstractXmlWriter implements ExtendedHierarchicalStreamWriter, XmlFriendlyWriter {

    private XmlFriendlyReplacer replacer;

    protected AbstractXmlWriter(){
        this(new XmlFriendlyReplacer());
    }

    protected AbstractXmlWriter(XmlFriendlyReplacer replacer) {
        this.replacer = replacer;
    }

    public void startNode(String name, Class clazz) {
        startNode(name);
    }

    /**
     * Escapes XML name (node or attribute) to be XML-friendly
     * 
     * @param name the unescaped XML name
     * @return An escaped name with original characters replaced
     */
    public String escapeXmlName(String name) {
        return replacer.escapeName(name);
    }

    public HierarchicalStreamWriter underlyingWriter() {
        return this;
    }

}
