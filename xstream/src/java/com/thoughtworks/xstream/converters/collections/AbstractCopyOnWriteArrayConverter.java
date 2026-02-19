/*
 * Copyright (C) 2026 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 18. February 2026 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.collections;

import java.util.ArrayList;
import java.util.Collection;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Base class for converters of the CopyOnWriteArray types.
 *
 * @author Joerg Schaible
 * @since upcoming
 */
public abstract class AbstractCopyOnWriteArrayConverter extends CollectionConverter {
    protected static final String ELEMENT_DEFAULT = "default";
    protected static final String ATTRIBUTE_SERIALIZATION = "serialization";
    protected static final String ATTRIBUTE_VALUE_CUSTOM = "custom";

    /**
     * Constructs a CopyOnWriteArraySetConverter for a CopyOnWriteArraySet or derived type.
     *
     * @param mapper the mapper
     * @param type the compatible CopyOnWriteArraySet type to handle
     * @author J&ouml;rg Schaible
     * @since upcoming
     */
    protected AbstractCopyOnWriteArrayConverter(final Mapper mapper, final Class<?> type) {
        super(mapper, type);
    }

    @Override
    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        @SuppressWarnings({"rawtypes", "unchecked"})
        final Collection<Object> collection = (Collection)createCollection(context.getRequiredType());
        int up = skipLegacyFormat(reader);
        final ArrayList<Object> list = new ArrayList<Object>();
        populateCollection(reader, context, list);
        while (up-- > 0) {
            reader.moveUp();
        }
        collection.addAll(list); // is internally optimized for adding ArrayList
        return collection;
    }

    /**
     * Skip the initial structure of the legacy format. The method must move down and forward in the stream directly to
     * the first element of the array and return the immersion depth.
     *
     * @param reader the stream reader
     * @return the immersion depth
     * @since upcoming
     */
    abstract protected int skipLegacyFormat(final HierarchicalStreamReader reader);

    /**
     * Throw {@link ConversionException} for unhandled serialization type.
     * 
     * @param serialization the serialization type
     * @since upcoming
     */
    protected void throwForUnhandledSerializationType(final String serialization) {
        final ConversionException ex = new ConversionException("Unmarshalling of unexpected legacy format failed.");
        ex.add("expected-serialization-attribute-value", ATTRIBUTE_VALUE_CUSTOM);
        ex.add("serialization-attribute-value", serialization);
        throw ex;
    }

    /**
     * Check and skip the legacy serialization format of a CopyOnWriteArraySet instance.
     * 
     * @param reader the stream reader
     * @param firstElement name of the first element or {@code null} to skip the check
     * @return the immersion depth
     * @since upcoming
     */
    protected int checkSerializableArraySetFormat(final HierarchicalStreamReader reader, final String firstElement) {
        int up = firstElement == null ? 1 : checkElement(firstElement, reader, false);
        up += checkElement(null, reader, false);
        up += checkElement(ELEMENT_DEFAULT, reader, true);
        up += checkElement(mapper().serializedClass(int.class), reader, true);
        return up;
    }

    /**
     * Check and skip the next element.
     * 
     * @param expected the expected element name
     * @param reader the stream reader
     * @param up <code>true</code> if the element should be left
     * @return 1 of the element was not left, else 0
     * @since upcoming
     */
    protected int checkElement(final String expected, final HierarchicalStreamReader reader, final boolean up) {
        reader.moveDown();
        final String actual = reader.getNodeName();
        if (expected != null && !expected.equals(actual)) {
            final ConversionException ex = new ConversionException("Unmarshalling of unexpected legacy format failed.");
            ex.add("expected-element", expected);
            ex.add("element", actual);
            throw ex;
        }
        if (up) {
            reader.moveUp();
        }
        return up ? 0 : 1;
    }
}
