/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2013, 2014, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 31. July 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.extended;

import java.util.regex.Pattern;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


/**
 * Converts a {@link Pattern} using two nested elements for the pattern itself and its flags.
 * <p>
 * Ensures that the pattern is compiled upon deserialization.
 * </p>
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class RegexPatternConverter implements Converter {

    /**
     * @since 1.4.5
     */
    public RegexPatternConverter() {
    }

    /**
     * @deprecated As of 1.4.5, use {@link #RegexPatternConverter()} instead
     */
    @Deprecated
    public RegexPatternConverter(final Converter defaultConverter) {
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return type == Pattern.class;
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final Pattern pattern = (Pattern)source;
        writer.startNode("pattern");
        writer.setValue(pattern.pattern());
        writer.endNode();
        writer.startNode("flags");
        writer.setValue(String.valueOf(pattern.flags()));
        writer.endNode();
    }

    @Override
    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        reader.moveDown();
        final String pattern = reader.getValue();
        reader.moveUp();
        reader.moveDown();
        final int flags = Integer.parseInt(reader.getValue());
        reader.moveUp();
        return Pattern.compile(pattern, flags);
    }

}
