package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;

public class CharArrayConverterTest extends AbstractAcceptanceTest {

    public void testMarshallsCharArrayAsSingleString() {
        char[] input = new char[] {'h','e','l','l','o'};

        String expected = "<char-array>hello</char-array>";
        assertBothWays(input, expected);
    }
}
