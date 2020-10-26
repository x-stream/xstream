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
