package com.thoughtworks.xstream.converters;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public interface Converter {

    boolean canConvert(Class type);

    void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context);

    Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context);

}
