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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.thoughtworks.xstream.converters.collections.MapConverter;
import com.thoughtworks.xstream.core.JVM;


public class ConcurrentTypesTest extends AbstractAcceptanceTest {

    public void testConcurrentHashMap() {
        final ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
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
