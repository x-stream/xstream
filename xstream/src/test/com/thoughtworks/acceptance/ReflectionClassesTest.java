/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.thoughtworks.acceptance;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class ReflectionClassesTest extends AbstractAcceptanceTest {
    public static class StupidObject {
        public String aField;
        public static int aStaticField;

        public StupidObject(final String arg) {
        }

        public void aMethod(final String something) {
        }

        public void aMethod(final int cheese) {
        }

        public static void aStaticMethod(final boolean bool) {
        }
    }

    public void testReflectionMethod() throws NoSuchMethodException {
        final Method method = StupidObject.class.getMethod("aMethod", new Class[]{String.class});

        final String expected = ""
            + "<method>\n"
            + "  <class>com.thoughtworks.acceptance.ReflectionClassesTest$StupidObject</class>\n"
            + "  <name>aMethod</name>\n"
            + "  <parameter-types>\n"
            + "    <class>java.lang.String</class>\n"
            + "  </parameter-types>\n"
            + "</method>";

        assertBothWays(method, expected);
    }

    public void testReflectionStaticMethod() throws NoSuchMethodException {
        final Method method = StupidObject.class.getMethod("aStaticMethod", new Class[]{boolean.class});

        final String expected = ""
            + "<method>\n"
            + "  <class>com.thoughtworks.acceptance.ReflectionClassesTest$StupidObject</class>\n"
            + "  <name>aStaticMethod</name>\n"
            + "  <parameter-types>\n"
            + "    <class>boolean</class>\n"
            + "  </parameter-types>\n"
            + "</method>";

        assertBothWays(method, expected);
    }

    public void testReflectionConstructor() throws NoSuchMethodException {
        final Constructor<StupidObject> constructor = StupidObject.class.getConstructor(String.class);

        final String expected = ""
            + "<constructor>\n"
            + "  <class>com.thoughtworks.acceptance.ReflectionClassesTest$StupidObject</class>\n"
            + "  <parameter-types>\n"
            + "    <class>java.lang.String</class>\n"
            + "  </parameter-types>\n"
            + "</constructor>";

        assertBothWays(constructor, expected);
    }

    public void testSupportsPrimitiveTypes() {
        assertBothWays(int.class, "<java-class>int</java-class>");
    }

    public void testReflectionField() throws NoSuchFieldException {
        final Field field = StupidObject.class.getField("aField");

        final String expected = ""
            + "<field>\n"
            + "  <name>aField</name>\n"
            + "  <clazz>com.thoughtworks.acceptance.ReflectionClassesTest$StupidObject</clazz>\n"
            + "</field>";

        assertBothWays(field, expected);
    }

    public void testReflectionStaticField() throws NoSuchFieldException {
        final Field field = StupidObject.class.getField("aStaticField");

        final String expected = ""
            + "<field>\n"
            + "  <name>aStaticField</name>\n"
            + "  <clazz>com.thoughtworks.acceptance.ReflectionClassesTest$StupidObject</clazz>\n"
            + "</field>";

        assertBothWays(field, expected);
    }

    public void testReflectionFieldMigrationFrom13() throws NoSuchFieldException {
        final Field field = StupidObject.class.getField("aField");

        final String xml = ""
            + "<field>\n"
            + "  <override>false</override>\n"
            + "  <clazz>com.thoughtworks.acceptance.ReflectionClassesTest$StupidObject</clazz>\n"
            + "  <slot>0</slot>\n"
            + "  <name>aField</name>\n"
            + "  <type>java.lang.String</type>\n"
            + "  <modifiers>1</modifiers>\n"
            + "  <root>\n"
            + "    <override>false</override>\n"
            + "    <clazz>com.thoughtworks.acceptance.ReflectionClassesTest$StupidObject</clazz>\n"
            + "    <slot>0</slot>\n"
            + "    <name>aField</name>\n"
            + "    <type>java.lang.String</type>\n"
            + "    <modifiers>1</modifiers>\n"
            + "  </root>\n"
            + "</field>";

        assertEquals(field, xstream.fromXML(xml));
    }
}
