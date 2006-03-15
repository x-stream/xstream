package com.thoughtworks.xstream.core;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.acceptance.someobjects.WithNamedList;
import com.thoughtworks.xstream.XStream;


public class ReferenceByIDMarshallingStrategyTest extends AbstractAcceptanceTest {

    protected void setUp() throws Exception {
        super.setUp();
        xstream.setMode(XStream.ID_REFERENCES);
    }

    public void testReferenceByIDIgnoresImplicitCollection() {
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

}
