/*
 * Copyright (C) 2003, 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 01. October 2003 by Joe Walnes
 */
package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.objects.Hardware;
import com.thoughtworks.acceptance.objects.SampleLists;
import com.thoughtworks.acceptance.objects.Software;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.JVM;

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
                "  <bad class=\"list\">\n" +
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
                "  <bad class=\"list\"/>\n" +
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

        assertBothWaysNormalized(set, expected, "set", "string", null);
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

    public void testSyncronizedList() {
        final String xml;
        if (JVM.is15()) {
            xml = 
                "<java.util.Collections_-SynchronizedList serialization=\"custom\">\n" +
                "  <java.util.Collections_-SynchronizedCollection>\n" +
                "    <default>\n" +
                "      <c class=\"linked-list\">\n" +
                "        <string>hi</string>\n" +
                "      </c>\n" +
                "      <mutex class=\"java.util.Collections$SynchronizedList\" reference=\"../../..\"/>\n" +
                "    </default>\n" +
                "  </java.util.Collections_-SynchronizedCollection>\n" +
                "  <java.util.Collections_-SynchronizedList>\n" +
                "    <default>\n" +
                "      <list class=\"linked-list\" reference=\"../../../java.util.Collections_-SynchronizedCollection/default/c\"/>\n" +
                "    </default>\n" +
                "  </java.util.Collections_-SynchronizedList>\n" +
                "</java.util.Collections_-SynchronizedList>";
        } else {
            xml = 
                "<java.util.Collections_-SynchronizedList>\n" +
                "  <c class=\"linked-list\">\n" +
                "    <string>hi</string>\n" +
                "  </c>\n" +
                "  <mutex class=\"java.util.Collections$SynchronizedList\" reference=\"..\"/>\n" +
                "  <list class=\"linked-list\" reference=\"../c\"/>\n" +
                "</java.util.Collections_-SynchronizedList>";
        }

        // synchronized list has circular reference
        xstream.setMode(XStream.XPATH_RELATIVE_REFERENCES);

        List list = Collections.synchronizedList(new LinkedList());
        list.add("hi");

        assertBothWays(list, xml);
    }

    public void testSyncronizedArrayList() {
        final String xml;
        if (JVM.is15()) {
            xml = 
                "<java.util.Collections_-SynchronizedRandomAccessList resolves-to=\"java.util.Collections$SynchronizedList\" serialization=\"custom\">\n" +
                "  <java.util.Collections_-SynchronizedCollection>\n" +
                "    <default>\n" +
                "      <c class=\"list\">\n" +
                "        <string>hi</string>\n" +
                "      </c>\n" +
                "      <mutex class=\"java.util.Collections$SynchronizedList\" reference=\"../../..\"/>\n" +
                "    </default>\n" +
                "  </java.util.Collections_-SynchronizedCollection>\n" +
                "  <java.util.Collections_-SynchronizedList>\n" +
                "    <default>\n" +
                "      <list reference=\"../../../java.util.Collections_-SynchronizedCollection/default/c\"/>\n" +
                "    </default>\n" +
                "  </java.util.Collections_-SynchronizedList>\n" +
                "</java.util.Collections_-SynchronizedRandomAccessList>";
        } else {
            xml = 
                "<java.util.Collections_-SynchronizedRandomAccessList resolves-to=\"java.util.Collections$SynchronizedList\">\n" +
                "  <c class=\"list\">\n" +
                "    <string>hi</string>\n" +
                "  </c>\n" +
                "  <mutex class=\"java.util.Collections$SynchronizedList\" reference=\"..\"/>\n" +
                "  <list reference=\"../c\"/>\n" +
                "</java.util.Collections_-SynchronizedRandomAccessList>";
        }

        // synchronized list has circular reference
        xstream.setMode(XStream.XPATH_RELATIVE_REFERENCES);

        List list = Collections.synchronizedList(new ArrayList());
        list.add("hi");

        assertBothWays(list, xml);
    }

    public void testEmptyList() {
        assertBothWays(Collections.EMPTY_LIST, "<java.util.Collections_-EmptyList/>");
    }
    
    public void testSingletonList() {
        assertBothWays(Collections.singletonList("XStream"), 
            "<java.util.Collections_-SingletonList>\n" +
            "  <element class=\"string\">XStream</element>\n" +
            "</java.util.Collections_-SingletonList>");
    }
    
    public void testSingletonMap() {
        assertBothWays(Collections.singletonMap("1", "one"), 
            "<java.util.Collections_-SingletonMap>\n" +
            "  <k class=\"string\">1</k>\n" +
            "  <v class=\"string\">one</v>\n" +
            "</java.util.Collections_-SingletonMap>");
    }
    
    public void testSingletonSet() {
        assertBothWays(Collections.singleton("XStream"), 
            "<java.util.Collections_-SingletonSet>\n" +
            "  <element class=\"string\">XStream</element>\n" +
            "</java.util.Collections_-SingletonSet>");
    }

    public void testPropertiesWithDefaults() {
        Properties defaults = new Properties();
        defaults.setProperty("1", "one");
        defaults.setProperty("2", "two");
        Properties properties = new Properties(defaults);
        properties.setProperty("1", "I");
        properties.setProperty("3", "III");

        assertBothWays(properties,
                "<properties>\n" +
                "  <property name=\"3\" value=\"III\"/>\n" + 
                "  <property name=\"1\" value=\"I\"/>\n" + 
                "  <defaults>\n" + 
                "    <property name=\"2\" value=\"two\"/>\n" + 
                "    <property name=\"1\" value=\"one\"/>\n" + 
                "  </defaults>\n" +
                "</properties>");
    }
    
    public void testUnmodifiableList() {
        // unmodifiable list has duplicate references
        xstream.setMode(XStream.XPATH_RELATIVE_REFERENCES);

        List list = new ArrayList();
        list.add("hi");
        list = Collections.unmodifiableList(list);

        assertBothWays(list,
                "<java.util.Collections_-UnmodifiableRandomAccessList resolves-to=\"java.util.Collections$UnmodifiableList\">\n" +
                "  <c class=\"list\">\n" +
                "    <string>hi</string>\n" +
                "  </c>\n" +
                "  <list reference=\"../c\"/>\n" +
                "</java.util.Collections_-UnmodifiableRandomAccessList>");
    }

    public void testLinkedHashSetRetainsOrdering() {
        Set set = new LinkedHashSet();
        set.add("Z");
        set.add("C");
        set.add("X");

        LinkedHashSet result = (LinkedHashSet) assertBothWays(set,
                "<linked-hash-set>\n" +
                "  <string>Z</string>\n" +
                "  <string>C</string>\n" +
                "  <string>X</string>\n" +
                "</linked-hash-set>");

        Object[] values = result.toArray();
        assertEquals("Z", values[0]);
        assertEquals("C", values[1]);
        assertEquals("X", values[2]);
    }

    public void testListFromArrayAsList() {
        List list = Arrays.asList(new String[] {"hi", "bye"});

        assertBothWays(list,
                "<java.util.Arrays_-ArrayList>\n" +
                "  <a class=\"string-array\">\n" +
                "    <string>hi</string>\n" +
                "    <string>bye</string>\n" +
                "  </a>\n" +
                "</java.util.Arrays_-ArrayList>");
    }
    
    public void testKeySetOfHashMapCanBeSerialized() {
        final Map map = new HashMap();
        map.put("JUnit", null);
        final Collection set = map.keySet();

        xstream.alias("key-set", set.getClass());

        assertBothWays(set,
                "<key-set>\n" +
                "  <outer-class>\n" +
                "    <entry>\n" +
                "      <string>JUnit</string>\n" +
                "      <null/>\n" +
                "    </entry>\n" +
                "  </outer-class>\n" +
                "</key-set>");
    }

    public void testValueSetOfHashMapCanBeSerialized() {
        final Map map = new HashMap();
        map.put(Boolean.TRUE, "JUnit");
        final Collection set = map.values();
        xstream.alias("value-set", set.getClass());

        assertBothWays(set,
                "<value-set>\n" +
                "  <outer-class>\n" +
                "    <entry>\n" +
                "      <boolean>true</boolean>\n" +
                "      <string>JUnit</string>\n" +
                "    </entry>\n" +
                "  </outer-class>\n" +
                "</value-set>");
    }

    public void testEntrySetOfHashMapCanBeSerialized() {
        final Map map = new HashMap();
        map.put(Boolean.TRUE, "JUnit");
        final Collection set = map.entrySet();
        xstream.alias("entry-set", set.getClass());

        if (JVM.is16() && System.getProperty("java.vm.vendor").indexOf("IBM") >= 0) {
            assertBothWays(set,
                "<entry-set>\n" +
                "  <associatedMap>\n" +
                "    <entry>\n" +
                "      <boolean>true</boolean>\n" +
                "      <string>JUnit</string>\n" +
                "    </entry>\n" +
                "  </associatedMap>\n" +
                "</entry-set>");
        } else {
            assertBothWays(set,
                    "<entry-set>\n" +
                    "  <outer-class>\n" +
                    "    <entry>\n" +
                    "      <boolean>true</boolean>\n" +
                    "      <string>JUnit</string>\n" +
                    "    </entry>\n" +
                    "  </outer-class>\n" +
                    "</entry-set>");
        }
    }
}
