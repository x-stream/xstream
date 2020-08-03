/*
 * Copyright (C) 2003, 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2014, 2018, 2020 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 26. September 2003 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Converts a char primitive or {@link Character} wrapper to
 * a string. If char is '\0' the representing string is empty.
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class CharConverter implements Converter, SingleValueConverter {

    @Override
    public boolean canConvert(final Class<?> type) {
        return type == char.class || type == Character.class;
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final Character ch = (Character)source;
        writer.setValue(toString(ch));
    }

    @Override
    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final String nullAttribute = reader.getAttribute("null");
        if (nullAttribute != null && nullAttribute.equals("true")) {
            return Character.valueOf('\0');
        } else {
            return fromString(reader.getValue());
        }
    }

    @Override
    public Object fromString(final String str) {
        if (str.length() == 0) {
            return Character.valueOf('\0');
        } else {
            return Character.valueOf(str.charAt(0));
        }
    }

    @Override
    public String toString(final Object obj) {
        final char ch = ((Character)obj).charValue();
        return ch == '\0' ? "" : obj.toString();
    }

}
