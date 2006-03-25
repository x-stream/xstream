package com.thoughtworks.xstream.core;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.acceptance.someobjects.WithNamedList;
import com.thoughtworks.xstream.XStream;

import java.util.ArrayList;


public class ReferenceByIDMarshallingStrategyTest extends AbstractAcceptanceTest {

    protected void setUp() throws Exception {
        super.setUp();
        xstream.setMode(XStream.ID_REFERENCES);
    }

    public void testIgnoresImplicitCollection() {
        xstream.alias("strings", WithNamedList.class);
        xstream.addImplicitCollection(WithNamedList.class, "things");
        WithNamedList wl = new WithNamedList("foo");
        wl.things.add("Hello");
        wl.things.add("Daniel");

        final String expected = "<strings id=\"1\">\n"
                + "  <name>foo</name>\n"
                + "  <string>Hello</string>\n"
                + "  <string>Daniel</string>\n"
                + "</strings>";

        assertBothWays(wl, expected);
    }

    static class List { 
        public Object o; 
        public ArrayList list = new ArrayList(); 
    } 
    
    public void testIgnoresImplicitCollectionAtAnyFieldPosition() {
        final List another = new List(); 
        another.o = new Object(); 
        another.list.add(new Object()); 
        xstream.addImplicitCollection(List.class, "list"); 
        xstream.alias("list", List.class);
        
        final String expected = "<list id=\"1\">\n"
            + "  <o id=\"2\"/>\n"
            + "  <object id=\"4\"/>\n"
            + "</list>";
        
        assertBothWays(another, expected);
    }
}
