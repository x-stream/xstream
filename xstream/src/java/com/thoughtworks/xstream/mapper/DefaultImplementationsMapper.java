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

import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.InitializationException;


/**
 * Mapper that resolves default implementations of classes. For example, mapper.serializedClass(ArrayList.class) will
 * return java.util.List. Calling mapper.defaultImplementationOf(List.class) will return ArrayList.
 * 
 * @author Joe Walnes
 */
public class DefaultImplementationsMapper extends MapperWrapper {

    private final Map<Class<?>, Class<?>> typeToImpl = new HashMap<>();
    private transient Map<Class<?>, Class<?>> implToType = new HashMap<>();

    public DefaultImplementationsMapper(final Mapper wrapped) {
        super(wrapped);
        addDefaults();
    }

    protected void addDefaults() {
        // null handling
        addDefaultImplementation(null, Mapper.Null.class);
        // register primitive types
        addDefaultImplementation(Boolean.class, boolean.class);
        addDefaultImplementation(Character.class, char.class);
        addDefaultImplementation(Integer.class, int.class);
        addDefaultImplementation(Float.class, float.class);
        addDefaultImplementation(Double.class, double.class);
        addDefaultImplementation(Short.class, short.class);
        addDefaultImplementation(Byte.class, byte.class);
        addDefaultImplementation(Long.class, long.class);
    }

    public void addDefaultImplementation(final Class<?> defaultImplementation, final Class<?> ofType) {
        if (defaultImplementation != null && defaultImplementation.isInterface()) {
            throw new InitializationException("Default implementation is not a concrete class: "
                + defaultImplementation.getName());
        }
        typeToImpl.put(ofType, defaultImplementation);
        implToType.put(defaultImplementation, ofType);
    }

    @Override
    public String serializedClass(final Class<?> type) {
        final Class<?> baseType = implToType.get(type);
        return baseType == null ? super.serializedClass(type) : super.serializedClass(baseType);
    }

    @Override
    public Class<?> defaultImplementationOf(final Class<?> type) {
        if (typeToImpl.containsKey(type)) {
            return typeToImpl.get(type);
        } else {
            return super.defaultImplementationOf(type);
        }
    }

    private Object readResolve() {
        implToType = new HashMap<>();
        for (final Class<?> type : typeToImpl.keySet()) {
            implToType.put(typeToImpl.get(type), type);
        }
        return this;
    }
}
