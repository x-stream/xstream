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

import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.XStream;

import junit.framework.TestCase;


/**
 * @author J&ouml;rg Schaible
 */
public class EnumToStringConverterTest extends TestCase {

    private XStream xstream;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        xstream = new XStream();
        xstream.alias("simple", SimpleEnum.class);
        xstream.alias("big", BigEnum.class);
        xstream.alias("polymorphic", PolymorphicEnum.class);

        final Map<String, SimpleEnum> map = new HashMap<String, SimpleEnum>();
        map.put("0xff0000", SimpleEnum.RED);
        map.put("0x00ff00", SimpleEnum.GREEN);
        map.put("0x0000ff", SimpleEnum.BLUE);
        xstream.registerConverter(new EnumToStringConverter<SimpleEnum>(SimpleEnum.class, map));
        xstream.registerConverter(new EnumToStringConverter<BigEnum>(BigEnum.class));
        xstream.registerConverter(new EnumToStringConverter<PolymorphicEnum>(PolymorphicEnum.class));
    }

    public void testMapsEnumToProvidedStringValue() {
        final String expectedXml = "<simple>0x00ff00</simple>";
        final SimpleEnum in = SimpleEnum.GREEN;
        assertEquals(expectedXml, xstream.toXML(in));
        assertEquals(in, xstream.fromXML(expectedXml));
    }

    public void testMapsEnumToStringDefaultValue() {
        final String expectedXml = "<big>C3</big>";
        final BigEnum in = BigEnum.C3;
        assertEquals(expectedXml, xstream.toXML(in));
        assertEquals(in, xstream.fromXML(expectedXml));
    }

    public void testMapsToPolymorphicStringValue() {
        final String expectedXml = "<polymorphic>banana</polymorphic>";
        final PolymorphicEnum in = PolymorphicEnum.B;
        assertEquals(expectedXml, xstream.toXML(in));
        assertEquals(in, xstream.fromXML(expectedXml));
    }

}
