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

import com.thoughtworks.acceptance.AbstractAcceptanceTest;


public class JavaClassConverterTest extends AbstractAcceptanceTest {

    public void testHandlesPrimitivesAndWrappers() {
        assertBothWays(int.class, "<java-class>int</java-class>");
        assertBothWays(Integer.class, "<java-class>java.lang.Integer</java-class>");

        assertBothWays(boolean.class, "<java-class>boolean</java-class>");
        assertBothWays(Boolean.class, "<java-class>java.lang.Boolean</java-class>");

        assertBothWays(void.class, "<java-class>void</java-class>");
        assertBothWays(Void.class, "<java-class>java.lang.Void</java-class>");
    }

    public static class A {}

    public void testHandlesArrays() {
        assertBothWays(A[].class,
            "<java-class>[Lcom.thoughtworks.xstream.converters.extended.JavaClassConverterTest$A;</java-class>");
        assertBothWays(int[].class, "<java-class>[I</java-class>");
    }

    public void testHandlesMultidimensioanlArrays() {
        assertBothWays(A[][].class,
            "<java-class>[[Lcom.thoughtworks.xstream.converters.extended.JavaClassConverterTest$A;</java-class>");
        assertBothWays(A[][][][].class,
            "<java-class>[[[[Lcom.thoughtworks.xstream.converters.extended.JavaClassConverterTest$A;</java-class>");

        assertBothWays(int[][].class, "<java-class>[[I</java-class>");
        assertBothWays(int[][][][].class, "<java-class>[[[[I</java-class>");
    }

    public static class B {}

    public void testResolvesUnloadedClassThatIsAnArray() {
        // subtleties in classloaders make this an awkward one
        final String input =
                "<java-class>[Lcom.thoughtworks.xstream.converters.extended.JavaClassConverterTest$B;</java-class>";
        final Class<?> result = xstream.<Class<?>>fromXML(input);
        assertEquals("[Lcom.thoughtworks.xstream.converters.extended.JavaClassConverterTest$B;", result.getName());
        assertTrue("Should be an array", result.isArray());
        assertEquals("com.thoughtworks.xstream.converters.extended.JavaClassConverterTest$B", result
            .getComponentType()
            .getName());
    }

    public void testHandlesJavaClassArray() {
        xstream.registerConverter(new JavaClassConverter(xstream.getMapper()) {});

        final Class<?>[] classes = new Class[]{ //
            Object.class, //
            Comparable.class, //
            null, //
            Throwable.class //
        };

        assertBothWays(classes, ""
            + "<java-class-array>\n"
            + "  <java-class>object</java-class>\n"
            + "  <java-class>java.lang.Comparable</java-class>\n"
            + "  <null/>\n"
            + "  <java-class>java.lang.Throwable</java-class>\n"
            + "</java-class-array>");
    }
}
