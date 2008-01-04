/*
 * Copyright (C) 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 04. January 2008 by Joerg Schaible
 */
package com.thoughtworks.acceptance;

import java.util.UUID;

public class Basic15TypesTest extends AbstractAcceptanceTest {

    public void testUUID() {
        UUID uuid = UUID.randomUUID();
        assertBothWays(uuid, "<uuid>" + uuid + "</uuid>");
    }

    public void testStringBuilder() {
        StringBuilder builder = new StringBuilder();
        builder.append("woo");
        String xml = xstream.toXML(builder);
        assertEquals(xml, "<string-builder>woo</string-builder>");
        StringBuilder out = (StringBuilder) xstream.fromXML(xml);
        assertEquals("woo", out.toString());
    }
}
