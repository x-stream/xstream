package com.thoughtworks.xstream.converters.javabean;

import com.thoughtworks.acceptance.StandardObject;
import com.thoughtworks.xstream.XStream;

import junit.framework.TestCase;

public class JavaBeanConverterTest extends TestCase {

    public static class World extends StandardObject {
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

        public byte getAByte() {
            return aByte;
        }

        public void setAByte(byte byte1) {
            aByte = byte1;
        }

        public Byte getAByteClass() {
            return aByteClass;
        }

        public void setAByteClass(Byte byteClass) {
            aByteClass = byteClass;
        }

        public float getAFloat() {
            return aFloat;
        }

        public void setAFloat(float float1) {
            aFloat = float1;
        }

        public Float getAFloatClass() {
            return aFloatClass;
        }

        public void setAFloatClass(Float floatClass) {
            aFloatClass = floatClass;
        }

        public long getALong() {
            return aLong;
        }

        public void setALong(long long1) {
            aLong = long1;
        }

        public Long getALongClass() {
            return aLongClass;
        }

        public void setALongClass(Long longClass) {
            aLongClass = longClass;
        }

        public boolean isAnBool() {
            return anBool;
        }

        public void setAnBool(boolean anBool) {
            this.anBool = anBool;
        }

        public Boolean getAnBoolean() {
            return anBoolean;
        }

        public void setAnBoolean(Boolean anBoolean) {
            this.anBoolean = anBoolean;
        }

        public char getAnChar() {
            return anChar;
        }

        public void setAnChar(char anChar) {
            this.anChar = anChar;
        }

        public Character getAnCharacter() {
            return anCharacter;
        }

        public void setAnCharacter(Character anCharacter) {
            this.anCharacter = anCharacter;
        }

        public int getAnInt() {
            return anInt;
        }

        public void setAnInt(int anInt) {
            this.anInt = anInt;
        }

        public Integer getAnInteger() {
            return anInteger;
        }

        public void setAnInteger(Integer anInteger) {
            this.anInteger = anInteger;
        }

        public String getAnString() {
            return anString;
        }

        public void setAnString(String anString) {
            this.anString = anString;
        }

        public short getAShort() {
            return aShort;
        }

        public void setAShort(short short1) {
            aShort = short1;
        }

        public Short getAShortClass() {
            return aShortClass;
        }

        public void setAShortClass(Short shortClass) {
            aShortClass = shortClass;
        }
    }

    public void testSerializesAllPrimitiveFieldsInACustomObject() {
        World world = new World();

        XStream xstream = new XStream();
        xstream.registerConverter(new JavaBeanConverter(xstream.getMapper(), "class"), -20);
        xstream.alias("world", World.class);


        String expected =
            "<world>\n" +
            "  <AByte>4</AByte>\n" +
            "  <AByteClass>5</AByteClass>\n" +
            "  <AFloat>8.0</AFloat>\n" +
            "  <AFloatClass>9.0</AFloatClass>\n" +
            "  <ALong>10</ALong>\n" +
            "  <ALongClass>11</ALongClass>\n" +
            "  <AShort>6</AShort>\n" +
            "  <AShortClass>7</AShortClass>\n" +
            "  <anBool>true</anBool>\n" +
            "  <anBoolean>false</anBoolean>\n" +
            "  <anChar>a</anChar>\n" +
            "  <anCharacter>w</anCharacter>\n" +
            "  <anInt>1</anInt>\n" +
            "  <anInteger>2</anInteger>\n" +
            "  <anString>XStream programming!</anString>\n" +
            "</world>";

        String result = xstream.toXML(world);

        assertEquals(expected, result);
    }

    /**
     * Only normal and trans are serializable properties, the field modifiers
     * does not matter
     */
    public static class TypesOfFields extends StandardObject {
        String normal = "normal";

        transient String trans = "transient";

        final String fin = "final";

        static String stat = "stat";

        public static String getStat() {
            return stat;
        }

        public static void setStat(String stat) {
            TypesOfFields.stat = stat;
        }

        public String getFin() {
            return fin;
        }

        public String getNormal() {
            return normal;
        }

        public void setNormal(String normal) {
            this.normal = normal;
        }

        public String getTrans() {
            return trans;
        }

        public void setTrans(String trans) {
            this.trans = trans;
        }


    }

    public void testDoesNotSerializeStaticFields() {
        TypesOfFields fields = new TypesOfFields();
        String expected = "" +
            "<types>\n" +
            "  <normal>normal</normal>\n" +
            "  <trans>transient</trans>\n" +
            "</types>";


        XStream xstream = new XStream();
        xstream.registerConverter(new JavaBeanConverter(xstream.getMapper(), "class"), -20);
        xstream.alias("types", TypesOfFields.class);

        String xml = xstream.toXML(fields);
        assertEquals(expected, xml);

    }

}