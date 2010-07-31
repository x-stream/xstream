/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2010 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 12. April 2005 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.javabean;

import junit.framework.TestCase;

import java.util.Comparator;

import com.thoughtworks.acceptance.objects.StandardObject;
import com.thoughtworks.xstream.XStream;


public class JavaBeanConverterTest extends TestCase {

    // Different JDK versions deliver properties in different order - so sort them!
    static class StringComparator implements Comparator {

        public int compare(Object o1, Object o2) {
            return ((String)o1).compareToIgnoreCase((String)o2);
        }

    }

    public static class World extends StandardObject {
        
        int anInt = 1;
        Integer anInteger = new Integer(2);
        char aChar = 'a';
        Character aCharacter = new Character('w');
        boolean aBool = true;
        Boolean aBoolean = new Boolean(false);
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

        public boolean isABool() {
            return aBool;
        }

        public void setABool(boolean aBool) {
            this.aBool = aBool;
        }

        public Boolean getABoolean() {
            return aBoolean;
        }

        public void setABoolean(Boolean aBoolean) {
            this.aBoolean = aBoolean;
        }

        public char getAChar() {
            return aChar;
        }

        public void setAChar(char aChar) {
            this.aChar = aChar;
        }

        public Character getACharacter() {
            return aCharacter;
        }

        public void setACharacter(Character aCharacter) {
            this.aCharacter = aCharacter;
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

        public String getAString() {
            return aString;
        }

        public void setAString(String aString) {
            this.aString = aString;
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
        xstream.registerConverter(new JavaBeanConverter(xstream.getMapper(), new BeanProvider(
            new StringComparator())), XStream.PRIORITY_VERY_LOW);
        xstream.alias("world", World.class);

        String expected = "" 
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

        String result = xstream.toXML(world);

        assertEquals(expected, result);
    }

    /**
     * Only normal and trans are serializable properties, the field modifiers do not matter
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
        String expected = ""
            + "<types>\n"
            + "  <normal>normal</normal>\n"
            + "  <trans>transient</trans>\n"
            + "</types>";

        XStream xstream = new XStream();
        xstream.registerConverter(new JavaBeanConverter(xstream.getMapper(), new BeanProvider(
            new StringComparator())), -20);
        xstream.alias("types", TypesOfFields.class);

        String xml = xstream.toXML(fields);
        assertEquals(expected, xml);

    }

    public static class SimpleBean extends StandardObject {
        private Object member;

        public Object getMember() {
            return this.member;
        }

        public void setMember(Object member) {
            this.member = member;
        }
    }

    public void testSupportsTypeAlias() {
        SimpleBean innerBean = new SimpleBean();
        SimpleBean bean = new SimpleBean();
        bean.setMember(innerBean);
        innerBean.setMember("foo");

        String expected = ""
            + "<bean>\n"
            + "  <member class=\"bean\">\n"
            + "    <member class=\"string\">foo</member>\n"
            + "  </member>\n"
            + "</bean>";

        XStream xstream = new XStream();
        xstream.registerConverter(new JavaBeanConverter(xstream.getMapper()), XStream.PRIORITY_VERY_LOW);
        xstream.alias("bean", SimpleBean.class);

        String xml = xstream.toXML(bean);
        assertEquals(expected, xml);
    }

    public void testDoesNotSerializeOmittedFields() {
        TypesOfFields fields = new TypesOfFields();
        String expected = "<types/>";

        XStream xstream = new XStream();
        xstream.registerConverter(new JavaBeanConverter(xstream.getMapper()), XStream.PRIORITY_VERY_LOW);
        xstream.alias("types", TypesOfFields.class);
        xstream.omitField(TypesOfFields.class, "trans");
        xstream.omitField(TypesOfFields.class, "foo");
        xstream.omitField(TypesOfFields.class, "normal");

        String xml = xstream.toXML(fields);
        assertEquals(expected, xml);
    }

    public void testDoesNotDeserializeOmittedFields() {
        TypesOfFields fields = new TypesOfFields();
        String xml = "" 
            + "<types>\n" 
            + "  <normal>foo</normal>\n" 
            + "  <foo>bar</foo>\n" 
            + "</types>";

        XStream xstream = new XStream();
        xstream.registerConverter(new JavaBeanConverter(xstream.getMapper()), XStream.PRIORITY_VERY_LOW);
        xstream.alias("types", TypesOfFields.class);
        xstream.omitField(TypesOfFields.class, "foo");
        xstream.omitField(TypesOfFields.class, "normal");

        TypesOfFields unmarshalledFields = (TypesOfFields)xstream.fromXML(xml);  
        assertEquals(fields, unmarshalledFields);
    }

    public static class UnsafeBean {
        public String getUnsafe() {
            throw new RuntimeException("Do not call");
        }
        public void setUnsafe(String value) {
            // ignore
        }
    }
    
    public void testDoesNotGetValueOfOmittedFields() {
        UnsafeBean bean = new UnsafeBean();
        String expected = "<unsafeBean/>";

        XStream xstream = new XStream();
        xstream.registerConverter(new JavaBeanConverter(xstream.getMapper()), XStream.PRIORITY_VERY_LOW);
        xstream.alias("unsafeBean", UnsafeBean.class);
        xstream.omitField(UnsafeBean.class, "unsafe");
    
        String xml = xstream.toXML(bean);
        assertEquals(expected, xml);
    }
    
    public static class Person {
        private String fName;
        private String lName;

        public Person() {
            // Bean constructor
        }

        public Person(String firstName, String lastName) {
            this.fName = firstName;
            this.lName = lastName;
        }

        public String getFirstName() {
            return fName;
        }

        public void setFirstName(String name) {
            fName = name;
        }

        public String getLastName() {
            return lName;
        }

        public void setLastName(String name) {
            lName = name;
        }
    }

    public static class Man extends Person {

        public Man() {
            // Bean constructor
            super();
        }

        public Man(String firstName, String lastName) {
            super(firstName, lastName);
        }

    }

    public void testDoesNotSerializeOmittedInheritedFields() {
        XStream xstream = new XStream();
        xstream.registerConverter(
            new JavaBeanConverter(xstream.getMapper()), XStream.PRIORITY_VERY_LOW);
        xstream.omitField(Person.class, "lastName");
        xstream.alias("man", Man.class);

        Man man = new Man("John", "Doe");
        String expected = "" 
            + "<man>\n" 
            + "  <firstName>John</firstName>\n" 
            + "</man>";

        assertEquals(expected, xstream.toXML(man));
    }

    public void testUseAliasInheritedFields() {
        XStream xstream = new XStream();
        xstream.registerConverter(
            new JavaBeanConverter(xstream.getMapper(), new BeanProvider(
                new StringComparator())), XStream.PRIORITY_VERY_LOW);
        xstream.aliasField("first-name", Person.class, "firstName");
        xstream.aliasField("last-name", Person.class, "lastName");
        xstream.alias("man", Man.class);

        Man man = new Man("John", "Doe");
        String expected = "" 
            + "<man>\n"
            + "  <first-name>John</first-name>\n"
            + "  <last-name>Doe</last-name>\n"
            + "</man>";

        assertEquals(expected, xstream.toXML(man));
    }
}
