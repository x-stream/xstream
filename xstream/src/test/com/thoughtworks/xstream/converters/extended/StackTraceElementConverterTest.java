package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;

/**
 * @author <a href="mailto:boxley@thoughtworks.com">B. K. Oxley (binkley)</a>
 * @author Joe Walnes
 */
public class StackTraceElementConverterTest extends AbstractAcceptanceTest {

    private StackTraceElementFactory factory = new StackTraceElementFactory();

    public void testSerializesStackTraceElement() {
        StackTraceElement trace = factory.unknownSourceElement("com.blah.SomeClass", "someMethod");
        String expectedXml = "<trace>com.blah.SomeClass.someMethod(Unknown Source)</trace>";
        assertBothWays(trace, expectedXml);
    }

    public void testIncludesDebugInformation() {
        StackTraceElement trace = factory.element("com.blah.SomeClass", "someMethod", "SomeClass.java", 22);
        String expectedXml = "<trace>com.blah.SomeClass.someMethod(SomeClass.java:22)</trace>";
        assertBothWays(trace, expectedXml);
    }

    public void testIncludesPartialDebugInformation() {
        StackTraceElement trace = factory.element("com.blah.SomeClass", "someMethod", "SomeClass.java");
        String expectedXml = "<trace>com.blah.SomeClass.someMethod(SomeClass.java)</trace>";
        assertBothWays(trace, expectedXml);
    }

    public void testIncludesNativeMethods() {
        StackTraceElement trace = factory.nativeMethodElement("com.blah.SomeClass", "someMethod");
        String expectedXml = "<trace>com.blah.SomeClass.someMethod(Native Method)</trace>";
        assertBothWays(trace, expectedXml);
    }

    public void testSupportsInnerClasses() {
        StackTraceElement trace = factory.unknownSourceElement("com.blah.SomeClass$Inner$2", "someMethod");
        String expectedXml = "<trace>com.blah.SomeClass$Inner$2.someMethod(Unknown Source)</trace>";
        assertBothWays(trace, expectedXml);
    }

}
