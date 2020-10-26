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

package com.thoughtworks.xstream.converters.enums;

import java.util.EnumMap;

import com.thoughtworks.xstream.XStream;

import junit.framework.TestCase;


public class EnumMapConverterTest extends TestCase {

    private XStream xstream;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        xstream = new XStream();
    }

    public void testIncludesEnumTypeInSerializedForm() {
        xstream.alias("simple", SimpleEnum.class);
        final EnumMap<SimpleEnum, String> map = new EnumMap<SimpleEnum, String>(SimpleEnum.class);
        map.put(SimpleEnum.BLUE, "sky");
        map.put(SimpleEnum.GREEN, "grass");

        final String expectedXml = ""
            + "<enum-map enum-type=\"simple\">\n"
            + "  <entry>\n"
            + "    <simple>GREEN</simple>\n"
            + "    <string>grass</string>\n"
            + "  </entry>\n"
            + "  <entry>\n"
            + "    <simple>BLUE</simple>\n"
            + "    <string>sky</string>\n"
            + "  </entry>\n"
            + "</enum-map>";

        assertEquals(expectedXml, xstream.toXML(map));
        assertEquals(map, xstream.fromXML(expectedXml));
    }

}
