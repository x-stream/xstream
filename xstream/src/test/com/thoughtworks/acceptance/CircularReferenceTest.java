package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.XStream;

public class CircularReferenceTest extends AbstractAcceptanceTest {

    protected void setUp() throws Exception {
        super.setUp();
        xstream.setMode(XStream.ID_REFERENCES);
        xstream.alias("person", Person.class);
    }

    public void testCircularReference() {
        Person bob = new Person("bob");
        Person jane = new Person("jane");
        bob.likes = jane;
        jane.likes = bob;

        String expected = "" +
                "<person id=\"1\">\n" +
                "  <firstname>bob</firstname>\n" +
                "  <likes id=\"2\">\n" +
                "    <firstname>jane</firstname>\n" +
                "    <likes reference=\"1\"/>\n" +
                "  </likes>\n" +
                "</person>";

        String xml = xstream.toXML(bob);
        assertEquals(expected, xml);

        Person bobOut = (Person) xstream.fromXML(xml);
        assertEquals("bob", bobOut.firstname);
        Person janeOut = bobOut.likes;

        assertEquals("jane", janeOut.firstname);

        assertSame(bobOut.likes, janeOut);
        assertSame(bobOut, janeOut.likes);
    }

    public void testCircularReferenceToSelf() {
        Person bob = new Person("bob");
        bob.likes = bob;

        String expected = "" +
                "<person id=\"1\">\n" +
                "  <firstname>bob</firstname>\n" +
                "  <likes reference=\"1\"/>\n" +
                "</person>";

        String xml = xstream.toXML(bob);
        assertEquals(expected, xml);

        Person bobOut = (Person) xstream.fromXML(xml);
        assertEquals("bob", bobOut.firstname);
        assertSame(bobOut, bobOut.likes);
    }

    public void testDeepCircularReferences() {
        Person bob = new Person("bob");
        Person jane = new Person("jane");
        Person ann = new Person("ann");
        Person poo = new Person("poo");

        bob.likes = jane;
        bob.loathes = ann;
        ann.likes = jane;
        ann.loathes = poo;
        poo.likes = jane;
        poo.loathes = ann;

        String xml = xstream.toXML(bob);
        Person bobOut = (Person) xstream.fromXML(xml);
        Person janeOut = bobOut.likes;
        Person annOut = bobOut.loathes;
        Person pooOut = annOut.loathes;

        assertEquals("bob", bobOut.firstname);
        assertEquals("jane", janeOut.firstname);
        assertEquals("ann", annOut.firstname);
        assertEquals("poo", pooOut.firstname);

        assertSame(janeOut, bobOut.likes);
        assertSame(annOut, bobOut.loathes);
        assertSame(janeOut, annOut.likes);
        assertSame(pooOut, annOut.loathes);
        assertSame(janeOut, pooOut.likes);
        assertSame(annOut, pooOut.loathes);
    }

    class Person {
        public String firstname;
        public Person likes;
        public Person loathes;

        public Person(String name) {
            this.firstname = name;
        }
    }

}
