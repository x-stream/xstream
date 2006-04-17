package com.thoughtworks.xstream.io.xml;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Wrapper that decorates a HierarchicalStreamDriver to use xml-friendly HierarchicalStreamReader
 * and HierarchicalStreamWriter.
 * 
 * @author Mauro Talevi
 */
public class XmlFriendlyDriverWrapper implements HierarchicalStreamDriver {
    
    private HierarchicalStreamDriver driver;
    private XmlFriendlyReplacer replacer;
        
    /**
     * Creates a wrapper with default XmlFriendlyReplacer
     * @param driver the delegate driver
     */
    public XmlFriendlyDriverWrapper(HierarchicalStreamDriver driver) {
        this(driver, new XmlFriendlyReplacer());
    }
    
    /**
     * Creates a wrapper with custom XmlFriendlyReplacer
     * @param driver the delegate driver
     * @param replacer the XmlFriendlyReplacer
     */
    public XmlFriendlyDriverWrapper(HierarchicalStreamDriver driver, XmlFriendlyReplacer replacer) {
        this.driver = driver;
        this.replacer = replacer;
    }

    public HierarchicalStreamReader createReader(Reader in) {
        return new XmlFriendlyReaderWrapper(driver.createReader(in), replacer);
    }

    public HierarchicalStreamReader createReader(InputStream in) {
        return new XmlFriendlyReaderWrapper(driver.createReader(in), replacer);
    }

    public HierarchicalStreamWriter createWriter(Writer out) {
        return new XmlFriendlyWriterWrapper(driver.createWriter(out), replacer);
    }

    public HierarchicalStreamWriter createWriter(OutputStream out) {
        return new XmlFriendlyWriterWrapper(driver.createWriter(out), replacer);
    }

}
