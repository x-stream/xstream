package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public abstract class AbstractCollectionConverter implements Converter {
    protected ClassMapper classMapper;
    protected String classAttributeIdentifier;

    public abstract boolean canConvert(Class type);

    public AbstractCollectionConverter(ClassMapper classMapper,String classAttributeIdentifier) {
        this.classMapper = classMapper;
        this.classAttributeIdentifier = classAttributeIdentifier;
    }

    public abstract void toXML(Object source, HierarchicalStreamWriter writer, MarshallingContext context);
    public abstract Object fromXML(HierarchicalStreamReader reader, UnmarshallingContext context);

    protected void writeItem(Object item, MarshallingContext context, HierarchicalStreamWriter writer) {
        if (item == null) {
            writer.startNode("null");
            writer.startNode();
        } else {
            writer.startNode(classMapper.lookupName(item.getClass()));
            context.convertAnother(item);
            writer.startNode();
        }
    }

    protected Object readItem(HierarchicalStreamReader reader, UnmarshallingContext context) {
        String classAttribute = reader.getAttribute(classAttributeIdentifier);
        Class type;
        if (classAttribute == null) {
            type = classMapper.lookupType(reader.getNodeName());
        } else {
            type = classMapper.lookupType(classAttribute);
        }
        return context.convertAnother(type);
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
