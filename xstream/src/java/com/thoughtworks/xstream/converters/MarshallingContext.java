package com.thoughtworks.xstream.converters;

import com.thoughtworks.xstream.objecttree.ObjectTree;
import com.thoughtworks.xstream.xml.XMLWriter;

public interface MarshallingContext {

    /** @deprecated */
    ObjectTree getObjectTree();

    /** @deprecated */
    XMLWriter getXMLWriter();

    /** @deprecated */
    ConverterLookup getConverterLookup();

    Object currentObject();

    void xmlWriteText(String text);
    void xmlStartElement(String fieldName);
    void xmlEndElement();

}
