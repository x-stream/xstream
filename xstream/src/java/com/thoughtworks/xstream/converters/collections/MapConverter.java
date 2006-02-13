package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 * Converts a java.util.Map to XML, specifying an 'entry'
 * element with 'key' and 'value' children.
 * <p>Note: 'key' and 'value' is not the name of the generated tag. The
 * children are serialized as normal elements and the implementation expects
 * them in the order 'key'/'value'.</p>
 * <p>Supports java.util.HashMap, java.util.Hashtable and
 * java.util.LinkedHashMap.</p>
 *
 * @author Joe Walnes
 */
public class MapConverter extends AbstractCollectionConverter {

    public MapConverter(Mapper mapper) {
        super(mapper);
    }

    public boolean canConvert(Class type) {
        return type.equals(HashMap.class)
                || type.equals(Hashtable.class)
                || (JVM.is14() && type.getName().equals("java.util.LinkedHashMap"));
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Map map = (Map) source;
        for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            writer.startNode(mapper().serializedClass(Map.Entry.class));

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
