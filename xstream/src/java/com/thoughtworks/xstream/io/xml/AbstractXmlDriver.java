/*
 * Copyright (C) 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 28. May 2005 by Mauro Talevi
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.AbstractDriver;
import com.thoughtworks.xstream.io.naming.NameCoder;


/**
 * Base class for HierarchicalStreamDrivers to use xml-based HierarchicalStreamReader and
 * HierarchicalStreamWriter.
 * 
 * @author Mauro Talevi
 * @author J&ouml;rg Schaible
 * @since 1.2
 * @deprecated As of 1.4
 */
public abstract class AbstractXmlDriver extends AbstractDriver {

    /**
     * Creates a AbstractXmlFriendlyDriver with default XmlFriendlyReplacer
     * 
     * @deprecated As of 1.4
     */
    public AbstractXmlDriver() {
        this(new XmlFriendlyNameCoder());
    }

    /**
     * Creates a AbstractXmlFriendlyDriver with default XmlFriendlyReplacer
     * @since 1.4
     */
    public AbstractXmlDriver(NameCoder nameCoder) {
        super(nameCoder);
    }

    /**
     * Creates a AbstractXmlFriendlyDriver with custom XmlFriendlyReplacer
     * 
     * @param replacer the XmlFriendlyReplacer
     * @deprecated As of 1.4
     */
    public AbstractXmlDriver(XmlFriendlyReplacer replacer) {
        this((NameCoder)replacer);
    }

    /**
     * @deprecated As of 1.4
     */
    protected XmlFriendlyReplacer xmlFriendlyReplacer() {
        NameCoder nameCoder = getNameCoder(); 
        return nameCoder instanceof XmlFriendlyReplacer ? (XmlFriendlyReplacer)nameCoder : null;
    }

}
