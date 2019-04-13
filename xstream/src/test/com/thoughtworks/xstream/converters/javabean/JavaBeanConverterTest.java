/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2010, 2011, 2013, 2014, 2015, 2016, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 12. April 2005 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.javabean;

import java.util.Comparator;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.acceptance.objects.StandardObject;
import com.thoughtworks.xstream.XStream;

import junit.framework.TestCase;


public class JavaBeanConverterTest extends TestCase {

    // Different JDK versions deliver properties in different order - so sort them!
    static class StringComparator implements Comparator<String> {

        @Override
        public int compare(final String o1, final String o2) {
            return o1.compareToIgnoreCase(o2);
        }

    }

    public static class World extends StandardObject {

        private static final long serialVersionUID = 200504L;

        int anInt = 1;
        Integer anInteger = new Integer(2);
        char aChar = 'a';
        Character aCharacter = new Character('w');
        boolean aBool = true;
        Boolean aBoolean = false;
        byte aByte = 4;
        Byte aByteClass = new Byte("5");
        short aShort = 6;
        Short aShortClass = new Short("7");
        float aFloat = 8f;
        Float aFloatClass = new Float("9");
        long aLong = 10;
        Long aLongClass = new Long("11");
        String aString = new String("XStream programming!");

        public byte getAByte() {
            return aByte;
        }

        public void setAByte(final byte byte1) {
            aByte = byte1;
        }

        public Byte getAByteClass() {
            return aByteClass;
        }

        public void setAByteClass(final Byte byteClass) {
            aByteClass = byteClass;
        }

        public float getAFloat() {
            return aFloat;
        }

        public void setAFloat(final float float1) {
            aFloat = float1;
        }

        public Float getAFloatClass() {
            return aFloatClass;
        }

        public void setAFloatClass(final Float floatClass) {
            aFloatClass = floatClass;
        }

        public long getALong() {
            return aLong;
        }

        public void setALong(final long long1) {
            aLong = long1;
        }

        public Long getALongClass() {
            return aLongClass;
        }

        public void setALongClass(final Long longClass) {
            aLongClass = longClass;
        }

        public boolean isABool() {
            return aBool;
        }

        public void setABool(final boolean aBool) {
            this.aBool = aBool;
        }

        public Boolean getABoolean() {
            return aBoolean;
        }

        public void setABoolean(final Boolean aBoolean) {
            this.aBoolean = aBoolean;
        }

        public char getAChar() {
            return aChar;
        }

        public void setAChar(final char aChar) {
            this.aChar = aChar;
        }

        public Character getACharacter() {
            return aCharacter;
        }

        public void setACharacter(final Character aCharacter) {
            this.aCharacter = aCharacter;
        }

        public int getAnInt() {
            return anInt;
        }

        public void setAnInt(final int anInt) {
            this.anInt = anInt;
        }

        public Integer getAnInteger() {
            return anInteger;
        }

        public void setAnInteger(final Integer anInteger) {
            this.anInteger = anInteger;
        }

        public String getAString() {
            return aString;
        }

        public void setAString(final String aString) {
            this.aString = aString;
        }

        public short getAShort() {
            return aShort;
        }

        public void setAShort(final short short1) {
            aShort = short1;
        }

        public Short getAShortClass() {
            return aShortClass;
        }

        public void setAShortClass(final Short shortClass) {
            aShortClass = shortClass;
        }
    }

    public void testSerializesAllPrimitiveFieldsInACustomObject() {
        final World world = new World();

        final XStream xstream = new XStream();
        xstream.registerConverter(new JavaBeanConverter(xstream.getMapper(), new BeanProvider(new StringComparator())),
            XStream.PRIORITY_LOW);
        xstream.alias("world", World.class);

        final String expected = ""
            + "<world>\n"
            + "  <ABool>true</ABool>\n"
            + "  <ABoolean>false</ABoolean>\n"
            + "  <AByte>4</AByte>\n"
            + "  <AByteClass>5</AByteClass>\n"
            + "  <AChar>a</AChar>\n"
            + "  <ACharacter>w</ACharacter>\n"
            + "  <AFloat>8.0</AFloat>\n"
            + "  <AFloatClass>9.0</AFloatClass>\n"
            + "  <ALong>10</ALong>\n"
            + "  <ALongClass>11</ALongClass>\n"
            + "  <anInt>1</anInt>\n"
            + "  <anInteger>2</anInteger>\n"
            + "  <AShort>6</AShort>\n"
            + "  <AShortClass>7</AShortClass>\n"
            + "  <AString>XStream programming!</AString>\n"
            + "</world>";

        final String result = xstream.toXML(world);

        assertEquals(expected, result);
    }

    public void testSerializesNullValue() {
        final World world = new World();
        world.setAString(null);

        final XStream xstream = new XStream();
        xstream.registerConverter(new JavaBeanConverter(xstream.getMapper(), new BeanProvider(new StringComparator())),
            XStream.PRIORITY_LOW);
        xstream.alias("world", World.class);
        xstream.allowTypes(World.class);

        final String expected = ""
            + "<world>\n"
            + "  <ABool>true</ABool>\n"
            + "  <ABoolean>false</ABoolean>\n"
            + "  <AByte>4</AByte>\n"
            + "  <AByteClass>5</AByteClass>\n"
            + "  <AChar>a</AChar>\n"
            + "  <ACharacter>w</ACharacter>\n"
            + "  <AFloat>8.0</AFloat>\n"
            + "  <AFloatClass>9.0</AFloatClass>\n"
            + "  <ALong>10</ALong>\n"
            + "  <ALongClass>11</ALongClass>\n"
            + "  <anInt>1</anInt>\n"
            + "  <anInteger>2</anInteger>\n"
            + "  <AShort>6</AShort>\n"
            + "  <AShortClass>7</AShortClass>\n"
            + "  <AString class=\"null\"/>\n"
            + "</world>";

        final String result = xstream.toXML(world);

        assertEquals(expected, result);

        final World world2 = xstream.<World>fromXML(result);
        assertEquals(null, world2.getAString());
    }

    /**
     * Only normal and trans are serializable properties, the field modifiers do not matter
     */
    public static class TypesOfFields extends StandardObject {

        private static final long serialVersionUID = 200504L;

        String normal = "normal";

        transient String trans = "transient";

        final String fin = "final";

        static String stat = "stat";

        public static String getStat() {
            return stat;
        }

        public static void setStat(final String stat) {
            TypesOfFields.stat = stat;
        }

        public String getFin() {
            return fin;
        }

        public String getNormal() {
            return normal;
        }

        public void setNormal(final String normal) {
            this.normal = normal;
        }

        public String getTrans() {
            return trans;
        }

        public void setTrans(final String trans) {
            this.trans = trans;
        }
    }

    public void testDoesNotSerializeStaticFields() {
        final TypesOfFields fields = new TypesOfFields();
        final String expected = ""
            + "<types>\n"
            + "  <normal>normal</normal>\n"
            + "  <trans>transient</trans>\n"
            + "</types>";

        final XStream xstream = new XStream();
        xstream.registerConverter(new JavaBeanConverter(xstream.getMapper(), new BeanProvider(new StringComparator())),
            -20);
        xstream.alias("types", TypesOfFields.class);

        final String xml = xstream.toXML(fields);
        assertEquals(expected, xml);

    }

    public static class SimpleBean extends StandardObject {

        private static final long serialVersionUID = 200707L;

        private Object member;

        public Object getMember() {
            return member;
        }

        public void setMember(final Object member) {
            this.member = member;
        }
    }

    public void testSupportsTypeAlias() {
        final SimpleBean innerBean = new SimpleBean();
        final SimpleBean bean = new SimpleBean();
        bean.setMember(innerBean);
        innerBean.setMember("foo");

        final String expected = ""
            + "<bean>\n"
            + "  <member class=\"bean\">\n"
            + "    <member class=\"string\">foo</member>\n"
            + "  </member>\n"
            + "</bean>";

        final XStream xstream = new XStream();
        xstream.registerConverter(new JavaBeanConverter(xstream.getMapper()), XStream.PRIORITY_LOW);
        xstream.alias("bean", SimpleBean.class);

        final String xml = xstream.toXML(bean);
        assertEquals(expected, xml);
    }

    public void testDoesNotSerializeOmittedFields() {
        final TypesOfFields fields = new TypesOfFields();
        final String expected = "<types/>";

        final XStream xstream = new XStream();
        xstream.registerConverter(new JavaBeanConverter(xstream.getMapper()), XStream.PRIORITY_LOW);
        xstream.alias("types", TypesOfFields.class);
        xstream.omitField(TypesOfFields.class, "trans");
        xstream.omitField(TypesOfFields.class, "foo");
        xstream.omitField(TypesOfFields.class, "normal");

        final String xml = xstream.toXML(fields);
        assertEquals(expected, xml);
    }

    public void testDoesNotDeserializeOmittedFields() {
        final TypesOfFields fields = new TypesOfFields();
        final String xml = "" //
            + "<types>\n"
            + "  <normal>foo</normal>\n"
            + "  <foo>bar</foo>\n"
            + "</types>";

        final XStream xstream = new XStream();
        xstream.allowTypesByWildcard(AbstractAcceptanceTest.class.getPackage().getName() + ".*objects.**");
        xstream.allowTypesByWildcard(this.getClass().getName() + "$*");
        xstream.registerConverter(new JavaBeanConverter(xstream.getMapper()), XStream.PRIORITY_LOW);
        xstream.alias("types", TypesOfFields.class);
        xstream.omitField(TypesOfFields.class, "foo");
        xstream.omitField(TypesOfFields.class, "normal");

        final TypesOfFields unmarshalledFields = xstream.<TypesOfFields>fromXML(xml);
        assertEquals(fields, unmarshalledFields);
    }

    public void testIgnoresUnknownFieldsMatchingPattern() {
        final TypesOfFields fields = new TypesOfFields();
        fields.setNormal("foo");
        final String xml = ""//
            + "<types>\n"
            + "  <normal>foo</normal>\n"
            + "  <foo>bar</foo>\n"
            + "</types>";

        final XStream xstream = new XStream();
        xstream.allowTypesByWildcard(AbstractAcceptanceTest.class.getPackage().getName() + ".*objects.**");
        xstream.allowTypesByWildcard(this.getClass().getName() + "$*");
        xstream.registerConverter(new JavaBeanConverter(xstream.getMapper()), XStream.PRIORITY_LOW);
        xstream.alias("types", TypesOfFields.class);
        xstream.ignoreUnknownElements("fo.*");

        final TypesOfFields unmarshalledFields = xstream.<TypesOfFields>fromXML(xml);
        assertEquals(fields, unmarshalledFields);
    }

    public static class UnsafeBean {
        public String getUnsafe() {
            throw new RuntimeException("Do not call");
        }

        public void setUnsafe(final String value) {
            // ignore
        }
    }

    public void testDoesNotGetValueOfOmittedFields() {
        final UnsafeBean bean = new UnsafeBean();
        final String expected = "<unsafeBean/>";

        final XStream xstream = new XStream();
        xstream.registerConverter(new JavaBeanConverter(xstream.getMapper()), XStream.PRIORITY_LOW);
        xstream.alias("unsafeBean", UnsafeBean.class);
        xstream.omitField(UnsafeBean.class, "unsafe");

        final String xml = xstream.toXML(bean);
        assertEquals(expected, xml);
    }

    public static class Person {
        private String fName;
        private String lName;

        public Person() {
            // Bean constructor
        }

        public Person(final String firstName, final String lastName) {
            fName = firstName;
            lName = lastName;
        }

        public String getFirstName() {
            return fName;
        }

        public void setFirstName(final String name) {
            fName = name;
        }

        public String getLastName() {
            return lName;
        }

        public void setLastName(final String name) {
            lName = name;
        }
    }

    public static class Man extends Person {

        public Man() {
            // Bean constructor
            super();
        }

        public Man(final String firstName, final String lastName) {
            super(firstName, lastName);
        }

    }

    public void testDoesNotSerializeOmittedInheritedFields() {
        final XStream xstream = new XStream();
        xstream.registerConverter(new JavaBeanConverter(xstream.getMapper()), XStream.PRIORITY_LOW);
        xstream.omitField(Person.class, "lastName");
        xstream.alias("man", Man.class);

        final Man man = new Man("John", "Doe");
        final String expected = "" //
            + "<man>\n"
            + "  <firstName>John</firstName>\n"
            + "</man>";

        assertEquals(expected, xstream.toXML(man));
    }

    public void testUseAliasInheritedFields() {
        final XStream xstream = new XStream();
        xstream.registerConverter(new JavaBeanConverter(xstream.getMapper(), new BeanProvider(new StringComparator())),
            XStream.PRIORITY_LOW);
        xstream.aliasField("first-name", Person.class, "firstName");
        xstream.aliasField("last-name", Person.class, "lastName");
        xstream.alias("man", Man.class);

        final Man man = new Man("John", "Doe");
        final String expected = ""
            + "<man>\n"
            + "  <first-name>John</first-name>\n"
            + "  <last-name>Doe</last-name>\n"
            + "</man>";

        assertEquals(expected, xstream.toXML(man));
    }

    public void testFailsFastIfPropertyIsDefinedTwice() {
        final XStream xstream = new XStream();
        xstream.allowTypesByWildcard(AbstractAcceptanceTest.class.getPackage().getName() + ".*objects.**");
        xstream.allowTypesByWildcard(this.getClass().getName() + "$*");
        xstream.registerConverter(new JavaBeanConverter(xstream.getMapper()), XStream.PRIORITY_LOW);
        final String input = "" //
            + "<types>\n"
            + "  <normal>foo</normal>\n"
            + "  <normal>bar</normal>\n"
            + "</types>";
        xstream.alias("types", TypesOfFields.class);

        try {

            xstream.fromXML(input);
            fail("Expected exception");

        } catch (final JavaBeanConverter.DuplicatePropertyException expected) {
            assertEquals("normal", expected.get("property"));
        }
    }

    public void testCanConvertDoesNotThrowException() {
        final JavaBeanConverter converter = new JavaBeanConverter(null);
        assertTrue(converter.canConvert(SimpleBean.class));
        assertFalse(converter.canConvert(null));
        assertFalse(converter.canConvert(long.class));
        assertFalse(converter.canConvert(Object[].class));
    }
}
