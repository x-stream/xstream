package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ColorConverter implements Converter {

    public boolean canConvert(Class type) {
        return type.equals(Color.class);
    }

    public void toXML(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Color color = (Color) source;
        write("red", color.getRed(), writer);
        write("green", color.getGreen(), writer);
        write("blue", color.getBlue(), writer);
        write("alpha", color.getAlpha(), writer);
    }

    public Object fromXML(UnmarshallingContext context) {
        Map elements = new HashMap();
        while (context.xmlNextChild()) {
            elements.put(context.xmlElementName(), Integer.valueOf(context.xmlText()));
            context.xmlPop();
        }
        Color color = new Color(
                ((Integer) elements.get("red")).intValue(),
                ((Integer) elements.get("green")).intValue(),
                ((Integer) elements.get("blue")).intValue(),
                ((Integer) elements.get("alpha")).intValue()
        );
        return color;
    }

    private void write(String fieldName, int value, HierarchicalStreamWriter writer) {
        writer.startElement(fieldName);
        writer.writeText(String.valueOf(value));
        writer.endElement();
    }

}
