package com.thoughtworks.xstream.converters.enums;

import junit.framework.TestCase;

import java.util.EnumSet;
import java.util.EnumMap;
import java.beans.XMLEncoder;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

import com.thoughtworks.xstream.XStream;

public class EnumMapConverterTest extends TestCase {

    private XStream xstream;

    protected void setUp() throws Exception {
        super.setUp();
        xstream = new XStream();
    }

    public void testIncludesEnumTypeInSerializedForm() {
        xstream.alias("simple", SimpleEnum.class);
        EnumMap map = new EnumMap<SimpleEnum,String>(SimpleEnum.class);
        map.put(SimpleEnum.BLUE, "sky");
        map.put(SimpleEnum.GREEN, "grass");

        String expectedXml = "" +
                "<enum-map enum-type=\"simple\">\n" +
                "  <entry>\n" +
                "    <simple>GREEN</simple>\n" +
                "    <string>grass</string>\n" +
                "  </entry>\n" +
                "  <entry>\n" +
                "    <simple>BLUE</simple>\n" +
                "    <string>sky</string>\n" +
                "  </entry>\n" +
                "</enum-map>";

        assertEquals(expectedXml, xstream.toXML(map));
        assertEquals(map, xstream.fromXML(expectedXml));
    }

}
