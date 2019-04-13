/*
 * Copyright (C) 2003, 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2013, 2018, 2019 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 26. September 2003 by Joe Walnes, merged with Basic15TypesTest
 */
package com.thoughtworks.acceptance;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;


public class BasicTypesTest extends AbstractAcceptanceTest {

    public void testPrimitiveNumbers() {
        assertBothWays(99, "<int>99</int>");
        assertBothWays(-99, "<int>-99</int>");
        assertBothWays(0, "<int>0</int>");
        assertBothWays(-123.45f, "<float>-123.45</float>");
        assertBothWays(-1234567890.12345, "<double>-1.23456789012345E9</double>");
        assertBothWays(123456789123456L, "<long>123456789123456</long>");
        assertBothWays((short)123, "<short>123</short>");
    }

    public void testDifferentBaseIntegers() {
        assertEquals(new Integer(255), xstream.fromXML("<int>0xFF</int>"));
        assertEquals(new Integer(255), xstream.fromXML("<int>#FF</int>"));
        assertEquals(new Integer(8), xstream.fromXML("<int>010</int>"));
        assertEquals(new Long(01777777773427777777773L), xstream.fromXML("<long>01777777773427777777773</long>"));
    }

    public void testNegativeIntegersInHex() {
        assertEquals(new Byte((byte)-1), xstream.fromXML("<byte>0xFF</byte>"));
        assertEquals(new Short((short)-1), xstream.fromXML("<short>0xFFFF</short>"));
        assertEquals(new Integer(-1), xstream.fromXML("<int>0xFFFFFFFF</int>"));
        assertEquals(new Long(-1), xstream.fromXML("<long>0xFFFFFFFFFFFFFFFF</long>"));
    }

    public void testNegativeIntegersInOctal() {
        assertEquals(new Byte((byte)-1), xstream.fromXML("<byte>0377</byte>"));
        assertEquals(new Short((short)-1), xstream.fromXML("<short>0177777</short>"));
        assertEquals(new Integer(-1), xstream.fromXML("<int>037777777777</int>"));
        assertEquals(new Long(-1), xstream.fromXML("<long>01777777777777777777777</long>"));
    }

    public void testOtherPrimitives() {
        assertBothWays('z', "<char>z</char>");
        assertBothWays(Boolean.TRUE, "<boolean>true</boolean>");
        assertBothWays(Boolean.FALSE, "<boolean>false</boolean>");
        assertBothWays((byte)44, "<byte>44</byte>");
    }

    public void testNullCharacter() {
        assertEquals(new Character('\0'), xstream.fromXML("<char null=\"true\"/>")); // pre XStream 1.3
        assertBothWays('\0', "<char></char>");
    }

    public void testStrings() {
        assertBothWays("hello world", "<string>hello world</string>");
        assertBothWays("-0770", "<string>-0770</string>");
    }

    public void testStringBuffer() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("woo");
        final String xml = xstream.toXML(buffer);
        assertEquals(xml, "<string-buffer>woo</string-buffer>");
        final StringBuffer out = xstream.fromXML(xml);
        assertEquals("woo", out.toString());
    }

    public void testBigInteger() {
        final BigInteger bigInteger = new BigInteger("1234567890123456");
        assertBothWays(bigInteger, "<big-int>1234567890123456</big-int>");
    }

    public void testBigDecimal() {
        final BigDecimal bigDecimal = new BigDecimal("1234567890123456.987654321");
        assertBothWays(bigDecimal, "<big-decimal>1234567890123456.987654321</big-decimal>");
    }

    public void testNull() {
        assertBothWays(null, "<null/>");
    }

    public void testNumberFormats() {
        assertEquals(1.0, xstream.<Double>fromXML("<double>1</double>"), 0.001);
        assertEquals(1.0f, xstream.<Float>fromXML("<float>1</float>"), 0.001);
    }

    public void testUUID() {
        final UUID uuid = UUID.randomUUID();
        assertBothWays(uuid, "<uuid>" + uuid + "</uuid>");
    }

    public void testStringBuilder() {
        final StringBuilder builder = new StringBuilder();
        builder.append("woo");
        final String xml = xstream.toXML(builder);
        assertEquals(xml, "<string-builder>woo</string-builder>");
        final StringBuilder out = xstream.fromXML(xml);
        assertEquals("woo", out.toString());
    }
}
