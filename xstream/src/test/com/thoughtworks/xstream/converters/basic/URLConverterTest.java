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
