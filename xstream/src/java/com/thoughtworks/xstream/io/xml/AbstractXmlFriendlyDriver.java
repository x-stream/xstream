package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Base class for HierarchicalStreamDrivers to use xml-friendly HierarchicalStreamReader
 * and HierarchicalStreamWriter.
 * 
 * @author Mauro Talevi
 */
public abstract class AbstractXmlFriendlyDriver implements HierarchicalStreamDriver {
    
    private XmlFriendlyReplacer replacer;
        
    /**
     * Creates a AbstractXmlFriendlyDriver with default XmlFriendlyReplacer
     */
    public AbstractXmlFriendlyDriver() {
        this(new XmlFriendlyReplacer());
    }

    /**
     * Creates a AbstractXmlFriendlyDriver with custom XmlFriendlyReplacer
     * @param replacer the XmlFriendlyReplacer
     */
    public AbstractXmlFriendlyDriver(XmlFriendlyReplacer replacer) {
        this.replacer = replacer;
    }

    protected XmlFriendlyReplacer replacer(){
        return replacer;
    }

    protected HierarchicalStreamReader decorate(HierarchicalStreamReader reader){
        return new XmlFriendlyReaderWrapper(reader, replacer);
    }

    protected HierarchicalStreamWriter decorate(HierarchicalStreamWriter writer){
        return new XmlFriendlyWriterWrapper(writer, replacer);
    }
    
}
