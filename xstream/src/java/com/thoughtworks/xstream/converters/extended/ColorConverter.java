package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.objecttree.ObjectTree;
import com.thoughtworks.xstream.xml.XMLReader;
import com.thoughtworks.xstream.xml.XMLWriter;

import java.awt.*;

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
        Color color = new Color(
                read("red", xmlReader),
                read("green", xmlReader),
                read("blue", xmlReader),
                read("alpha", xmlReader)
        );
        objectGraph.set(color);
    }

    private int read(String field, XMLReader reader) {
        reader.child(field);
        int result = Integer.parseInt(reader.text());
        reader.pop();
        return result;
    }
}
