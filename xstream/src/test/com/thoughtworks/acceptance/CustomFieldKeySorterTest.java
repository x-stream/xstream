/*
 * Copyright (C) 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 17. May 2007 by Joerg Schaible
 */
package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.FieldKey;
import com.thoughtworks.xstream.converters.reflection.FieldKeySorter;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;


/**
 * @author J&ouml;rg Schaible
 */
public class CustomFieldKeySorterTest extends AbstractAcceptanceTest {

    protected XStream createXStream() {
        return new XStream(new PureJavaReflectionProvider(new FieldDictionary(
            new AlphabeticalFieldkeySorter())));
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

        String xml = ""
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

        public Map sort(Class type, Map keyedByFieldKey) {
            Map map = new TreeMap(new Comparator() {

                public int compare(Object o1, Object o2) {
                    final FieldKey fieldKey1 = (FieldKey)o1;
                    final FieldKey fieldKey2 = (FieldKey)o2;
                    return fieldKey1.getFieldName().compareTo(fieldKey2.getFieldName());
                }
            });
            map.putAll(keyedByFieldKey);
            return map;
        }

    }
}
