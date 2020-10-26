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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Converts an array of objects or primitives, using a nested child element for each item.
 * 
 * @author Joe Walnes
 * @see com.thoughtworks.xstream.converters.extended.NamedArrayConverter
 */
public class ArrayConverter extends AbstractCollectionConverter {

    public ArrayConverter(final Mapper mapper) {
        super(mapper);
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return type != null && type.isArray();
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final int length = Array.getLength(source);
        for (int i = 0; i < length; i++) {
            final Object item = Array.get(source, i);
            writeCompleteItem(item, context, writer);
        }

    }

    @Override
    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        // read the items from xml into a list (the array size is not known until all items have been read)
        final List<Object> items = new ArrayList<>();
        while (reader.hasMoreChildren()) {
            final Object item = readCompleteItem(reader, context, null); // TODO: arg, what should replace null?
            items.add(item);
        }
        // now convertAnother the list into an array
        final Object array = Array.newInstance(context.getRequiredType().getComponentType(), items.size());
        int i = 0;
        for (final Object item : items) {
            Array.set(array, i++, item);
        }
        return array;
    }
}
