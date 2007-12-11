/*
 * Copyright (C) 2003, 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 26. September 2003 by Joe Walnes
 */
package com.thoughtworks.acceptance;

import java.math.BigDecimal;
import java.math.BigInteger;

public class BasicTypesTest extends AbstractAcceptanceTest {

    public void testPrimitiveNumbers() {
        assertBothWays(new Integer(99), "<int>99</int>");
        assertBothWays(new Integer(-99), "<int>-99</int>");
        assertBothWays(new Integer(0), "<int>0</int>");
        assertBothWays(new Float(-123.45f), "<float>-123.45</float>");
        assertBothWays(new Double(-1234567890.12345), "<double>-1.23456789012345E9</double>");
        assertBothWays(new Long(123456789123456L), "<long>123456789123456</long>");
        assertBothWays(new Short((short) 123), "<short>123</short>");
    }

    public void testDifferentBaseIntegers() {
        assertEquals(new Integer(255), xstream.fromXML("<int>0xFF</int>"));
        assertEquals(new Integer(8), xstream.fromXML("<int>010</int>"));
    }

    public void testNegativeIntegersInHex() {
        assertEquals(new Byte((byte)-1), xstream.fromXML("<byte>0xFF</byte>"));
        assertEquals(new Short((short)-1), xstream.fromXML("<short>0xFFFF</short>"));
        assertEquals(new Integer(-1), xstream.fromXML("<int>0xFFFFFFFF</int>"));
        assertEquals(new Long(Long.MAX_VALUE), xstream.fromXML("<long>0x7FFFFFFFFFFFFFFF</long>"));
    }

    public void testNegativeIntegersInOctal() {
        assertEquals(new Byte((byte)-1), xstream.fromXML("<byte>0377</byte>"));
        assertEquals(new Short((short)-1), xstream.fromXML("<short>0177777</short>"));
        assertEquals(new Integer(-1), xstream.fromXML("<int>037777777777</int>"));
        assertEquals(new Long(Long.MAX_VALUE), xstream.fromXML("<long>0777777777777777777777</long>"));
    }

    public void testOtherPrimitives() {
        assertBothWays(new Character('z'), "<char>z</char>");
        assertBothWays(Boolean.TRUE, "<boolean>true</boolean>");
        assertBothWays(Boolean.FALSE, "<boolean>false</boolean>");
        assertBothWays(new Byte((byte) 44), "<byte>44</byte>");
    }

    public void testNullCharacter() {
        assertEquals(new Character('\0'), xstream.fromXML("<char null=\"true\"/>")); // pre XStream 1.3 
        assertBothWays(new Character('\0'), "<char></char>");
    }

    public void testNonUnicodeCharacter() {
        assertBothWays(new Character('\uffff'), "<char>&#xffff;</char>");
    }

    public void testStrings() {
        assertBothWays("hello world", "<string>hello world</string>");
    }

    public void testStringsWithISOControlCharacter() {
        assertBothWays("hello\u0004world", "<string>hello&#x4;world</string>");
        assertBothWays("hello\u0096world", "<string>hello&#x96;world</string>");
    }

    public void testStringBuffer() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("woo");
        String xml = xstream.toXML(buffer);
        assertEquals(xml, "<string-buffer>woo</string-buffer>");
        StringBuffer out = (StringBuffer) xstream.fromXML(xml);
        assertEquals("woo", out.toString());
    }

    public void testBigInteger() {
        BigInteger bigInteger = new BigInteger("1234567890123456");
        assertBothWays(bigInteger, "<big-int>1234567890123456</big-int>");
    }

    public void testBigDecimal() {
        BigDecimal bigDecimal = new BigDecimal("1234567890123456.987654321");
        assertBothWays(bigDecimal, "<big-decimal>1234567890123456.987654321</big-decimal>");
    }
    
    public void testNull() {
        assertBothWays(null, "<null/>");
    }

    public void testNumberFormats() {
        assertEquals(1.0, ((Double)xstream.fromXML("<double>1</double>")).doubleValue(), 0.001);
        assertEquals(1.0f, ((Float)xstream.fromXML("<float>1</float>")).floatValue(), 0.001);
    }
}
