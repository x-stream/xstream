/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2017, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 23. February 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.collections;

import java.util.Properties;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;


public class PropertiesConverterTest extends AbstractAcceptanceTest {

    public void testConvertsPropertiesObjectToShortKeyValueElements() {
        final Properties properties = new Properties();
        properties.setProperty("hello", "world");
        properties.setProperty("foo", "cheese");

        final String expected = ""
            + "<properties>\n"
            + "  <property name=\"hello\" value=\"world\"/>\n"
            + "  <property name=\"foo\" value=\"cheese\"/>\n"
            + "</properties>";
        assertBothWaysNormalized(properties, expected, "properties", "property", "@name");
    }

    public void testIncludesDefaultProperties() {
        final Properties defaults = new Properties();
        defaults.setProperty("host", "localhost");
        defaults.setProperty("port", "80");

        final Properties override = new Properties(defaults);
        override.setProperty("port", "999");

        // sanity check
        assertEquals("Unexpected overriden property", "999", override.getProperty("port"));
        assertEquals("Unexpected default property", "localhost", override.getProperty("host"));

        final String expected = ""
            + "<properties>\n"
            + "  <property name=\"port\" value=\"999\"/>\n"
            + "  <defaults>\n"
            + "    <property name=\"port\" value=\"80\"/>\n"
            + "    <property name=\"host\" value=\"localhost\"/>\n"
            + "  </defaults>\n"
            + "</properties>";

        final Properties out = this.<Properties>assertBothWays(override, expected);
        assertEquals("Unexpected overriden property", "999", out.getProperty("port"));
        assertEquals("Unexpected default property", "localhost", out.getProperty("host"));
        assertEquals(override, out);
    }

    public void testCanSortElements() {
        final Properties defaults = new Properties();
        defaults.setProperty("host", "localhost");
        defaults.setProperty("port", "80");

        final Properties override = new Properties(defaults);
        override.setProperty("port", "999");
        override.setProperty("domain", "codehaus.org");

        final String expected = ""
            + "<properties>\n"
            + "  <property name=\"domain\" value=\"codehaus.org\"/>\n"
            + "  <property name=\"port\" value=\"999\"/>\n"
            + "  <defaults>\n"
            + "    <property name=\"host\" value=\"localhost\"/>\n"
            + "    <property name=\"port\" value=\"80\"/>\n"
            + "  </defaults>\n"
            + "</properties>";

        xstream.registerConverter(new PropertiesConverter(true));
        final Properties out = this.<Properties>assertBothWays(override, expected);
        assertEquals("Unexpected overriden property", "999", out.getProperty("port"));
        assertEquals("Unexpected default property", "localhost", out.getProperty("host"));
        assertEquals(override, out);
    }
}
