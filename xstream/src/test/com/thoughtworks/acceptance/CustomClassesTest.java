package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.io.xml.XppReader;

import java.io.StringReader;

public class CustomClassesTest extends AbstractAcceptanceTest {

    public static class SamplePerson extends StandardObject {
        int anInt;
        String firstName;
        String lastName;
        transient String aComment = "";
    }

    public void testCustomObjectWithBasicFields() {

        xstream.alias("friend", SamplePerson.class);

        SamplePerson person = new SamplePerson();
        person.anInt = 3;
        person.firstName = "Joe";
        person.lastName = "Walnes";

        String expected =
                "<friend>\n" +
                "  <anInt>3</anInt>\n" +
                "  <firstName>Joe</firstName>\n" +
                "  <lastName>Walnes</lastName>\n" +
                "</friend>";

        assertBothWays(person, expected);

    }

    public static class SamplePersonHolder {
        String aString;
        SamplePerson brother;

        public boolean equals(Object obj) {
            SamplePersonHolder containerObject = (SamplePersonHolder) obj;
            return (aString == null ? containerObject.aString == null : aString.equals(containerObject.aString))
                    && brother.equals(containerObject.brother);
        }
    }

    public void testCustomObjectWithCustomObjectField() {
        xstream.alias("friend", SamplePerson.class);
        xstream.alias("personHolder", SamplePersonHolder.class);

        SamplePersonHolder personHolder = new SamplePersonHolder();
        personHolder.aString = "hello world";

        SamplePerson person = new SamplePerson();
        person.anInt = 3;
        person.firstName = "Joe";
        person.lastName = "Walnes";

        personHolder.brother = person;

        String expected =
                "<personHolder>\n" +
                "  <aString>hello world</aString>\n" +
                "  <brother>\n" +
                "    <anInt>3</anInt>\n" +
                "    <firstName>Joe</firstName>\n" +
                "    <lastName>Walnes</lastName>\n" +
                "  </brother>\n" +
                "</personHolder>";

        assertBothWays(personHolder, expected);

    }

    public void testCustomObjectWithCustomObjectFieldsSetToNull() {
        xstream.alias("friend", SamplePerson.class);
        xstream.alias("personHolder", SamplePersonHolder.class);

        SamplePersonHolder personHolder = new SamplePersonHolder();
        personHolder.aString = null;

        SamplePerson person = new SamplePerson();
        person.anInt = 3;
        person.firstName = "Joe";
        person.lastName = null;

        personHolder.brother = person;

        String expected =
                "<personHolder>\n" +
                "  <brother>\n" +
                "    <anInt>3</anInt>\n" +
                "    <firstName>Joe</firstName>\n" +
                "  </brother>\n" +
                "</personHolder>";

        assertBothWays(personHolder, expected);

    }

    public void testCustomObjectCanBeInstantiatedExternallyBeforeDeserialization() {
        xstream.alias("friend", SamplePerson.class);
        xstream.alias("personHolder", SamplePersonHolder.class);

        String xml =
                "<personHolder>\n" +
                "  <aString>hello world</aString>\n" +
                "  <brother>\n" +
                "    <anInt>3</anInt>\n" +
                "    <firstName>Joe</firstName>\n" +
                "    <lastName>Walnes</lastName>\n" +
                "  </brother>\n" +
                "</personHolder>";

        // execute
        SamplePersonHolder alreadyInstantiated = new SamplePersonHolder();
        xstream.unmarshal(new XppReader(new StringReader(xml)), alreadyInstantiated);

        // verify
        SamplePersonHolder expectedResult = new SamplePersonHolder();
        expectedResult.aString = "hello world";

        SamplePerson expectedPerson = new SamplePerson();
        expectedPerson.anInt = 3;
        expectedPerson.firstName = "Joe";
        expectedPerson.lastName = "Walnes";
        expectedResult.brother = expectedPerson;

        assertEquals(expectedResult, alreadyInstantiated);
    }

    public void testCustomObjectWillNotUnmarshalTransientFields() {

        xstream.alias("friend", SamplePerson.class);

        String xml =
                "<friend>\n" +
                "  <anInt>3</anInt>\n" +
                "  <firstName>Joe</firstName>\n" +
                "  <lastName>Walnes</lastName>\n" +
                "  <aComment>XStream Despot</aComment>\n" +
                "</friend>";

        try {
            xstream.fromXML(xml);
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertTrue(e.getMessage().indexOf("aComment") >= 0);
        }
    }

    public void testNullObjectsDoNotHaveFieldsWritten() {

        xstream.alias("cls", WithSomeFields.class);

        WithSomeFields obj = new WithSomeFields();

        String expected = "<cls/>";

        assertBothWays(obj, expected);
    }

    public void testEmptyStringsAreNotTreatedAsNulls() {
        xstream.alias("cls", WithSomeFields.class);

        WithSomeFields obj = new WithSomeFields();
        obj.b = "";

        String expected = "" +
                "<cls>\n" +
                "  <b></b>\n" +
                "</cls>";

        assertBothWays(obj, expected);
    }

    public static class WithSomeFields extends StandardObject {
        Object a;
        String b;
    }

    public void testNullsAreDistinguishedFromEmptyStrings() {
        LotsOfStrings in = new LotsOfStrings();
        in.a = ".";
        in.b = "";
        in.c = null;

        String xml = xstream.toXML(in);
        LotsOfStrings out = (LotsOfStrings) xstream.fromXML(xml);

        assertEquals(".", out.a);
        assertEquals("", out.b);
        assertNull(out.c);
    }

    public static class LotsOfStrings {
        String a;
        String b;
        String c;
    }

    public void testFieldWithObjectType() {
        String expected = "" +
                "<thing>\n" +
                "  <one>1.0</one>\n" +
                "  <two class=\"double\">2.0</two>\n" +
                "</thing>";
        xstream.alias("thing", FieldWithObjectType.class);

        assertBothWays(new FieldWithObjectType(), expected);
    }

    public static class FieldWithObjectType extends StandardObject {
        Double one = new Double(1.0);
        Object two = new Double(2.0);
    }

    public void testFailsFastIfFieldIsDefinedTwice() {
        String input = "" +
                "<thing>\n" +
                "  <one>1.0</one>\n" +
                "  <one>2.0</one>\n" +
                "</thing>";
        xstream.alias("thing", FieldWithObjectType.class);

        try {

            xstream.fromXML(input);
            fail("Expected exception");

        } catch (ReflectionConverter.DuplicateFieldException expected) {
            assertEquals("one", expected.getShortMessage());
        }
    }
    
    public static class TransientInitializingClass extends StandardObject {
        private transient String s = "";
        private Object readResolve() {
            this.s = "foo";
            return this;
        }
    }

    public void testCustomObjectWithTransientFieldInitialization() {

        xstream.alias("tran", TransientInitializingClass.class);

        TransientInitializingClass tran = new TransientInitializingClass();

        String expected = "<tran/>";

        TransientInitializingClass serialized = (TransientInitializingClass)assertBothWays(tran, expected);
        assertEquals("foo", serialized.s);
    }
}
