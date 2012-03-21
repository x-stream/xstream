/*
 * Copyright (C) 2012 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 21. March 2012 by Joerg Schaible
 */
package com.thoughtworks.acceptance;

import java.util.concurrent.ConcurrentHashMap;

public class Concurrent15TypesTest extends AbstractAcceptanceTest {
    
    public void testConcurrentHashMap() {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
        map.put("walnes", "joe");
        String xml = xstream.toXML(map);
        String expected = 
               "<concurrent-hash-map>\n"
            + "  <entry>\n"
            + "    <string>walnes</string>\n"
            + "    <string>joe</string>\n"
            + "  </entry>\n"
            + "</concurrent-hash-map>";
        assertEquals(xml, expected);
        @SuppressWarnings("unchecked")
        ConcurrentHashMap<String, String> out = (ConcurrentHashMap<String, String>) xstream.fromXML(xml);
        assertEquals("{walnes=joe}", out.toString());
    }
}
