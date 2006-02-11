package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Special converter to signify nulls at the root level.
 *
 * @author Joe Walnes
 */
public class NullConverter implements Converter {

    public boolean canConvert(Class type) {
        return type == null || type.equals(ClassMapper.Null.class);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        writer.startNode("null");
        writer.endNode();
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        return null;
    }
}
