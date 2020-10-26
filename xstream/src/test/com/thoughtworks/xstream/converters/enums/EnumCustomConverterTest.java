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
import com.thoughtworks.xstream.converters.enums.EnumMapperTest.TypeWithEnums;

import junit.framework.TestCase;


/**
 * @author J&ouml;rg Schaible
 */
public class EnumCustomConverterTest extends TestCase {

    private XStream xstream;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        xstream = new XStream();
        xstream.allowTypes(TypeWithEnums.class);
        xstream.allowTypesByWildcard(this.getClass().getName() + "$*");
        xstream.alias("simple", SimpleEnum.class);
        xstream.alias("polymorphic", PolymorphicEnum.class);
    }

    public void testCanBeUsedDirectly() {
        xstream.registerConverter(new PolymorphicEnumConverter<PolymorphicEnum>(PolymorphicEnum.class));
        final String expectedXml = "<polymorphic>b</polymorphic>";
        final PolymorphicEnum in = PolymorphicEnum.B;
        assertEquals(expectedXml, xstream.toXML(in));
        assertEquals(in, xstream.fromXML(expectedXml));
    }

    public void testCanBeUsedForMember() {
        xstream.registerConverter(new PolymorphicEnumConverter<PolymorphicEnum>(PolymorphicEnum.class));
        xstream.alias("type", TypeWithEnums.class);
        xstream.autodetectAnnotations(true);
        final String expectedXml = "" // force format
            + "<type simple=\"GREEN\">\n"
            + "  <poly>b</poly>\n"
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

    public void testCanBeUsedForAttribute() {
        xstream.registerConverter(new PolymorphicEnumConverter<PolymorphicEnum>(PolymorphicEnum.class));
        xstream.alias("type", TypeWithEnums.class);
        xstream.useAttributeFor(PolymorphicEnum.class);
        xstream.autodetectAnnotations(true);
        final String expectedXml = "" // force format
            + "<type poly=\"b\" simple=\"GREEN\">\n"
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

    public void testCanBeUsedLocallyForAttribute() {
        xstream.registerLocalConverter(TypeWithEnums.class, "poly", new PolymorphicEnumConverter<PolymorphicEnum>(
            PolymorphicEnum.class));
        xstream.alias("type", TypeWithEnums.class);
        xstream.useAttributeFor(PolymorphicEnum.class);
        xstream.autodetectAnnotations(true);
        final String expectedXml = "" // force format
            + "<type poly=\"b\" simple=\"GREEN\">\n"
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

    private final static class PolymorphicEnumConverter<T extends Enum<T>> extends EnumSingleValueConverter<T> {
        private PolymorphicEnumConverter(final Class<T> type) {
            super(type);
        }

        @Override
        public Object fromString(final String str) {
            return super.fromString(str.toUpperCase());
        }

        @Override
        public String toString(final Object obj) {
            return super.toString(obj).toLowerCase();
        }
    }
}
