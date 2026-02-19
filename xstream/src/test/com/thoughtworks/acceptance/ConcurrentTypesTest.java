/*
<<<<<<< HEAD:xstream/src/test/com/thoughtworks/acceptance/ConcurrentTypesTest.java
 * Copyright (C) 2012, 2015, 2017, 2018, 2021, 2022, 2023 XStream Committers.
=======
 * Copyright (C) 2012, 2015, 2017, 2018, 2022, 2023, 2026 XStream Committers.
>>>>>>> a53eed40 (Add converter for CopyOnWriteArray types. Closes #463.):xstream/src/test/com/thoughtworks/acceptance/Concurrent15TypesTest.java
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 17. July 2018 by Joerg Schaible, renamed from Concurrent15TypesTest
 */
package com.thoughtworks.acceptance;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import com.thoughtworks.acceptance.objects.Original;
import com.thoughtworks.acceptance.objects.Replaced;
import com.thoughtworks.xstream.converters.collections.MapConverter;


public class ConcurrentTypesTest extends AbstractAcceptanceTest {

    public void testConcurrentHashMap() {
        final ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        map.put("walnes", "joe");
        final String xml = xstream.toXML(map);
        final String expected = ""
            + "<concurrent-hash-map>\n"
            + "  <entry>\n"
            + "    <string>walnes</string>\n"
            + "    <string>joe</string>\n"
            + "  </entry>\n"
            + "</concurrent-hash-map>";
        assertEquals(xml, expected);
        final ConcurrentHashMap<String, String> out = xstream.fromXML(xml);
        assertEquals("{walnes=joe}", out.toString());
    }

    public static class DerivedConcurrentHashMap extends ConcurrentHashMap<Object, Object> {
        private static final long serialVersionUID = 1L;
    }

    public void testDerivedConcurrentHashMap() {
        xstream.alias("derived-map", DerivedConcurrentHashMap.class);
        xstream.registerConverter(new MapConverter(xstream.getMapper(), DerivedConcurrentHashMap.class));

        final Map<Object, Object> map = new DerivedConcurrentHashMap();
        map.put("test", "JUnit");

        final String xml = ""
            + "<derived-map>\n"
            + "  <entry>\n"
            + "    <string>test</string>\n"
            + "    <string>JUnit</string>\n"
            + "  </entry>\n"
            + "</derived-map>";

        assertBothWays(map, xml);
    }

    public void testAtomicBoolean() {
        final AtomicBoolean atomicBoolean = new AtomicBoolean();
        assertBothWays(atomicBoolean, "<atomic-boolean>" + atomicBoolean + "</atomic-boolean>");
    }

    public void testAtomicBooleanWithOldFormat() {
        assertEquals(new AtomicBoolean(true).toString(), xstream.fromXML("" //
            + "<java.util.concurrent.atomic.AtomicBoolean>\n" //
            + "  <value>1</value>\n" //
            + "</java.util.concurrent.atomic.AtomicBoolean>").toString());
    }

    public void testAtomicInteger() {
        final AtomicInteger atomicInteger = new AtomicInteger(42);
        assertBothWays(atomicInteger, "<atomic-int>" + atomicInteger + "</atomic-int>");
    }

    public void testAtomicIntegerWithOldFormat() {
        assertEquals(new AtomicInteger(42).toString(), xstream.fromXML("" //
            + "<java.util.concurrent.atomic.AtomicInteger>\n" //
            + "  <value>42</value>\n" //
            + "</java.util.concurrent.atomic.AtomicInteger>").toString());
    }

    public void testAtomicLong() {
        final AtomicLong atomicLong = new AtomicLong(42);
        assertBothWays(atomicLong, "<atomic-long>" + atomicLong + "</atomic-long>");
    }

    public void testAtomicLongWithOldFormat() {
        assertEquals(new AtomicInteger(42).toString(), xstream.fromXML("" //
            + "<java.util.concurrent.atomic.AtomicLong>\n" //
            + "  <value>42</value>\n" //
            + "</java.util.concurrent.atomic.AtomicLong>").toString());
    }

    public void testAtomicReference() {
        final AtomicReference<String> atomicRef = new AtomicReference<>("test");
        assertBothWays(atomicRef, ("" //
            + "<atomic-reference>\n" //
            + "  <value class='string'>test</value>\n" //
            + "</atomic-reference>").replace('\'', '"'));
    }

    @SuppressWarnings("unchecked")
    public void testAtomicReferenceWithOldFormat() {
        assertEquals(new AtomicReference<>("test").get(), ((AtomicReference<String>)xstream.fromXML("" //
            + "<java.util.concurrent.atomic.AtomicReference>\n" //
            + "  <value class='string'>test</value>\n" //
            + "</java.util.concurrent.atomic.AtomicReference>")).get());
    }

    public void testEmptyAtomicReference() {
        final AtomicReference<?> atomicRef = new AtomicReference<>();
        assertBothWays(atomicRef, "<atomic-reference/>");
    }

    public void testAtomicReferenceWithAlias() {
        xstream.aliasField("junit", AtomicReference.class, "value");
        final AtomicReference<String> atomicRef = new AtomicReference<>("test");
        assertBothWays(atomicRef, ("" //
            + "<atomic-reference>\n" //
            + "  <junit class='string'>test</junit>\n" //
            + "</atomic-reference>").replace('\'', '"'));
    }

    public void testAtomicReferenceWithReplaced() {
        xstream.alias("original", Original.class);
        xstream.alias("replaced", Replaced.class);
        final AtomicReference<Original> atomicRef = new AtomicReference<>(new Original("test"));
        assertBothWays(atomicRef, ("" //
            + "<atomic-reference>\n" //
            + "  <value class='original' resolves-to='replaced'>\n"
            + "    <replacedValue>TEST</replacedValue>\n"
            + "  </value>\n" //
            + "</atomic-reference>").replace('\'', '"'));
    }

    public void testCopyOnWriteArrayList() {
        final CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>(Arrays.asList("A", "B", "C"));
        assertBothWays(list, ("" //
            + "<copy-on-write-array-list>\n"
            + "  <string>A</string>\n"
            + "  <string>B</string>\n"
            + "  <string>C</string>\n"
            + "</copy-on-write-array-list>").replace('\'', '"'));
    }

    public void testCopyOnWriteArrayListFormatJavaRuntime() {
        final CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>(Arrays.asList("A", "B", "C"));
        assertEquals(list, xstream.fromXML(("" //
            + "<java.util.concurrent.CopyOnWriteArrayList serialization='custom'>\n"
            + "  <java.util.concurrent.CopyOnWriteArrayList>\n"
            + "    <default/>\n"
            + "    <int>3</int>\n"
            + "    <string>A</string>\n"
            + "    <string>B</string>\n"
            + "    <string>C</string>\n"
            + "  </java.util.concurrent.CopyOnWriteArrayList>\n"
            + "</java.util.concurrent.CopyOnWriteArrayList>").replace('\'', '"')));
    }

    public void testCopyOnWriteArraySet() {
        final CopyOnWriteArraySet<String> set = new CopyOnWriteArraySet<String>(Arrays.asList("A"));
        assertBothWays(set, ("" //
            + "<copy-on-write-array-set serialization='xstream'>\n"
            + "  <string>A</string>\n"
            + "</copy-on-write-array-set>").replace('\'', '"'));
    }

    public void testCopyOnWriteArraySetFormatFromOldJavaRuntime() {
        final CopyOnWriteArraySet<String> set = new CopyOnWriteArraySet<String>(Arrays.asList("A", "B", "C"));
        // Versions before 8.482, 11.0.30, 17.0.18, 21.0.10 and 25.0.2
        assertEquals(set, xstream.fromXML(("" //
            + "<java.util.concurrent.CopyOnWriteArraySet>\n"
            + "  <al serialization='custom'>\n"
            + "    <java.util.concurrent.CopyOnWriteArrayList>\n"
            + "      <default/>\n"
            + "      <int>3</int>\n"
            + "      <string>B</string>\n"
            + "      <string>A</string>\n"
            + "      <string>C</string>\n"
            + "    </java.util.concurrent.CopyOnWriteArrayList>\n"
            + "  </al>\n"
            + "</java.util.concurrent.CopyOnWriteArraySet>").replace('\'', '"')));
    }

    public void testCopyOnWriteArraySetFormatFromNewerJavaRuntime() {
        final CopyOnWriteArraySet<String> set = new CopyOnWriteArraySet<String>(Arrays.asList("A", "B", "C"));
        // Versions since 8.482, 11.0.30, 17.0.18, 21.0.10 and 25.0.2
        assertEquals(set, xstream.fromXML(("" //
            + "<java.util.concurrent.CopyOnWriteArraySet serialization='custom'>\n"
            + "  <unserializable-parents/>\n"
            + "  <java.util.concurrent.CopyOnWriteArraySet>\n"
            + "    <default>\n"
            + "      <al serialization='custom'>\n"
            + "        <java.util.concurrent.CopyOnWriteArrayList>\n"
            + "          <default/>\n"
            + "          <int>3</int>\n"
            + "          <string>C</string>\n"
            + "          <string>A</string>\n"
            + "          <string>B</string>\n"
            + "        </java.util.concurrent.CopyOnWriteArrayList>\n"
            + "      </al>\n"
            + "    </default>\n"
            + "  </java.util.concurrent.CopyOnWriteArraySet>\n"
            + "</java.util.concurrent.CopyOnWriteArraySet>").replace('\'', '"')));
    }
}
