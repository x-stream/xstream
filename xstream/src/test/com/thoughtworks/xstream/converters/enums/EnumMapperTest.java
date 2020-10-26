/*
 * Copyright (C) 2008, 2014, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 12. February 2008 by Joerg Schaible
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
