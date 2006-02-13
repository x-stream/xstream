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
