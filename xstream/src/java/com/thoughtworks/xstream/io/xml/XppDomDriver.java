package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.xml.xppdom.Xpp3DomBuilder;

import java.io.StringReader;

public class XppDomDriver
        implements HierarchicalStreamDriver {
    public HierarchicalStreamReader createReader(String xml) {
        try {
            return new XppDomReader(Xpp3DomBuilder.build(new StringReader(xml)));
        } catch (Exception e) {
            throw new StreamException(e);
        }
    }
}
