package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ColorConverter implements Converter {

    public boolean canConvert(Class type) {
        return type.equals(Color.class);
    }

    public void toXML(MarshallingContext context) {
        Color color = (Color) context.currentObject();
        write("red", color.getRed(), context);
        write("green", color.getGreen(), context);
        write("blue", color.getBlue(), context);
        write("alpha", color.getAlpha(), context);
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

    private void write(String fieldName, int value, MarshallingContext context) {
        context.xmlStartElement(fieldName);
        context.xmlWriteText(String.valueOf(value));
        context.xmlEndElement();
    }

}
