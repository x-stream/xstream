package com.thoughtworks.xstream.core;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.acceptance.StandardObject;

import java.util.ArrayList;
import java.util.List;

public class ReferenceByXPathMarshallingStrategyTest extends AbstractAcceptanceTest {

    protected void setUp() throws Exception {
        super.setUp();
        xstream.setMarshallingStrategy(new ReferenceByXPathMarshallingStrategy());
        xstream.alias("thing", Thing.class);
    }

    class Thing extends StandardObject {
        private String name;

        public Thing(String name) {
            this.name = name;
        }
    }

    public void testX() {
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
