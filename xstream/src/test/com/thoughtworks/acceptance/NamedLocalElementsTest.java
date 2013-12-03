/*
 * Copyright (C) 2013 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 20. September 2013 by Joerg Schaible
 */
package com.thoughtworks.acceptance;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.acceptance.objects.Category;
import com.thoughtworks.acceptance.objects.SampleMaps;
import com.thoughtworks.xstream.converters.extended.NamedArrayConverter;
import com.thoughtworks.xstream.converters.extended.NamedCollectionConverter;
import com.thoughtworks.xstream.converters.extended.NamedMapConverter;

/**
 * Tests named elements of collections and maps.
 * 
 * @author J&ouml;rg Schaible
 */
public class NamedLocalElementsTest extends AbstractAcceptanceTest {
    
    protected void setUp() throws Exception {
        super.setUp();
        xstream.alias("category", Category.class);
        xstream.alias("maps", SampleMaps.class);
        xstream.alias("arrays", Arrays.class);
        xstream.aliasField("products", SampleMaps.class, "good");
        xstream.addDefaultImplementation(LinkedHashMap.class, Map.class);
    }

    public void testListElementsWithFinalType() {
        xstream.registerLocalConverter(Category.class, "products", 
            new NamedCollectionConverter(xstream.getMapper(), "product", String.class));
        
        List products = new ArrayList();
        products.add("SiteMesh");
        products.add("XStream");
        Category category = new Category("Joe Walnes", "joe");
        category.setProducts(products);
        
        String expected = ""
            + "<category>\n"
            + "  <name>Joe Walnes</name>\n"
            + "  <id>joe</id>\n"
            + "  <products>\n"
            + "    <product>SiteMesh</product>\n"
            + "    <product>XStream</product>\n"
            + "  </products>\n"
            + "</category>";
        
        assertBothWays(category, expected);
    }

    public void testListElementsWithSuperTypes() {
        xstream.registerLocalConverter(Category.class, "products", 
            new NamedCollectionConverter(xstream.getMapper(), "product", Object.class));
        
        List products = new ArrayList();
        products.add("SiteMesh");
        products.add(new StringBuffer("XStream"));
        Category category = new Category("Joe Walnes", "joe");
        category.setProducts(products);
        
        String expected = (""
            + "<category>\n"
            + "  <name>Joe Walnes</name>\n"
            + "  <id>joe</id>\n"
            + "  <products>\n"
            + "    <product class='string'>SiteMesh</product>\n"
            + "    <product class='string-buffer'>XStream</product>\n"
            + "  </products>\n"
            + "</category>").replace('\'', '"');
        
        assertBothWays(category, expected);
    }

    public void testListWithNullElements() {
        xstream.registerLocalConverter(Category.class, "products", 
            new NamedCollectionConverter(xstream.getMapper(), "product", String.class));
        
        List products = new ArrayList();
        products.add("SiteMesh");
        products.add(null);
        products.add("XStream");
        Category category = new Category("Joe Walnes", "joe");
        category.setProducts(products);
        
        String expected = (""
                + "<category>\n"
                + "  <name>Joe Walnes</name>\n"
                + "  <id>joe</id>\n"
                + "  <products>\n"
                + "    <product>SiteMesh</product>\n"
                + "    <product class='null'/>\n"
                + "    <product>XStream</product>\n"
                + "  </products>\n"
                + "</category>").replace('\'', '"');
        
        assertBothWays(category, expected);
    }

    public void testMapElementsWithFinalType() {
        xstream.registerLocalConverter(SampleMaps.class, "good", 
            new NamedMapConverter(xstream.getMapper(), "product", "name", String.class, "domain", String.class));
        
        SampleMaps maps = new SampleMaps();
        maps.bad = null;
        maps.good = new LinkedHashMap();
        maps.good.put("SiteMesh", "com.opensymphony");
        maps.good.put("XStream", "com.thoughtworks");
        
        String expected = ""
            + "<maps>\n"
            + "  <products>\n"
            + "    <product>\n"
            + "      <name>SiteMesh</name>\n"
            + "      <domain>com.opensymphony</domain>\n"
            + "    </product>\n"
            + "    <product>\n"
            + "      <name>XStream</name>\n"
            + "      <domain>com.thoughtworks</domain>\n"
            + "    </product>\n"
            + "  </products>\n"
            + "</maps>";
        
        assertBothWays(maps, expected);
    }

    public void testMapElementsWithSuperType() {
        xstream.registerLocalConverter(SampleMaps.class, "good", 
            new NamedMapConverter(xstream.getMapper(), "product", "name", Object.class, "price", Number.class));
        
        SampleMaps maps = new SampleMaps();
        maps.bad = null;
        maps.good = new LinkedHashMap();
        maps.good.put("SiteMesh", new Integer(42));
        maps.good.put(new StringBuffer("XStream"), new Double(42));
        
        String expected = (""
            + "<maps>\n"
            + "  <products>\n"
            + "    <product>\n"
            + "      <name class='string'>SiteMesh</name>\n"
            + "      <price class='int'>42</price>\n"
            + "    </product>\n"
            + "    <product>\n"
            + "      <name class='string-buffer'>XStream</name>\n"
            + "      <price class='double'>42.0</price>\n"
            + "    </product>\n"
            + "  </products>\n"
            + "</maps>").replace('\'', '"');
        
        assertBothWays(maps, expected);
    }

    public void testMapWithNullElements() {
        xstream.registerLocalConverter(SampleMaps.class, "good", 
            new NamedMapConverter(xstream.getMapper(), "product", "name", String.class, "domain", String.class));
        
        SampleMaps maps = new SampleMaps();
        maps.bad = null;
        maps.good = new LinkedHashMap();
        maps.good.put(null, "com.opensymphony");
        maps.good.put("XStream", null);
        
        String expected = (""
                + "<maps>\n"
                + "  <products>\n"
                + "    <product>\n"
                + "      <name class='null'/>\n"
                + "      <domain>com.opensymphony</domain>\n"
                + "    </product>\n"
                + "    <product>\n"
                + "      <name>XStream</name>\n"
                + "      <domain class='null'/>\n"
                + "    </product>\n"
                + "  </products>\n"
                + "</maps>").replace('\'', '"');
        
        assertBothWays(maps, expected);
    }

    public void testMapWithSwappedKeyAndValueElements() {
        xstream.registerLocalConverter(SampleMaps.class, "good", 
            new NamedMapConverter(xstream.getMapper(), "product", "name", String.class, "domain", String.class));
        
        SampleMaps maps = new SampleMaps();
        maps.bad = null;
        maps.good = new LinkedHashMap();
        maps.good.put("SiteMesh", "com.opensymphony");
        maps.good.put("XStream", "com.thoughtworks");
        
        String xml = ""
            + "<maps>\n"
            + "  <products>\n"
            + "    <product>\n"
            + "      <name>SiteMesh</name>\n"
            + "      <domain>com.opensymphony</domain>\n"
            + "    </product>\n"
            + "    <product>\n"
            + "      <domain>com.thoughtworks</domain>\n"
            + "      <name>XStream</name>\n"
            + "    </product>\n"
            + "  </products>\n"
            + "</maps>";
        
        assertEquals(maps, xstream.fromXML(xml));
    }

    public void testMapElementsUsingSameNames() {
        xstream.registerLocalConverter(SampleMaps.class, "good", 
            new NamedMapConverter(xstream.getMapper(), "test", "test", String.class, "test", String.class));
        
        SampleMaps maps = new SampleMaps();
        maps.bad = null;
        maps.good = new LinkedHashMap();
        maps.good.put("SiteMesh", "com.opensymphony");
        maps.good.put("XStream", "com.thoughtworks");
        
        String expected = ""
            + "<maps>\n"
            + "  <products>\n"
            + "    <test>\n"
            + "      <test>SiteMesh</test>\n"
            + "      <test>com.opensymphony</test>\n"
            + "    </test>\n"
            + "    <test>\n"
            + "      <test>XStream</test>\n"
            + "      <test>com.thoughtworks</test>\n"
            + "    </test>\n"
            + "  </products>\n"
            + "</maps>";
        
        assertBothWays(maps, expected);
    }

    public void testMapElementsWithoutEntry() {
        xstream.registerLocalConverter(SampleMaps.class, "good", 
            new NamedMapConverter(xstream.getMapper(), null, "name", String.class, "domain", String.class));
        
        SampleMaps maps = new SampleMaps();
        maps.bad = null;
        maps.good = new LinkedHashMap();
        maps.good.put("SiteMesh", "com.opensymphony");
        maps.good.put("XStream", "com.thoughtworks");
        
        String expected = ""
            + "<maps>\n"
            + "  <products>\n"
            + "    <name>SiteMesh</name>\n"
            + "    <domain>com.opensymphony</domain>\n"
            + "    <name>XStream</name>\n"
            + "    <domain>com.thoughtworks</domain>\n"
            + "  </products>\n"
            + "</maps>";
        
        assertBothWays(maps, expected);
    }

    public void testMapWithNullElementsWithoutEntry() {
        xstream.registerLocalConverter(SampleMaps.class, "good", 
            new NamedMapConverter(xstream.getMapper(), null, "name", String.class, "domain", String.class));
        
        SampleMaps maps = new SampleMaps();
        maps.bad = null;
        maps.good = new LinkedHashMap();
        maps.good.put(null, "com.opensymphony");
        maps.good.put("XStream", null);
        
        String expected = (""
                + "<maps>\n"
                + "  <products>\n"
                + "    <name class='null'/>\n"
                + "    <domain>com.opensymphony</domain>\n"
                + "    <name>XStream</name>\n"
                + "    <domain class='null'/>\n"
                + "  </products>\n"
                + "</maps>").replace('\'', '"');
        
        assertBothWays(maps, expected);
    }

    public void testMapWithSwappedKeyAndValueElementsWithoutEntry() {
        xstream.registerLocalConverter(SampleMaps.class, "good", 
            new NamedMapConverter(xstream.getMapper(), null, "name", String.class, "domain", String.class));
        
        SampleMaps maps = new SampleMaps();
        maps.bad = null;
        maps.good = new LinkedHashMap();
        maps.good.put("SiteMesh", "com.opensymphony");
        maps.good.put("XStream", "com.thoughtworks");
        
        String xml = ""
            + "<maps>\n"
            + "  <products>\n"
            + "    <domain>com.opensymphony</domain>\n"
            + "    <name>SiteMesh</name>\n"
            + "    <name>XStream</name>\n"
            + "    <domain>com.thoughtworks</domain>\n"
            + "  </products>\n"
            + "</maps>";
        
        assertEquals(maps, xstream.fromXML(xml));
    }

    public void testMapElementsUsingSameNamesWithoutEntry() {
        xstream.registerLocalConverter(SampleMaps.class, "good", 
            new NamedMapConverter(xstream.getMapper(), null, "test", String.class, "test", String.class));
        
        SampleMaps maps = new SampleMaps();
        maps.bad = null;
        maps.good = new LinkedHashMap();
        maps.good.put("SiteMesh", "com.opensymphony");
        maps.good.put("XStream", "com.thoughtworks");
        
        String expected = ""
            + "<maps>\n"
            + "  <products>\n"
            + "    <test>SiteMesh</test>\n"
            + "    <test>com.opensymphony</test>\n"
            + "    <test>XStream</test>\n"
            + "    <test>com.thoughtworks</test>\n"
            + "  </products>\n"
            + "</maps>";
        
        assertBothWays(maps, expected);
    }

    public void testMapElementsUsingAttributesOnly() {
        xstream.registerLocalConverter(
            SampleMaps.class, "good", new NamedMapConverter(
                xstream.getMapper(), "product", "name", String.class, "domain", String.class,
                true, true, xstream.getConverterLookup()));
        
        SampleMaps maps = new SampleMaps();
        maps.bad = null;
        maps.good = new LinkedHashMap();
        maps.good.put("SiteMesh", "com.opensymphony");
        maps.good.put("XStream", "com.thoughtworks");
        
        String expected = (""
            + "<maps>\n"
            + "  <products>\n"
            + "    <product name='SiteMesh' domain='com.opensymphony'/>\n"
            + "    <product name='XStream' domain='com.thoughtworks'/>\n"
            + "  </products>\n"
            + "</maps>").replace('\'', '"');
        
        assertBothWays(maps, expected);
    }

    public void testMapElementsWithNullUsingAttributesOnly() {
        xstream.registerLocalConverter(
            SampleMaps.class, "good", new NamedMapConverter(
                xstream.getMapper(), "product", "name", String.class, "domain", String.class,
                true, true, xstream.getConverterLookup()));
        
        SampleMaps maps = new SampleMaps();
        maps.bad = null;
        maps.good = new LinkedHashMap();
        maps.good.put(null, "com.opensymphony");
        maps.good.put("XStream", null);
        
        String expected = (""
            + "<maps>\n"
            + "  <products>\n"
            + "    <product domain='com.opensymphony'/>\n"
            + "    <product name='XStream'/>\n"
            + "  </products>\n"
            + "</maps>").replace('\'', '"');
        
        assertBothWays(maps, expected);
    }

    public void testMapElementsUsingAttributeAndText() {
        xstream.registerLocalConverter(
            SampleMaps.class, "good", new NamedMapConverter(
                xstream.getMapper(), "product", "name", String.class, null, String.class,
                true, false, xstream.getConverterLookup()));
        
        SampleMaps maps = new SampleMaps();
        maps.bad = null;
        maps.good = new LinkedHashMap();
        maps.good.put("SiteMesh", "com.opensymphony");
        maps.good.put("XStream", "com.thoughtworks");
        
        String expected = (""
            + "<maps>\n"
            + "  <products>\n"
            + "    <product name='SiteMesh'>com.opensymphony</product>\n"
            + "    <product name='XStream'>com.thoughtworks</product>\n"
            + "  </products>\n"
            + "</maps>").replace('\'', '"');
        
        assertBothWays(maps, expected);
    }

    public void testMapElementsUsingAttributeForKeyOnly() {
        xstream.registerLocalConverter(
            SampleMaps.class, "good", new NamedMapConverter(
                xstream.getMapper(), "product", "name", String.class, "domain", String.class,
                true, false, xstream.getConverterLookup()));
        
        SampleMaps maps = new SampleMaps();
        maps.bad = null;
        maps.good = new LinkedHashMap();
        maps.good.put("SiteMesh", "com.opensymphony");
        maps.good.put("XStream", "com.thoughtworks");
        
        String expected = (""
            + "<maps>\n"
            + "  <products>\n"
            + "    <product name='SiteMesh'>\n"
            + "      <domain>com.opensymphony</domain>\n"
            + "    </product>\n"
            + "    <product name='XStream'>\n"
            + "      <domain>com.thoughtworks</domain>\n"
            + "    </product>\n"
            + "  </products>\n"
            + "</maps>").replace('\'', '"');
        
        assertBothWays(maps, expected);
    }

    public void testMapElementsUsingAttributeForValueOnly() {
        xstream.registerLocalConverter(
            SampleMaps.class, "good", new NamedMapConverter(
                xstream.getMapper(), "product", "name", String.class, "domain", String.class,
                false, true, xstream.getConverterLookup()));
        
        SampleMaps maps = new SampleMaps();
        maps.bad = null;
        maps.good = new LinkedHashMap();
        maps.good.put("SiteMesh", "com.opensymphony");
        maps.good.put("XStream", "com.thoughtworks");
        
        String expected = (""
            + "<maps>\n"
            + "  <products>\n"
            + "    <product domain='com.opensymphony'>\n"
            + "      <name>SiteMesh</name>\n"
            + "    </product>\n"
            + "    <product domain='com.thoughtworks'>\n"
            + "      <name>XStream</name>\n"
            + "    </product>\n"
            + "  </products>\n"
            + "</maps>").replace('\'', '"');
        
        assertBothWays(maps, expected);
    }
    
    public void testInvalidMapConfiguration() {
        test(null, "key", "value", true, true);
        test(null, "key", "value", false, true);
        test(null, "key", "value", true, false);
        test(null, "key", null, false, false);
        test("entry", "key", null, false, false);
        test("entry", "key", null, false, true);
        test("entry", null, "value", false, false);
        test("entry", null, "value", true, false);
        test("entry", "foo", "foo", true, true);
    }
    
    private void test(String entry, String key, String value, boolean keyAttr, boolean valueAttr) {
        try {
            new NamedMapConverter(
                xstream.getMapper(), entry, key, String.class, value, String.class, keyAttr,
                valueAttr, xstream.getConverterLookup());
            fail("Thrown " + IllegalArgumentException.class.getName() + " expected");
        } catch (final IllegalArgumentException e) {
            // OK
        }
    }
    
    public static class Arrays {
        String[] strings;
        Object[] objects;
        int[] ints;
        short[][] shortArrays;
    }
    
    public void testArrayWithFinalType() {
        xstream.registerLocalConverter(Arrays.class, "strings", 
            new NamedArrayConverter(String[].class, xstream.getMapper(), "name"));
        
        Arrays arrays = new Arrays();
        arrays.strings = new String[] {
            "joe", "mauro" 
        };
        
        String expected = ""
            + "<arrays>\n"
            + "  <strings>\n"
            + "    <name>joe</name>\n"
            + "    <name>mauro</name>\n"
            + "  </strings>\n"
            + "</arrays>";
        
        assertBothWays(arrays, expected);
    }
    
    public void testArrayWithSuperTypes() {
        xstream.registerLocalConverter(Arrays.class, "objects", 
            new NamedArrayConverter(Object[].class, xstream.getMapper(), "item"));
        
        Arrays arrays = new Arrays();
        arrays.objects = new Object[] {
            "joe", Boolean.TRUE, new Integer(47) 
        };
        
        String expected = (""
            + "<arrays>\n"
            + "  <objects>\n"
            + "    <item class='string'>joe</item>\n"
            + "    <item class='boolean'>true</item>\n"
            + "    <item class='int'>47</item>\n"
            + "  </objects>\n"
            + "</arrays>").replace('\'', '"');
        
        assertBothWays(arrays, expected);
    }
    
    public void testArrayWithNullElement() {
        xstream.registerLocalConverter(Arrays.class, "strings", 
            new NamedArrayConverter(String[].class, xstream.getMapper(), "name"));
        
        Arrays arrays = new Arrays();
        arrays.strings = new String[] {
            "joe", null, "mauro" 
        };
        
        String expected = ""
            + "<arrays>\n"
            + "  <strings>\n"
            + "    <name>joe</name>\n"
            + "    <name class=\"null\"/>\n"
            + "    <name>mauro</name>\n"
            + "  </strings>\n"
            + "</arrays>";
        
        assertBothWays(arrays, expected);
    }
    
    public void testArrayWithPrimitives() {
        xstream.registerLocalConverter(Arrays.class, "ints", 
            new NamedArrayConverter(int[].class, xstream.getMapper(), "value"));
        
        Arrays arrays = new Arrays();
        arrays.ints = new int[] {
            47, 0, -3 
        };
        
        String expected = ""
            + "<arrays>\n"
            + "  <ints>\n"
            + "    <value>47</value>\n"
            + "    <value>0</value>\n"
            + "    <value>-3</value>\n"
            + "  </ints>\n"
            + "</arrays>";
        
        assertBothWays(arrays, expected);
    }
    
    public void testArrayWithPrimitiveArrays() {
        xstream.registerLocalConverter(Arrays.class, "shortArrays", 
            new NamedArrayConverter(short[][].class, xstream.getMapper(), "values"));
        
        Arrays arrays = new Arrays();
        arrays.shortArrays = new short[][] {
            {47, 0, -3},
            null,
            {13, 7} 
        };
        
        String expected = ""
            + "<arrays>\n"
            + "  <shortArrays>\n"
            + "    <values>\n"
            + "      <short>47</short>\n"
            + "      <short>0</short>\n"
            + "      <short>-3</short>\n"
            + "    </values>\n"
            + "    <values class=\"null\"/>\n"
            + "    <values>\n"
            + "      <short>13</short>\n"
            + "      <short>7</short>\n"
            + "    </values>\n"
            + "  </shortArrays>\n"
            + "</arrays>";
        
        assertBothWays(arrays, expected);
    }
}
