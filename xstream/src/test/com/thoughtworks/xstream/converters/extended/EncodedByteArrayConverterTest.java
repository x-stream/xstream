package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;

public class EncodedByteArrayConverterTest extends AbstractAcceptanceTest {

    public void testMarshallsCharArrayAsSingleString() {

        byte[] input = new byte[] {0, 120, -124, 22, 33, 0, 5};
        String expected = "<byte-array>AHiEFiEABQ==</byte-array>";

        xstream.registerConverter(new EncodedByteArrayConverter());

        assertBothWays(input, expected);
    }
}
