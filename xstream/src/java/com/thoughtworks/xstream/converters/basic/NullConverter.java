package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class NullConverter implements Converter {

    public boolean canConvert(Class type) {
        return type == null;
    }

    public void toXML(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        writer.startElement("null");
        writer.endElement();
    }

    public Object fromXML(HierarchicalStreamReader reader, UnmarshallingContext context) {
        return null;
    }
}
