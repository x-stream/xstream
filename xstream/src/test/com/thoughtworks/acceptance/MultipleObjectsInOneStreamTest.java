package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppReader;

import java.io.StringReader;
import java.io.StringWriter;

public class MultipleObjectsInOneStreamTest extends AbstractAcceptanceTest {

    public static class Person extends StandardObject {

        private String firstName;
        private String lastName;

        public Person(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

    }

    protected void setUp() throws Exception {
        super.setUp();
        xstream.alias("person", Person.class);
    }

    public void testReadAndWriteMultipleObjectsInOneStream() {
        StringWriter buffer = new StringWriter();

        // serialize
        HierarchicalStreamWriter writer = new PrettyPrintWriter(buffer);
        writer.startNode("people");
        xstream.marshal(new Person("Postman", "Pat"), writer);
        xstream.marshal(new Person("Bob", "Builder"), writer);
        xstream.marshal(new Person("Tinky", "Winky"), writer);
        writer.endNode();

        assertEquals(""
                + "<people>\n"
                + "  <person>\n"
                + "    <firstName>Postman</firstName>\n"
                + "    <lastName>Pat</lastName>\n"
                + "  </person>\n"
                + "  <person>\n"
                + "    <firstName>Bob</firstName>\n"
                + "    <lastName>Builder</lastName>\n"
                + "  </person>\n"
                + "  <person>\n"
                + "    <firstName>Tinky</firstName>\n"
                + "    <lastName>Winky</lastName>\n"
                + "  </person>\n"
                + "</people>",
                buffer.toString());

        // deserialize
        HierarchicalStreamReader reader = new XppReader(new StringReader(buffer.toString()));

        assertTrue("should be another object to read (1)", reader.hasMoreChildren());
        reader.moveDown();
        assertEquals(new Person("Postman", "Pat"), xstream.unmarshal(reader));
        reader.moveUp();

        assertTrue("should be another object to read (2)", reader.hasMoreChildren());
        reader.moveDown();
        assertEquals(new Person("Bob", "Builder"), xstream.unmarshal(reader));
        reader.moveUp();

        assertTrue("should be another object to read (3)", reader.hasMoreChildren());
        reader.moveDown();
        assertEquals(new Person("Tinky", "Winky"), xstream.unmarshal(reader));
        reader.moveUp();

        assertFalse("should be no more objects", reader.hasMoreChildren());
    }
}
