package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;

public class EncodedByteArrayConverterTest extends AbstractAcceptanceTest {

    public void testMarshallsCharArrayAsSingleString() {
        byte[] input =  {0, 120, -124, 22, 33, 0, 5};
        String expected = "<byte-array>AHiEFiEABQ==</byte-array>";

        assertBothWays(input, expected);
    }

    public void testUnmarshallsOldByteArraysThatHaveNotBeenEncoding() {
        // for backwards compatability
        String input = ""
                + "<byte-array>\n"
                + "  <byte>0</byte>\n"
                + "  <byte>120</byte>\n"
                + "  <byte>-124</byte>\n"
                + "  <byte>22</byte>\n"
                + "  <byte>33</byte>\n"
                + "  <byte>0</byte>\n"
                + "  <byte>5</byte>\n"
                + "</byte-array>";

        byte[] expected = {0, 120, -124, 22, 33, 0, 5};
        assertByteArrayEquals(expected, (byte[])xstream.fromXML(input));
    }
}
