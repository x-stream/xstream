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

import com.thoughtworks.xstream.core.util.Primitives;


/**
 * Mapper that allows a fully qualified class name to be replaced with an alias.
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class ClassAliasingMapper extends MapperWrapper {

    private final Map<Class<?>, String> typeToName = new HashMap<>();
    private final Map<String, String> classToName = new HashMap<>();
    private transient Map<String, String> nameToType = new HashMap<>();

    public ClassAliasingMapper(final Mapper wrapped) {
        super(wrapped);
    }

    public void addClassAlias(final String name, final Class<?> type) {
        nameToType.put(name, type.getName());
        classToName.put(type.getName(), name);
    }

    public void addTypeAlias(final String name, final Class<?> type) {
        nameToType.put(name, type.getName());
        typeToName.put(type, name);
    }

    @Override
    public String serializedClass(final Class<?> type) {
        final String alias = classToName.get(type.getName());
        if (alias != null) {
            return alias;
        } else {
            for (final Class<?> compatibleType : typeToName.keySet()) {
                if (compatibleType.isAssignableFrom(type)) {
                    return typeToName.get(compatibleType);
                }
            }
            return super.serializedClass(type);
        }
    }

    @Override
    public Class<?> realClass(String elementName) {
        final String mappedName = nameToType.get(elementName);

        if (mappedName != null) {
            final Class<?> type = Primitives.primitiveType(mappedName);
            if (type != null) {
                return type;
            }
            elementName = mappedName;
        }

        return super.realClass(elementName);
    }

    /**
     * @deprecated As of 1.4.9
     */
    @Deprecated
    public boolean itemTypeAsAttribute(final Class<?> clazz) {
        return classToName.containsKey(clazz.getName());
    }

    /**
     * @deprecated As of 1.4.9
     */
    @Deprecated
    public boolean aliasIsAttribute(final String name) {
        return nameToType.containsKey(name);
    }

    private Object readResolve() {
        nameToType = new HashMap<>();
        for (final String type : classToName.keySet()) {
            nameToType.put(classToName.get(type), type);
        }
        for (final Class<?> type : typeToName.keySet()) {
            nameToType.put(typeToName.get(type), type.getName());
        }
        return this;
    }
}
