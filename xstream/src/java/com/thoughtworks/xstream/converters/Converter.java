package com.thoughtworks.xstream.converters;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public interface Converter {

    boolean canConvert(Class type);

    void toXML(Object source, HierarchicalStreamWriter writer, MarshallingContext context);

    Object fromXML(HierarchicalStreamReader reader, UnmarshallingContext context);

}
