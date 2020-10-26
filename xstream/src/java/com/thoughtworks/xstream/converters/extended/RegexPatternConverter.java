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
