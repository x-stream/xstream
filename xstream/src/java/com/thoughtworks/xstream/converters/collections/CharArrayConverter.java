package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;

public class CharArrayConverter implements Converter {

    public boolean canConvert(Class type) {
        return type.isArray() && (
                    type.getComponentType().equals(char.class)
                || type.getComponentType().equals(Character.class));
    }

    public void toXML(MarshallingContext context) {
        char[] chars = (char[]) context.currentObject();
        context.xmlWriteText(new String(chars));
    }

    public Object fromXML(UnmarshallingContext context) {
        return context.xmlText().toCharArray();
    }
}
