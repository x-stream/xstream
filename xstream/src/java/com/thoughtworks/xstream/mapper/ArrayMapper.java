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

import com.thoughtworks.xstream.core.util.Primitives;


/**
 * Mapper that detects arrays and changes the name so it can identified as an array (for example Foo[] gets serialized
 * as foo-array). Supports multi-dimensional arrays.
 * 
 * @author Joe Walnes
 */
public class ArrayMapper extends MapperWrapper {

    public ArrayMapper(final Mapper wrapped) {
        super(wrapped);
    }

    @Override
    public String serializedClass(Class<?> type) {
        final StringBuilder arraySuffix = new StringBuilder();
        String name = null;
        while (type.isArray()) {
            name = super.serializedClass(type);
            if (type.getName().equals(name)) {
                type = type.getComponentType();
                arraySuffix.append("-array");
                name = null;
            } else {
                break;
            }
        }
        if (name == null) {
            name = boxedTypeName(type);
        }
        if (name == null) {
            name = super.serializedClass(type);
        }
        if (arraySuffix.length() > 0) {
            return name + arraySuffix;
        } else {
            return name;
        }
    }

    @Override
    public Class<?> realClass(String elementName) {
        int dimensions = 0;

        // strip off "-array" suffix
        while (elementName.endsWith("-array")) {
            elementName = elementName.substring(0, elementName.length() - 6); // cut off -array
            ++dimensions;
        }

        if (dimensions > 0) {
            Class<?> componentType = Primitives.primitiveType(elementName);
            if (componentType == null) {
                componentType = super.realClass(elementName);
            }
            while (componentType.isArray()) {
                componentType = componentType.getComponentType();
                ++dimensions;
            }
            return super.realClass(arrayType(dimensions, componentType));
        } else {
            return super.realClass(elementName);
        }
    }

    private String arrayType(final int dimensions, final Class<?> componentType) {
        final StringBuilder className = new StringBuilder();
        for (int i = 0; i < dimensions; i++) {
            className.append('[');
        }
        if (componentType.isPrimitive()) {
            className.append(Primitives.representingChar(componentType));
            return className.toString();
        } else {
            className.append('L').append(componentType.getName()).append(';');
            return className.toString();
        }
    }

    private String boxedTypeName(final Class<?> type) {
        return Primitives.isBoxed(type) ? type.getName() : null;
    }
}
