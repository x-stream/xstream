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

package com.thoughtworks.xstream.converters;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


/**
 * Wrapper to convert a {@link com.thoughtworks.xstream.converters.SingleValueConverter} into a
 * {@link com.thoughtworks.xstream.converters.Converter}.
 * 
 * @author J&ouml;rg Schaible
 * @see com.thoughtworks.xstream.converters.Converter
 * @see com.thoughtworks.xstream.converters.SingleValueConverter
 */
public class SingleValueConverterWrapper implements Converter, SingleValueConverter, ErrorReporter {

    private final SingleValueConverter wrapped;

    public SingleValueConverterWrapper(final SingleValueConverter wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return wrapped.canConvert(type);
    }

    @Override
    public String toString(final Object obj) {
        return wrapped.toString(obj);
    }

    @Override
    public Object fromString(final String str) {
        return wrapped.fromString(str);
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        writer.setValue(toString(source));
    }

    @Override
    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        return fromString(reader.getValue());
    }

    @Override
    public void appendErrors(final ErrorWriter errorWriter) {
        errorWriter.add("wrapped-converter", wrapped == null ? "(null)" : wrapped.getClass().getName());
        if (wrapped instanceof ErrorReporter) {
            ((ErrorReporter)wrapped).appendErrors(errorWriter);
        }
    }
}
