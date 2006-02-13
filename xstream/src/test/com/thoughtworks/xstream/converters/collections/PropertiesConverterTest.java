package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.XStream;

import junit.framework.TestCase;

import java.util.Properties;

public class PropertiesConverterTest extends TestCase {

    public void testConvertsPropertiesObjectToShortKeyValueElements() {
        Properties in = new Properties();
        in.setProperty("hello", "world");
        in.setProperty("foo", "cheese");

        String expectedXML = "" +
                "<properties>\n" +
                "  <property name=\"hello\" value=\"world\"/>\n" +
                "  <property name=\"foo\" value=\"cheese\"/>\n" +
                "</properties>";
        XStream xstream = new XStream();
        String actualXML = xstream.toXML(in);
        assertEquals(expectedXML, actualXML);

        Properties expectedOut = new Properties();
        expectedOut.setProperty("hello", "world");
        expectedOut.setProperty("foo", "cheese");
        Properties actualOut = (Properties) xstream.fromXML(actualXML);
        assertEquals(in, actualOut);
        assertEquals(in.toString(), actualOut.toString());

    }

    public void testIncludesDefaultProperties() {
        Properties defaults = new Properties();
        defaults.setProperty("host", "localhost");
        defaults.setProperty("port", "80");

        Properties override = new Properties(defaults);
        override.setProperty("port", "999");

        // sanity check
        assertEquals("Unexpected overriden property", "999", override.getProperty("port"));
        assertEquals("Unexpected default property", "localhost", override.getProperty("host"));

        String expectedXML = "" +
                "<properties>\n" +
                "  <property name=\"port\" value=\"999\"/>\n" +
                "  <defaults>\n" +
                "    <property name=\"port\" value=\"80\"/>\n" +
                "    <property name=\"host\" value=\"localhost\"/>\n" +
                "  </defaults>\n" +
                "</properties>";

        XStream xstream = new XStream();
        String actualXML = xstream.toXML(override);
        assertEquals(expectedXML, actualXML);

        Properties out = (Properties) xstream.fromXML(actualXML);
        assertEquals("Unexpected overriden property", "999", out.getProperty("port"));
        assertEquals("Unexpected default property", "localhost", out.getProperty("host"));
        assertEquals(override, out);
    }

}
