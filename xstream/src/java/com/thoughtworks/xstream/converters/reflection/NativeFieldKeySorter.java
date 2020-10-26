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
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;


/**
 * Sort the fields in their natural order. Fields are returned in their declaration order, fields of base classes first.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2.2
 */
public class NativeFieldKeySorter implements FieldKeySorter {

    @Override
    public Map<FieldKey, Field> sort(final Class<?> type, final Map<FieldKey, Field> keyedByFieldKey) {
        final Map<FieldKey, Field> map = new TreeMap<FieldKey, Field>(new Comparator<FieldKey>() {

            @Override
            public int compare(final FieldKey fieldKey1, final FieldKey fieldKey2) {
                int i = fieldKey1.getDepth() - fieldKey2.getDepth();
                if (i == 0) {
                    i = fieldKey1.getOrder() - fieldKey2.getOrder();
                }
                return i;
            }
        });
        map.putAll(keyedByFieldKey);
        return map;
    }

}
