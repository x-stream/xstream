package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.objecttree.ObjectTree;
import com.thoughtworks.xstream.xml.XMLReader;
import com.thoughtworks.xstream.xml.XMLWriter;

import java.util.Iterator;
import java.util.Map;

public class MapConverter extends AbstractCollectionConverter {

    public MapConverter(ClassMapper classMapper) {
        super(classMapper);
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
        int childCount = xmlReader.childCount();
        Map map = (Map) createCollection(requiredType);
        for (int i = 0; i < childCount; i++) {
            xmlReader.child(i);

            Object key = readItem(xmlReader, 0, objectGraph, converterLookup);
            Object value = readItem(xmlReader, 1, objectGraph, converterLookup);
            map.put(key, value);

            xmlReader.pop();
        }
        objectGraph.set(map);
    }

}
