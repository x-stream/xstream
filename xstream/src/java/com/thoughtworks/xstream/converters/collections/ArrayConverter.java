package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.objecttree.ObjectTree;
import com.thoughtworks.xstream.xml.XMLReader;
import com.thoughtworks.xstream.xml.XMLWriter;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ArrayConverter extends AbstractCollectionConverter {

    public ArrayConverter(ClassMapper classMapper,String classAttributeIdentifier) {
        super(classMapper,classAttributeIdentifier);
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
        // read the items from xml into a list
        List items = new LinkedList();
        while (xmlReader.nextChild()) {
            Object item = readItem(xmlReader, objectGraph, converterLookup);
            items.add(item);
            xmlReader.pop();
        }
        // now convert the list into an array
        // (this has to be done as a separate list as the array size is not
        //  known until all items have been read)
        Object array = Array.newInstance(requiredType.getComponentType(), items.size());
        int i = 0;
        for (Iterator iterator = items.iterator(); iterator.hasNext();) {
            Object item = (Object) iterator.next();
            Array.set(array, i, item);
            i++;
        }
        objectGraph.set(array);
    }
}
