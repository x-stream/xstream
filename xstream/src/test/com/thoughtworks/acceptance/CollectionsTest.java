/*
 * Copyright (C) 2003, 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011, 2017, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 01. October 2003 by Joe Walnes
 */
package com.thoughtworks.acceptance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import com.thoughtworks.acceptance.objects.Hardware;
import com.thoughtworks.acceptance.objects.SampleLists;
import com.thoughtworks.acceptance.objects.Software;
import com.thoughtworks.xstream.XStream;


public class CollectionsTest extends AbstractAcceptanceTest {

    public void testListsCanContainCustomObjects() {
        final SampleLists<Object, Object> lists = new SampleLists<>();
        lists.good.add(new Software("apache", "geronimo"));
        lists.good.add(new Software("caucho", "resin"));
        lists.good.add(new Hardware("risc", "strong-arm"));
        lists.bad.add(new Software("apache", "jserv"));

        xstream.alias("lists", SampleLists.class);
        xstream.alias("software", Software.class);
        xstream.alias("hardware", Hardware.class);

        final String expected = ""
            + "<lists>\n"
            + "  <good>\n"
            + "    <software>\n"
            + "      <vendor>apache</vendor>\n"
            + "      <name>geronimo</name>\n"
            + "    </software>\n"
            + "    <software>\n"
            + "      <vendor>caucho</vendor>\n"
            + "      <name>resin</name>\n"
            + "    </software>\n"
            + "    <hardware>\n"
            + "      <arch>risc</arch>\n"
            + "      <name>strong-arm</name>\n"
            + "    </hardware>\n"
            + "  </good>\n"
            + "  <bad class=\"list\">\n"
            + "    <software>\n"
            + "      <vendor>apache</vendor>\n"
            + "      <name>jserv</name>\n"
            + "    </software>\n"
            + "  </bad>\n"
            + "</lists>";

        assertBothWays(lists, expected);
    }

    public void testListsCanContainBasicObjects() {
        final SampleLists<Object, ?> lists = new SampleLists<>();
        lists.good.add("hello");
        lists.good.add(new Integer(3));
        lists.good.add(Boolean.TRUE);

        xstream.alias("lists", SampleLists.class);

        final String expected = ""
            + "<lists>\n"
            + "  <good>\n"
            + "    <string>hello</string>\n"
            + "    <int>3</int>\n"
            + "    <boolean>true</boolean>\n"
            + "  </good>\n"
            + "  <bad class=\"list\"/>\n"
            + "</lists>";

        assertBothWays(lists, expected);
    }

    public void testListCanBeRootObject() {
        final Collection<String> list = new ArrayList<>();
        list.add("hi");
        list.add("bye");

        final String expected = "" //
            + "<list>\n"
            + "  <string>hi</string>\n"
            + "  <string>bye</string>\n"
            + "</list>";

        assertBothWays(list, expected);
    }

    public void testSetCanBeRootObject() {
        final Collection<String> set = new HashSet<>();
        set.add("hi");
        set.add("bye");

        final String expected = "" //
            + "<set>\n"
            + "  <string>hi</string>\n"
            + "  <string>bye</string>\n"
            + "</set>";

        assertBothWaysNormalized(set, expected, "set", "string", null);
    }

    public void testVector() {
        final Vector<String> vector = new Vector<>();
        vector.addElement("a");
        vector.addElement("b");

        assertBothWays(vector, ""//
            + "<vector>\n"
            + "  <string>a</string>\n"
            + "  <string>b</string>\n"
            + "</vector>");
    }

    public void testSyncronizedList() {
        final String xml = ""
            + "<java.util.Collections_-SynchronizedList serialization=\"custom\">\n"
            + "  <java.util.Collections_-SynchronizedCollection>\n"
            + "    <default>\n"
            + "      <c class=\"linked-list\">\n"
            + "        <string>hi</string>\n"
            + "      </c>\n"
            + "      <mutex class=\"java.util.Collections$SynchronizedList\" reference=\"../../..\"/>\n"
            + "    </default>\n"
            + "  </java.util.Collections_-SynchronizedCollection>\n"
            + "  <java.util.Collections_-SynchronizedList>\n"
            + "    <default>\n"
            + "      <list class=\"linked-list\" reference=\"../../../java.util.Collections_-SynchronizedCollection/default/c\"/>\n"
            + "    </default>\n"
            + "  </java.util.Collections_-SynchronizedList>\n"
            + "</java.util.Collections_-SynchronizedList>";

        // synchronized list has circular reference
        xstream.setMode(XStream.XPATH_RELATIVE_REFERENCES);

        final List<String> list = Collections.synchronizedList(new LinkedList<String>());
        list.add("hi");

        assertBothWays(list, xml);
    }

    public void testSyncronizedArrayList() {
        final String xml = ""
            + "<java.util.Collections_-SynchronizedRandomAccessList resolves-to=\"java.util.Collections$SynchronizedList\" serialization=\"custom\">\n"
            + "  <java.util.Collections_-SynchronizedCollection>\n"
            + "    <default>\n"
            + "      <c class=\"list\">\n"
            + "        <string>hi</string>\n"
            + "      </c>\n"
            + "      <mutex class=\"java.util.Collections$SynchronizedList\" reference=\"../../..\"/>\n"
            + "    </default>\n"
            + "  </java.util.Collections_-SynchronizedCollection>\n"
            + "  <java.util.Collections_-SynchronizedList>\n"
            + "    <default>\n"
            + "      <list reference=\"../../../java.util.Collections_-SynchronizedCollection/default/c\"/>\n"
            + "    </default>\n"
            + "  </java.util.Collections_-SynchronizedList>\n"
            + "</java.util.Collections_-SynchronizedRandomAccessList>";

        // synchronized list has circular reference
        xstream.setMode(XStream.XPATH_RELATIVE_REFERENCES);

        final List<String> list = Collections.synchronizedList(new ArrayList<String>());
        list.add("hi");

        assertBothWays(list, xml);
    }

    public void testEmptyList() {
        assertBothWays(Collections.EMPTY_LIST, "<empty-list/>");
    }

    public void testEmptySet() {
        assertBothWays(Collections.EMPTY_SET, "<empty-set/>");
    }

    public void testEmptyListIsImmutable() {
        final List<List<?>> list = new ArrayList<>();
        list.add(Collections.EMPTY_LIST);
        list.add(Collections.EMPTY_LIST);
        assertBothWays(list, ""//
            + "<list>\n"
            + "  <empty-list/>\n"
            + "  <empty-list/>\n"
            + "</list>");
    }

    public void testEmptySetIsImmutable() {
        final List<Set<?>> list = new ArrayList<>();
        list.add(Collections.EMPTY_SET);
        list.add(Collections.EMPTY_SET);
        assertBothWays(list, ""//
            + "<list>\n"
            + "  <empty-set/>\n"
            + "  <empty-set/>\n"
            + "</list>");
    }

    public void testEmptyListIsSingleton() {
        assertSame(Collections.EMPTY_LIST, xstream.fromXML("<empty-list/>"));
    }

    public void testEmptySetIsSingleton() {
        assertSame(Collections.EMPTY_SET, xstream.fromXML("<empty-set/>"));
    }

    public void testSingletonList() {
        assertBothWays(Collections.singletonList("XStream"), ""//
            + "<singleton-list>\n"
            + "  <string>XStream</string>\n"
            + "</singleton-list>");
    }

    public void testSingletonSet() {
        assertBothWays(Collections.singleton("XStream"), ""//
            + "<singleton-set>\n"
            + "  <string>XStream</string>\n"
            + "</singleton-set>");
    }

    public void testPropertiesWithDefaults() {
        final Properties defaults = new Properties();
        defaults.setProperty("1", "one");
        defaults.setProperty("2", "two");
        final Properties properties = new Properties(defaults);
        properties.setProperty("1", "I");
        properties.setProperty("3", "III");

        assertBothWaysNormalized(properties, ""//
            + "<properties>\n"
            + "  <property name=\"3\" value=\"III\"/>\n"
            + "  <property name=\"1\" value=\"I\"/>\n"
            + "  <defaults>\n"
            + "    <property name=\"2\" value=\"two\"/>\n"
            + "    <property name=\"1\" value=\"one\"/>\n"
            + "  </defaults>\n"
            + "</properties>", "properties", "property", "@name");
    }

    public void testUnmodifiableList() {
        // unmodifiable list has duplicate references
        xstream.setMode(XStream.XPATH_RELATIVE_REFERENCES);

        List<String> list = new ArrayList<>();
        list.add("hi");
        list = Collections.unmodifiableList(list);

        assertBothWays(list, ""
            + "<java.util.Collections_-UnmodifiableRandomAccessList resolves-to=\"java.util.Collections$UnmodifiableList\">\n"
            + "  <c class=\"list\">\n"
            + "    <string>hi</string>\n"
            + "  </c>\n"
            + "  <list reference=\"../c\"/>\n"
            + "</java.util.Collections_-UnmodifiableRandomAccessList>");
    }

    public void testLinkedHashSetRetainsOrdering() {
        final Set<String> set = new LinkedHashSet<>();
        set.add("Z");
        set.add("C");
        set.add("X");

        final LinkedHashSet<String> result = assertBothWays(set, ""//
            + "<linked-hash-set>\n"
            + "  <string>Z</string>\n"
            + "  <string>C</string>\n"
            + "  <string>X</string>\n"
            + "</linked-hash-set>");

        final Object[] values = result.toArray();
        assertEquals("Z", values[0]);
        assertEquals("C", values[1]);
        assertEquals("X", values[2]);
    }

    public void testListFromArrayAsList() {
        final List<String> list = Arrays.asList(new String[]{"hi", "bye"});

        assertBothWays(list, ""//
            + "<java.util.Arrays_-ArrayList>\n"
            + "  <a class=\"string-array\">\n"
            + "    <string>hi</string>\n"
            + "    <string>bye</string>\n"
            + "  </a>\n"
            + "</java.util.Arrays_-ArrayList>");
    }

    public void testKeySetOfHashMapCanBeSerialized() {
        final Map<String, ?> map = new HashMap<>();
        map.put("JUnit", null);
        final Collection<String> set = map.keySet();

        xstream.alias("key-set", set.getClass());

        assertBothWays(set, ""//
            + "<key-set>\n"
            + "  <outer-class>\n"
            + "    <entry>\n"
            + "      <string>JUnit</string>\n"
            + "      <null/>\n"
            + "    </entry>\n"
            + "  </outer-class>\n"
            + "</key-set>");
    }

    public void testValueSetOfHashMapCanBeSerialized() {
        final Map<Boolean, String> map = new HashMap<>();
        map.put(Boolean.TRUE, "JUnit");
        final Collection<String> set = map.values();
        xstream.alias("value-set", set.getClass());

        assertBothWays(set, ""//
            + "<value-set>\n"
            + "  <outer-class>\n"
            + "    <entry>\n"
            + "      <boolean>true</boolean>\n"
            + "      <string>JUnit</string>\n"
            + "    </entry>\n"
            + "  </outer-class>\n"
            + "</value-set>");
    }

    public void testEntrySetOfHashMapCanBeSerialized() {
        final Map<Boolean, String> map = new HashMap<>();
        map.put(Boolean.TRUE, "JUnit");
        final Collection<Map.Entry<Boolean, String>> set = map.entrySet();
        xstream.alias("entry-set", set.getClass());

        if (System.getProperty("java.vm.vendor").contains("IBM")) {
            assertBothWays(set, ""//
                + "<entry-set>\n"
                + "  <associatedMap>\n"
                + "    <entry>\n"
                + "      <boolean>true</boolean>\n"
                + "      <string>JUnit</string>\n"
                + "    </entry>\n"
                + "  </associatedMap>\n"
                + "</entry-set>");
        } else {
            assertBothWays(set, ""//
                + "<entry-set>\n"
                + "  <outer-class>\n"
                + "    <entry>\n"
                + "      <boolean>true</boolean>\n"
                + "      <string>JUnit</string>\n"
                + "    </entry>\n"
                + "  </outer-class>\n"
                + "</entry-set>");
        }
    }
}
