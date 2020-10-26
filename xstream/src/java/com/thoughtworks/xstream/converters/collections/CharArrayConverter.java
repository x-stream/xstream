/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2014, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 06. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


/**
 * Converts a char[] as a single string.
 * 
 * @author Joe Walnes
 */
public class CharArrayConverter implements Converter {

    @Override
    public boolean canConvert(final Class<?> type) {
        return type != null && type.isArray() && type.getComponentType().equals(char.class);
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        char[] chars = (char[])source;
        writer.setValue(new String(chars));
    }

    @Override
    public char[] unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        return reader.getValue().toCharArray();
    }
}
