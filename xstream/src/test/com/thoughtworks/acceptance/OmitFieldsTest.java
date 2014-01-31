/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2010, 2012, 2013, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 20. June 2005 by Joe Walnes
 */
package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.objects.StandardObject;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;


public class OmitFieldsTest extends AbstractAcceptanceTest {

    public static class Thing extends StandardObject {
        transient String alwaysIgnore;
        String sometimesIgnore;
        String neverIgnore;
    }

    public void testTransientFieldsAreOmittedByDefault() {
        Thing in = new Thing();
        in.alwaysIgnore = "a";
        in.sometimesIgnore = "b";
        in.neverIgnore = "c";

        String expectedXml = ""
            + "<thing>\n"
            + "  <sometimesIgnore>b</sometimesIgnore>\n"
            + "  <neverIgnore>c</neverIgnore>\n"
            + "</thing>";

        xstream.alias("thing", Thing.class);

        String actualXml = xstream.toXML(in);
        assertEquals(expectedXml, actualXml);

        Thing out = (Thing)xstream.fromXML(actualXml);
        assertEquals(null, out.alwaysIgnore);
        assertEquals("b", out.sometimesIgnore);
        assertEquals("c", out.neverIgnore);
    }

    public void testAdditionalFieldsCanBeExplicitlyOmitted() {
        Thing in = new Thing();
        in.alwaysIgnore = "a";
        in.sometimesIgnore = "b";
        in.neverIgnore = "c";

        String expectedXml = "" // 
            + "<thing>\n" // 
            + "  <neverIgnore>c</neverIgnore>\n" // 
            + "</thing>";

        xstream.alias("thing", Thing.class);
        xstream.omitField(Thing.class, "sometimesIgnore");

        String actualXml = xstream.toXML(in);
        assertEquals(expectedXml, actualXml);

        Thing out = (Thing)xstream.fromXML(actualXml);
        assertEquals(null, out.alwaysIgnore);
        assertEquals(null, out.sometimesIgnore);
        assertEquals("c", out.neverIgnore);
    }

    public static class DerivedThing extends Thing {
        String derived;
    }

    public void testInheritedFieldsCanBeExplicitlyOmittedThroughFacade() {
        DerivedThing in = new DerivedThing();
        in.alwaysIgnore = "a";
        in.sometimesIgnore = "b";
        in.neverIgnore = "c";
        in.derived = "d";

        String expectedXml = ""
            + "<thing>\n"
            + "  <neverIgnore>c</neverIgnore>\n"
            + "  <derived>d</derived>\n"
            + "</thing>";

        xstream.alias("thing", DerivedThing.class);
        xstream.omitField(Thing.class, "sometimesIgnore");

        String actualXml = xstream.toXML(in);
        assertEquals(expectedXml, actualXml);

        DerivedThing out = (DerivedThing)xstream.fromXML(actualXml);
        assertEquals(null, out.alwaysIgnore);
        assertEquals(null, out.sometimesIgnore);
        assertEquals("c", out.neverIgnore);
        assertEquals("d", out.derived);
    }

    public void testFieldsOfDerivedTypesCanBeExplicitlyOmittedThroughFacade() {
        DerivedThing in = new DerivedThing();
        in.alwaysIgnore = "a";
        in.sometimesIgnore = "b";
        in.neverIgnore = "c";
        in.derived = "d";

        String expectedXml = ""
            + "<thing>\n"
            + "  <sometimesIgnore>b</sometimesIgnore>\n"
            + "  <neverIgnore>c</neverIgnore>\n"
            + "</thing>";

        xstream.alias("thing", DerivedThing.class);
        xstream.omitField(DerivedThing.class, "derived");

        String actualXml = xstream.toXML(in);
        assertEquals(expectedXml, actualXml);

        DerivedThing out = (DerivedThing)xstream.fromXML(actualXml);
        assertEquals(null, out.alwaysIgnore);
        assertEquals("b", out.sometimesIgnore);
        assertEquals("c", out.neverIgnore);
        assertEquals(null, out.derived);
    }

    public static class AnotherThing extends StandardObject {
        String stuff;
        String cheese;
        String myStuff;
        String myCheese;
    }

    public void testFieldsCanBeIgnoredUsingCustomStrategy() {
        AnotherThing in = new AnotherThing();
        in.stuff = "a";
        in.cheese = "b";
        in.myStuff = "c";
        in.myCheese = "d";

        String expectedXml = ""
            + "<thing>\n"
            + "  <stuff>a</stuff>\n"
            + "  <cheese>b</cheese>\n"
            + "</thing>";

        class OmitFieldsWithMyPrefixMapper extends MapperWrapper {
            public OmitFieldsWithMyPrefixMapper(Mapper wrapped) {
                super(wrapped);
            }

            public boolean shouldSerializeMember(Class definedIn, String fieldName) {
                return !fieldName.startsWith("my");
            }
        }

        xstream = new XStream() {
            protected MapperWrapper wrapMapper(MapperWrapper next) {
                return new OmitFieldsWithMyPrefixMapper(next);
            }
        };

        xstream.allowTypes(new Class[]{AnotherThing.class});
        xstream.alias("thing", AnotherThing.class);

        String actualXml = xstream.toXML(in);
        assertEquals(expectedXml, actualXml);

        AnotherThing out = (AnotherThing)xstream.fromXML(actualXml);
        assertEquals("a", out.stuff);
        assertEquals("b", out.cheese);
        assertEquals(null, out.myStuff);
        assertEquals(null, out.myCheese);
    }

    public void testDeletedElementCanBeOmitted() {
        String expectedXml = ""
            + "<thing>\n"
            + "  <meanwhileDeletedIgnore>c</meanwhileDeletedIgnore>\n"
            + "  <sometimesIgnore>b</sometimesIgnore>\n"
            + "  <neverIgnore>c</neverIgnore>\n"
            + "</thing>";

        xstream.alias("thing", Thing.class);
        xstream.omitField(Thing.class, "meanwhileDeletedIgnore");

        Thing out = (Thing)xstream.fromXML(expectedXml);
        assertEquals("b", out.sometimesIgnore);
        assertEquals("c", out.neverIgnore);
    }

    public void testDeletedElementWithReferenceCanBeOmitted() {
        String expectedXml = ""
            + "<thing>\n"
            + "  <meanwhileDeletedIgnore reference=\"..\"/>\n"
            + "  <sometimesIgnore>b</sometimesIgnore>\n"
            + "  <neverIgnore>c</neverIgnore>\n"
            + "</thing>";

        xstream.alias("thing", Thing.class);
        xstream.omitField(Thing.class, "meanwhileDeletedIgnore");

        Thing out = (Thing)xstream.fromXML(expectedXml);
        assertEquals("b", out.sometimesIgnore);
        assertEquals("c", out.neverIgnore);
    }

    public void testDeletedElementWithClassAttributeCanBeOmitted() {
        String expectedXml = ""
            + "<thing>\n"
            + "  <meanwhileDeletedIgnore class=\"thing\"/>\n"
            + "  <sometimesIgnore>b</sometimesIgnore>\n"
            + "  <neverIgnore>c</neverIgnore>\n"
            + "</thing>";

        xstream.alias("thing", Thing.class);
        xstream.omitField(Thing.class, "meanwhileDeletedIgnore");

        Thing out = (Thing)xstream.fromXML(expectedXml);
        assertEquals("b", out.sometimesIgnore);
        assertEquals("c", out.neverIgnore);
    }

    public void testDeletedAttributeCanBeOmitted() {
        String expectedXml = ""
            + "<thing meanwhileDeletedIgnore='c'>\n"
            + "  <sometimesIgnore>b</sometimesIgnore>\n"
            + "  <neverIgnore>c</neverIgnore>\n"
            + "</thing>";

        xstream.alias("thing", Thing.class);
        xstream.omitField(Thing.class, "meanwhileDeletedIgnore");

        Thing out = (Thing)xstream.fromXML(expectedXml);
        assertEquals("b", out.sometimesIgnore);
        assertEquals("c", out.neverIgnore);
    }

    public void testAttributeCanBeOmitted() {
        String expectedXml = "<thing neverIgnore=\"c\"/>";

        xstream.alias("thing", Thing.class);
        xstream.useAttributeFor(String.class);
        xstream.omitField(Thing.class, "sometimesIgnore");

        Thing in = new Thing();
        in.alwaysIgnore = "a";
        in.sometimesIgnore = "b";
        in.neverIgnore = "c";
        assertEquals(expectedXml, xstream.toXML(in));

        Thing out = (Thing)xstream.fromXML(expectedXml);
        assertNull(out.sometimesIgnore);
        assertEquals("c", out.neverIgnore);
    }

    public void testExistingFieldsCanBeExplicitlyOmittedAtDeserialization() {
        String actualXml = ""
            + "<thing>\n"
            + "  <sometimesIgnore>foo</sometimesIgnore>\n" 
            + "  <neverIgnore>c</neverIgnore>\n" 
            + "</thing>";

        xstream.alias("thing", Thing.class);
        xstream.omitField(Thing.class, "sometimesIgnore");

        Thing out = (Thing)xstream.fromXML(actualXml);
        assertEquals(null, out.alwaysIgnore);
        assertEquals(null, out.sometimesIgnore);
        assertEquals("c", out.neverIgnore);
    }

    public void testExistingFieldsInDerivedClassesCanBeExplicitlyOmittedAtDeserialization() {
        String actualXml = ""
            + "<thing>\n"
            + "  <sometimesIgnore>b</sometimesIgnore>\n" 
            + "  <neverIgnore>c</neverIgnore>\n" 
            + "  <derived>foo</derived>\n" 
            + "</thing>";

        xstream.alias("thing", DerivedThing.class);
        xstream.omitField(DerivedThing.class, "derived");

        DerivedThing out = (DerivedThing)xstream.fromXML(actualXml);
        assertEquals(null, out.alwaysIgnore);
        assertEquals("b", out.sometimesIgnore);
        assertEquals("c", out.neverIgnore);
        assertEquals(null, out.derived);
    }

    public void testExistingInheritedFieldsCanBeExplicitlyOmittedAtDeserialization() {
        String actualXml = ""
            + "<thing>\n"
            + "  <sometimesIgnore>foo</sometimesIgnore>\n" 
            + "  <neverIgnore>c</neverIgnore>\n" 
            + "  <derived>d</derived>\n" 
            + "</thing>";

        xstream.alias("thing", DerivedThing.class);
        xstream.omitField(Thing.class, "sometimesIgnore");

        DerivedThing out = (DerivedThing)xstream.fromXML(actualXml);
        assertEquals(null, out.alwaysIgnore);
        assertEquals(null, out.sometimesIgnore);
        assertEquals("c", out.neverIgnore);
        assertEquals("d", out.derived);
    }

    public void testIgnoreUnknownElementsMatchingPattern() {
        String actualXml = ""
            + "<thing>\n"
            + "  <sometimesIgnore>foo</sometimesIgnore>\n" 
            + "  <neverIgnore>c</neverIgnore>\n" 
            + "  <foobar>f</foobar>\n" 
            + "  <derived>d</derived>\n" 
            + "</thing>";

        xstream.alias("thing", DerivedThing.class);
        xstream.omitField(Thing.class, "sometimesIgnore");
        xstream.ignoreUnknownElements("foo.*");

        DerivedThing out = (DerivedThing)xstream.fromXML(actualXml);
        assertEquals(null, out.alwaysIgnore);
        assertEquals(null, out.sometimesIgnore);
        assertEquals("c", out.neverIgnore);
        assertEquals("d", out.derived);
        
        try {
            xstream.fromXML(actualXml.replaceAll("foobar", "unknown"));
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            String message = e.getMessage();
            assertTrue(message,
                e.getMessage().substring(0, message.indexOf('\n')).endsWith(
                    DerivedThing.class.getName() + ".unknown"));
        }
    }
    
    public void testIgnoreNonExistingElementsMatchingTypeAlias() {
        xstream.alias("thing", Thing.class);
        xstream.ignoreUnknownElements("string");
        Thing thing = new Thing();
        String provided = "" 
            + "<thing>\n" 
            + "  <string>string 1</string>\n" 
            + "</thing>";
        String expected = "<thing/>";
        assertWithAsymmetricalXml(thing, provided, expected);
    }
    
    public void testIgnoredElementIsNotInstantiated() {
        xstream.alias("thing", Thing.class);
        xstream.ignoreUnknownElements("int");
        Thing thing = new Thing();
        String provided = "" 
            + "<thing>\n" 
            + "  <int>invalid</int>\n" 
            + "</thing>";
        String expected = "<thing/>";
        assertWithAsymmetricalXml(thing, provided, expected);
    }

    static class ThingAgain extends Thing {
        String sometimesIgnore;

        void setHidden(String s) {
            super.sometimesIgnore = s;
        }
        
        String getHidden() {
            return super.sometimesIgnore;
        }
    }

    public void testAnOmittedFieldMakesADefinedInAttributeSuperfluous() {
        ThingAgain in = new ThingAgain();
        in.alwaysIgnore = "a";
        in.setHidden("b");
        in.neverIgnore = "c";
        in.sometimesIgnore = "d";

        xstream.alias("thing", ThingAgain.class);
        xstream.omitField(ThingAgain.class, "sometimesIgnore");

        String expectedXml = ""
            + "<thing>\n"
            + "  <sometimesIgnore>b</sometimesIgnore>\n"
            + "  <neverIgnore>c</neverIgnore>\n"
            + "</thing>";

        String actualXml = xstream.toXML(in);
        assertEquals(expectedXml, actualXml);

        ThingAgain out = (ThingAgain)xstream.fromXML(expectedXml);
        assertNull(out.alwaysIgnore);
        assertEquals("b", out.getHidden());
        assertEquals("c", out.neverIgnore);
        assertNull(out.sometimesIgnore);
    }
}
