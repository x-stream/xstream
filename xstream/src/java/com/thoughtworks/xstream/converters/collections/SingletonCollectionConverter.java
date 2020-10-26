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

import java.util.Collection;
import java.util.Collections;

import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Converts singleton collections (list and set) to XML, specifying a nested element for the item.
 * <p>
 * Supports Collections.singleton(Object) and Collections.singletonList(Object).
 * </p>
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4.2
 */
public class SingletonCollectionConverter extends CollectionConverter {

    private static final Class<?> LIST = Collections.singletonList(Boolean.TRUE).getClass();
    private static final Class<?> SET = Collections.singleton(Boolean.TRUE).getClass();

    /**
     * Construct a SingletonCollectionConverter.
     * 
     * @param mapper the mapper
     * @since 1.4.2
     */
    public SingletonCollectionConverter(final Mapper mapper) {
        super(mapper);
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return LIST == type || SET == type;
    }

    @Override
    public Collection<?> unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final Object item = readCompleteItem(reader, context, null);
        return context.getRequiredType() == LIST ? Collections.singletonList(item) : Collections.singleton(item);
    }
}
