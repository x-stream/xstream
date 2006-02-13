package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import javax.swing.plaf.FontUIResource;

import java.awt.Font;
import java.util.Map;

public class FontConverter implements Converter {

    public boolean canConvert(Class type) {
        // String comparison is used here because Font.class loads the class which in turns instantiates AWT,
        // which is nasty if you don't want it.
        return type.getName().equals("java.awt.Font") || type.getName().equals("javax.swing.plaf.FontUIResource");
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Font font = (Font) source;
        Map attributes = font.getAttributes();
        writer.startNode("attributes"); // <attributes>
        context.convertAnother(attributes);
        writer.endNode(); // </attributes>
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        reader.moveDown(); // into <attributes>
        Map attributes = (Map) context.convertAnother(null, Map.class);
        reader.moveUp(); // out of </attributes>
        Font font = Font.getFont(attributes);
        if (context.getRequiredType() == FontUIResource.class) {
            return new FontUIResource(font);
        } else {
            return font;
        }
    }
}
