package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.xml.xpp3.Xpp3DomBuilder;

import java.io.StringReader;

public class Xpp3Driver
        implements HierarchicalStreamDriver {
    public HierarchicalStreamReader createReader(String xml) {
        try {
            return new Xpp3Reader(Xpp3DomBuilder.build(new StringReader(xml)));
        } catch (Exception e) {
            throw new StreamException(e);
        }
    }
}
