/*
 * Copyright (C) 2003, 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 26. September 2003 by Joe Walnes
 */
package com.thoughtworks.acceptance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.acceptance.objects.OpenSourceSoftware;
import com.thoughtworks.acceptance.objects.StandardObject;


public class InheritanceTest extends AbstractAcceptanceTest {
    public void testHandlesInheritanceHierarchies() {
        final OpenSourceSoftware openSourceSoftware = new OpenSourceSoftware("apache", "geronimo", "license");
        final String xml = ""//
            + "<oss>\n"
            + "  <vendor>apache</vendor>\n"
            + "  <name>geronimo</name>\n"
            + "  <license>license</license>\n"
            + "</oss>";

        xstream.alias("oss", OpenSourceSoftware.class);
        assertBothWays(openSourceSoftware, xml);
    }

    public static class ParentClass {
        private String name;

        public ParentClass(final String name) {
            this.name = name;
        }

        public String getParentName() {
            return name;
        }
    }

    public static class ChildClass extends ParentClass {
        private String name;

        public ChildClass(final String parentName, final String childName) {
            super(parentName);
            name = childName;
        }

        public String getChildName() {
            return name;
        }

        @Override
        public String toString() {
            return getParentName() + "/" + getChildName();
        }

        @Override
        public boolean equals(final Object obj) {
            return toString().equals(obj.toString());
        }

        @Override
        public int hashCode() {
            return getParentName().hashCode() | getChildName().hashCode();
        }
    }

    public void testInheritanceHidingPrivateFieldOfSameName() {
        xstream.alias("parent", ParentClass.class);
        xstream.alias("child", ChildClass.class);

        final ChildClass child = new ChildClass("PARENT", "CHILD");
        // sanity checks
        assertEquals("PARENT", child.getParentName());
        assertEquals("CHILD", child.getChildName());

        final String expected = ""
            + "<child>\n"
            + "  <name defined-in=\"parent\">PARENT</name>\n"
            + "  <name>CHILD</name>\n"
            + "</child>";

        assertBothWays(child, expected);
    }

    public static class StaticChildClass extends ParentClass {
        private static String name = "CHILD";

        public StaticChildClass(final String parentName) {
            super(parentName);
        }
    }

    public void testHandlesStaticFieldInChildDoesNotHideFieldInParent() {
        xstream.alias("child", StaticChildClass.class);

        final StaticChildClass child = new StaticChildClass("PARENT");
        final String expected = "" + "<child>\n" + "  <name>PARENT</name>\n" + "</child>";

        assertBothWays(child, expected);
        assertEquals("PARENT", child.getParentName());
        assertEquals("CHILD", StaticChildClass.name);
    }

    public static class ParentA<T> extends StandardObject {
        private static final long serialVersionUID = 200405L;
        private final List<T> stuff = new ArrayList<>();

        public List<T> getParentStuff() {
            return stuff;
        }
    }

    public static class ChildA<K, V, T> extends ParentA<T> {
        private static final long serialVersionUID = 200405L;
        private final Map<K, V> stuff = new HashMap<>();

        public Map<K, V> getChildStuff() {
            return stuff;
        }

        @Override
        public boolean equals(final Object obj) {
            final ChildA<?, ?, ?> a = (ChildA<?, ?, ?>)obj;
            if (!getChildStuff().getClass().equals(a.getChildStuff().getClass())) {
                return false;
            }
            if (!getParentStuff().getClass().equals(a.getParentStuff().getClass())) {
                return false;
            }
            return getChildStuff().equals(a.getChildStuff()) && getParentStuff().equals(a.getParentStuff());
        }
    }

    public void testHiddenFieldsWithDifferentType() {
        xstream.alias("child-a", ChildA.class);
        xstream.alias("parent-a", ParentA.class);
        final ChildA<String, String, String> childA = new ChildA<>();
        childA.getChildStuff().put("hello", "world");
        childA.getParentStuff().add("woo");
        final String expected = ""
            + "<child-a>\n"
            + "  <stuff defined-in=\"parent-a\">\n"
            + "    <string>woo</string>\n"
            + "  </stuff>\n"
            + "  <stuff>\n"
            + "    <entry>\n"
            + "      <string>hello</string>\n"
            + "      <string>world</string>\n"
            + "    </entry>\n"
            + "  </stuff>\n"
            + "</child-a>";
        assertBothWays(childA, expected);
    }
}
