package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Converts a char[] to XML, storing the contents as a single
 * String.
 *
 * @author Joe Walnes
 */
public class CharArrayConverter implements Converter {

    public boolean canConvert(Class type) {
        return type.isArray() && type.getComponentType().equals(char.class);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        char[] chars = (char[]) source;
        writer.setValue(new String(chars));
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        return reader.getValue().toCharArray();
    }
}
