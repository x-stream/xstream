package com.thoughtworks.acceptance;

public class CustomClassesTest extends AbstractAcceptanceTest {

    class SamplePerson extends StandardObject {
        int anInt;
        String firstName;
        String lastName;
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

    class SamplePersonHolder {
        String aString;
        SamplePerson brother;

        public boolean equals(Object obj) {
            SamplePersonHolder containerObject = (SamplePersonHolder) obj;
            return aString.equals(containerObject.aString)
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

    public class WithSomeFields extends StandardObject {
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

    class FieldWithObjectType extends StandardObject {
        Double one = new Double(1.0);
        Object two = new Double(2.0);
    }
}
