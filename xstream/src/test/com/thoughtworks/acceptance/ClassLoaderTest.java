package com.thoughtworks.acceptance;

public class ClassLoaderTest extends AbstractAcceptanceTest {

    private String classLoaderCall;

    public void testAllowsClassLoaderToBeOverriden() {
        xstream.setClassLoader(new MockClassLoader());
        assertEquals("hello", xstream.fromXML("<java.BANG.String>hello</java.BANG.String>"));
        assertEquals("java.BANG.String", classLoaderCall);
    }

    private class MockClassLoader extends ClassLoader {
        public Class loadClass(String name) {
            classLoaderCall = name;
            return String.class;
        }
    }
}
