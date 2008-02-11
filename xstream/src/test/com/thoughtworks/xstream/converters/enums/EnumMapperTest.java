/*
 * Copyright (C) 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 12. February 2008 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.enums;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;


// ***** READ THIS *****
// This class will only compile with JDK 1.5.0 or above as it test Java enums.
// If you are using an earlier version of Java, just don't try to build this class. XStream
// should work fine without it.

/**
 * @author J&ouml;rg Schaible
 */
public class EnumMapperTest extends TestCase {

    private XStream xstream;

    protected void setUp() throws Exception {
        super.setUp();
        xstream = new XStream();
        xstream.alias("simple", SimpleEnum.class);
        xstream.alias("polymorphic", PolymorphicEnum.class);
    }

    static class Bowl {
        Fruit fruit;
    }

    public void testSupportsDefaultImplementationToBeAnEnum() {
        xstream.alias("bowl", Bowl.class);
        xstream.addDefaultImplementation(PolymorphicEnum.class, Fruit.class);
        String expectedXml = "" // force format
            + "<bowl>\n"
            + "  <fruit>B</fruit>\n"
            + "</bowl>";
        Bowl in = new Bowl();
        in.fruit = PolymorphicEnum.B;
        assertEquals(expectedXml, xstream.toXML(in));
        Bowl out = (Bowl)xstream.fromXML(expectedXml);
        assertSame(out.fruit, PolymorphicEnum.B);
    }

    static class TypeWithEnums {
        PolymorphicEnum fruit;
        @XStreamAsAttribute        
        SimpleEnum simple;
    }

    public void testSupportsEnumAsAttribute() {
        xstream.alias("bowl", TypeWithEnums.class);
        xstream.useAttributeFor(PolymorphicEnum.class);
        String expectedXml = "<bowl fruit=\"B\" simple=\"GREEN\"/>";
        TypeWithEnums in = new TypeWithEnums();
        in.fruit = PolymorphicEnum.B;
        in.simple = SimpleEnum.GREEN;
        assertEquals(expectedXml, xstream.toXML(in));
        TypeWithEnums out = (TypeWithEnums)xstream.fromXML(expectedXml);
        assertSame(out.fruit, PolymorphicEnum.B);
    }

    public void testEnumsAreImmutable() {
        List in = new ArrayList();
        in.add(SimpleEnum.GREEN);
        in.add(SimpleEnum.GREEN);
        in.add(PolymorphicEnum.A);
        in.add(PolymorphicEnum.A);
        String expectedXml = ""
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
