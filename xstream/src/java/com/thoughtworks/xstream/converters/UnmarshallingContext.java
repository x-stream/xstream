package com.thoughtworks.xstream.converters;

import com.thoughtworks.xstream.objecttree.ObjectTree;
import com.thoughtworks.xstream.xml.XMLReader;

public interface UnmarshallingContext {
    /** @deprecated */
    ObjectTree getObjectTree();

    /** @deprecated */
    XMLReader getXMLReader();

    /** @deprecated */
    ConverterLookup getConverterLookup();

    /** @deprecated */
    Class getRequiredType();

    String xmlText();
    String xmlElementName();
    void xmlPop();
    boolean xmlNextChild();
}
