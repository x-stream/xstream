package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.XStream;

import java.util.ArrayList;
import java.util.List;

public class RelativeXPathDuplicateReferenceTest extends AbstractDuplicateReferenceTest {

    // tests inherited from superclass

    protected void setUp() throws Exception {
        super.setUp();
        xstream.setMode(XStream.XPATH_RELATIVE_REFERENCES);
    }

    public void testXmlContainsReferencePaths() {

        Thing sameThing = new Thing("hello");
        Thing anotherThing = new Thing("hello");

        List list = new ArrayList();
        list.add(sameThing);
        list.add(sameThing);
        list.add(anotherThing);

        String expected = "" +
                "<list>\n" +
                "  <thing>\n" +
                "    <field>hello</field>\n" +
                "  </thing>\n" +
                "  <thing reference=\"../thing\"/>\n" +
                "  <thing>\n" +
                "    <field>hello</field>\n" +
                "  </thing>\n" +
                "</list>";

        assertEquals(expected, xstream.toXML(list));
    }

}
