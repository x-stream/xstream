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
