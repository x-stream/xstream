package com.thoughtworks.xstream.converters;

import com.thoughtworks.xstream.objecttree.ObjectTree;
import com.thoughtworks.xstream.xml.XMLReader;
import com.thoughtworks.xstream.xml.XMLWriter;

public interface Converter {
    void toXML(ObjectTree objectGraph, XMLWriter xmlWriter, ConverterLookup converterLookup);

    void fromXML(ObjectTree objectGraph, XMLReader xmlReader, ConverterLookup converterLookup, Class requiredType);
}
