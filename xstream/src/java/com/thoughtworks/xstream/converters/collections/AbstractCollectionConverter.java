package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;

public abstract class AbstractCollectionConverter implements Converter {
    protected ClassMapper classMapper;
    protected String classAttributeIdentifier;

    public abstract boolean canConvert(Class type);

    public AbstractCollectionConverter(ClassMapper classMapper,String classAttributeIdentifier) {
        this.classMapper = classMapper;
        this.classAttributeIdentifier = classAttributeIdentifier;
    }

    public abstract void toXML(MarshallingContext context);
    public abstract Object fromXML(UnmarshallingContext context);

    protected void writeItem(Object item, MarshallingContext context) {
        if (item == null) {
            context.xmlStartElement("null");
            context.xmlEndElement();
        } else {
            context.xmlStartElement(classMapper.lookupName(item.getClass()));
            context.convert(item);
            context.xmlEndElement();
        }
    }

    protected Object readItem(UnmarshallingContext context) {
        String classAttribute = context.xmlAttribute(classAttributeIdentifier);
        Class type;
        if (classAttribute == null) {
            type = classMapper.lookupType(context.xmlElementName());
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
