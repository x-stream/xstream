package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.objects.Hardware;
import com.thoughtworks.acceptance.objects.SampleLists;
import com.thoughtworks.acceptance.objects.Software;

import java.util.ArrayList;
import java.util.List;

public class ListTest extends AbstractAcceptanceTest {

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
                "  <good>\n" +
                "    <software>\n" +
                "      <vendor>apache</vendor>\n" +
                "      <name>geronimo</name>\n" +
                "    </software>\n" +
                "    <software>\n" +
                "      <vendor>caucho</vendor>\n" +
                "      <name>resin</name>\n" +
                "    </software>\n" +
                "    <hardware>\n" +
                "      <arch>risc</arch>\n" +
                "      <name>strong-arm</name>\n" +
                "    </hardware>\n" +
                "  </good>\n" +
                "  <bad>\n" +
                "    <software>\n" +
                "      <vendor>apache</vendor>\n" +
                "      <name>jserv</name>\n" +
                "    </software>\n" +
                "  </bad>\n" +
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
                "  <good>\n" +
                "    <string>hello</string>\n" +
                "    <int>3</int>\n" +
                "    <boolean>true</boolean>\n" +
                "  </good>\n" +
                "  <bad>\n" +
                "  </bad>\n" +
                "</lists>";

        assertBothWays(lists, expected);
    }

    public void testListCanBeRootObject() {
        List list = new ArrayList();
        list.add("hi");
        list.add("bye");

        String expected = "" +
                "<list>\n" +
                "  <string>hi</string>\n" +
                "  <string>bye</string>\n" +
                "</list>";

        assertBothWays(list, expected);
    }
}
