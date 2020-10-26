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

package com.thoughtworks.xstream.converters.reflection;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;


public class NativeFieldKeySorterTest extends TestCase {

    static class Base {
        String yyy;
        String ccc;
        String bbb;
    }

    static class First extends Base {
        String aaa;
    }

    static class Second extends First {
        String xxx;
        String zzz;
    }

    public void testDoesSortInDeclarationOrderWithFieldsOfBaseClassFirst() {
        final String[] fieldOrder = new String[]{"yyy", "ccc", "bbb", "aaa", "xxx", "zzz"};
        final FieldKeySorter sorter = new NativeFieldKeySorter();
        final Map<FieldKey, Field> originalMap = buildMap(Second.class);
        final Map<FieldKey, Field> map = sorter.sort(Second.class, originalMap);
        final Field[] fields = map.values().toArray(new Field[map.size()]);
        assertEquals(fieldOrder.length, fields.length);
        for (int i = 0; i < fieldOrder.length; i++) {
            assertEquals("Field[" + i + ']', fieldOrder[i], fields[i].getName());
        }
    }

    private Map<FieldKey, Field> buildMap(final Class<?> type) {
        final Map<FieldKey, Field> map = new LinkedHashMap<>();
        Class<?> cls = type;
        while (!cls.equals(Object.class)) {
            final Field[] fields = cls.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                final Field field = fields[i];
                if (field.isSynthetic() && field.getName().startsWith("$jacoco")) {
                    continue;
                }
                map.put(new FieldKey(field.getName(), cls, i), field);
            }
            cls = cls.getSuperclass();
        }
        return map;
    }
}
