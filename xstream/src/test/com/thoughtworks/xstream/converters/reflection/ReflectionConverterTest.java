package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.acceptance.StandardObject;
import com.thoughtworks.xstream.XStream;
import junit.framework.TestCase;

public class ReflectionConverterTest extends TestCase {

    public class World extends StandardObject {
        int anInt = 1;
        Integer anInteger = new Integer(2);
        char anChar = 'a';
        Character anCharacter = new Character('w');
        boolean anBool = true;
        Boolean anBoolean = new Boolean(false);
        byte aByte = 4;
        Byte aByteClass = new Byte("5");
        short aShort = 6;
        Short aShortClass = new Short("7");
        float aFloat = 8f;
        Float aFloatClass = new Float("9");
        long aLong = 10;
        Long aLongClass = new Long("11");
        String anString = new String("XStream programming!");
    }

    public void testSerializesAllPrimitiveFieldsInACustomObject() {
        World world = new World();

        XStream xstream = new XStream();
        xstream.alias("world", World.class);

        String expected =
                "<world>\n" +
                "  <anInt>1</anInt>\n" +
                "  <anInteger>2</anInteger>\n" +
                "  <anChar>a</anChar>\n" +
                "  <anCharacter>w</anCharacter>\n" +
                "  <anBool>true</anBool>\n" +
                "  <anBoolean>false</anBoolean>\n" +
                "  <aByte>4</aByte>\n" +
                "  <aByteClass>5</aByteClass>\n" +
                "  <aShort>6</aShort>\n" +
                "  <aShortClass>7</aShortClass>\n" +
                "  <aFloat>8.0</aFloat>\n" +
                "  <aFloatClass>9.0</aFloatClass>\n" +
                "  <aLong>10</aLong>\n" +
                "  <aLongClass>11</aLongClass>\n" +
                "  <anString>XStream programming!</anString>\n" +
                "</world>";

        assertEquals(expected, xstream.toXML(world));
    }

    static class TypesOfFields extends StandardObject {
        String normal = "normal";
        transient String trans = "transient";
        final String fin = "final";
        static String stat = "stat";
    }

    public void testDoesNotSerializeTransientOrStaticOrFinalFields() {
        TypesOfFields fields = new TypesOfFields();
        String expected = "" +
                "<types>\n" +
                "  <normal>normal</normal>\n" +
                "</types>";

        XStream xstream = new XStream();
        xstream.alias("types", TypesOfFields.class);

        String xml = xstream.toXML(fields);
        assertEquals(expected, xml);

    }

}
