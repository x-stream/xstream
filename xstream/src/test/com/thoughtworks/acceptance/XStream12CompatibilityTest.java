/*
 * Copyright (C) 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 27. June 2007 by Joerg Schaible
 */
package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.objects.OpenSourceSoftware;
import com.thoughtworks.acceptance.objects.StandardObject;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.XStream12FieldKeySorter;
import com.thoughtworks.xstream.core.ReferenceByXPathMarshallingStrategy;
import com.thoughtworks.xstream.core.ReferenceByXPathUnmarshaller;
import com.thoughtworks.xstream.core.TreeUnmarshaller;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Test XStream 1.2 compatibility.
 * 
 * @author J&ouml;rg Schaible
 */
public class XStream12CompatibilityTest extends AbstractAcceptanceTest {

    public static class ParentClass {
        String name;
    }

    public static class ChildClass extends ParentClass {
        String name;
        
        ChildClass() {
            this("JDK", "1.3");
        }

        ChildClass(final String parent, final String child) {
            ((ParentClass)this).name = parent;
            name = child;
        }
        
        public String toString() {
            return ((ParentClass)this).name + "/" + name;
        }
    }

    public void testCanDeserializeHiddenFieldsWithSameTypeWrittenWithXStream11() {
        xstream.alias("parent", ParentClass.class);
        xstream.alias("child", ChildClass.class);

        final String in = "" +
                "<child>\n" +
                "  <name>CHILD</name>\n" +
                "  <name defined-in=\"parent\">PARENT</name>\n" +
                "</child>";
        
        final ChildClass child = (ChildClass)xstream.fromXML(in);
        assertEquals("PARENT/CHILD", child.toString());
    }

    public static class ParentA extends StandardObject {
        private List stuff = new ArrayList();

        public List getParentStuff() {
            return stuff;
        }
    }

    public static class ChildA extends ParentA {
        private Map stuff = new HashMap();

        public Map getChildStuff() {
            return stuff;
        }
    }

    public void testCanDeserializeHiddenFieldsWithDifferentTypeWrittenWithXStream11() {
        xstream.alias("child-a", ChildA.class);
        xstream.alias("parent-a", ParentA.class);
        String expected = "" +
                "<child-a>\n" +
                "  <stuff>\n" +
                "    <entry>\n" +
                "      <string>hello</string>\n" +
                "      <string>world</string>\n" +
                "    </entry>\n" +
                "  </stuff>\n" +
                "  <stuff defined-in=\"parent-a\">\n" +
                "    <string>foo</string>\n" +
                "  </stuff>\n" +
                "</child-a>";
        
        ChildA childA = (ChildA)xstream.fromXML(expected);
        assertEquals("world", childA.getChildStuff().get("hello"));
        assertEquals("foo", childA.getParentStuff().iterator().next());
    }

    public void testCanWriteInheritanceHierarchiesInOldOrder() {
        xstream = new XStream(new PureJavaReflectionProvider(new FieldDictionary(new XStream12FieldKeySorter())));
        OpenSourceSoftware openSourceSoftware = new OpenSourceSoftware("apache", "geronimo", "license");
        String xml =
                "<oss>\n" +
                "  <license>license</license>\n" +
                "  <vendor>apache</vendor>\n" +
                "  <name>geronimo</name>\n" +
                "</oss>";

        xstream.alias("oss", OpenSourceSoftware.class);
        assertEquals(xml, xstream.toXML(openSourceSoftware));
    }

    private final class XStream12ReferenceByXPathUnmarshaller extends
        ReferenceByXPathUnmarshaller {
        private XStream12ReferenceByXPathUnmarshaller(
            Object root, HierarchicalStreamReader reader, ConverterLookup converterLookup,
            Mapper mapper) {
            super(root, reader, converterLookup, mapper);
            isXmlFriendly = false;
        }
    }

    public void testCanReadXmlUnfriendlyXPathReferences() {
        xstream.setMarshallingStrategy(new ReferenceByXPathMarshallingStrategy(ReferenceByXPathMarshallingStrategy.RELATIVE) {

            protected TreeUnmarshaller createUnmarshallingContext(Object root,
                HierarchicalStreamReader reader, ConverterLookup converterLookup, Mapper mapper) {
                return new XStream12ReferenceByXPathUnmarshaller(root, reader, converterLookup, mapper);
            }
            
        });
        xstream.alias("foo$bar", StringBuffer.class);
        xstream.alias("x_y", StringBuffer.class);
        String xml =
            "<list>\n" +
            "  <foo_-bar>foo</foo_-bar>\n" +
            "  <foo_-bar reference=\"../foo$bar\"/>\n" +
            "  <x__y>bar</x__y>\n" +
            "  <x__y reference=\"../x_y\"/>\n" +
            "</list>";
        
        List list = (List)xstream.fromXML(xml);
        assertEquals(4, list.size());
        assertSame(list.get(0), list.get(1));
        assertEquals("foo", list.get(0).toString());
        assertSame(list.get(2), list.get(3));
        assertEquals("bar", list.get(2).toString());
    }
}
