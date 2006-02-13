package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

/**
 * Converts most common Collections (Lists and Sets) to XML, specifying a nested
 * element for each item.
 * <p/>
 * <p>Supports java.util.ArrayList, java.util.HashSet,
 * java.util.LinkedList, java.util.Vector and java.util.LinkedHashSet.</p>
 *
 * @author Joe Walnes
 */
public class CollectionConverter extends AbstractCollectionConverter {

    public CollectionConverter(Mapper mapper) {
        super(mapper);
    }

    public boolean canConvert(Class type) {
        return type.equals(ArrayList.class)
                || type.equals(HashSet.class)
                || type.equals(LinkedList.class)
                || type.equals(Vector.class)
                || (JVM.is14() && type.getName().equals("java.util.LinkedHashSet"));
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
        populateCollection(reader, context, collection);
        return collection;
    }

    protected void populateCollection(HierarchicalStreamReader reader, UnmarshallingContext context, Collection collection) {
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            Object item = readItem(reader, context, collection);
            collection.add(item);
            reader.moveUp();
        }
    }

}
