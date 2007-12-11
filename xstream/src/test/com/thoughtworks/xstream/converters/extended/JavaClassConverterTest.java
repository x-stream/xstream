/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 16. February 2005 by Joe Walnes
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
        assertBothWays(int[].class,
                "<java-class>[I</java-class>");
    }

    public void testHandlesMultidimensioanlArrays() {
        assertBothWays(A[][].class,
                "<java-class>[[Lcom.thoughtworks.xstream.converters.extended.JavaClassConverterTest$A;</java-class>");
        assertBothWays(A[][][][].class,
                "<java-class>[[[[Lcom.thoughtworks.xstream.converters.extended.JavaClassConverterTest$A;</java-class>");

        assertBothWays(int[][].class,
                "<java-class>[[I</java-class>");
        assertBothWays(int[][][][].class,
                "<java-class>[[[[I</java-class>");
    }

    public static class B {}

    public void testResolvesUnloadedClassThatIsAnArray() {
        // subtleties in classloaders make this an awkward one
        String input = "<java-class>[Lcom.thoughtworks.xstream.converters.extended.JavaClassConverterTest$B;</java-class>";
        Class result = (Class) xstream.fromXML(input);
        assertEquals("[Lcom.thoughtworks.xstream.converters.extended.JavaClassConverterTest$B;", result.getName());
        assertTrue("Should be an array", result.isArray());
        assertEquals("com.thoughtworks.xstream.converters.extended.JavaClassConverterTest$B", result.getComponentType().getName());
    }
}
