package com.thoughtworks.xstream.converters.extended.base64;

import junit.framework.TestCase;

public class Base64EncoderTest extends TestCase {

    private Base64Encoder encoder = new Base64Encoder();

    public void testConvertsBase64NumberToAsciiCharacter() {
        assertEquals('A', encoder.charOfBase64Number(0));
        assertEquals('E', encoder.charOfBase64Number(4));
        assertEquals('Z', encoder.charOfBase64Number(25));
        assertEquals('a', encoder.charOfBase64Number(26));
        assertEquals('e', encoder.charOfBase64Number(30));
        assertEquals('z', encoder.charOfBase64Number(51));
        assertEquals('0', encoder.charOfBase64Number(52));
        assertEquals('3', encoder.charOfBase64Number(55));
        assertEquals('9', encoder.charOfBase64Number(61));
        assertEquals('+', encoder.charOfBase64Number(62));
        assertEquals('/', encoder.charOfBase64Number(63));
    }

    public void testConverts3BytesOf8BitsTo4CharsOf6Bits() {
        byte[] in = new byte[] { (byte)155, (byte)162, (byte)233 };
        char[] result = encoder.tripleByteToQuadChar(in);
        assertEquals("m6Lp", new String(result));
    }

}
