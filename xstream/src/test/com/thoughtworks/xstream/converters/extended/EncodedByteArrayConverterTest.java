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

package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.acceptance.objects.StandardObject;


public class EncodedByteArrayConverterTest extends AbstractAcceptanceTest {

    public void testMarshallsCharArrayAsSingleString() {
        final byte[] input = {0, 120, -124, 22, 33, 0, 5};
        final String expected = "<byte-array>AHiEFiEABQ==</byte-array>";

        assertBothWays(input, expected);
    }

    public void testUnmarshallsOldByteArraysThatHaveNotBeenEncoding() {
        // for backwards compatibility
        final String input = ""
            + "<byte-array>\n"
            + "  <byte>0</byte>\n"
            + "  <byte>120</byte>\n"
            + "  <byte>-124</byte>\n"
            + "  <byte>22</byte>\n"
            + "  <byte>33</byte>\n"
            + "  <byte>0</byte>\n"
            + "  <byte>5</byte>\n"
            + "</byte-array>";

        final byte[] expected = {0, 120, -124, 22, 33, 0, 5};
        assertByteArrayEquals(expected, (byte[])xstream.fromXML(input));
    }

    public void testUnmarshallsEmptyByteArrays() {
        final byte[] input = {};
        final String expected = "<byte-array></byte-array>";

        assertBothWays(input, expected);
    }

    public static class TestObject extends StandardObject {
        private static final long serialVersionUID = 200504L;
         byte[] data;
         boolean something;
    }

    public void testUnmarshallsEmptyByteArrayAsFieldOfAnotherObject() {
        // exposes a weird bug that was in the XML pull readers.
        final TestObject in = new TestObject();
        in.data = new byte[0];

        xstream.alias("TestObject", TestObject.class);
        final String expectedXml = ""
            + "<TestObject>\n"
            + "  <data></data>\n"
            + "  <something>false</something>\n"
            + "</TestObject>";
        assertBothWays(in, expectedXml);
    }

}
