package com.thoughtworks.xstream.converters.extended;

import java.nio.charset.Charset;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;

public class CharsetConverterTest extends AbstractAcceptanceTest {

	public void testHandlesSimpleCharset() {
		assertBothWays(Charset.forName("US-ASCII"), "<charset>US-ASCII</charset>");
	}

}
