package com.thoughtworks.xstream.converters.enums;

import com.thoughtworks.xstream.XStream;
import junit.framework.TestCase;

// ***** READ THIS *****
// This class will only compile with JDK 1.5.0 or above as it test Java enums.
// If you are using an earlier version of Java, just don't try to build this class. XStream should work fine without it.

/**
 * @author Joe Walnes
 * @author Bryan Coleman
 */
public class EnumConverterTest extends TestCase {

    private XStream xstream;

    protected void setUp() throws Exception {
        super.setUp();
        xstream = new XStream();
        xstream.alias("simple", SimpleEnum.class);
        xstream.alias("polymorphic", PolymorphicEnum.class);
    }

    public void testRepresentsEnumAsSingleStringValue() {
        String expectedXml = "<simple>GREEN</simple>";
        SimpleEnum in = SimpleEnum.GREEN;
        assertEquals(expectedXml, xstream.toXML(in));
        assertEquals(in, xstream.fromXML(expectedXml));
    }

    public void testRepresentsPolymorphicEnumAsSingleStringValue() {
        String expectedXml = "<polymorphic>B</polymorphic>";
        PolymorphicEnum in = PolymorphicEnum.B;
        assertEquals(expectedXml, xstream.toXML(in));
        assertEquals(in, xstream.fromXML(expectedXml));
    }

    public void testDeserializedEnumIsTheSameNotJustEqual() {
        assertSame(SimpleEnum.GREEN, xstream.fromXML(xstream.toXML(SimpleEnum.GREEN)));
        assertSame(PolymorphicEnum.B, xstream.fromXML(xstream.toXML(PolymorphicEnum.B)));
    }

    public void testResolvesSpecializedPolymorphicEnum() {
        PolymorphicEnum in;
        PolymorphicEnum out;

        in = PolymorphicEnum.A;
        out = (PolymorphicEnum) xstream.fromXML(xstream.toXML(in));
        assertEquals("apple", out.fruit());

        in = PolymorphicEnum.B;
        out = (PolymorphicEnum) xstream.fromXML(xstream.toXML(in));
        assertEquals("banana", out.fruit());
    }

}
