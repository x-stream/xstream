/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 06. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;

public class CharArrayConverterTest extends AbstractAcceptanceTest {

    public void testMarshallsCharArrayAsSingleString() {
        char[] input = new char[] {'h','e','l','l','o'};

        String expected = "<char-array>hello</char-array>";
        assertBothWays(input, expected);
    }
}
