package com.thoughtworks.xstream;

import com.thoughtworks.xstream.alias.DefaultClassMapper;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.lookup.DefaultConverterLookup;
import com.thoughtworks.xstream.objecttree.ObjectTree;
import com.thoughtworks.xstream.objecttree.reflection.ObjectFactory;
import com.thoughtworks.xstream.objecttree.reflection.ReflectionObjectGraph;
import com.thoughtworks.xstream.objecttree.reflection.SunReflectionObjectFactory;
import com.thoughtworks.xstream.xml.XMLReader;
import com.thoughtworks.xstream.xml.XMLWriter;
import com.thoughtworks.xstream.xml.dom4j.Dom4JXMLReader;
import com.thoughtworks.xstream.xml.text.PrettyPrintXMLWriter;
import org.dom4j.DocumentException;

import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

public class XStream {

    private DefaultClassMapper classMapper = new DefaultClassMapper();
    private ConverterLookup converterLookup = new DefaultConverterLookup(classMapper);

    public XStream() {
        alias("string", String.class);
        alias("int", Integer.class);
        alias("float", Float.class);
        alias("double", Double.class);
        alias("long", Long.class);
        alias("short", Short.class);
        alias("char", Character.class);
        alias("byte", Byte.class);
        alias("boolean", Boolean.class);
        alias("date", Date.class);

        alias("map", Map.class, HashMap.class);
        alias("list", List.class, ArrayList.class);

        alias("linked-list", LinkedList.class);
        alias("tree-map", TreeMap.class);
    }

    public void alias(String elementName, Class type, Class defaultImplementation) {
        classMapper.alias(elementName, type, defaultImplementation);
    }

    public void alias(String elementName, Class type) {
        alias(elementName, type, type);
    }

    public String toXML(Object obj) {
        Writer stringWriter = new StringWriter();
        XMLWriter xmlWriter = new PrettyPrintXMLWriter(stringWriter);
        ObjectFactory objectFactory = new SunReflectionObjectFactory();
        ObjectTree objectGraph = new ReflectionObjectGraph(obj, objectFactory);
        Converter rootConverter = converterLookup.lookup(obj.getClass());
        xmlWriter.pushElement(classMapper.lookupName(obj.getClass()));
        rootConverter.toXML(objectGraph, xmlWriter, converterLookup);
        xmlWriter.pop();
        return stringWriter.toString();
    }

    public Object fromXML(String xml) {
        try {
            // @TODO: Use an XMLReader that doesn't rely on DOM4J
            XMLReader xmlReader = new Dom4JXMLReader(xml);
            Class type = classMapper.lookupType(xmlReader.name());
            ObjectFactory objectFactory = new SunReflectionObjectFactory();
            ObjectTree objectGraph = new ReflectionObjectGraph(type, objectFactory);
            Converter rootConverter = converterLookup.lookup(type);
            rootConverter.fromXML(objectGraph, xmlReader, converterLookup, type);
            return objectGraph.get();
        } catch (DocumentException e) {
            throw new RuntimeException("Cannot parse xml", e);
        }
    }

}
