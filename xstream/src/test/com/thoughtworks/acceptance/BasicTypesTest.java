package com.thoughtworks.acceptance;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

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

    public void testOtherPrimitives() {
        assertBothWays(new Character('z'), "<char>z</char>");
        assertBothWays(Boolean.TRUE, "<boolean>true</boolean>");
        assertBothWays(Boolean.FALSE, "<boolean>false</boolean>");
        assertBothWays(new Byte((byte) 44), "<byte>44</byte>");
    }

    public void testStrings() {
        assertBothWays("hello world", "<string>hello world</string>");
    }

    public void testStringBuffer() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("woo");
        String xml = xstream.toXML(buffer);
        assertEquals(xml, "<string-buffer>woo</string-buffer>");
        StringBuffer out = (StringBuffer) xstream.fromXML(xml);
        assertEquals("woo", out.toString());
    }

    public void testDate() {
        Date date = new Date(103, 02, 15, 8, 22, 7);
        assertBothWays(date, "<date>2003-03-15 08:22:07.0 GMT</date>");
    }

    public void testBigInteger() {
        BigInteger bigInteger = new BigInteger("1234567890123456");
        assertBothWays(bigInteger, "<big-int>1234567890123456</big-int>");
    }

    public void testBigDecimal() {
        BigDecimal bigDecimal = new BigDecimal("1234567890123456.987654321");
        assertBothWays(bigDecimal, "<big-decimal>1234567890123456.987654321</big-decimal>");
    }

}
