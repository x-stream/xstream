package com.thoughtworks.xstream.converters.basic;

import junit.framework.TestCase;

import java.util.Date;
import java.net.URL;
import java.net.MalformedURLException;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;

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
