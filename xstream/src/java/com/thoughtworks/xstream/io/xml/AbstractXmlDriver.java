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
