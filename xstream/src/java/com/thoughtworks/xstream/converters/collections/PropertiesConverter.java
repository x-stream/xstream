package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.objecttree.ObjectTree;
import com.thoughtworks.xstream.xml.XMLWriter;
import com.thoughtworks.xstream.xml.XMLReader;
import com.thoughtworks.xstream.alias.ClassMapper;

import java.util.Map;
import java.util.Iterator;
import java.util.Properties;

public class PropertiesConverter implements Converter {

    public boolean canConvert(Class type) {
        return Properties.class.isAssignableFrom(type);
    }

    public void toXML(ObjectTree objectGraph, XMLWriter xmlWriter, ConverterLookup converterLookup) {
        Map map = (Map) objectGraph.get();
        for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            xmlWriter.startElement("property");
            xmlWriter.addAttribute("name", entry.getKey().toString());
            xmlWriter.addAttribute("value", entry.getValue().toString());
            xmlWriter.endElement();
        }
    }

    public void fromXML(ObjectTree objectTree, XMLReader xmlReader, ConverterLookup converterLookup, Class requiredType) {
        Properties properties = new Properties();
        while (xmlReader.nextChild()) {
            xmlReader.nextChild();
            String name = xmlReader.attribute("name");
            String value = xmlReader.attribute("value");
            properties.setProperty(name, value);
            xmlReader.pop();
        }
        objectTree.set(properties);
    }
}
