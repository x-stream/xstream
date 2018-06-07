/*
 * Copyright (C) 2003, 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2014, 2015, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 01. October 2003 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.extended;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


/**
 * Converts an AWT {@link Color}, using four nested elements: red, green, blue, alpha.
 *
 * @author Joe Walnes
 */
public class ColorConverter implements Converter {

    @Override
    public boolean canConvert(final Class<?> type) {
        // String comparison is used here because Color.class loads the class which in turns instantiates AWT,
        // which is nasty if you don't want it.
        return type != null && type.getName().equals("java.awt.Color");
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final Color color = (Color)source;
        write("red", color.getRed(), writer);
        write("green", color.getGreen(), writer);
        write("blue", color.getBlue(), writer);
        write("alpha", color.getAlpha(), writer);
    }

    @Override
    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final Map<String, Integer> elements = new HashMap<>();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            elements.put(reader.getNodeName(), Integer.valueOf(reader.getValue()));
            reader.moveUp();
        }
        return new Color(elements.get("red").intValue(), elements.get("green").intValue(), elements
            .get("blue")
            .intValue(), elements.get("alpha").intValue());
    }

    private void write(final String fieldName, final int value, final HierarchicalStreamWriter writer) {
        writer.startNode(fieldName, int.class);
        writer.setValue(String.valueOf(value));
        writer.endNode();
    }
}
