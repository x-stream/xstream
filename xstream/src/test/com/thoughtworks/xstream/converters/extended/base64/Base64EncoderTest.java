package com.thoughtworks.xstream.converters.extended.base64;

import junit.framework.TestCase;

public class Base64EncoderTest extends TestCase {

    private Base64Encoder encoder = new Base64Encoder();

    public void testEncodesEntireByteArrayAsString() {
        byte[] input = "hello world".getBytes();
        String expected = "aGVsbG8gd29ybGQ="; // according to other base64 encoders
        assertEquals(expected, encoder.encode(input));
    }

//    public void testConverts3BytesOf8BitsTo4CharsOf6Bits() {
//        assertEquals("m6Lp", encoder.encode(new byte[] {-101, -94, -23}));
//    }
//
//    public void testPadsMissingBytesAtEnd() {
//        assertEquals("m6I=", encoder.encode(new byte[] {b(155), b(162)}));
//        assertEquals("mw==", encoder.encode(new byte[] {b(155)}));
//    }

}
