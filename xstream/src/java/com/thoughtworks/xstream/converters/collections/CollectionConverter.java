package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.objecttree.ObjectTree;
import com.thoughtworks.xstream.xml.XMLReader;
import com.thoughtworks.xstream.xml.XMLWriter;

import java.util.Collection;
import java.util.Iterator;

public class CollectionConverter extends AbstractCollectionConverter {

    public CollectionConverter(ClassMapper classMapper) {
        super(classMapper);
    }

    public boolean canConvert(Class type) {
        return Collection.class.isAssignableFrom(type);
    }

    public void toXML(ObjectTree objectGraph, XMLWriter xmlWriter, ConverterLookup converterLookup) {
        Collection collection = (Collection) objectGraph.get();
        for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
            Object item = iterator.next();
            writeItem(item, xmlWriter, converterLookup, objectGraph);
        }
    }

    public void fromXML(ObjectTree objectGraph, XMLReader xmlReader, ConverterLookup converterLookup, Class requiredType) {
        Collection collection = (Collection) createCollection(requiredType);
        int childCount = xmlReader.childCount();
        for (int i = 0; i < childCount; i++) {
            Object item = readItem(xmlReader, i, objectGraph, converterLookup);
            collection.add(item);
        }
        objectGraph.set(collection);
    }

}
