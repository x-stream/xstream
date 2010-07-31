/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009 XStream Committers.
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

    protected void setUp() throws Exception {
        super.setUp();
        TimeZoneChanger.change("GMT");
    }

    protected void tearDown() throws Exception {
        TimeZoneChanger.reset();
        super.tearDown();
    }

    public static class One implements HasID {
        public ID id;
        public Two two;

        public void setID(ID id) {
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
        public ID id;
    }

    public static class ID {
        public ID(String value) {
            this.value = value;
        }

        public String value;
    }

    private static class MyIDConverter extends AbstractSingleValueConverter {
        public boolean canConvert(Class type) {
            return type.equals(ID.class);
        }

        public String toString(Object obj) {
            return obj == null ? null : ((ID) obj).value;
        }

        public Object fromString(String str) {
            return new ID(str);
        }
    }
    
    static class C
    {
        private Date dt;
        private String str;
        private int i;
        
        C() {
            // for JDK 1.3
        }

        C(Date dt, String st, int i)
        {
            this.dt = dt;
            this.str = st;
            this.i = i;
        }
    }

    public void testAllowsAttributeWithCustomConverterAndFieldName() {
        One one = new One();
        one.two = new Two();
        one.id  = new ID("hullo");

        xstream.alias("one", One.class);
        xstream.useAttributeFor("id", ID.class);
        xstream.registerConverter(new MyIDConverter());

        String expected =
                "<one id=\"hullo\">\n" +
                "  <two/>\n" +
                "</one>";
        assertBothWays(one, expected);
    }

    public void testDoesNotAllowAttributeWithCustomConverterAndDifferentFieldName() {
        One one = new One();
        one.two = new Two();
        one.id  = new ID("hullo");

        xstream.alias("one", One.class);
        xstream.useAttributeFor("foo", ID.class);
        xstream.registerConverter(new MyIDConverter());

        String expected =
                "<one>\n" +
                "  <id>hullo</id>\n" +
                "  <two/>\n" +
                "</one>";
        assertBothWays(one, expected);
    }

    // TODO: Currently not possible, see comment in AbstractReflectionProvider.doUnmarshal 
    public void todoTestHidingMemberCanBeWrittenIfAliasDiffers() {
        Four four = new Four();
        four.two = new Two();
        four.id  = new ID("4");
        four.setID(new ID("1"));

        xstream.alias("four", Four.class);
        xstream.aliasField("id4", Four.class, "id");
        xstream.useAttributeFor(ID.class);
        xstream.registerConverter(new MyIDConverter());

        String expected =
                "<four id=\"1\" id4=\"4\">\n" +
                "  <two/>\n" +
                "</four>";
        assertBothWays(four, expected);
    }

    public void testAllowsAttributeWithKnownConverterAndFieldName() throws Exception {
        Three three = new Three();
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        three.date = format.parse("19/02/2006");

        xstream.alias("three", Three.class);
        xstream.useAttributeFor("date", Date.class);
        
        String expected =
            "<three date=\"2006-02-19 00:00:00.0 UTC\"/>";
        assertBothWays(three, expected);
    }

    public void testAllowsAttributeWithArbitraryFieldType() {
        One one = new One();
        one.two = new Two();
        one.id  = new ID("hullo");

        xstream.alias("one", One.class);
        xstream.useAttributeFor(ID.class);
        xstream.registerConverter(new MyIDConverter());

        String expected =
                "<one id=\"hullo\">\n" +
                "  <two/>\n" +
                "</one>";
        assertBothWays(one, expected);
    }

    public void testDoesNotAllowAttributeWithNullAttribute() {
        One one = new One();
        one.two = new Two();

        xstream.alias("one", One.class);
        xstream.useAttributeFor(ID.class);
        xstream.registerConverter(new MyIDConverter());

        String expected =
                "<one>\n" +
                "  <two/>\n" +
                "</one>";
        assertBothWays(one, expected);
    }    
    
    public void testAllowsAttributeToBeAliased() {
        One one = new One();
        one.two = new Two();
        one.id  = new ID("hullo");

        xstream.alias("one", One.class);
        xstream.aliasAttribute("id-alias", "id");
        xstream.useAttributeFor("id", ID.class);        
        xstream.registerConverter(new MyIDConverter());

        String expected =
                "<one id-alias=\"hullo\">\n" +
                "  <two/>\n" +
                "</one>";
        assertBothWays(one, expected);
    }
    
    public void testCanHandleNullValues() {
        C c = new C(null, null, 0);
        xstream.alias("C", C.class);
        xstream.useAttributeFor(Date.class);
        xstream.useAttributeFor(String.class);
        String expected =
            "<C>\n" +
            "  <i>0</i>\n" +
            "</C>";
        assertBothWays(c, expected);
    }
    
    public void testCanHandlePrimitiveValues() {
        C c = new C(null, null, 0);
        xstream.alias("C", C.class);
        xstream.useAttributeFor(Date.class);
        xstream.useAttributeFor(String.class);
        xstream.useAttributeFor(int.class);
        String expected ="<C i=\"0\"/>";
        assertBothWays(c, expected);
    }

    static class Name {
        private String name;
        Name() {
            // for JDK 1.3
        }
        Name(String name) {
            this.name = name;
        }
    }
    
    static class Camera {
        private String name;
        protected Name n;

        Camera() {
            // for JDK 1.3
        }
        
        Camera(String name) {
            this.name = name;
        }
    }
    
    public void testAllowsAnAttributeForASpecificField() {
    	xstream.alias("camera", Camera.class);
    	xstream.useAttributeFor(Camera.class, "name");
    	Camera camera = new Camera("Rebel 350");
        camera.n = new Name("foo");
    	String expected = "" +
    			"<camera name=\"Rebel 350\">\n" +
    			"  <n>\n" +
    			"    <name>foo</name>\n" +
    			"  </n>\n" +
    			"</camera>";
    	assertBothWays(camera, expected);
    }

    public void testAllowsAnAttributeForASpecificAliasedField() {
    	xstream.alias("camera", Camera.class);
    	xstream.aliasAttribute(Camera.class, "name", "model");
    	Camera camera = new Camera("Rebel 350");
        camera.n = new Name("foo");
        String expected = "" +
            "<camera model=\"Rebel 350\">\n" +
            "  <n>\n" +
            "    <name>foo</name>\n" +
            "  </n>\n" +
            "</camera>";
    	assertBothWays(camera, expected);
    }
    
    static class PersonalizedCamera extends Camera {
        private String owner;

        PersonalizedCamera() {
            // for JDK 1.3
        }
        
        PersonalizedCamera(String name, String owner) {
            super(name);
            this.owner = owner;
        }
    }
    
    public void testAllowsAnAttributeForASpecificFieldInASuperClass() {
        xstream.alias("camera", PersonalizedCamera.class);
        xstream.useAttributeFor(Camera.class, "name");
        PersonalizedCamera camera = new PersonalizedCamera("Rebel 350", "Guilherme");
        camera.n = new Name("foo");
        String expected = "" +
        		"<camera name=\"Rebel 350\">\n" +
                        "  <n>\n" +
                        "    <name>foo</name>\n" +
                        "  </n>\n" +
                        "  <owner>Guilherme</owner>\n" +
        		"</camera>";
        assertBothWays(camera, expected);
    }
    
    public void testAllowsAnAttributeForAFieldOfASpecialTypeAlsoInASuperClass() {
        xstream.alias("camera", PersonalizedCamera.class);
        xstream.useAttributeFor("name", String.class);
        PersonalizedCamera camera = new PersonalizedCamera("Rebel 350", "Guilherme");
        camera.n = new Name("foo");
        String expected = "" +
                        "<camera name=\"Rebel 350\">\n" +
                        "  <n name=\"foo\"/>\n" +
                        "  <owner>Guilherme</owner>\n" +
                        "</camera>";
        assertBothWays(camera, expected);
    }

    public static class TransientIdField {
        transient String id;
        String name;

        public TransientIdField() {
            // for JDK 1.3
        }

        public TransientIdField(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public boolean equals(Object obj) {
            return name.equals(((TransientIdField)obj).name);
        }
    }

    public void testAttributeNamedLikeTransientFieldDoesNotAbortDeserializationOfFollowingFields() {
        xstream.setMode(XStream.ID_REFERENCES);
        xstream.alias("transient", TransientIdField.class);

        TransientIdField field = new TransientIdField("foo", "test");
        String xml = "" // 
            + "<transient id=\"1\">\n" // 
            + "  <name>test</name>\n" // 
            + "</transient>";

        assertBothWays(field, xml);
    }
    
    static class Person {
        String _name;
        int _age;
        Person() {} // JDK 1.3
        Person(String name, int age) {
            this._name = name;
            this._age = age;
        }
    };
    
    public void testAttributeMayHaveXmlUnfriendlyName() {
        xstream.alias("person", Person.class);
        xstream.useAttributeFor(Person.class, "_name");
        xstream.useAttributeFor(Person.class, "_age");
        Person person = new Person("joe", 25);
        String xml = "<person __name=\"joe\" __age=\"25\"/>";
        assertBothWays(person, xml);
    }
}
