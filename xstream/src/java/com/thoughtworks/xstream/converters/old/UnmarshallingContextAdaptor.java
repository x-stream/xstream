package com.thoughtworks.xstream.converters.old;

import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.objecttree.ObjectTree;
import com.thoughtworks.xstream.xml.XMLReader;

public class UnmarshallingContextAdaptor implements UnmarshallingContext {
    private ObjectTree objectTree;
    private XMLReader xmlReader;
    private ConverterLookup converterLookup;
    private Class type;

    public UnmarshallingContextAdaptor(ObjectTree objectTree, XMLReader xmlReader, ConverterLookup converterLookup, Class type) {
        this.objectTree = objectTree;
        this.xmlReader = xmlReader;
        this.converterLookup = converterLookup;
        this.type = type;
    }

    public ObjectTree getObjectTree() {
        return objectTree;
    }

    public XMLReader getXMLReader() {
        return xmlReader;
    }

    public ConverterLookup getConverterLookup() {
        return converterLookup;
    }

    public Class getRequiredType() {
        return type;
    }
}
