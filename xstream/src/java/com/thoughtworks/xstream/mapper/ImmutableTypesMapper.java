/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2015, 2021 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 22. January 2005 by Joe Walnes
 */
package com.thoughtworks.xstream.mapper;

import java.util.HashSet;
import java.util.Set;

/**
 * Mapper that specifies which types are basic immutable types. Types that are marked as immutable will be written
 * multiple times in the serialization stream without using references.
 * <p>
 * Note, that an already persisted stream might still contain references for immutable types. They can be dereferenced
 * at deserialization time, unless the type is explicitly declared as unreferenceable. However, this is only possible at
 * the expense of memory book-keeping all instances.
 * </p>
 *
 * @author Joe Walnes
 */
public class ImmutableTypesMapper extends MapperWrapper {

    private final Set unreferenceableTypes = new HashSet();
    private final Set immutableTypes = new HashSet();

    public ImmutableTypesMapper(Mapper wrapped) {
        super(wrapped);
    }

    /**
     * @deprecated As of 1.4.9 use {@link #addImmutableType(Class, boolean)}
     */
    public void addImmutableType(Class type) {
        addImmutableType(type, true);
    }

    /**
     * Declare a type as immutable.
     *
     * @param type the immutable type
     * @param isReferenceable flag for possible references
     * @since 1.4.9
     */
    public void addImmutableType(final Class type, final boolean isReferenceable) {
        immutableTypes.add(type);
        if (!isReferenceable) {
            unreferenceableTypes.add(type);
        } else {
            unreferenceableTypes.remove(type);
        }
    }

    public boolean isImmutableValueType(Class type) {
        if (immutableTypes.contains(type)) {
            return true;
        } else {
            return super.isImmutableValueType(type);
        }
    }

    public boolean isReferenceable(final Class type) {
        if (immutableTypes.contains(type)) {
            return !unreferenceableTypes.contains(type);
        } else {
            return super.isReferenceable(type);
        }
    }
}
