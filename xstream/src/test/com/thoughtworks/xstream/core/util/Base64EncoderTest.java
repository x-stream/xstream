package com.thoughtworks.xstream.core.util;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;

public class Base64EncoderTest extends AbstractAcceptanceTest {

    private Base64Encoder encoder = new Base64Encoder();

    public void testEncodesEntireByteArrayAsString() {
        byte input[] = "hello world".getBytes();
        String expected = "aGVsbG8gd29ybGQ=";
        assertEquals(expected, encoder.encode(input));
        assertByteArrayEquals(input, encoder.decode(expected));
    }

    public void testWrapsLinesAt76Chars() {
        byte input[] = ("hello world. hello world. hello world. hello world. hello world. hello world. hello world. "
                + "hello world. hello world. hello world. hello world. hello world. hello world. hello world. ").getBytes();
        String expected = "aGVsbG8gd29ybGQuIGhlbGxvIHdvcmxkLiBoZWxsbyB3b3JsZC4gaGVsbG8gd29ybGQuIGhlbGxv\n"
                + "IHdvcmxkLiBoZWxsbyB3b3JsZC4gaGVsbG8gd29ybGQuIGhlbGxvIHdvcmxkLiBoZWxsbyB3b3Js\n"
                + "ZC4gaGVsbG8gd29ybGQuIGhlbGxvIHdvcmxkLiBoZWxsbyB3b3JsZC4gaGVsbG8gd29ybGQuIGhl\n"
                + "bGxvIHdvcmxkLiA=";
        assertEquals(expected, encoder.encode(input));
        assertByteArrayEquals(input, encoder.decode(expected));
    }

    public void testPadsSingleMissingByteWhenNotMultipleOfThree() {
        byte input[] = { 1, 2, 3, 4, 5 };
        String expected = "AQIDBAU=";
        assertEquals(expected, encoder.encode(input));
        assertByteArrayEquals(input, encoder.decode(expected));
    }

    public void testPadsDoubleMissingByteWhenNotMultipleOfThree() {
        byte input[] = { 1, 2, 3, 4  };
        String expected = "AQIDBA==";
        assertEquals(expected, encoder.encode(input));
        assertByteArrayEquals(input, encoder.decode(expected));
    }

    public void testDoesNotPadWhenMultipleOfThree() {
        byte input[] = { 1, 2, 3, 4, 5, 6 };
        String expected = "AQIDBAUG";
        assertEquals(expected, encoder.encode(input));
        assertByteArrayEquals(input, encoder.decode(expected));
    }

    public void testHandlesAllPositiveBytes() {
        byte input[] = new byte[127];
        for (int i = 0; i < 126; i++) input[i] = (byte) (i + 1);
        String expected = "AQIDBAUGBwgJCgsMDQ4PEBESExQVFhcYGRobHB0eHyAhIiMkJSYnKCkqKywtLi8wMTIzNDU2Nzg5\n"
                + "Ojs8PT4/QEFCQ0RFRkdISUpLTE1OT1BRUlNUVVZXWFlaW1xdXl9gYWJjZGVmZ2hpamtsbW5vcHFy\n"
                + "c3R1dnd4eXp7fH1+AA==";
        assertEquals(expected, encoder.encode(input));
        assertByteArrayEquals(input, encoder.decode(expected));
    }

    public void testHandlesAllNegativeBytes() {
        byte input[] = new byte[128];
        for (int i = 0; i < 127; i++) input[i] = (byte) (-1 - i);
        String expected = "//79/Pv6+fj39vX08/Lx8O/u7ezr6uno5+bl5OPi4eDf3t3c29rZ2NfW1dTT0tHQz87NzMvKycjH\n"
                + "xsXEw8LBwL++vby7urm4t7a1tLOysbCvrq2sq6qpqKempaSjoqGgn56dnJuamZiXlpWUk5KRkI+O\n"
                + "jYyLiomIh4aFhIOCgQA=";
        assertEquals(expected, encoder.encode(input));
        assertByteArrayEquals(input, encoder.decode(expected));
    }

    public void testHandlesZeroByte() {
        byte input[] = { 0, 0, 0, 0 };
        String expected = "AAAAAA==";
        assertEquals(expected, encoder.encode(input));
        assertByteArrayEquals(input, encoder.decode(expected));
    }

    public void testProducesEmptyStringWhenNoBytesGiven() {
        byte input[] = new byte[0];
        String expected = "";
        assertEquals(expected, encoder.encode(input));
        assertByteArrayEquals(input, encoder.decode(expected));
    }

}
