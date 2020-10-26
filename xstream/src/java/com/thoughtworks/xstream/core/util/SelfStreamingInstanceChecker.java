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

package com.thoughtworks.xstream.core.util;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


/**
 * A special converter that prevents self-serialization. The serializing XStream instance adds a converter of this type
 * to prevent self-serialization and will throw an exception instead.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2
 */
public class SelfStreamingInstanceChecker implements Converter {

    private final Object self;
    private Converter defaultConverter;
    private final ConverterLookup lookup;

    /**
     * @since 1.4.5
     */
    public SelfStreamingInstanceChecker(final ConverterLookup lookup, final Object xstream) {
        this.lookup = lookup;
        self = xstream;
    }

    /**
     * @deprecated As of 1.4.5 use {@link #SelfStreamingInstanceChecker(ConverterLookup, Object)}
     */
    @Deprecated
    public SelfStreamingInstanceChecker(final Converter defaultConverter, final Object xstream) {
        this.defaultConverter = defaultConverter;
        self = xstream;
        lookup = null;
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return type == self.getClass();
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        if (source == self) {
            throw new ConversionException("Cannot marshal the XStream instance in action");
        }
        getConverter().marshal(source, writer, context);
    }

    @Override
    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        return getConverter().unmarshal(reader, context);
    }

    private Converter getConverter() {
        return defaultConverter != null ? defaultConverter : lookup.lookupConverterForType(Object.class);
    }
}
