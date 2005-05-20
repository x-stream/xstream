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

    public void testGetMajorJavaVersion() {
        assertEquals("1.5.0_01", 1.5f, JVM.getMajorJavaVersion("1.5.0_01"), 0.001f);
        assertEquals("1.4.2_05", 1.4f, JVM.getMajorJavaVersion("1.4.2_05"), 0.001f);
        assertEquals("1.3.1_08", 1.3f, JVM.getMajorJavaVersion("1.3.1_08"), 0.001f);
        assertEquals("PERC(R)", JVM.DEFAULT_JAVA_VERSION, JVM.getMajorJavaVersion("PERC(R) VM 4.1.0519 Platform: [Linux]"), 0.001f);
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
