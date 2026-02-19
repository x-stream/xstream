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

import java.util.concurrent.CopyOnWriteArraySet;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Converts a CopyOnWriteArraySet.
 *
 * @author Joerg Schaible
 * @since upcoming
 */
public class CopyOnWriteArraySetConverter extends AbstractCopyOnWriteArrayConverter {
    private static final String ELEMENT_UNSERIALIZABLE_PARENTS = "unserializable-parents";
    private static final String ATTRIBUTE_VALUE_XSTREAM = "xstream";

    /**
     * Constructs a CopyOnWriteArraySetConverter.
     *
     * @param mapper the mapper
     * @since upcoming
     */
    public CopyOnWriteArraySetConverter(final Mapper mapper) {
        this(mapper, CopyOnWriteArraySet.class);
    }

    /**
     * Constructs a CopyOnWriteArraySetConverter for a CopyOnWriteArraySet or derived type.
     *
     * @param mapper the mapper
     * @param type the compatible CopyOnWriteArraySet type to handle
     * @since upcoming
     */
    protected CopyOnWriteArraySetConverter(
            final Mapper mapper, @SuppressWarnings("rawtypes") final Class<? extends CopyOnWriteArraySet> type) {
        super(mapper, type);
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        if (getCollectionType() == CopyOnWriteArraySet.class) {
            final String attributeName = mapper().aliasForSystemAttribute(ATTRIBUTE_SERIALIZATION);
            if (attributeName != null) {
                writer.addAttribute(attributeName, ATTRIBUTE_VALUE_XSTREAM);
            }
        }
        super.marshal(source, writer, context);
    }

    @Override
    protected int skipLegacyFormat(final HierarchicalStreamReader reader) {
        int up = 0;
        if (getCollectionType() == CopyOnWriteArraySet.class) {
            final Mapper mapper = mapper();
            final String attributeName = mapper.aliasForSystemAttribute(ATTRIBUTE_SERIALIZATION);
            final String serialization = attributeName != null ? reader.getAttribute(attributeName) : null;
            if (!ATTRIBUTE_VALUE_XSTREAM.equals(serialization)) {
                if (ATTRIBUTE_VALUE_CUSTOM.equals(serialization)) {
                    up += checkElement(ELEMENT_UNSERIALIZABLE_PARENTS, reader, true);
                    up += checkElement(null, reader, false);
                    up += checkElement(ELEMENT_DEFAULT, reader, false);
                }
                if (serialization == null || up > 0) {
                    up = checkSerializableArraySetFormat(reader, "al");
                } else if (serialization != null) {
                    throwForUnhandledSerializationType(serialization);
                }
            }
        }
        return up;
    }
}
