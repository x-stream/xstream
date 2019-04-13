/*
 * Copyright (C) 2012, 2015, 2017, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 17. July 2018 by Joerg Schaible, renamed from Concurrent15TypesTest
 */
package com.thoughtworks.acceptance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.thoughtworks.xstream.converters.collections.MapConverter;
import com.thoughtworks.xstream.core.JVM;


public class ConcurrentTypesTest extends AbstractAcceptanceTest {

    public void testConcurrentHashMap() {
        final ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        map.put("walnes", "joe");
        final String xml = xstream.toXML(map);
        final String expected = ""
            + "<concurrent-hash-map>\n"
            + "  <entry>\n"
            + "    <string>walnes</string>\n"
            + "    <string>joe</string>\n"
            + "  </entry>\n"
            + "</concurrent-hash-map>";
        assertEquals(xml, expected);
        final ConcurrentHashMap<String, String> out = xstream.fromXML(xml);
        assertEquals("{walnes=joe}", out.toString());
    }

    public static class DerivedConcurrentHashMap extends ConcurrentHashMap<Object, Object> {
        private static final long serialVersionUID = 1L;
    }

    public void testDerivedConcurrentHashMap() {
        if (JVM.isVersion(8)) {
            xstream.alias("derived-map", DerivedConcurrentHashMap.class);
            xstream.registerConverter(new MapConverter(xstream.getMapper(), DerivedConcurrentHashMap.class));

            final Map<Object, Object> map = new DerivedConcurrentHashMap();
            map.put("test", "JUnit");

            final String xml = ""
                + "<derived-map>\n"
                + "  <entry>\n"
                + "    <string>test</string>\n"
                + "    <string>JUnit</string>\n"
                + "  </entry>\n"
                + "</derived-map>";

            assertBothWays(map, xml);
        }
    }
}
