package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.XStream;

import java.util.ArrayList;
import java.util.List;

public class DuplicateReferenceTest extends AbstractAcceptanceTest {

    protected void setUp() throws Exception {
        super.setUp();
        xstream.setMode(XStream.ID_REFERENCES);
        xstream.alias("thing", Thing.class);
    }

    public void testReferencesAreWrittenToXml() {

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

        String xml = xstream.toXML(list);

        assertEquals(expected, xml);

        List result = (List) xstream.fromXML(xml);

        assertEquals(list, result);
    }

    public void testReferencesAreTheSameObjectWhenDeserialized() {

        Thing sameThing = new Thing("hello");
        Thing anotherThing = new Thing("hello");

        List list = new ArrayList();
        list.add(sameThing);
        list.add(sameThing);
        list.add(anotherThing);

        String xml = xstream.toXML(list);
        List result = (List) xstream.fromXML(xml);

        Thing t0 = (Thing) result.get(0);
        Thing t1 = (Thing) result.get(1);
        Thing t2 = (Thing) result.get(2);

        t0.field = "bye";

        assertEquals("bye", t0.field);
        assertEquals("bye", t1.field);
        assertEquals("hello", t2.field);

    }

    class Thing extends StandardObject {
        public String field;

        public Thing(String field) {
            this.field = field;
        }
    }

}
