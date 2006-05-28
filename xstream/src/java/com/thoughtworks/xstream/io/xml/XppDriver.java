package com.thoughtworks.xstream.io.xml;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class XppDriver extends AbstractXmlFriendlyDriver {
    
    private static boolean xppLibraryPresent;

    public XppDriver() {
        super(new XmlFriendlyReplacer());
    }

    public XppDriver(XmlFriendlyReplacer replacer) {
        super(replacer);
    }

    public HierarchicalStreamReader createReader(Reader xml) {
        loadLibrary();
        return decorate(new XppReader(xml));
    }

    public HierarchicalStreamReader createReader(InputStream in) {
        return createReader(new InputStreamReader(in));
    }

    private void loadLibrary() {
        if (!xppLibraryPresent) {
            try {
                Class.forName("org.xmlpull.mxp1.MXParser");
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("XPP3 pull parser library not present. Specify another driver." +
                        " For example: new XStream(new DomDriver())");
            }
            xppLibraryPresent = true;
        }
    }

    public HierarchicalStreamWriter createWriter(Writer out) {
        return decorate(new PrettyPrintWriter(out));
    }

    public HierarchicalStreamWriter createWriter(OutputStream out) {
        return createWriter(new OutputStreamWriter(out));
    }
}
