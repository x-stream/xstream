package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.*;

public class MapConverter extends AbstractCollectionConverter {

    public MapConverter(ClassMapper classMapper, String classAttributeIdentifier) {
        super(classMapper, classAttributeIdentifier);
    }

    public boolean canConvert(Class type) {
        return type.equals(HashMap.class)
                || type.equals(Hashtable.class)
                || type.equals(LinkedHashMap.class);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Map map = (Map) source;
        for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            writer.startNode("entry");

            writeItem(entry.getKey(), context, writer);
            writeItem(entry.getValue(), context, writer);

            writer.endNode();
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Map map = (Map) createCollection(context.getRequiredType());
        populateMap(reader, context, map);
        return map;
    }

    protected void populateMap(HierarchicalStreamReader reader, UnmarshallingContext context, Map map) {
        while (reader.hasMoreChildren()) {
            reader.moveDown();

            reader.moveDown();
            Object key = readItem(reader, context, map);
            reader.moveUp();

            reader.moveDown();
            Object value = readItem(reader, context, map);
            reader.moveUp();

            map.put(key, value);

            reader.moveUp();
        }
    }

}
