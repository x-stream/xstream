/*
 * Copyright (C) 2007, 2014, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 17. May 2007 by Joerg Schaible
 */
package com.thoughtworks.acceptance;

import java.lang.reflect.Field;
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
            final Map<FieldKey, Field> map = new TreeMap<>((final FieldKey fieldKey1, final FieldKey fieldKey2) -> fieldKey1.getFieldName().compareTo(fieldKey2.getFieldName()));
            map.putAll(keyedByFieldKey);
            return map;
        }
    }
}
