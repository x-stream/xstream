package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.objecttree.ObjectTree;
import com.thoughtworks.xstream.xml.XMLReader;
import com.thoughtworks.xstream.xml.XMLWriter;

public class NullConverter implements Converter {

    public boolean canConvert(Class type) {
        return type == null;
    }

    public void toXML(ObjectTree objectGraph, XMLWriter xmlWriter, ConverterLookup converterLookup) {
        xmlWriter.startElement("null");
        xmlWriter.endElement();
    }

    public void fromXML(ObjectTree objectGraph, XMLReader xmlReader, ConverterLookup converterLookup, Class requiredType) {
        // no need to do anything
    }
}
