package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.io.Reader;
import java.io.Writer;

public class XppDriver implements HierarchicalStreamDriver {

    private static boolean xppLibraryPresent;

    public HierarchicalStreamReader createReader(Reader xml) {
        if (!xppLibraryPresent) {
            try {
                Class.forName("org.xmlpull.mxp1.MXParser");
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("XPP3 pull parser library not present. Specify another driver." +
                        " For example: new XStream(new DomDriver())");
            }
            xppLibraryPresent = true;
        }
        return new XppReader(xml);
    }

    public HierarchicalStreamWriter createWriter(Writer out) {
        return new PrettyPrintWriter(out);
    }
}
