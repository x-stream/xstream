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

    private final Set<Class<?>> unreferenceableTypes = new HashSet<>();
    private final Set<Class<?>> immutableTypes = new HashSet<>();

    public ImmutableTypesMapper(final Mapper wrapped) {
        super(wrapped);
    }

    /**
     * @deprecated As of 1.4.9 use {@link #addImmutableType(Class, boolean)}
     */
    @Deprecated
    public void addImmutableType(final Class<?> type) {
        addImmutableType(type, true);
    }

    /**
     * Declare a type as immutable.
     *
     * @param type the immutable type
     * @param isReferenceable flag for possible references
     * @since 1.4.9
     */
    public void addImmutableType(final Class<?> type, final boolean isReferenceable) {
        immutableTypes.add(type);
        if (!isReferenceable) {
            unreferenceableTypes.add(type);
        } else {
            unreferenceableTypes.remove(type);
        }
    }

    @Override
    public boolean isImmutableValueType(final Class<?> type) {
        if (immutableTypes.contains(type)) {
            return true;
        } else {
            return super.isImmutableValueType(type);
        }
    }

    @Override
    public boolean isReferenceable(final Class<?> type) {
        if (unreferenceableTypes.contains(type)) {
            return false;
        } else {
            return super.isReferenceable(type);
        }
    }
}
