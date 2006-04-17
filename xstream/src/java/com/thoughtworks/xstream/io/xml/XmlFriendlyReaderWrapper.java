package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.ReaderWrapper;

/**
 * Wrapper to unescape xml-friendly node and attribute names to Java names
 * 
 * @author Mauro Talevi
 */
public class XmlFriendlyReaderWrapper extends ReaderWrapper {

    private XmlFriendlyReplacer replacer;

    public XmlFriendlyReaderWrapper(HierarchicalStreamReader reader) {
        this(reader, new XmlFriendlyReplacer());
    }
    
    public XmlFriendlyReaderWrapper(HierarchicalStreamReader reader, XmlFriendlyReplacer replacer) {
        super(reader);
        this.replacer = replacer;
    }

    public String getNodeName() {
        return replacer.unescapeName(super.getNodeName());
    }
    
    public String getAttributeName(int index) {
        return replacer.unescapeName(super.getAttributeName(index));
    }

}