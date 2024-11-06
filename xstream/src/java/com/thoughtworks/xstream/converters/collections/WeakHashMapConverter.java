/*
 * Copyright (C) 2024 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 19. October 2024 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.collections;

import java.util.WeakHashMap;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


/**
 * Converts a WeakHashMap. A WeakHashMap is supposed to release its elements when they are no longer referenced.
 * Therefore is at unmarshalling time no guarantee that an entry is still available when it is referenced later in the
 * stream. As consequence the converter will marshal no elements at all, it will create an empty WeakHashMap at
 * unmarshalling time.
 *
 * @author Joerg Schaible
 * @since 1.4.21
 */
public class WeakHashMapConverter implements Converter {

    @Override
    public boolean canConvert(final Class<?> type) {
        return WeakHashMap.class == type;
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        // do nothing
    }

    @Override
    public WeakHashMap<?, ?> unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        @SuppressWarnings("rawtypes")
        final WeakHashMap<?, ?> weakHashMap = new WeakHashMap();
        return weakHashMap;
    }
}
