package com.thoughtworks.acceptance;

public class CustomClassesTest extends AbstractAcceptanceTest {

    class SamplePerson {
        int anInt;
        String firstName;
        String lastName;

        public boolean equals(Object obj) {
            SamplePerson samplePerson = (SamplePerson) obj;
            return anInt == samplePerson.anInt
                    && firstName.equals(samplePerson.firstName)
                    && lastName.equals(samplePerson.lastName);
        }
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


}
