package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.XStream;
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

        String expectedXml = "" +
                "<thing>\n" +
                "  <sometimesIgnore>b</sometimesIgnore>\n" +
                "  <neverIgnore>c</neverIgnore>\n" +
                "</thing>";

        xstream.alias("thing", Thing.class);

        String actualXml = xstream.toXML(in);
        assertEquals(expectedXml, actualXml);

        Thing out = (Thing) xstream.fromXML(actualXml);
        assertEquals(null, out.alwaysIgnore);
        assertEquals("b", out.sometimesIgnore);
        assertEquals("c", out.neverIgnore);
    }

    public void testAdditionalFieldsCanBeExplicitlyOmittedThroughFacade() {
        Thing in = new Thing();
        in.alwaysIgnore = "a";
        in.sometimesIgnore = "b";
        in.neverIgnore = "c";

        String expectedXml = "" +
                "<thing>\n" +
                "  <neverIgnore>c</neverIgnore>\n" +
                "</thing>";

        xstream.alias("thing", Thing.class);
        xstream.omitField(Thing.class, "sometimesIgnore");

        String actualXml = xstream.toXML(in);
        assertEquals(expectedXml, actualXml);

        Thing out = (Thing) xstream.fromXML(actualXml);
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

        String expectedXml = "" +
                "<thing>\n" +
                "  <derived>d</derived>\n" +
                "  <neverIgnore>c</neverIgnore>\n" +
                "</thing>";

        xstream.alias("thing", DerivedThing.class);
        xstream.omitField(Thing.class, "sometimesIgnore");

        String actualXml = xstream.toXML(in);
        assertEquals(expectedXml, actualXml);

        DerivedThing out = (DerivedThing) xstream.fromXML(actualXml);
        assertEquals(null, out.alwaysIgnore);
        assertEquals(null, out.sometimesIgnore);
        assertEquals("c", out.neverIgnore);
        assertEquals("d", out.derived);
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

        String expectedXml = "" +
                "<thing>\n" +
                "  <stuff>a</stuff>\n" +
                "  <cheese>b</cheese>\n" +
                "</thing>";

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

        xstream.alias("thing", AnotherThing.class);

        String actualXml = xstream.toXML(in);
        assertEquals(expectedXml, actualXml);

        AnotherThing out = (AnotherThing) xstream.fromXML(actualXml);
        assertEquals("a", out.stuff);
        assertEquals("b", out.cheese);
        assertEquals(null, out.myStuff);
        assertEquals(null, out.myCheese);
    }
}
