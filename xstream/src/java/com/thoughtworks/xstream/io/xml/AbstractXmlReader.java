package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

/**
 * Abstract base implementation of HierarchicalStreamReader that provides common functionality
 * to all XML-based readers.
 * 
 * @author Mauro Talevi
 * @since 1.2
 */
public abstract class AbstractXmlReader implements HierarchicalStreamReader {

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
    protected String unescapeXmlName(String name) {
        return replacer.unescapeName(name);
    }
    
}
