/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 23. December 2004 by Mauro Talevi
 */
package com.thoughtworks.xstream.converters.extended;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;


public class JavaMethodConverterTest extends AbstractAcceptanceTest {

    public void testSupportsPublicMethods() throws Exception {
        final Method method = AnIntClass.class.getDeclaredMethod("setValue", Integer.TYPE);
        final String expected = "<method>\n"
            + "  <class>com.thoughtworks.xstream.converters.extended.JavaMethodConverterTest$AnIntClass</class>\n"
            + "  <name>setValue</name>\n"
            + "  <parameter-types>\n"
            + "    <class>int</class>\n"
            + "  </parameter-types>\n"
            + "</method>";
        assertBothWays(method, expected);
    }

    public void testSupportsPrivateMethods() throws NoSuchMethodException {
        final Method method = AnIntClass.class.getDeclaredMethod("privateMethod");
        final String expected = "<method>\n"
            + "  <class>com.thoughtworks.xstream.converters.extended.JavaMethodConverterTest$AnIntClass</class>\n"
            + "  <name>privateMethod</name>\n"
            + "  <parameter-types/>\n"
            + "</method>";
        assertBothWays(method, expected);
    }

    public void testSupportsConstructor() throws NoSuchMethodException {
        final Constructor<?> constructor = AnIntClass.class.getDeclaredConstructor(int.class);
        final String expected = "<constructor>\n"
            + "  <class>com.thoughtworks.xstream.converters.extended.JavaMethodConverterTest$AnIntClass</class>\n"
            + "  <parameter-types>\n"
            + "    <class>int</class>\n"
            + "  </parameter-types>\n"
            + "</constructor>";
        assertBothWays(constructor, expected);
    }

    static class AnIntClass {
        private int value = 0;

        protected AnIntClass(final int integer) {
            value = integer;
        }

        public int getValue() {
            return value;
        }

        public void setValue(final int integer) {
            value = integer;
        }

        @SuppressWarnings("unused")
        private void privateMethod() {
        }
    }
}
