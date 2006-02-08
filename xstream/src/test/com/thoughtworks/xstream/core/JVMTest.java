package com.thoughtworks.xstream.core;

import junit.framework.TestCase;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class JVMTest extends TestCase {

    final static Method getMajorJavaVersion;
    static {
        try {
            getMajorJavaVersion = JVM.class.getDeclaredMethod("getMajorJavaVersion", new Class[]{String.class});
            getMajorJavaVersion.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new InternalError("Missing JVM.getMajorJavaVersion");
        }
    }

    public void testDoesNotInsantiateStaticBlocksWhenLoadingClasses() {
        try {
            new JVM().loadClass("com.thoughtworks.xstream.core.EvilClass");
        } catch (ExceptionInInitializerError error) {
            fail("Static block was called");
        }
    }

    public void testGetMajorJavaVersion() {
        assertEquals("1.5.0_01", 1.5f, getMajorJavaVersion("1.5.0_01"), 0.001f);
        assertEquals("1.4.2_05", 1.4f, getMajorJavaVersion("1.4.2_05"), 0.001f);
        assertEquals("1.3.1_08", 1.3f, getMajorJavaVersion("1.3.1_08"), 0.001f);
        assertEquals("PERC(R)", JVM.DEFAULT_JAVA_VERSION, getMajorJavaVersion("PERC(R) VM 4.1.0519 Platform: [Linux]"), 0.001f);
    }
    
    private float getMajorJavaVersion(String version) {
        try {
            Float f = (Float)getMajorJavaVersion.invoke(null, new Object[]{version});
            return f.floatValue();
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot call JVM.getMajorJavaVersion");
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Error calling JVM.getMajorJavaVersion");
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
