package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.XStream;

public class IDCircularReferenceTest extends AbstractCircularReferenceTest {

    // inherits test from superclass
    protected void setUp() throws Exception {
        super.setUp();
        xstream.setMode(XStream.ID_REFERENCES);
    }

    public void testCircularReferenceXml() {
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

        assertEquals(expected, xstream.toXML(bob));
    }

    public void testCircularReferenceToSelfXml() {
        Person bob = new Person("bob");
        bob.likes = bob;

        String expected = "" +
                "<person id=\"1\">\n" +
                "  <firstname>bob</firstname>\n" +
                "  <likes reference=\"1\"/>\n" +
                "</person>";

        assertEquals(expected, xstream.toXML(bob));
    }

}
