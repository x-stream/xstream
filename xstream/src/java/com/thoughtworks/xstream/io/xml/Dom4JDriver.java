package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.StreamException;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

public class Dom4JDriver implements HierarchicalStreamDriver {
    public HierarchicalStreamReader createReader(String xml) {
        try {
            return new Dom4Reader(DocumentHelper.parseText(xml));
        } catch (DocumentException e) {
            throw new StreamException(e);
        }
    }

}
