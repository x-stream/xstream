/*
 * Copyright (C) 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 05. August 2011 Joerg Schaible
 */
package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.objects.Hardware;
import com.thoughtworks.acceptance.objects.Product;
import com.thoughtworks.acceptance.objects.SampleMaps;
import com.thoughtworks.acceptance.objects.Software;
import com.thoughtworks.acceptance.objects.StandardObject;
import com.thoughtworks.xstream.converters.collections.MapConverter;
import com.thoughtworks.xstream.core.util.OrderRetainingMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ImplicitMapTest extends AbstractAcceptanceTest {

    public static class Farm extends StandardObject {
        int size;
        List animals = new ArrayList();

        public Farm(int size) {
            this.size = size;
        }

        public void add(Animal animal) {
            animals.add(animal);
        }
    }

    public static class Animal extends StandardObject implements Comparable {
        String name;

        public Animal(String name) {
            this.name = name;
        }

        public int compareTo(Object o) {
            return name.compareTo(((Animal)o).name);
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        xstream.registerConverter(new MapConverter(xstream.getMapper()) {
            public boolean canConvert(Class type) {
                return type == OrderRetainingMap.class;
            }
        });
        xstream.addDefaultImplementation(OrderRetainingMap.class, Map.class);
        xstream.alias("sample", SampleMaps.class);
        xstream.alias("software", Software.class);
        xstream.alias("hardware", Hardware.class);
        xstream.alias("product", Product.class);
    }

    public void testWithout() {
        SampleMaps sample = new SampleMaps();
        sample.good = new OrderRetainingMap();
        sample.good.put("Windows", new Software("Microsoft", "Windows"));
        sample.good.put("Linux", new Software("Red Hat", "Linux"));

        String expected = "" +
                "<sample>\n" +
                "  <good>\n" +
                "    <entry>\n" +
                "      <string>Windows</string>\n" +
                "      <software>\n" +
                "        <vendor>Microsoft</vendor>\n" +
                "        <name>Windows</name>\n" +
                "      </software>\n" +
                "    </entry>\n" +
                "    <entry>\n" +
                "      <string>Linux</string>\n" +
                "      <software>\n" +
                "        <vendor>Red Hat</vendor>\n" +
                "        <name>Linux</name>\n" +
                "      </software>\n" +
                "    </entry>\n" +
                "  </good>\n" +
                "  <bad/>\n" +
                "</sample>";

        assertBothWays(sample, expected);
    }

    public void testWithMap() {
        SampleMaps sample = new SampleMaps();
        sample.good = new OrderRetainingMap();
        sample.good.put("Windows", new Software("Microsoft", "Windows"));
        sample.good.put("Linux", new Software("Red Hat", "Linux"));

        String expected = "" +
                "<sample>\n" +
                "  <software>\n" +
                "    <vendor>Microsoft</vendor>\n" +
                "    <name>Windows</name>\n" +
                "  </software>\n" +
                "  <software>\n" +
                "    <vendor>Red Hat</vendor>\n" +
                "    <name>Linux</name>\n" +
                "  </software>\n" +
                "  <bad/>\n" +
                "</sample>";

        xstream.addImplicitMap(SampleMaps.class, "good", Software.class, "name");
        assertBothWays(sample, expected);
    }

    public static class MegaSampleMaps extends SampleMaps {
        Map other = new OrderRetainingMap();
        {
            good = new OrderRetainingMap();
            bad = new OrderRetainingMap();
        }
    }
    
    public void testInheritsImplicitMapFromSuperclass() {
        xstream.alias("MEGA-sample", MegaSampleMaps.class);

        SampleMaps sample = new MegaSampleMaps(); // subclass
        sample.good.put("Windows", new Software("Microsoft", "Windows"));
        sample.good.put("Linux", new Software("Red Hat", "Linux"));

        String expected = "" +
                "<MEGA-sample>\n" +
                "  <software>\n" +
                "    <vendor>Microsoft</vendor>\n" +
                "    <name>Windows</name>\n" +
                "  </software>\n" +
                "  <software>\n" +
                "    <vendor>Red Hat</vendor>\n" +
                "    <name>Linux</name>\n" +
                "  </software>\n" +
                "  <bad/>\n" +
                "  <other/>\n" +
                "</MEGA-sample>";

        xstream.addImplicitMap(SampleMaps.class, "good", Software.class, "name");
        assertBothWays(sample, expected);
    }

    public void testSupportsInheritedAndDirectDeclaredImplicitMapAtOnce() {
        xstream.alias("MEGA-sample", MegaSampleMaps.class);

        MegaSampleMaps sample = new MegaSampleMaps(); // subclass
        sample.good.put("Windows", new Software("Microsoft", "Windows"));
        sample.good.put("Linux", new Software("Red Hat", "Linux"));
        sample.other.put("i386", new Hardware("i386", "Intel"));
        
        String expected = "" +
                "<MEGA-sample>\n" +
                "  <software>\n" +
                "    <vendor>Microsoft</vendor>\n" +
                "    <name>Windows</name>\n" +
                "  </software>\n" +
                "  <software>\n" +
                "    <vendor>Red Hat</vendor>\n" +
                "    <name>Linux</name>\n" +
                "  </software>\n" +
                "  <bad/>\n" +
                "  <hardware>\n" +
                "    <arch>i386</arch>\n" +
                "    <name>Intel</name>\n" +
                "  </hardware>\n" +
                "</MEGA-sample>";

        xstream.addImplicitMap(SampleMaps.class, "good", Software.class, "name");
        xstream.addImplicitMap(MegaSampleMaps.class, "other", Hardware.class, "arch");
        assertBothWays(sample, expected);
    }

    public void testAllowsSubclassToOverrideImplicitMapInSuperclass() {
        xstream.alias("MEGA-sample", MegaSampleMaps.class);

        SampleMaps sample = new MegaSampleMaps(); // subclass
        sample.good.put("Windows", new Software("Microsoft", "Windows"));
        sample.good.put("Linux", new Software("Red Hat", "Linux"));

        String expected = "" +
                "<MEGA-sample>\n" +
                "  <software>\n" +
                "    <vendor>Microsoft</vendor>\n" +
                "    <name>Windows</name>\n" +
                "  </software>\n" +
                "  <software>\n" +
                "    <vendor>Red Hat</vendor>\n" +
                "    <name>Linux</name>\n" +
                "  </software>\n" +
                "  <bad/>\n" +
                "  <other/>\n" +
                "</MEGA-sample>";

        xstream.addImplicitMap(MegaSampleMaps.class, "good", Software.class, "name");
        assertBothWays(sample, expected);
    }

    public void testDefaultMapBasedOnType() {
        xstream.alias("MEGA-sample", MegaSampleMaps.class);

        MegaSampleMaps sample = new MegaSampleMaps();
        sample.good.put("Windows", new Software("Microsoft", "Windows"));
        sample.good.put("Linux", new Software("Red Hat", "Linux"));
        sample.good.put("Chrome", new Software("Google", "Chrome"));
        sample.bad.put("iPhone", new Product("iPhone", "i", 399.99));
        sample.other.put("Intel", new Hardware("i386", "Intel"));
        sample.other.put("AMD", new Hardware("amd64", "AMD"));
        
        String expected = "" +
                "<MEGA-sample>\n" +
                "  <software>\n" +
                "    <vendor>Microsoft</vendor>\n" +
                "    <name>Windows</name>\n" +
                "  </software>\n" +
                "  <software>\n" +
                "    <vendor>Red Hat</vendor>\n" +
                "    <name>Linux</name>\n" +
                "  </software>\n" +
                "  <software>\n" +
                "    <vendor>Google</vendor>\n" +
                "    <name>Chrome</name>\n" +
                "  </software>\n" +
                "  <product>\n" +
                "    <name>iPhone</name>\n" +
                "    <id>i</id>\n" +
                "    <price>399.99</price>\n" +
                "  </product>\n" +
                "  <hardware>\n" +
                "    <arch>i386</arch>\n" +
                "    <name>Intel</name>\n" +
                "  </hardware>\n" +
                "  <hardware>\n" +
                "    <arch>amd64</arch>\n" +
                "    <name>AMD</name>\n" +
                "  </hardware>\n" +
                "</MEGA-sample>";

        xstream.addImplicitMap(SampleMaps.class, "good", Software.class, "name");
        xstream.addImplicitMap(SampleMaps.class, "bad", Product.class, "name");
        xstream.addImplicitMap(MegaSampleMaps.class, "other", Hardware.class, "name");
        assertBothWays(sample, expected);
    }

    public void testWithSortedMap() {
        SampleMaps sample = new SampleMaps();
        sample.good = new TreeMap();
        sample.good.put("Windows", new Software("Microsoft", "Windows"));
        sample.good.put("Linux", new Software("Red Hat", "Linux"));

        String expected = "" +
                "<sample>\n" +
                "  <software>\n" +
                "    <vendor>Red Hat</vendor>\n" +
                "    <name>Linux</name>\n" +
                "  </software>\n" +
                "  <software>\n" +
                "    <vendor>Microsoft</vendor>\n" +
                "    <name>Windows</name>\n" +
                "  </software>\n" +
                "  <bad/>\n" +
                "</sample>";

        xstream.addDefaultImplementation(TreeMap.class, Map.class);
        xstream.addImplicitMap(SampleMaps.class, "good", Software.class, "name");
        assertBothWays(sample, expected);
    }

    public void testWithExplicitItemNameMatchingTheNameOfTheFieldWithTheMap() {
        SampleMaps sample = new SampleMaps();
        sample.bad = new OrderRetainingMap();
        sample.bad.put("Windows", new Software("Microsoft", "Windows"));
        sample.bad.put("Linux", new Software("Red Hat", "Linux"));

        String expected = "" +
                "<sample>\n" +
                "  <good/>\n" +
                "  <bad>\n" +
                "    <vendor>Microsoft</vendor>\n" +
                "    <name>Windows</name>\n" +
                "  </bad>\n" +
                "  <bad>\n" +
                "    <vendor>Red Hat</vendor>\n" +
                "    <name>Linux</name>\n" +
                "  </bad>\n" +
                "</sample>";

        xstream.addImplicitMap(SampleMaps.class, "bad", "bad", Software.class, "name");
        assertBothWays(sample, expected);
    }

    public void testWithImplicitNameMatchingTheNameOfTheFieldWithTheMap() {
        SampleMaps sample = new SampleMaps();
        sample.bad = new OrderRetainingMap();
        sample.bad.put("Windows", new Software("Microsoft", "Windows"));
        sample.bad.put("Linux", new Software("Red Hat", "Linux"));

        String expected = "" +
                "<sample>\n" +
                "  <good/>\n" +
                "  <bad>\n" +
                "    <vendor>Microsoft</vendor>\n" +
                "    <name>Windows</name>\n" +
                "  </bad>\n" +
                "  <bad>\n" +
                "    <vendor>Red Hat</vendor>\n" +
                "    <name>Linux</name>\n" +
                "  </bad>\n" +
                "</sample>";

        xstream.addImplicitMap(SampleMaps.class, "bad", Software.class, "name");
        xstream.alias("bad", Software.class);
        assertBothWays(sample, expected);
    }

    public void testWithAliasedItemNameMatchingTheAliasedNameOfTheFieldWithTheMap() {
        SampleMaps sample = new SampleMaps();
        sample.bad = new OrderRetainingMap();
        sample.bad.put("Windows", new Software("Microsoft", "Windows"));
        sample.bad.put("Linux", new Software("Red Hat", "Linux"));

        String expected = "" +
                "<sample>\n" +
                "  <good/>\n" +
                "  <test>\n" +
                "    <vendor>Microsoft</vendor>\n" +
                "    <name>Windows</name>\n" +
                "  </test>\n" +
                "  <test>\n" +
                "    <vendor>Red Hat</vendor>\n" +
                "    <name>Linux</name>\n" +
                "  </test>\n" +
                "</sample>";

        xstream.addImplicitMap(SampleMaps.class, "bad", "test", Software.class, "name");
        xstream.aliasField("test", SampleMaps.class, "bad");
        assertBothWays(sample, expected);
    }

    public void testWithNullElement() {
        SampleMaps sample = new SampleMaps();
        sample.good = new OrderRetainingMap();
        sample.good.put(null, null);
        sample.good.put("Linux", new Software("Red Hat", "Linux"));

        String expected = "" +
                "<sample>\n" +
                "  <null/>\n" +
                "  <software>\n" +
                "    <vendor>Red Hat</vendor>\n" +
                "    <name>Linux</name>\n" +
                "  </software>\n" +
                "  <bad/>\n" +
                "</sample>";

        xstream.addImplicitMap(SampleMaps.class, "good", Software.class, "name");
        assertBothWays(sample, expected);
    }

    public void testWithAliasAndNullElement() {
        SampleMaps sample = new SampleMaps();
        sample.good = new OrderRetainingMap();
        sample.good.put(null, null);
        sample.good.put("Linux", new Software("Red Hat", "Linux"));

        String expected = "" +
                "<sample>\n" +
                "  <null/>\n" +
                "  <code>\n" +
                "    <vendor>Red Hat</vendor>\n" +
                "    <name>Linux</name>\n" +
                "  </code>\n" +
                "  <bad/>\n" +
                "</sample>";

        xstream.addImplicitMap(SampleMaps.class, "good", "code", Software.class, "name");
        assertBothWays(sample, expected);
    }

    public void testCollectsDifferentTypesWithFieldOfSameName() {
        SampleMaps sample = new SampleMaps();
        sample.good = new OrderRetainingMap();
        sample.good.put("iPhone", new Product("iPhone", "i", 399.99));
        sample.good.put("Linux", new Software("Red Hat", "Linux"));
        sample.good.put("Intel", new Hardware("i386", "Intel"));

        String expected = "" +
                "<sample>\n" +
                "  <product>\n" +
                "    <name>iPhone</name>\n" +
                "    <id>i</id>\n" +
                "    <price>399.99</price>\n" +
                "  </product>\n" +
                "  <software>\n" +
                "    <vendor>Red Hat</vendor>\n" +
                "    <name>Linux</name>\n" +
                "  </software>\n" +
                "  <hardware>\n" +
                "    <arch>i386</arch>\n" +
                "    <name>Intel</name>\n" +
                "  </hardware>\n" +
                "  <bad/>\n" +
                "</sample>";

        xstream.addImplicitMap(SampleMaps.class, "good", null, "name");
        assertBothWays(sample, expected);
    }

    public void testSeparatesItemsBasedOnItemName() {
        SampleMaps sample = new SampleMaps();
        sample.good = new OrderRetainingMap();
        sample.good.put("Chrome", new Software("Google", "Chrome"));
        sample.bad = new OrderRetainingMap();
        sample.bad.put("Linux", new Software("Red Hat", "Linux"));
        sample.bad.put("Windows", new Software("Microsoft", "Windows"));

        String expected = "" +
                "<sample>\n" +
                "  <g>\n" +
                "    <vendor>Google</vendor>\n" +
                "    <name>Chrome</name>\n" +
                "  </g>\n" +
                "  <b>\n" +
                "    <vendor>Red Hat</vendor>\n" +
                "    <name>Linux</name>\n" +
                "  </b>\n" +
                "  <b>\n" +
                "    <vendor>Microsoft</vendor>\n" +
                "    <name>Windows</name>\n" +
                "  </b>\n" +
                "</sample>";

        xstream.addImplicitMap(SampleMaps.class, "good", "g", Software.class, "name");
        xstream.addImplicitMap(SampleMaps.class, "bad", "b", Software.class, "name");
        assertBothWays(sample, expected);
    }

    public void testWithoutKeyField() {
        SampleMaps sample = new SampleMaps();
        sample.good = new OrderRetainingMap();
        sample.good.put("Windows", new Software("Microsoft", "Windows"));
        sample.good.put("Linux", new Software("Red Hat", "Linux"));

        String expected = "" +
                "<sample>\n" +
                "  <entry>\n" +
                "    <string>Windows</string>\n" +
                "    <software>\n" +
                "      <vendor>Microsoft</vendor>\n" +
                "      <name>Windows</name>\n" +
                "    </software>\n" +
                "  </entry>\n" +
                "  <entry>\n" +
                "    <string>Linux</string>\n" +
                "    <software>\n" +
                "      <vendor>Red Hat</vendor>\n" +
                "      <name>Linux</name>\n" +
                "    </software>\n" +
                "  </entry>\n" +
                "  <bad/>\n" +
                "</sample>";

        xstream.addImplicitMap(SampleMaps.class, "good", null, null);
        assertBothWays(sample, expected);
    }

    public void testCanUsePrimitiveAsKey() {
        SampleMaps sample = new SampleMaps();
        sample.good = new OrderRetainingMap();
        sample.good.put(new Double(399.99), new Product("iPhone", "i", 399.99));

        String expected = "" +
                "<sample>\n" +
                "  <product>\n" +
                "    <name>iPhone</name>\n" +
                "    <id>i</id>\n" +
                "    <price>399.99</price>\n" +
                "  </product>\n" +
                "  <bad/>\n" +
                "</sample>";

        xstream.addImplicitMap(SampleMaps.class, "good", Product.class, "price");
        assertBothWays(sample, expected);
    }
}
