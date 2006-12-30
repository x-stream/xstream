package com.thoughtworks.xstream.core;

import junit.framework.TestCase;

public class JVMTest extends TestCase {

    public void testDoesNotInsantiateStaticBlocksWhenLoadingClasses() {
        try {
            new JVM().loadClass("com.thoughtworks.xstream.core.EvilClass");
        } catch (ExceptionInInitializerError error) {
            fail("Static block was called");
        }
    }
}

class EvilClass {

    static {
        evil();
    }

    static void evil() {
        throw new RuntimeException("Evil");
    }
}
