package com.thoughtworks.acceptance;

public class EncodingTest extends AbstractAcceptanceTest {

    public void testCanDealWithUtfText() {
        String input = "Jšrg";

        String expected = "<string>Jšrg</string>";

        assertBothWays(input, expected);

    }
}
