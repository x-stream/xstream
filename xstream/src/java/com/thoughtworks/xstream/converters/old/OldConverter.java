package com.thoughtworks.xstream.converters.old;

import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.objecttree.ObjectTree;
import com.thoughtworks.xstream.xml.XMLReader;
import com.thoughtworks.xstream.xml.XMLWriter;

public interface OldConverter {

    boolean canConvert(Class type);

    void toXML(ObjectTree objectGraph, XMLWriter xmlWriter, ConverterLookup converterLookup);

    void fromXML(ObjectTree objectGraph, XMLReader xmlReader, ConverterLookup converterLookup, Class requiredType);
}
