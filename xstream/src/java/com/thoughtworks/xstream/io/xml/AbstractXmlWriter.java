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
public abstract class AbstractXmlWriter implements ExtendedHierarchicalStreamWriter {

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
    protected String escapeXmlName(String name) {
        return replacer.escapeName(name);
    }

    public HierarchicalStreamWriter underlyingWriter() {
        return this;
    }

}
