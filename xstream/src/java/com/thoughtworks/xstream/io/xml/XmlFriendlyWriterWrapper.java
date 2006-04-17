package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.WriterWrapper;

/**
 * Wrapper to escape Java names to xml-friendly node and attribute names
 * 
 * @author Mauro Talevi
 */
public class XmlFriendlyWriterWrapper extends WriterWrapper {

    private XmlFriendlyReplacer replacer;

    public XmlFriendlyWriterWrapper(HierarchicalStreamWriter writer) {
        this(writer, new XmlFriendlyReplacer());
    }
    
    public XmlFriendlyWriterWrapper(HierarchicalStreamWriter writer, XmlFriendlyReplacer replacer) {
        super(writer);
        this.replacer = replacer;
    }

    public void startNode(String name) {
        super.startNode(replacer.escapeName(name));
    }

    public void addAttribute(String key, String value) {
        super.addAttribute(replacer.escapeName(key), value);
    }

}