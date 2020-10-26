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

import java.util.TreeMap;
import java.util.TreeSet;


/**
 * Test XStream 1.3 compatibility.
 *
 * @author J&ouml;rg Schaible
 */
public class XStream13CompatibilityTest extends AbstractAcceptanceTest {

    public void testCanReadOldTreeSet() {
        final String in = ""
            + "<tree-set>\n"
            + "  <no-comparator/>\n"
            + "  <string>one</string>\n"
            + "  <string>two</string>\n"
            + "</tree-set>";
        final TreeSet<String> expected = new TreeSet<>();
        expected.add("two");
        expected.add("one");
        assertEquals(expected, xstream.fromXML(in));
    }

    public void testCanReadOldTreeMap() {
        final String in = ""
            + "<tree-map>\n"
            + "  <no-comparator/>\n"
            + "  <entry>\n"
            + "    <string>one</string>\n"
            + "    <int>1</int>\n"
            + "  </entry>\n"
            + "  <entry>\n"
            + "    <string>two</string>\n"
            + "    <int>2</int>\n"
            + "  </entry>\n"
            + "</tree-map>";
        final TreeMap<String, Integer> expected = new TreeMap<>();
        expected.put("two", new Integer(2));
        expected.put("one", new Integer(1));
        assertEquals(expected, xstream.fromXML(in));
    }
}
