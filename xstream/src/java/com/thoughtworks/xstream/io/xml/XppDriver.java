package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

import java.io.StringReader;

public class XppDriver implements HierarchicalStreamDriver {

    public HierarchicalStreamReader createReader(String xml) {
        return new XppReader(new StringReader(xml));
    }
}
