package com.thoughtworks.xstream.core;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.acceptance.StandardObject;
import com.thoughtworks.acceptance.someobjects.WithList;
import com.thoughtworks.xstream.XStream;

import java.util.ArrayList;
import java.util.List;

public class ReferenceByXPathMarshallingStrategyTest extends AbstractAcceptanceTest {

    protected void setUp() throws Exception {
        super.setUp();
        xstream.setMode(XStream.XPATH_REFERENCES);
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

    public void testStoresReferencesUsingXPath() {
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

    static class WithNamedList extends WithList {
        private final String name;

        public WithNamedList(final String name) {
            this.name = name;
        }
    }

    // @TODO: XSTR-283
    public void TODOtestReferenceByXPathWorksWithReferencedImplicitCollection() {
        xstream.alias("strings", WithNamedList.class);
        xstream.addImplicitCollection(WithNamedList.class, "things");
        WithNamedList[] wls = new WithNamedList[]{new WithNamedList("foo"), new WithNamedList("bar")};
        wls[0].things.add("Hello");
        wls[0].things.add("Daniel");
        wls[1].things = wls[0].things;

        final String expected =
               "<strings-array>\n"
            + "  <strings>\n"
            + "    <string>Hello</string>\n"
            + "    <string>Daniel</string>\n"
            + "  </strings>\n"
            + "  <strings reference=\"../strings\"/>\n"
            + "</strings-array>";

        final WithNamedList[] serialized = (WithNamedList[])assertBothWays(wls, expected);
        assertSame(serialized[0].things, serialized[1].things);
        assertEquals("foo", serialized[0].name);
        assertEquals("bar", serialized[1].name);
    }

}
