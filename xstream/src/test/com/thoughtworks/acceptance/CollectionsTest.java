package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.objects.Hardware;
import com.thoughtworks.acceptance.objects.SampleLists;
import com.thoughtworks.acceptance.objects.Software;
import com.thoughtworks.xstream.XStream;

import java.util.*;

public class CollectionsTest extends AbstractAcceptanceTest {

    public void testListsCanContainCustomObjects() {
        SampleLists lists = new SampleLists();
        lists.good.add(new Software("apache", "geronimo"));
        lists.good.add(new Software("caucho", "resin"));
        lists.good.add(new Hardware("risc", "strong-arm"));
        lists.bad.add(new Software("apache", "jserv"));

        xstream.alias("lists", SampleLists.class);
        xstream.alias("software", Software.class);
        xstream.alias("hardware", Hardware.class);

        String expected = "" +
                "<lists>\n" +
                "  <bad class=\"list\">\n" +
                "    <software>\n" +
                "      <name>jserv</name>\n" +
                "      <vendor>apache</vendor>\n" +
                "    </software>\n" +
                "  </bad>\n" +
                "  <good>\n" +
                "    <software>\n" +
                "      <name>geronimo</name>\n" +
                "      <vendor>apache</vendor>\n" +
                "    </software>\n" +
                "    <software>\n" +
                "      <name>resin</name>\n" +
                "      <vendor>caucho</vendor>\n" +
                "    </software>\n" +
                "    <hardware>\n" +
                "      <arch>risc</arch>\n" +
                "      <name>strong-arm</name>\n" +
                "    </hardware>\n" +
                "  </good>\n" +
                "</lists>";

        assertBothWays(lists, expected);
    }

    public void testListsCanContainBasicObjects() {
        SampleLists lists = new SampleLists();
        lists.good.add("hello");
        lists.good.add(new Integer(3));
        lists.good.add(Boolean.TRUE);

        xstream.alias("lists", SampleLists.class);

        String expected = "" +
                "<lists>\n" +
                "  <bad class=\"list\"/>\n" +
                "  <good>\n" +
                "    <string>hello</string>\n" +
                "    <int>3</int>\n" +
                "    <boolean>true</boolean>\n" +
                "  </good>\n" +
                "</lists>";

        assertBothWays(lists, expected);
    }

    public void testListCanBeRootObject() {
        Collection list = new ArrayList();
        list.add("hi");
        list.add("bye");

        String expected = "" +
                "<list>\n" +
                "  <string>hi</string>\n" +
                "  <string>bye</string>\n" +
                "</list>";

        assertBothWays(list, expected);
    }

    public void testSetCanBeRootObject() {
        Collection set = new HashSet();
        set.add("hi");
        set.add("bye");

        String expected = "" +
                "<set>\n" +
                "  <string>hi</string>\n" +
                "  <string>bye</string>\n" +
                "</set>";

        assertBothWays(set, expected);
    }

    public void testVector() {
        Vector vector = new Vector();
        vector.addElement("a");
        vector.addElement("b");

        assertBothWays(vector,
                "<vector>\n" +
                "  <string>a</string>\n" +
                "  <string>b</string>\n" +
                "</vector>");
    }

    public void testSyncronizedWrapper() {
        // syncronized list has circular reference
        xstream.setMode(XStream.XPATH_REFERENCES);

        List list = Collections.synchronizedList(new LinkedList());
        list.add("hi");

        assertBothWays(list,
                "<java.util.Collections-SynchronizedList>\n" +
                "  <c class=\"linked-list\">\n" +
                "    <string>hi</string>\n" +
                "  </c>\n" +
                "  <list class=\"linked-list\" reference=\"../c\"/>\n" +
                "  <mutex class=\"java.util.Collections-SynchronizedList\" reference=\"..\"/>\n" +
                "</java.util.Collections-SynchronizedList>");
    }

    public void testEmptyList() {
        assertBothWays(Collections.EMPTY_LIST, "<java.util.Collections-EmptyList/>");
    }

    public void testUnmodifiableList() {
        // unodifiable list has duplicate refs
        xstream.setMode(XStream.XPATH_REFERENCES);

        List list = new ArrayList();
        list.add("hi");
        list = Collections.unmodifiableList(list);

        assertBothWays(list,
                "<java.util.Collections-UnmodifiableRandomAccessList>\n" +
                "  <c class=\"list\">\n" +
                "    <string>hi</string>\n" +
                "  </c>\n" +
                "  <list reference=\"../c\"/>\n" +
                "</java.util.Collections-UnmodifiableRandomAccessList>");
    }
}
