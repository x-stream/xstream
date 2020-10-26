/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
