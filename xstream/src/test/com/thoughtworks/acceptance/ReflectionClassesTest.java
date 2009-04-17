/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 08. April 2004 by Joe Walnes
 */
package com.thoughtworks.acceptance;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionClassesTest extends AbstractAcceptanceTest {
    public static class StupidObject {
        public String aField;
        public static int aStaticField;
        public StupidObject(String arg) {
        }

        public void aMethod(String something) {
        }

        public void aMethod(int cheese) {
        }

        public static void aStaticMethod(boolean bool) {
        }
    }

    public void testReflectionMethod() throws NoSuchMethodException {
        Method method = StupidObject.class.getMethod("aMethod", new Class[]{String.class});

        String expected =
                "<method>\n" +
                "  <class>com.thoughtworks.acceptance.ReflectionClassesTest$StupidObject</class>\n" +
                "  <name>aMethod</name>\n" +
                "  <parameter-types>\n" +
                "    <class>java.lang.String</class>\n" +
                "  </parameter-types>\n" +
                "</method>";

        assertBothWays(method, expected);
    }

    public void testReflectionStaticMethod() throws NoSuchMethodException {
        Method method = StupidObject.class.getMethod("aStaticMethod", new Class[]{boolean.class});

        String expected =
                "<method>\n" +
                "  <class>com.thoughtworks.acceptance.ReflectionClassesTest$StupidObject</class>\n" +
                "  <name>aStaticMethod</name>\n" +
                "  <parameter-types>\n" +
                "    <class>boolean</class>\n" +
                "  </parameter-types>\n" +
                "</method>";

        assertBothWays(method, expected);
    }

    public void testReflectionConstructor() throws NoSuchMethodException {
        Constructor constructor = StupidObject.class.getConstructor(new Class[]{String.class});

        String expected =
                "<constructor>\n" +
                "  <class>com.thoughtworks.acceptance.ReflectionClassesTest$StupidObject</class>\n" +
                "  <parameter-types>\n" +
                "    <class>java.lang.String</class>\n" +
                "  </parameter-types>\n" +
                "</constructor>";

        assertBothWays(constructor, expected);
    }

    public void testSupportsPrimitiveTypes() {
        assertBothWays(int.class, "<java-class>int</java-class>");
    }

    public void testReflectionField() throws NoSuchFieldException {
        Field field = StupidObject.class.getField("aField");

        String expected =
                "<field>\n" +
                "  <name>aField</name>\n" +
                "  <class>com.thoughtworks.acceptance.ReflectionClassesTest$StupidObject</class>\n" +
                "</field>";

        assertBothWays(field, expected);
    }

    public void testReflectionStaticField() throws NoSuchFieldException {
        Field field = StupidObject.class.getField("aStaticField");

        String expected =
                "<field>\n" +
                "  <name>aStaticField</name>\n" +
                "  <class>com.thoughtworks.acceptance.ReflectionClassesTest$StupidObject</class>\n" +
                "</field>";

        assertBothWays(field, expected);
    }

}
