package com.thoughtworks.xstream.converters;

import com.thoughtworks.xstream.objecttree.ObjectTree;
import com.thoughtworks.xstream.xml.XMLReader;

public interface UnmarshallingContext {
    ObjectTree getObjectTree();

    XMLReader getXMLReader();

    ConverterLookup getConverterLookup();

    Class getRequiredType();
}
