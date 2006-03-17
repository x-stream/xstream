package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.XStream;

public class AbsoluteXPathCircularReferenceTest extends AbstractCircularReferenceTest {

    // inherits test from superclass
    protected void setUp() throws Exception {
        super.setUp();
        xstream.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);
    }

    public void testCircularReferenceXml() {
        Person bob = new Person("bob");
        Person jane = new Person("jane");
        bob.likes = jane;
        jane.likes = bob;

        String expected = "" +
                "<person>\n" +
                "  <firstname>bob</firstname>\n" +
                "  <likes>\n" +
                "    <firstname>jane</firstname>\n" +
                "    <likes reference=\"/person\"/>\n" +
                "  </likes>\n" +
                "</person>";

        assertEquals(expected, xstream.toXML(bob));
    }

    public void testCircularReferenceToSelfXml() {
        Person bob = new Person("bob");
        bob.likes = bob;

        String expected = "" +
                "<person>\n" +
                "  <firstname>bob</firstname>\n" +
                "  <likes reference=\"/person\"/>\n" +
                "</person>";

        assertEquals(expected, xstream.toXML(bob));
    }

}
