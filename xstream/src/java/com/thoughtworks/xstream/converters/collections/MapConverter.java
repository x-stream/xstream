package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.Iterator;
import java.util.Map;

public class MapConverter extends AbstractCollectionConverter {

    public MapConverter(ClassMapper classMapper,String classAttributeIdentifier) {
        super(classMapper,classAttributeIdentifier);
    }

    public boolean canConvert(Class type) {
        return Map.class.isAssignableFrom(type);
    }

    public void toXML(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Map map = (Map) source;
        for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            writer.startElement("entry");

            writeItem(entry.getKey(), context, writer);
            writeItem(entry.getValue(), context, writer);

            writer.endElement();
        }
    }

    public Object fromXML(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Map map = (Map) createCollection(context.getRequiredType());
        while (reader.nextChild()) {

            reader.nextChild();
            Object key = readItem(reader, context);
            reader.pop();

            reader.nextChild();
            Object value = readItem(reader, context);
            reader.pop();

            map.put(key, value);

            reader.pop();
        }
        return map;
    }

}
