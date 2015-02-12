/*
 * Copyright (C) 2012, 2015 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 21. March 2012 by Joerg Schaible
 */
package com.thoughtworks.acceptance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.thoughtworks.xstream.core.JVM;


public class Concurrent15TypesTest extends AbstractAcceptanceTest {

    public void testConcurrentHashMap() {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
        map.put("walnes", "joe");
        String xml = xstream.toXML(map);
        String expected = ""
            + "<concurrent-hash-map>\n"
            + "  <entry>\n"
            + "    <string>walnes</string>\n"
            + "    <string>joe</string>\n"
            + "  </entry>\n"
            + "</concurrent-hash-map>";
        assertEquals(xml, expected);
        @SuppressWarnings("unchecked")
        ConcurrentHashMap<String, String> out = (ConcurrentHashMap<String, String>)xstream.fromXML(xml);
        assertEquals("{walnes=joe}", out.toString());
    }

    public static class DerivedConcurrentHashMap extends ConcurrentHashMap<Object, Object> {
        private static final long serialVersionUID = 1L;
    }

    public void testDerivedConcurrentHashMap() {
        if (JVM.is18()) {
            xstream.alias("derived-map", DerivedConcurrentHashMap.class);

            Map<Object, Object> map = new DerivedConcurrentHashMap();
            map.put("test", "JUnit");

            String xml = ""
                + "<derived-map serialization=\"custom\">\n"
                + "  <unserializable-parents/>\n"
                + "  <concurrent-hash-map>\n"
                + "    <default>\n"
                + "      <segmentMask>15</segmentMask>\n"
                + "    </default>\n"
                + "    <string>test</string>\n"
                + "    <string>JUnit</string>\n"
                + "    <null/>\n"
                + "    <null/>\n"
                + "  </concurrent-hash-map>\n"
                + "</derived-map>";

            assertBothWays(map, xml);
        }
    }
}
