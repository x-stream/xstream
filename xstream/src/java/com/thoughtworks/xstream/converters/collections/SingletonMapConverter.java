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

import java.util.Collections;
import java.util.Map;

import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Converts a singleton map to XML, specifying an 'entry' element with 'key' and 'value' children.
 * <p>
 * Note: 'key' and 'value' is not the name of the generated tag. The children are serialized as normal elements and the
 * implementation expects them in the order 'key'/'value'.
 * </p>
 * <p>
 * Supports Collections.singletonMap.
 * </p>
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4.2
 */
public class SingletonMapConverter extends MapConverter {

    private static final Class<?> MAP = Collections.singletonMap(Boolean.TRUE, null).getClass();

    /**
     * Construct a SingletonMapConverter.
     * 
     * @param mapper
     * @since 1.4.2
     */
    public SingletonMapConverter(final Mapper mapper) {
        super(mapper);
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return MAP == type;
    }

    @Override
    public Map<?, ?> unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        reader.moveDown();
        final Object key = readCompleteItem(reader, context, null);
        final Object value = readCompleteItem(reader, context, null);
        reader.moveUp();

        return Collections.singletonMap(key, value);
    }

}
