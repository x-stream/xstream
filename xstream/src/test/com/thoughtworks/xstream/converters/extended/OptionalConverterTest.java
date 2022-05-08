package com.thoughtworks.xstream.converters.extended;

import java.util.Optional;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;

public class OptionalConverterTest extends AbstractAcceptanceTest {

	public void testEmpty() {
        String expected =
                "<optional>\n" +
                "  <null/>\n" +
                "</optional>";

		assertBothWays(Optional.empty(), expected);
	}

    public void testWithStringValue() {
        String expected =
        "<optional>\n" +
        "  <string>hi</string>\n" +
        "</optional>";

	
		assertBothWays(Optional.of("hi"), expected);
    }

    public void testWithIntValue() {
        String expected = 
        "<optional>\n" +
        "  <int>1</int>\n" +
        "</optional>";

	
		assertBothWays(Optional.of(1), expected);
    }
}
