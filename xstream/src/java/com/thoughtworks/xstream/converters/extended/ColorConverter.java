package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.objecttree.ObjectTree;
import com.thoughtworks.xstream.xml.XMLReader;
import com.thoughtworks.xstream.xml.XMLWriter;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ColorConverter implements Converter {
    public boolean canConvert(Class type) {
        return type.equals(Color.class);
    }

    public void toXML(ObjectTree objectGraph, XMLWriter xmlWriter, ConverterLookup converterLookup) {
        Color color = (Color) objectGraph.get();
        write("red", color.getRed(), xmlWriter);
        write("green", color.getGreen(), xmlWriter);
        write("blue", color.getBlue(), xmlWriter);
        write("alpha", color.getAlpha(), xmlWriter);
    }

    private void write(String fieldName, int value, XMLWriter xmlWriter) {
        xmlWriter.startElement(fieldName);
        xmlWriter.writeText(String.valueOf(value));
        xmlWriter.endElement();
    }

    public void fromXML(ObjectTree objectGraph, XMLReader xmlReader, ConverterLookup converterLookup, Class requiredType) {
        Map elements = new HashMap();
        while (xmlReader.nextChild()) {
            elements.put(xmlReader.name(), Integer.valueOf(xmlReader.text()));
            xmlReader.pop();
        }
        Color color = new Color(
                ((Integer) elements.get("red")).intValue(),
                ((Integer) elements.get("green")).intValue(),
                ((Integer) elements.get("blue")).intValue(),
                ((Integer) elements.get("alpha")).intValue()
        );
        objectGraph.set(color);
    }

}
