package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.old.MarshallingContextAdaptor;
import com.thoughtworks.xstream.converters.old.OldConverter;
import com.thoughtworks.xstream.converters.old.UnmarshallingContextAdaptor;
import com.thoughtworks.xstream.objecttree.ObjectTree;
import com.thoughtworks.xstream.xml.XMLReader;
import com.thoughtworks.xstream.xml.XMLWriter;

public abstract class AbstractCollectionConverter implements OldConverter {
    protected ClassMapper classMapper;
    protected String classAttributeIdentifier;

    public abstract boolean canConvert(Class type);

    public AbstractCollectionConverter(ClassMapper classMapper,String classAttributeIdentifier) {
        this.classMapper = classMapper;
        this.classAttributeIdentifier = classAttributeIdentifier;
    }

    public abstract void toXML(ObjectTree objectGraph, XMLWriter xmlWriter, ConverterLookup converterLookup);

    public abstract void fromXML(ObjectTree objectGraph, XMLReader xmlReader, ConverterLookup converterLookup, Class requiredType);

    protected void writeItem(Object item, XMLWriter xmlWriter, ConverterLookup converterLookup, ObjectTree objectGraph) {
        if (item == null) {
            xmlWriter.startElement("null");
            xmlWriter.endElement();
        } else {
            Class type = item.getClass();
            xmlWriter.startElement(classMapper.lookupName(type));
            Converter converter = converterLookup.lookupConverterForType(type);
            converter.toXML(new MarshallingContextAdaptor(objectGraph.newStack(item), xmlWriter, converterLookup));
            xmlWriter.endElement();
        }
    }

    protected Object readItem(XMLReader xmlReader, ObjectTree objectGraph, ConverterLookup converterLookup) {
        String classAttribute = xmlReader.attribute(classAttributeIdentifier);
        Class type;
        if (classAttribute == null) {
            type = classMapper.lookupType(xmlReader.name());
        } else {
            type = classMapper.lookupType(classAttribute);
        }
        ObjectTree itemWriter = objectGraph.newStack(type);
        Converter converter = converterLookup.lookupConverterForType(type);
        return converter.fromXML(new UnmarshallingContextAdaptor(itemWriter, xmlReader, converterLookup, type));
    }

    protected Object createCollection(Class type) {
        Class defaultType = classMapper.lookupDefaultType(type);
        try {
            return defaultType.newInstance();
        } catch (InstantiationException e) {
            throw new ConversionException("Cannot instantiate " + defaultType.getName(), e);
        } catch (IllegalAccessException e) {
            throw new ConversionException("Cannot instantiate " + defaultType.getName(), e);
        }
    }
}
