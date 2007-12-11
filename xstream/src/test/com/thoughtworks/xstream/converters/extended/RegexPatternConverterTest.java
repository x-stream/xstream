/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 28. July 2006 by Guilerme Silveira
 */
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
