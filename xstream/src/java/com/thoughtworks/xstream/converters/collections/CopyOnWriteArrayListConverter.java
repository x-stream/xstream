/*
 * Copyright (C) 2026 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 17. February 2026 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.collections;

import java.util.concurrent.CopyOnWriteArrayList;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Converts a CopyOnWriteArrayList.
 *
 * @author Joerg Schaible
 * @since upcoming
 */
public class CopyOnWriteArrayListConverter extends AbstractCopyOnWriteArrayConverter {
    /**
     * Constructs a CopyOnWriteArrayListConverter.
     *
     * @param mapper the mapper
     * @since upcoming
     */
    public CopyOnWriteArrayListConverter(final Mapper mapper) {
        this(mapper, CopyOnWriteArrayList.class);
    }

    /**
     * Constructs a CopyOnWriteArrayListConverter for a CopyOnWriteArrayList or derived type.
     *
     * @param mapper the mapper
     * @param type the compatible CopyOnWriteArrayList type to handle
     * @since upcoming
     */
    protected CopyOnWriteArrayListConverter(final Mapper mapper, final Class<?> type) {
        super(mapper, type);
    }

    @Override
    protected int skipLegacyFormat(final HierarchicalStreamReader reader) {
        int up = 0;
        final Class<?> type = getCollectionType();
        if (type == CopyOnWriteArrayList.class) {
            final String serialization = reader.getAttribute(mapper().aliasForSystemAttribute(ATTRIBUTE_SERIALIZATION));
            if (ATTRIBUTE_VALUE_CUSTOM.equals(serialization)) {
                up = checkSerializableArraySetFormat(reader, null);
            } else if (serialization != null) {
                throwForUnhandledSerializationType(serialization);
            }
        }
        return up;
    }
}
