/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2014, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.collections;

import java.util.BitSet;
import java.util.StringTokenizer;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


/**
 * Converts a {@link BitSet}, as a compact comma delimited list of ones and zeros.
 * 
 * @author Joe Walnes
 */
public class BitSetConverter implements Converter {

    @Override
    public boolean canConvert(final Class<?> type) {
        return type == BitSet.class;
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final BitSet bitSet = (BitSet)source;
        final StringBuilder buffer = new StringBuilder();
        boolean seenFirst = false;
        for (int i = 0; i < bitSet.length(); i++) {
            if (bitSet.get(i)) {
                if (seenFirst) {
                    buffer.append(',');
                } else {
                    seenFirst = true;
                }
                buffer.append(i);
            }
        }
        writer.setValue(buffer.toString());
    }

    @Override
    public BitSet unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final BitSet result = new BitSet();
        final StringTokenizer tokenizer = new StringTokenizer(reader.getValue(), ",", false);
        while (tokenizer.hasMoreTokens()) {
            final int index = Integer.parseInt(tokenizer.nextToken());
            result.set(index);
        }
        return result;
    }
}
