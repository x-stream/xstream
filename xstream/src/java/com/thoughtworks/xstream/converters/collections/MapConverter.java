package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.objecttree.ObjectTree;
import com.thoughtworks.xstream.xml.XMLReader;
import com.thoughtworks.xstream.xml.XMLWriter;

import java.util.Iterator;
import java.util.Map;

public class MapConverter extends AbstractCollectionConverter {

    public MapConverter(ClassMapper classMapper,String classAttributeIdentifier) {
        super(classMapper,classAttributeIdentifier);
    }

    public boolean canConvert(Class type) {
        return Map.class.isAssignableFrom(type);
    }

    public void toXML(ObjectTree objectGraph, XMLWriter xmlWriter, ConverterLookup converterLookup) {
        Map map = (Map) objectGraph.get();
        for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            xmlWriter.startElement("entry");

            writeItem(entry.getKey(), xmlWriter, converterLookup, objectGraph);
            writeItem(entry.getValue(), xmlWriter, converterLookup, objectGraph);

            xmlWriter.endElement();
        }
    }

    public void fromXML(ObjectTree objectGraph, XMLReader xmlReader, ConverterLookup converterLookup, Class requiredType) {
        Map map = (Map) createCollection(requiredType);
        while (xmlReader.nextChild()) {

            xmlReader.nextChild();
            Object key = readItem(xmlReader, objectGraph, converterLookup);
            xmlReader.pop();

            xmlReader.nextChild();
            Object value = readItem(xmlReader, objectGraph, converterLookup);
            xmlReader.pop();

            map.put(key, value);

            xmlReader.pop();
        }
        objectGraph.set(map);
    }

}
