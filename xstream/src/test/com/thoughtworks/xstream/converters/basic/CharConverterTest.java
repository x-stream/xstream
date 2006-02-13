package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;

public class CharConverterTest extends AbstractAcceptanceTest {

    public void testIndicatesNullChar() {
        assertBothWays(new Character('x'), "<char>x</char>");
        assertBothWays(new Character('\0'), "<char null=\"true\"/>");
    }

}
