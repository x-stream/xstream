/*
 * Copyright (c) 2006, 2020 Oracle and/or its affiliates. All rights reserved.
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

package com.thoughtworks.xstream.core.util;

import java.util.HashMap;
import java.util.Map;


/**
 * Utility class for primitives.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2.1
 */
public final class Primitives {
    private final static Map<Class<?>, Class<?>> BOX = new HashMap<>();
    private final static Map<Class<?>, Class<?>> UNBOX = new HashMap<>();
    private final static Map<String, Class<?>> NAMED_PRIMITIVE = new HashMap<>();
    private final static Map<Class<?>, Character> REPRESENTING_CHAR = new HashMap<>();

    static {
        final Class<?>[][] boxing = new Class[][]{
            {Byte.TYPE, Byte.class}, {Character.TYPE, Character.class}, {Short.TYPE, Short.class}, //
            {Integer.TYPE, Integer.class}, {Long.TYPE, Long.class}, {Float.TYPE, Float.class}, //
            {Double.TYPE, Double.class}, {Boolean.TYPE, Boolean.class}, {Void.TYPE, Void.class}};
        final Character[] representingChars = {
            Character.valueOf('B'), Character.valueOf('C'), Character.valueOf('S'), Character.valueOf('I'), //
            Character.valueOf('J'), Character.valueOf('F'), Character.valueOf('D'), Character.valueOf('Z'), null};
        for (int i = 0; i < boxing.length; i++) {
            final Class<?> primitiveType = boxing[i][0];
            final Class<?> boxedType = boxing[i][1];
            BOX.put(primitiveType, boxedType);
            UNBOX.put(boxedType, primitiveType);
            NAMED_PRIMITIVE.put(primitiveType.getName(), primitiveType);
            REPRESENTING_CHAR.put(primitiveType, representingChars[i]);
        }
    }

    /**
     * Get the boxed type for a primitive.
     * 
     * @param type the primitive type
     * @return the boxed type or null
     */
    static public Class<?> box(final Class<?> type) {
        return BOX.get(type);
    }

    /**
     * Get the primitive type for a boxed one.
     * 
     * @param type the boxed type
     * @return the primitive type or null
     */
    static public Class<?> unbox(final Class<?> type) {
        return UNBOX.get(type);
    }

    /**
     * Check for a boxed type.
     * 
     * @param type the type to check
     * @return <code>true</code> if the type is boxed
     * @since 1.4
     */
    static public boolean isBoxed(final Class<?> type) {
        return UNBOX.containsKey(type);
    }

    /**
     * Get the primitive type by name.
     * 
     * @param name the name of the type
     * @return the Java type or <code>null</code>
     * @since 1.4
     */
    static public Class<?> primitiveType(final String name) {
        return NAMED_PRIMITIVE.get(name);
    }

    /**
     * Get the representing character of a primitive type.
     * 
     * @param type the primitive type
     * @return the representing character or 0
     * @since 1.4
     */
    static public char representingChar(final Class<?> type) {
        final Character ch = REPRESENTING_CHAR.get(type);
        return ch == null ? 0 : ch.charValue();
    }
}
