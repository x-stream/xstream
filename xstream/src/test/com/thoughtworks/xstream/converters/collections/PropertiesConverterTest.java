/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
