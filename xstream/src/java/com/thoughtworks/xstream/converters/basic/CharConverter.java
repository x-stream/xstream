package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Converts a char primitive or java.lang.Character wrapper to
 * a String. If char is \0, this will be marked as an attribute as
 * XML does not allow this.
 *
 * @author Joe Walnes
 */
public class CharConverter implements Converter {

    public boolean canConvert(Class type) {
        return type.equals(char.class) || type.equals(Character.class);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        if (source.toString().equals("\0")) {
            writer.addAttribute("null", "true");
        } else {
            writer.setValue(source.toString());
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        String nullAttribute = reader.getAttribute("null");
        if (nullAttribute != null && nullAttribute.equals("true")) {
            return new Character('\0');
        } else {
            return new Character(reader.getValue().charAt(0));
        }
    }

}
