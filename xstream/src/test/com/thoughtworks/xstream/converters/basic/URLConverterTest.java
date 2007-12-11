/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 25. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;

import java.net.MalformedURLException;
import java.net.URL;

public class URLConverterTest extends AbstractAcceptanceTest {

    public void testConvertsToSingleString() throws MalformedURLException {

        assertBothWays(
                new URL("http://www.apple.com:2020/path/blah.html?abc#2"),
                "<url>http://www.apple.com:2020/path/blah.html?abc#2</url>");

        assertBothWays(
                new URL("file:/c:/winnt/blah.txt"),
                "<url>file:/c:/winnt/blah.txt</url>");
    }

}
