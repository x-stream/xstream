package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.xml.xppdom.Xpp3DomBuilder;

import java.io.Reader;

public class XppDomDriver implements HierarchicalStreamDriver {
    public HierarchicalStreamReader createReader(Reader xml) {
        try {
            return new XppDomReader(Xpp3DomBuilder.build(xml));
        } catch (Exception e) {
            throw new StreamException(e);
        }
    }
}
