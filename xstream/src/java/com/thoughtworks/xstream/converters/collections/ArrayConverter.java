package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.objecttree.ObjectTree;
import com.thoughtworks.xstream.xml.XMLReader;
import com.thoughtworks.xstream.xml.XMLWriter;

import java.lang.reflect.Array;

public class ArrayConverter extends AbstractCollectionConverter {

    public ArrayConverter(ClassMapper classMapper) {
        super(classMapper);
    }

    public boolean canConvert(Class type) {
        return type.isArray();
    }

    public void toXML(ObjectTree objectGraph, XMLWriter xmlWriter, ConverterLookup converterLookup) {
        Object array = objectGraph.get();
        int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            Object item = Array.get(array, i);
            writeItem(item, xmlWriter, converterLookup, objectGraph);
        }
    }

    public void fromXML(ObjectTree objectGraph, XMLReader xmlReader, ConverterLookup converterLookup, Class requiredType) {
        int size = xmlReader.childCount();
        Object array = Array.newInstance(requiredType.getComponentType(), size);
        for (int i = 0; i < size; i++) {
            Object item = readItem(xmlReader, i, objectGraph, converterLookup);
            Array.set(array, i, item);
        }
        objectGraph.set(array);
    }


}
