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

package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.MarshallingStrategy;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Basic functionality of a tree based marshalling strategy.
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @since 1.3
 */
public abstract class AbstractTreeMarshallingStrategy implements MarshallingStrategy {

    @Override
    public Object unmarshal(final Object root, final HierarchicalStreamReader reader, final DataHolder dataHolder,
            final ConverterLookup converterLookup, final Mapper mapper) {
        final TreeUnmarshaller context = createUnmarshallingContext(root, reader, converterLookup, mapper);
        return context.start(dataHolder);
    }

    @Override
    public void marshal(final HierarchicalStreamWriter writer, final Object obj, final ConverterLookup converterLookup,
            final Mapper mapper, final DataHolder dataHolder) {
        final TreeMarshaller context = createMarshallingContext(writer, converterLookup, mapper);
        context.start(obj, dataHolder);
    }

    protected abstract TreeUnmarshaller createUnmarshallingContext(Object root, HierarchicalStreamReader reader,
            ConverterLookup converterLookup, Mapper mapper);

    protected abstract TreeMarshaller createMarshallingContext(HierarchicalStreamWriter writer,
            ConverterLookup converterLookup, Mapper mapper);
}
