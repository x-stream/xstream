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

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import junit.framework.TestCase;


/**
 * @author J&ouml;rg Schaible
 */
public class EnumMapperTest extends TestCase {

    private XStream xstream;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        xstream = new XStream();
        xstream.alias("simple", SimpleEnum.class);
        xstream.alias("polymorphic", PolymorphicEnum.class);
        xstream.allowTypesByWildcard(getClass().getName() + "$*");
    }

    static class Bowl {
        Fruit fruit;
    }

    public void testSupportsDefaultImplementationToBeAnEnum() {
        xstream.alias("bowl", Bowl.class);
        xstream.addDefaultImplementation(PolymorphicEnum.class, Fruit.class);
        final String expectedXml = "" // force format
            + "<bowl>\n"
            + "  <fruit>B</fruit>\n"
            + "</bowl>";
        final Bowl in = new Bowl();
        in.fruit = PolymorphicEnum.B;
        assertEquals(expectedXml, xstream.toXML(in));
        final Bowl out = xstream.<Bowl>fromXML(expectedXml);
        assertSame(out.fruit, PolymorphicEnum.B);
    }

    static class TypeWithEnums {
        PolymorphicEnum poly;
        @XStreamAsAttribute
        SimpleEnum simple;
        BigEnum big;
    }

    public void testSupportsEnumAsAttribute() {
        xstream.alias("type", TypeWithEnums.class);
        xstream.useAttributeFor(PolymorphicEnum.class);
        xstream.autodetectAnnotations(true);
        final String expectedXml = "" // force format
            + "<type poly=\"B\" simple=\"GREEN\">\n"
            + "  <big>C3</big>\n"
            + "</type>";
        final TypeWithEnums in = new TypeWithEnums();
        in.poly = PolymorphicEnum.B;
        in.simple = SimpleEnum.GREEN;
        in.big = BigEnum.C3;
        assertEquals(expectedXml, xstream.toXML(in));
        final TypeWithEnums out = xstream.<TypeWithEnums>fromXML(expectedXml);
        assertSame(out.poly, PolymorphicEnum.B);
        assertSame(out.simple, SimpleEnum.GREEN);
    }

    public void testEnumsAreImmutable() {
        final List<Enum<?>> in = new ArrayList<Enum<?>>();
        in.add(SimpleEnum.GREEN);
        in.add(SimpleEnum.GREEN);
        in.add(PolymorphicEnum.A);
        in.add(PolymorphicEnum.A);
        final String expectedXml = ""
            + "<list>\n"
            + "  <simple>GREEN</simple>\n"
            + "  <simple>GREEN</simple>\n"
            + "  <polymorphic>A</polymorphic>\n"
            + "  <polymorphic>A</polymorphic>\n"
            + "</list>";
        assertEquals(expectedXml, xstream.toXML(in));
        assertEquals(in, xstream.fromXML(expectedXml));
    }
}
