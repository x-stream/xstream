/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 20. February 2006 by Mauro Talevi
 */
package com.thoughtworks.acceptance;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.thoughtworks.xstream.testutil.TimeZoneChanger;


/**
 * @author Paul Hammant
 * @author Ian Cartwright
 * @author Mauro Talevi
 * @author J&ouml;rg Schaible
 * @author Guilherme Silveira
 */
public class AttributeTest extends AbstractAcceptanceTest {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TimeZoneChanger.change("GMT");
    }

    @Override
    protected void tearDown() throws Exception {
        TimeZoneChanger.reset();
        super.tearDown();
    }

    public static class One implements HasID {
        public ID id;
        public Two two;

        @Override
        public void setID(final ID id) {
            this.id = id;
        }
    }

    public static interface HasID {
        void setID(ID id);
    }

    public static class Two {}

    public static class Three {
        public Date date;
    }

    public static class Four extends One {
        @SuppressWarnings("hiding")
        public ID id;
    }

    public static class ID {
        public ID(final String value) {
            this.value = value;
        }

        public String value;
    }

    private static class MyIDConverter extends AbstractSingleValueConverter {
        @Override
        public boolean canConvert(final Class<?> type) {
            return type.equals(ID.class);
        }

        @Override
        public String toString(final Object obj) {
            return obj == null ? null : ((ID)obj).value;
        }

        @Override
        public Object fromString(final String str) {
            return new ID(str);
        }
    }

    static class C {
        Date dt;
        String str;
        int i;

        C(final Date dt, final String st, final int i) {
            this.dt = dt;
            str = st;
            this.i = i;
        }
    }

    public void testAllowsAttributeWithCustomConverterAndFieldName() {
        final One one = new One();
        one.two = new Two();
        one.id = new ID("hullo");

        xstream.alias("one", One.class);
        xstream.useAttributeFor("id", ID.class);
        xstream.registerConverter(new MyIDConverter());

        final String expected = ""//
            + "<one id=\"hullo\">\n"
            + "  <two/>\n"
            + "</one>";
        assertBothWays(one, expected);
    }

    public void testDoesNotAllowAttributeWithCustomConverterAndDifferentFieldName() {
        final One one = new One();
        one.two = new Two();
        one.id = new ID("hullo");

        xstream.alias("one", One.class);
        xstream.useAttributeFor("foo", ID.class);
        xstream.registerConverter(new MyIDConverter());

        final String expected = ""//
            + "<one>\n"
            + "  <id>hullo</id>\n"
            + "  <two/>\n"
            + "</one>";
        assertBothWays(one, expected);
    }

    // TODO: Currently not possible, see comment in AbstractReflectionProvider.doUnmarshal
    public void todoTestHidingMemberCanBeWrittenIfAliasDiffers() {
        final Four four = new Four();
        four.two = new Two();
        four.id = new ID("4");
        four.setID(new ID("1"));

        xstream.alias("four", Four.class);
        xstream.aliasField("id4", Four.class, "id");
        xstream.useAttributeFor(ID.class);
        xstream.registerConverter(new MyIDConverter());

        final String expected = ""//
            + "<four id=\"1\" id4=\"4\">\n"
            + "  <two/>\n"
            + "</four>";
        assertBothWays(four, expected);
    }

    public void testAllowsAttributeWithKnownConverterAndFieldName() throws Exception {
        final Three three = new Three();
        final DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        three.date = format.parse("19/02/2006");

        xstream.alias("three", Three.class);
        xstream.useAttributeFor("date", Date.class);

        final String expected = "<three date=\"2006-02-19 00:00:00.0 UTC\"/>";
        assertBothWays(three, expected);
    }

    public void testAllowsAttributeWithArbitraryFieldType() {
        final One one = new One();
        one.two = new Two();
        one.id = new ID("hullo");

        xstream.alias("one", One.class);
        xstream.useAttributeFor(ID.class);
        xstream.registerConverter(new MyIDConverter());

        final String expected = ""//
            + "<one id=\"hullo\">\n"
            + "  <two/>\n"
            + "</one>";
        assertBothWays(one, expected);
    }

    public void testDoesNotAllowAttributeWithNullAttribute() {
        final One one = new One();
        one.two = new Two();

        xstream.alias("one", One.class);
        xstream.useAttributeFor(ID.class);
        xstream.registerConverter(new MyIDConverter());

        final String expected = ""//
            + "<one>\n"
            + "  <two/>\n"
            + "</one>";
        assertBothWays(one, expected);
    }

    public void testAllowsAttributeToBeAliased() {
        final One one = new One();
        one.two = new Two();
        one.id = new ID("hullo");

        xstream.alias("one", One.class);
        xstream.aliasAttribute("id-alias", "id");
        xstream.useAttributeFor("id", ID.class);
        xstream.registerConverter(new MyIDConverter());

        final String expected = ""//
            + "<one id-alias=\"hullo\">\n"
            + "  <two/>\n"
            + "</one>";
        assertBothWays(one, expected);
    }

    public void testCanHandleNullValues() {
        final C c = new C(null, null, 0);
        xstream.alias("C", C.class);
        xstream.useAttributeFor(Date.class);
        xstream.useAttributeFor(String.class);
        final String expected = ""//
            + "<C>\n"
            + "  <i>0</i>\n"
            + "</C>";
        assertBothWays(c, expected);
    }

    public void testCanHandlePrimitiveValues() {
        final C c = new C(null, null, 0);
        xstream.alias("C", C.class);
        xstream.useAttributeFor(Date.class);
        xstream.useAttributeFor(String.class);
        xstream.useAttributeFor(int.class);
        final String expected = "<C i=\"0\"/>";
        assertBothWays(c, expected);
    }

    static class Name {
        String name;

        Name(final String name) {
            this.name = name;
        }
    }

    static class Camera {
        String name;
        protected Name n;

        Camera(final String name) {
            this.name = name;
        }
    }

    public void testAllowsAnAttributeForASpecificField() {
        xstream.alias("camera", Camera.class);
        xstream.useAttributeFor(Camera.class, "name");
        final Camera camera = new Camera("Rebel 350");
        camera.n = new Name("foo");
        final String expected = ""
            + "<camera name=\"Rebel 350\">\n"
            + "  <n>\n"
            + "    <name>foo</name>\n"
            + "  </n>\n"
            + "</camera>";
        assertBothWays(camera, expected);
    }

    public void testAllowsAnAttributeForASpecificAliasedField() {
        xstream.alias("camera", Camera.class);
        xstream.aliasAttribute(Camera.class, "name", "model");
        final Camera camera = new Camera("Rebel 350");
        camera.n = new Name("foo");
        final String expected = ""
            + "<camera model=\"Rebel 350\">\n"
            + "  <n>\n"
            + "    <name>foo</name>\n"
            + "  </n>\n"
            + "</camera>";
        assertBothWays(camera, expected);
    }

    static class PersonalizedCamera extends Camera {
        String owner;

        PersonalizedCamera(final String name, final String owner) {
            super(name);
            this.owner = owner;
        }
    }

    public void testAllowsAnAttributeForASpecificFieldInASuperClass() {
        xstream.alias("camera", PersonalizedCamera.class);
        xstream.useAttributeFor(Camera.class, "name");
        final PersonalizedCamera camera = new PersonalizedCamera("Rebel 350", "Guilherme");
        camera.n = new Name("foo");
        final String expected = ""
            + "<camera name=\"Rebel 350\">\n"
            + "  <n>\n"
            + "    <name>foo</name>\n"
            + "  </n>\n"
            + "  <owner>Guilherme</owner>\n"
            + "</camera>";
        assertBothWays(camera, expected);
    }

    public void testAllowsAnAttributeForAFieldOfASpecialTypeAlsoInASuperClass() {
        xstream.alias("camera", PersonalizedCamera.class);
        xstream.useAttributeFor("name", String.class);
        final PersonalizedCamera camera = new PersonalizedCamera("Rebel 350", "Guilherme");
        camera.n = new Name("foo");
        final String expected = ""
            + "<camera name=\"Rebel 350\">\n"
            + "  <n name=\"foo\"/>\n"
            + "  <owner>Guilherme</owner>\n"
            + "</camera>";
        assertBothWays(camera, expected);
    }

    public static class TransientIdField {
        transient String id;
        String name;

        public TransientIdField(final String id, final String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public boolean equals(final Object obj) {
            return name.equals(((TransientIdField)obj).name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }

    public void testAttributeNamedLikeTransientFieldDoesNotAbortDeserializationOfFollowingFields() {
        xstream.setMode(XStream.ID_REFERENCES);
        xstream.alias("transient", TransientIdField.class);

        final TransientIdField field = new TransientIdField("foo", "test");
        final String xml = "" //
            + "<transient id=\"1\">\n" //
            + "  <name>test</name>\n" //
            + "</transient>";

        assertBothWays(field, xml);
    }

    static class Person {
        String _name;
        int _age;

        Person() {
        } // JDK 1.3

        Person(final String name, final int age) {
            _name = name;
            _age = age;
        }
    };

    public void testAttributeMayHaveXmlUnfriendlyName() {
        xstream.alias("person", Person.class);
        xstream.useAttributeFor(Person.class, "_name");
        xstream.useAttributeFor(Person.class, "_age");
        final Person person = new Person("joe", 25);
        final String xml = "<person __name=\"joe\" __age=\"25\"/>";
        assertBothWays(person, xml);
    }
}
