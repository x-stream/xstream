package com.thoughtworks.xstream.converters.composite;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.objecttree.ObjectTree;
import com.thoughtworks.xstream.xml.XMLReader;
import com.thoughtworks.xstream.xml.XMLWriter;

public class ObjectWithFieldsConverter implements Converter {

    private ClassMapper classMapper;

    public ObjectWithFieldsConverter(ClassMapper classMapper) {
        this.classMapper = classMapper;
    }

    public void toXML(ObjectTree objectGraph, XMLWriter xmlWriter, ConverterLookup converterLookup) {
        String[] fieldNames = objectGraph.fieldNames();
        for (int i = 0; i < fieldNames.length; i++) {
            String fieldName = fieldNames[i];

            objectGraph.push(fieldName);

            if (objectGraph.get() != null) {
                writeFieldAsXML(xmlWriter, fieldName, objectGraph, converterLookup);
            }

            objectGraph.pop();
        }
    }

    private void writeFieldAsXML(XMLWriter xmlWriter, String fieldName, ObjectTree objectGraph, ConverterLookup converterLookup) {
        xmlWriter.pushElement(fieldName);

        writeClassAttributeInXMLIfNotDefaultImplementation(objectGraph, xmlWriter);
        Converter converter = converterLookup.lookup(objectGraph.type());
        converter.toXML(objectGraph, xmlWriter, converterLookup);

        xmlWriter.pop();
    }

    protected void writeClassAttributeInXMLIfNotDefaultImplementation(ObjectTree objectGraph, XMLWriter xmlWriter) {
        Class actualType = objectGraph.get().getClass();
        Class defaultType = classMapper.lookupDefaultType(objectGraph.type());
        if (!actualType.equals(defaultType)) {
            xmlWriter.attribute("class", classMapper.lookupName(actualType));
        }
    }

    public void fromXML(final ObjectTree objectGraph, XMLReader xmlReader, ConverterLookup converterLookup, Class requiredType) {
        objectGraph.create(requiredType);
        String[] fieldNames = objectGraph.fieldNames();
        for (int i = 0; i < fieldNames.length; i++) {
            String fieldName = fieldNames[i];
            objectGraph.push(fieldName);
            xmlReader.child(fieldName);

            Class type = determineWhichImplementationToUse(xmlReader, objectGraph);
            Converter converter = converterLookup.lookup(type);
            converter.fromXML(objectGraph, xmlReader, converterLookup, type);

            xmlReader.pop();
            objectGraph.pop();
        }
    }

    private Class determineWhichImplementationToUse(XMLReader xmlReader, final ObjectTree objectGraph) {
        String classAttribute = xmlReader.attribute("class");
        Class type;
        if (classAttribute == null) {
            type = objectGraph.type();
        } else {
            type = classMapper.lookupType(classAttribute);
        }
        return type;
    }

}
