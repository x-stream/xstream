package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.StreamException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.io.Reader;

public class Dom4JDriver implements HierarchicalStreamDriver {
    public HierarchicalStreamReader createReader(Reader text) {
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(text);
            return new Dom4JReader(document);
        } catch (DocumentException e) {
            throw new StreamException(e);
        }
    }

}
