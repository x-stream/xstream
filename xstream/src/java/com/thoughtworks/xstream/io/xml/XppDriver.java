package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

import java.io.Reader;

public class XppDriver implements HierarchicalStreamDriver {

    public HierarchicalStreamReader createReader(Reader xml) {
        return new XppReader(xml);
    }
}
