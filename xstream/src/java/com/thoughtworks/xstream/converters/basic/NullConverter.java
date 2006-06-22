package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * Special converter to signify nulls at the root level.
 *
 * @author Joe Walnes
 */
public class NullConverter implements Converter {

    public boolean canConvert(Class type) {
        return type == null || Mapper.Null.class.isAssignableFrom(type);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        ExtendedHierarchicalStreamWriterHelper.startNode(writer, "null", null);
        writer.endNode();
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        return null;
    }
}
