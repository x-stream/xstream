package com.thoughtworks.acceptance;

public class EncodingTest extends AbstractAcceptanceTest {

    public void testCanDealWithUtfText() {
        assertBothWays("J\u00F6rg", "<string>J\u00F6rg</string>");
    }

    public void testCanDealWithNullCharactersInText() {
        assertBothWays("X\0Y", "<string>X&#x0;Y</string>");
    }

    public void testEscapesXmlUnfriendlyChars() {
        assertBothWays("<", "<string>&lt;</string>");
        assertBothWays(">", "<string>&gt;</string>");
        assertBothWays("<>", "<string>&lt;&gt;</string>");
        assertBothWays("<=", "<string>&lt;=</string>");
        assertBothWays(">=", "<string>&gt;=</string>");
        assertBothWays("&", "<string>&amp;</string>");
        assertBothWays("'", "<string>&apos;</string>");
        assertBothWays("\"", "<string>&quot;</string>");
    }

}
