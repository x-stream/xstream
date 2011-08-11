/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 08. May 2004 by Joe Walnes
 */
package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.io.StreamException;

public class ErrorTest extends AbstractAcceptanceTest {

    public static class Thing {
        String one;
        int two;
    }

    protected void setUp() throws Exception {
        super.setUp();
        xstream.alias("thing", Thing.class);
    }

    public void testUnmarshallerThrowsExceptionWithDebuggingInfo() {
        try {
            xstream.fromXML(""
                + "<thing>\n" 
                + "  <one>string 1</one>\n" 
                + "  <two>another string</two>\n" 
                + "</thing>");
            fail("Error expected");
        } catch (ConversionException e) {
            assertEquals("java.lang.NumberFormatException",
                    e.get("cause-exception"));
            if (JVM.is14()) {
                assertEquals("For input string: \"another string\"",
                        e.get("cause-message"));
            } else {
                assertEquals("another string",
                        e.get("cause-message"));
            }
            assertEquals(Integer.class.getName(),
                    e.get("class"));
            assertEquals("/thing/two",
                    e.get("path"));
            assertEquals("3",
                    e.get("line number"));
            assertEquals("java.lang.Integer",
                    e.get("required-type"));
            assertEquals(Thing.class.getName(),
                e.get("class[1]"));
        }
    }

    public void testInvalidXml() {
        try {
            xstream.fromXML(""
                + "<thing>\n" 
                + "  <one>string 1</one>\n" 
                + "  <two><<\n" 
                + "</thing>");
            fail("Error expected");
        } catch (ConversionException e) {
            assertEquals(StreamException.class.getName(),
                    e.get("cause-exception"));
            assertNotNull(e.get("cause-message")); // depends on parser
            assertEquals("/thing/two",
                    e.get("path"));
            assertEquals("3",
                    e.get("line number"));
        }

    }
    
    public void testNonExistingMember() {
        try {
            xstream.fromXML("" 
                + "<thing>\n" 
                + "  <one>string 1</one>\n" 
                + "  <three>3</three>\n" 
                + "</thing>");
            fail("Error expected");
        } catch (ConversionException e) {
            assertEquals("three",
                    e.get("field"));
            assertEquals("/thing/three",
                    e.get("path"));
            assertEquals("3",
                    e.get("line number"));
        }
        
    }
}
