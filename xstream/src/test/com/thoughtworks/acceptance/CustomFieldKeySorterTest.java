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

package com.thoughtworks.acceptance;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.FieldKey;
import com.thoughtworks.xstream.converters.reflection.FieldKeySorter;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;


/**
 * @author J&ouml;rg Schaible
 */
public class CustomFieldKeySorterTest extends AbstractAcceptanceTest {

    @Override
    protected XStream createXStream() {
        final XStream xstream = new XStream(new PureJavaReflectionProvider(new FieldDictionary(
            new AlphabeticalFieldkeySorter())));
        setupSecurity(xstream);
        return xstream;
    }

    static class Base {
        String yyy = "y";
        String ccc = "c";
        String bbb = "b";
    }

    static class First extends Base {
        String aaa = "a";
    }

    static class Second extends First {
        String xxx = "x";
        String zzz = "z";
    }

    public void testSortsAlphabetically() {
        xstream.alias("second", Second.class);

        final String xml = ""
            + "<second>\n"
            + "  <aaa>a</aaa>\n"
            + "  <bbb>b</bbb>\n"
            + "  <ccc>c</ccc>\n"
            + "  <xxx>x</xxx>\n"
            + "  <yyy>y</yyy>\n"
            + "  <zzz>z</zzz>\n"
            + "</second>";

        assertBothWays(new Second(), xml);
    }

    private static class AlphabeticalFieldkeySorter implements FieldKeySorter {
        @Override
        public Map<FieldKey, Field> sort(final Class<?> type, final Map<FieldKey, Field> keyedByFieldKey) {
            final Map<FieldKey, Field> map = new TreeMap<>(new Comparator<FieldKey>() {

                @Override
                public int compare(final FieldKey fieldKey1, final FieldKey fieldKey2) {
                    return fieldKey1.getFieldName().compareTo(fieldKey2.getFieldName());
                }
            });
            map.putAll(keyedByFieldKey);
            return map;
        }
    }
}
