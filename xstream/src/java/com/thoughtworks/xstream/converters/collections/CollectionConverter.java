package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.*;

public class CollectionConverter extends AbstractCollectionConverter {

    public CollectionConverter(ClassMapper classMapper, String classAttributeIdentifier) {
        super(classMapper, classAttributeIdentifier);
    }

    public boolean canConvert(Class type) {
        return type.equals(ArrayList.class)
                || type.equals(HashSet.class)
                || type.equals(LinkedList.class)
                || type.equals(Vector.class)
                || type.equals(LinkedHashSet.class);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Collection collection = (Collection) source;
        for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
            Object item = iterator.next();
            writeItem(item, context, writer);
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Collection collection = (Collection) createCollection(context.getRequiredType());
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            Object item = readItem(reader, context, collection);
            collection.add(item);
            reader.moveUp();
        }
        return collection;
    }

}
