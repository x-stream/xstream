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

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.StreamException;


public class ErrorTest extends AbstractAcceptanceTest {

    public static class Thing {
        String one;
        int two;
    }

    @Override
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
        } catch (final ConversionException e) {
            assertEquals("java.lang.NumberFormatException", e.get("cause-exception"));
            assertEquals("For input string: \"another string\"", e.get("cause-message"));
            assertEquals(Integer.class.getName(), e.get("class"));
            assertEquals("/thing/two", e.get("path"));
            assertEquals("3", e.get("line number"));
            assertEquals("java.lang.Integer", e.get("required-type"));
            assertEquals(Thing.class.getName(), e.get("class[1]"));
        }
    }

    public void testInvalidXml() {
        try {
            xstream.fromXML(""//
                + "<thing>\n"
                + "  <one>string 1</one>\n"
                + "  <two><<\n"
                + "</thing>");
            fail("Error expected");
        } catch (final ConversionException e) {
            assertEquals(StreamException.class.getName(), e.get("cause-exception"));
            assertNotNull(e.get("cause-message")); // depends on parser
            assertEquals("/thing/two", e.get("path"));
            assertEquals("3", e.get("line number"));
        }
    }

    public void testNonExistingMember() {
        try {
            xstream.fromXML("" //
                + "<thing>\n"
                + "  <one>string 1</one>\n"
                + "  <three>3</three>\n"
                + "</thing>");
            fail("Error expected");
        } catch (final ConversionException e) {
            assertEquals("three", e.get("field"));
            assertEquals("/thing/three", e.get("path"));
            assertEquals("3", e.get("line number"));
        }
    }

    public void testNonExistingMemberMatchingAlias() {
        try {
            xstream.fromXML("" //
                + "<thing>\n"
                + "  <string>string 1</string>\n"
                + "</thing>");
            fail("Error expected");
        } catch (final ConversionException e) {
            assertEquals("/thing/string", e.get("path"));
            assertEquals("2", e.get("line number"));
        }
    }
}
