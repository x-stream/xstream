package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Base helper class for converters that need to handle
 * collections of items (arrays, Lists, Maps, etc).
 * <p/>
 * <p>Typically, subclasses of this will converter the outer
 * structure of the collection, loop through the contents and
 * call readItem() or writeItem() for each item.</p>
 *
 * @author Joe Walnes
 */
public abstract class AbstractCollectionConverter implements Converter {
    protected ClassMapper classMapper;
    protected String classAttributeIdentifier;

    public abstract boolean canConvert(Class type);

    public AbstractCollectionConverter(ClassMapper classMapper, String classAttributeIdentifier) {
        this.classMapper = classMapper;
        this.classAttributeIdentifier = classAttributeIdentifier;
    }

    public abstract void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context);

    public abstract Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context);

    protected void writeItem(Object item, MarshallingContext context, HierarchicalStreamWriter writer) {
        if (item == null) {
            // todo: this is duplicated in TreeMarshaller.start()
            writer.startNode(classMapper.lookupName(ClassMapper.Null.class));
            writer.endNode();
        } else {
            writer.startNode(classMapper.lookupName(item.getClass()));
            context.convertAnother(item);
            writer.endNode();
        }
    }

    protected Object readItem(HierarchicalStreamReader reader, UnmarshallingContext context, Object current) {
        String classAttribute = reader.getAttribute(classAttributeIdentifier);
        Class type;
        if (classAttribute == null) {
            type = classMapper.lookupType(reader.getNodeName());
        } else {
            type = classMapper.lookupType(classAttribute);
        }
        return context.convertAnother(current, type);
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
