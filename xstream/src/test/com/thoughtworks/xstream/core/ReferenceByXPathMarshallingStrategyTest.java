package com.thoughtworks.xstream.core;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.acceptance.StandardObject;
import com.thoughtworks.xstream.XStream;

import java.util.ArrayList;
import java.util.List;


public class ReferenceByXPathMarshallingStrategyTest extends AbstractAcceptanceTest {

    protected void setUp() throws Exception {
        super.setUp();
        xstream.alias("thing", Thing.class);
    }

    public static class Thing extends StandardObject {
        private String name;

        public Thing() {
        }

        public Thing(String name) {
            this.name = name;
        }
    }

    public void testStoresReferencesUsingRelativeXPath() {
        xstream.setMode(XStream.XPATH_RELATIVE_REFERENCES);

        Thing a = new Thing("a");
        Thing b = new Thing("b");
        Thing c = b;

        List list = new ArrayList();
        list.add(a);
        list.add(b);
        list.add(c);

        String expected = "" +
                "<list>\n" +
                "  <thing>\n" +
                "    <name>a</name>\n" +
                "  </thing>\n" +
                "  <thing>\n" +
                "    <name>b</name>\n" +
                "  </thing>\n" +
                "  <thing reference=\"../thing[2]\"/>\n" + // xpath
                "</list>";

        assertBothWays(list, expected);
    }

    public void testStoresReferencesUsingAbsoluteXPath() {
        xstream.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);

        Thing a = new Thing("a");
        Thing b = new Thing("b");
        Thing c = b;

        List list = new ArrayList();
        list.add(a);
        list.add(b);
        list.add(c);

        String expected = "" +
                "<list>\n" +
                "  <thing>\n" +
                "    <name>a</name>\n" +
                "  </thing>\n" +
                "  <thing>\n" +
                "    <name>b</name>\n" +
                "  </thing>\n" +
                "  <thing reference=\"/list/thing[2]\"/>\n" + // xpath
                "</list>";

        assertBothWays(list, expected);
    }
}
