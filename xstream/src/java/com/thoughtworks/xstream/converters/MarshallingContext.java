package com.thoughtworks.xstream.converters;

import com.thoughtworks.xstream.objecttree.ObjectTree;
import com.thoughtworks.xstream.xml.XMLWriter;

public interface MarshallingContext {
    ObjectTree getObjectTree();

    XMLWriter getXMLWriter();

    ConverterLookup getConverterLookup();
}
