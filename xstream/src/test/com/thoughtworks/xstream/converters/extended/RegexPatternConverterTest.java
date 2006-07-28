package com.thoughtworks.xstream.converters.extended;

import java.util.regex.Pattern;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;

public class RegexPatternConverterTest extends AbstractAcceptanceTest {

	public void testHandlesSimplePattern() {
		Pattern root = Pattern.compile(".*");
		String xml = "<java.util.regex.Pattern>\n"
				+ "  <pattern>.*</pattern>\n" + "  <flags>0</flags>\n"
				+ "</java.util.regex.Pattern>";
		assertBothWays(root, xml);
	}

}
