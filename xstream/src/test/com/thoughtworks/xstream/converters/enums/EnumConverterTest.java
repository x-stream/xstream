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

import com.thoughtworks.xstream.XStream;

import junit.framework.TestCase;


/**
 * @author Joe Walnes
 * @author Bryan Coleman
 */
public class EnumConverterTest extends TestCase {

    private XStream xstream;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        xstream = new XStream();
        xstream.alias("simple", SimpleEnum.class);
        xstream.alias("polymorphic", PolymorphicEnum.class);
    }

    public void testRepresentsEnumAsSingleStringValue() {
        final String expectedXml = "<simple>GREEN</simple>";
        final SimpleEnum in = SimpleEnum.GREEN;
        assertEquals(expectedXml, xstream.toXML(in));
        assertEquals(in, xstream.fromXML(expectedXml));
    }

    public void testRepresentsPolymorphicEnumAsSingleStringValue() {
        final String expectedXml = "<polymorphic>B</polymorphic>";
        final PolymorphicEnum in = PolymorphicEnum.B;
        assertEquals(expectedXml, xstream.toXML(in));
        assertEquals(in, xstream.fromXML(expectedXml));
    }

    public void testDeserializedEnumIsTheSameNotJustEqual() {
        assertSame(SimpleEnum.GREEN, xstream.fromXML(xstream.toXML(SimpleEnum.GREEN)));
        assertSame(PolymorphicEnum.B, xstream.fromXML(xstream.toXML(PolymorphicEnum.B)));
    }

    public void testResolvesSpecializedPolymorphicEnum() {
        PolymorphicEnum in;
        PolymorphicEnum out;

        in = PolymorphicEnum.A;
        out = xstream.<PolymorphicEnum>fromXML(xstream.toXML(in));
        assertEquals("apple", ((Fruit)out).fruit()); // see Bug ID: 6522780

        in = PolymorphicEnum.B;
        out = xstream.<PolymorphicEnum>fromXML(xstream.toXML(in));
        assertEquals("banana", ((Fruit)out).fruit()); // see Bug ID: 6522780
    }

}
