package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.objecttree.ObjectTree;
import com.thoughtworks.xstream.xml.XMLReader;
import com.thoughtworks.xstream.xml.XMLWriter;

public abstract class AbstractCollectionConverter implements Converter {
    private ClassMapper classMapper;

    public abstract boolean canConvert(Class type);

    public AbstractCollectionConverter(ClassMapper classMapper) {
        this.classMapper = classMapper;
    }

    public abstract void toXML(ObjectTree objectGraph, XMLWriter xmlWriter, ConverterLookup converterLookup);

    public abstract void fromXML(ObjectTree objectGraph, XMLReader xmlReader, ConverterLookup converterLookup, Class requiredType);

    protected void writeItem(Object item, XMLWriter xmlWriter, ConverterLookup converterLookup, ObjectTree objectGraph) {
        Class type = item.getClass();
        xmlWriter.startElement(classMapper.lookupName(type));
        Converter converter = converterLookup.lookup(type);
        converter.toXML(objectGraph.newStack(item), xmlWriter, converterLookup);
        xmlWriter.endElement();
    }

    protected Object readItem(XMLReader xmlReader, int childIndex, ObjectTree objectGraph, ConverterLookup converterLookup) {
        xmlReader.child(childIndex);
        Class type = classMapper.lookupType(xmlReader.name());
        ObjectTree itemWriter = objectGraph.newStack(type);
        Converter converter = converterLookup.lookup(type);
        converter.fromXML(itemWriter, xmlReader, converterLookup, type);
        xmlReader.pop();
        return itemWriter.get();
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
