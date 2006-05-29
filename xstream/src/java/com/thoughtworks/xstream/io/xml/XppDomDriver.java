package com.thoughtworks.xstream.io.xml;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.xml.xppdom.Xpp3DomBuilder;

public class XppDomDriver extends AbstractXmlFriendlyDriver {
    
    public XppDomDriver() {
        super(new XmlFriendlyReplacer());
    }

    public XppDomDriver(XmlFriendlyReplacer replacer) {
        super(replacer);
    }
    
    public HierarchicalStreamReader createReader(Reader xml) {
        try {
            return xmlFriendlyReader(new XppDomReader(Xpp3DomBuilder.build(xml)));
        } catch (Exception e) {
            throw new StreamException(e);
        }
    }

    public HierarchicalStreamReader createReader(InputStream in) {
        return createReader(new InputStreamReader(in));
    }

    public HierarchicalStreamWriter createWriter(Writer out) {
        return xmlFriendlyWriter(new PrettyPrintWriter(out));
    }

    public HierarchicalStreamWriter createWriter(OutputStream out) {
        return createWriter(new OutputStreamWriter(out));
    }
}
