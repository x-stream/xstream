package com.thoughtworks.xstream.converters.old;

import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.objecttree.ObjectTree;
import com.thoughtworks.xstream.xml.XMLWriter;

public class MarshallingContextAdaptor implements MarshallingContext {
    private ObjectTree objectTree;
    private XMLWriter xmlWriter;
    private ConverterLookup converterLookup;

    public MarshallingContextAdaptor(ObjectTree objectTree, XMLWriter xmlWriter, ConverterLookup converterLookup) {
        this.objectTree = objectTree;
        this.xmlWriter = xmlWriter;
        this.converterLookup = converterLookup;
    }

    public ObjectTree getObjectTree() {
        return objectTree;
    }

    public XMLWriter getXMLWriter() {
        return xmlWriter;
    }

    public ConverterLookup getConverterLookup() {
        return converterLookup;
    }
}
