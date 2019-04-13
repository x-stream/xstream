/*
 * Copyright (C) 2003, 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2011, 2017, 2018, 2019 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 26. September 2003 by Joe Walnes
 */
package com.thoughtworks.acceptance;

import java.io.StringReader;

import com.thoughtworks.acceptance.objects.StandardObject;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.core.util.DefaultDriver;


public class CustomClassesTest extends AbstractAcceptanceTest {

    public static class SamplePerson extends StandardObject {
        private static final long serialVersionUID = 200309L;
        int anInt;
        String firstName;
        String lastName;
        transient String aComment = "";
    }

    public void testCustomObjectWithBasicFields() {
        xstream.alias("friend", SamplePerson.class);

        final SamplePerson person = new SamplePerson();
        person.anInt = 3;
        person.firstName = "Joe";
        person.lastName = "Walnes";

        final String expected = ""
            + "<friend>\n"
            + "  <anInt>3</anInt>\n"
            + "  <firstName>Joe</firstName>\n"
            + "  <lastName>Walnes</lastName>\n"
            + "</friend>";

        assertBothWays(person, expected);
    }

    public static class SamplePersonHolder {
        String aString;
        SamplePerson brother;

        @Override
        public boolean equals(final Object obj) {
            final SamplePersonHolder containerObject = (SamplePersonHolder)obj;
            return (aString == null ? containerObject.aString == null : aString.equals(containerObject.aString))
                && brother.equals(containerObject.brother);
        }

        @Override
        public int hashCode() {
            return (aString == null ? 0 : aString.hashCode()) | (brother == null ? 0 : brother.hashCode());
        }
    }

    public void testCustomObjectWithCustomObjectField() {
        xstream.alias("friend", SamplePerson.class);
        xstream.alias("personHolder", SamplePersonHolder.class);

        final SamplePersonHolder personHolder = new SamplePersonHolder();
        personHolder.aString = "hello world";

        final SamplePerson person = new SamplePerson();
        person.anInt = 3;
        person.firstName = "Joe";
        person.lastName = "Walnes";

        personHolder.brother = person;

        final String expected = ""
            + "<personHolder>\n"
            + "  <aString>hello world</aString>\n"
            + "  <brother>\n"
            + "    <anInt>3</anInt>\n"
            + "    <firstName>Joe</firstName>\n"
            + "    <lastName>Walnes</lastName>\n"
            + "  </brother>\n"
            + "</personHolder>";

        assertBothWays(personHolder, expected);
    }

    public void testCustomObjectWithCustomObjectFieldsSetToNull() {
        xstream.alias("friend", SamplePerson.class);
        xstream.alias("personHolder", SamplePersonHolder.class);

        final SamplePersonHolder personHolder = new SamplePersonHolder();
        personHolder.aString = null;

        final SamplePerson person = new SamplePerson();
        person.anInt = 3;
        person.firstName = "Joe";
        person.lastName = null;

        personHolder.brother = person;

        final String expected = ""
            + "<personHolder>\n"
            + "  <brother>\n"
            + "    <anInt>3</anInt>\n"
            + "    <firstName>Joe</firstName>\n"
            + "  </brother>\n"
            + "</personHolder>";

        assertBothWays(personHolder, expected);
    }

    public void testCustomObjectCanBeInstantiatedExternallyBeforeDeserialization() {
        xstream.alias("friend", SamplePerson.class);
        xstream.alias("personHolder", SamplePersonHolder.class);

        final String xml = ""
            + "<personHolder>\n"
            + "  <aString>hello world</aString>\n"
            + "  <brother>\n"
            + "    <anInt>3</anInt>\n"
            + "    <firstName>Joe</firstName>\n"
            + "    <lastName>Walnes</lastName>\n"
            + "  </brother>\n"
            + "</personHolder>";

        // execute
        final SamplePersonHolder alreadyInstantiated = new SamplePersonHolder();
        xstream.unmarshal(DefaultDriver.create().createReader(new StringReader(xml)), alreadyInstantiated);

        // verify
        final SamplePersonHolder expectedResult = new SamplePersonHolder();
        expectedResult.aString = "hello world";

        final SamplePerson expectedPerson = new SamplePerson();
        expectedPerson.anInt = 3;
        expectedPerson.firstName = "Joe";
        expectedPerson.lastName = "Walnes";
        expectedResult.brother = expectedPerson;

        assertEquals(expectedResult, alreadyInstantiated);
    }

    public void testCustomObjectWillNotUnmarshalTransientFields() {
        xstream.alias("friend", SamplePerson.class);

        final String xml = "<friend>\n"
            + "  <anInt>3</anInt>\n"
            + "  <firstName>Joe</firstName>\n"
            + "  <lastName>Walnes</lastName>\n"
            + "  <aComment>XStream Despot</aComment>\n"
            + "</friend>";

        final SamplePerson person = xstream.fromXML(xml);
        assertNull(person.aComment);
    }

    static class Joe extends SamplePerson {
        private static final long serialVersionUID = 200703L;
        boolean aBoolean;
    }

    public void testCustomObjectWillNotUnmarshalInheritedTransientFields() {
        xstream.alias("joe", Joe.class);

        final String xml = ""
            + "<joe>\n"
            + "  <anInt>3</anInt>\n"
            + "  <firstName>Joe</firstName>\n"
            + "  <lastName>Walnes</lastName>\n"
            + "  <aComment>XStream Despot</aComment>\n"
            + "  <aBoolean>true</aBoolean>\n"
            + "</joe>";

        final Joe joe = xstream.fromXML(xml);
        assertNull(joe.aComment);
    }

    public void testCustomObjectWillNotUnmarshalTransientFieldsFromAttributes() {
        xstream.alias("friend", SamplePerson.class);

        final String xml = ""
            + "<friend aComment='XStream Despot'>\n"
            + "  <anInt>3</anInt>\n"
            + "  <firstName>Joe</firstName>\n"
            + "  <lastName>Walnes</lastName>\n"
            + "</friend>";

        // without attribute definition
        SamplePerson person = xstream.fromXML(xml);
        assertNull(person.aComment);

        xstream.useAttributeFor("aComment", String.class);

        // with attribute definition
        person = xstream.fromXML(xml);
        assertNull(person.aComment);
    }

    public void testNullObjectsDoNotHaveFieldsWritten() {
        xstream.alias("cls", WithSomeFields.class);

        final WithSomeFields obj = new WithSomeFields();
        final String expected = "<cls/>";
        assertBothWays(obj, expected);
    }

    public void testEmptyStringsAreNotTreatedAsNulls() {
        xstream.alias("cls", WithSomeFields.class);

        final WithSomeFields obj = new WithSomeFields();
        obj.b = "";

        final String expected = ""//
            + "<cls>\n"
            + "  <b></b>\n"
            + "</cls>";

        assertBothWays(obj, expected);
    }

    public static class WithSomeFields extends StandardObject {
        private static final long serialVersionUID = 200310L;
        Object a;
        String b;
    }

    public void testNullsAreDistinguishedFromEmptyStrings() {
        final LotsOfStrings in = new LotsOfStrings();
        in.a = ".";
        in.b = "";
        in.c = null;

        final String xml = xstream.toXML(in);
        final LotsOfStrings out = xstream.fromXML(xml);

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
        final String expected = ""
            + "<thing>\n"
            + "  <one>1.0</one>\n"
            + "  <two class=\"double\">2.0</two>\n"
            + "</thing>";
        xstream.alias("thing", FieldWithObjectType.class);

        assertBothWays(new FieldWithObjectType(), expected);
    }

    public static class FieldWithObjectType extends StandardObject {
        private static final long serialVersionUID = 200403L;
        Double one = 1.0;
        Object two = 2.0;
    }

    public void testFailsFastIfFieldIsDefinedTwice() {
        final String input = "" //
            + "<thing>\n"
            + "  <one>1.0</one>\n"
            + "  <one>2.0</one>\n"
            + "</thing>";
        xstream.alias("thing", FieldWithObjectType.class);

        try {
            xstream.fromXML(input);
            fail("Expected exception");
        } catch (final ReflectionConverter.DuplicateFieldException expected) {
            assertEquals("one", expected.get("field"));
        }
    }

    public static class TransientInitializingClass extends StandardObject {
        private static final long serialVersionUID = 200603L;
        private transient String s = "";

        private Object readResolve() {
            s = "foo";
            return this;
        }
    }

    public void testCustomObjectWithTransientFieldInitialization() {
        xstream.alias("tran", TransientInitializingClass.class);

        final TransientInitializingClass tran = new TransientInitializingClass();
        final String expected = "<tran/>";
        final TransientInitializingClass serialized = assertBothWays(tran, expected);
        assertEquals("foo", serialized.s);
    }
}
