package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.SingleValueConverter;
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
public class CharConverter implements Converter, SingleValueConverter {

    public boolean canConvert(Class type) {
        return type.equals(char.class) || type.equals(Character.class);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        writer.setValue(toString(source));
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        String nullAttribute = reader.getAttribute("null");
        if (nullAttribute != null && nullAttribute.equals("true")) {
            return new Character('\0');
        } else {
            return fromString(reader.getValue());
        }
    }

    public Object fromString(String str) {
        if (str.length() == 0) {
            return new Character('\0');
        } else {
            return new Character(str.charAt(0));
        }
    }

    public String toString(Object obj) {
        char ch = ((Character)obj).charValue();
        return ch == '\0' ? "" : obj.toString();
    }

}
