package com.thoughtworks.xstream.converters.enums;

import com.thoughtworks.xstream.XStream;
import junit.framework.TestCase;

// ***** READ THIS *****
// This class will only compile with JDK 1.5.0 or above as it test Java enums.
// If you are using an earlier version of Java, just don't try to build this class. XStream should work fine without it.
public class EnumConverterTest extends TestCase {

    enum Stuff {
        RED, GREEN, BLUE;
    }

    public void testRepresentsEnumAsSingleStringValue() {
        XStream xstream = new XStream();
        xstream.alias("stuff", Stuff.class);
        xstream.registerConverter(new EnumConverter());
        String expectedXml = "<stuff>GREEN</stuff>";
        assertEquals(expectedXml, xstream.toXML(Stuff.GREEN));
        assertSame(Stuff.GREEN, xstream.fromXML(expectedXml));
    }
}
