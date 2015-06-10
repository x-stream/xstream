/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2014 XStream Committers.
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
 * 
 * @author Joe Walnes
 */
public class ImmutableTypesMapper extends MapperWrapper {

    private final Set<Class<?>> unreferenceableTypes = new HashSet<Class<?>>();
    private final Set<Class<?>> immutableTypes = new HashSet<Class<?>>();

    public ImmutableTypesMapper(final Mapper wrapped) {
        super(wrapped);
    }

    public void addImmutableType(final Class<?> type, boolean isReferenceable) {
        immutableTypes.add(type);
        if(! isReferenceable) { unreferenceableTypes.add(type); }
    }

    @Override
    public boolean isImmutableValueType(final Class<?> type) {
        return immutableTypes.contains(type) || super.isImmutableValueType(type);
    }

    @Override
    public boolean isReferenceable(final Class<?> type) {
        return ! unreferenceableTypes.contains(type);
    }
}
