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
