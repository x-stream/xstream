package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;
import junit.framework.TestCase;

import java.util.Properties;

public class PropertiesConverterTest extends TestCase {

    public void testConvertsPropertiesObjectToShortKeyValueElements() {
        Properties in = new Properties();
        in.setProperty("hello", "world");
        in.setProperty("foo", "cheese");

        XStream xStream = new XStream(new XppDriver());

        String expectedXML = "" +
                "<properties>\n" +
                "  <property name=\"hello\" value=\"world\"/>\n" +
                "  <property name=\"foo\" value=\"cheese\"/>\n" +
                "</properties>";
        String actualXML = xStream.toXML(in);
        assertEquals(expectedXML, actualXML);

        Properties expectedOut = new Properties();
        expectedOut.setProperty("hello", "world");
        expectedOut.setProperty("foo", "cheese");
        Properties actualOut = (Properties) xStream.fromXML(actualXML);
        assertEquals(in, actualOut);
        assertEquals(in.toString(), actualOut.toString());

    }

}
