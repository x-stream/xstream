package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.XStream;

import java.util.ArrayList;
import java.util.List;

public class IDDuplicateReferenceTest extends AbstractDuplicateReferenceTest {

    // tests inherited from superclass

    protected void setUp() throws Exception {
        super.setUp();
        xstream.setMode(XStream.ID_REFERENCES);
    }

    public void testXmlContainsReferenceIds() {

        Thing sameThing = new Thing("hello");
        Thing anotherThing = new Thing("hello");

        List list = new ArrayList();
        list.add(sameThing);
        list.add(sameThing);
        list.add(anotherThing);

        String expected = "" +
                "<list id=\"1\">\n" +
                "  <thing id=\"2\">\n" +
                "    <field>hello</field>\n" +
                "  </thing>\n" +
                "  <thing reference=\"2\"/>\n" +
                "  <thing id=\"3\">\n" +
                "    <field>hello</field>\n" +
                "  </thing>\n" +
                "</list>";

        assertEquals(expected, xstream.toXML(list));
    }

}
