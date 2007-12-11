/*
 * Copyright (C) 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 28. May 2005 by Mauro Talevi
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamDriver;

/**
 * Base class for HierarchicalStreamDrivers to use xml-based HierarchicalStreamReader
 * and HierarchicalStreamWriter.
 * 
 * @author Mauro Talevi
 * @since 1.2
 */
public abstract class AbstractXmlDriver implements HierarchicalStreamDriver {
    
    private XmlFriendlyReplacer replacer;
        
    /**
     * Creates a AbstractXmlFriendlyDriver with default XmlFriendlyReplacer
     */
    public AbstractXmlDriver() {
        this(new XmlFriendlyReplacer());
    }

    /**
     * Creates a AbstractXmlFriendlyDriver with custom XmlFriendlyReplacer
     * @param replacer the XmlFriendlyReplacer
     */
    public AbstractXmlDriver(XmlFriendlyReplacer replacer) {
        this.replacer = replacer;
    }

    protected XmlFriendlyReplacer xmlFriendlyReplacer(){
        return replacer;
    }
    
}
