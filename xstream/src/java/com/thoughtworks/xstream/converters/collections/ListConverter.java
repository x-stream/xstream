package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.objecttree.ObjectTree;
import com.thoughtworks.xstream.xml.XMLReader;
import com.thoughtworks.xstream.xml.XMLWriter;

import java.util.Iterator;
import java.util.List;

public class ListConverter extends AbstractCollectionConverter {

    public ListConverter(ClassMapper classMapper, Class defaultImplementation) {
        super(classMapper, defaultImplementation);
    }

    public boolean canConvert(Class type) {
        return List.class.isAssignableFrom(type);
    }

    public void toXML(ObjectTree objectGraph, XMLWriter xmlWriter, ConverterLookup converterLookup) {
        List list = (List) objectGraph.get();
        for (Iterator iterator = list.iterator(); iterator.hasNext();) {
            Object item = iterator.next();
            writeItem(item, xmlWriter, converterLookup, objectGraph);
        }
    }

    public void fromXML(ObjectTree objectGraph, XMLReader xmlReader, ConverterLookup converterLookup, Class requiredType) {
        List list = (List) createCollection(requiredType);
        int childCount = xmlReader.childCount();
        for (int i = 0; i < childCount; i++) {
            Object item = readItem(xmlReader, i, objectGraph, converterLookup);
            list.add(item);
        }
        objectGraph.set(list);
    }

}
