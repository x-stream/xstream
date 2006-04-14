package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;

import java.lang.reflect.Field;
import java.security.AccessControlException;
import java.text.AttributedString;
import java.util.HashMap;
import java.util.Map;

public class JVM {

    private ReflectionProvider reflectionProvider;
    private Map loaderCache = new HashMap();

    private static final boolean reverseFieldOrder;
    private static final float majorJavaVersion = getMajorJavaVersion(System.getProperty("java.specification.version"));

    static final float DEFAULT_JAVA_VERSION = 1.3f;

    static {
        boolean reverse = false;
        final Field[] fields = AttributedString.class.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getName().equals("text")) {
                reverse = i > 3;
            }
        }
        reverseFieldOrder = reverse;
    }

    /**
     * Parses the java version system property to determine the major java version,
     * ie 1.x
     *
     * @param javaVersion the system property 'java.specification.version'
     * @return A float of the form 1.x
     */
    private static final float getMajorJavaVersion(String javaVersion) {
        try {
            return Float.parseFloat(javaVersion.substring(0, 3));
        } catch ( NumberFormatException e ){
            // Some JVMs may not conform to the x.y.z java.version format
            return DEFAULT_JAVA_VERSION;
        }
    }

    public static boolean is14() {
        return majorJavaVersion >= 1.4f;
    }

    public static boolean is15() {
        return majorJavaVersion >= 1.5f;
    }

    private static boolean isSun() {
        return System.getProperty("java.vm.vendor").indexOf("Sun") != -1;
    }

    private static boolean isApple() {
        return System.getProperty("java.vm.vendor").indexOf("Apple") != -1;
    }

    private static boolean isHPUX() {
        return System.getProperty("java.vm.vendor").indexOf("Hewlett-Packard Company") != -1;
    }

    private static boolean isIBM() {
    	return System.getProperty("java.vm.vendor").indexOf("IBM") != -1;
    }

    private static boolean isBlackdown() {
        return System.getProperty("java.vm.vendor").indexOf("Blackdown") != -1;
    }

    public Class loadClass(String name) {
        try {
            Class clazz = (Class)loaderCache.get(name);
            if (clazz == null) {
                clazz = Class.forName(name, false, getClass().getClassLoader());
                loaderCache.put(name, clazz);
            }
            return clazz;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public synchronized ReflectionProvider bestReflectionProvider() {
        if (reflectionProvider == null) {
            try {
                if ( canUseSun14ReflectionProvider() ) {
                    String cls = "com.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider";
                    reflectionProvider = (ReflectionProvider) loadClass(cls).newInstance();
                } else {
                    reflectionProvider = new PureJavaReflectionProvider();
                }
            } catch (InstantiationException e) {
                reflectionProvider = new PureJavaReflectionProvider();
            } catch (IllegalAccessException e) {
                reflectionProvider = new PureJavaReflectionProvider();
            } catch (AccessControlException e) {
                // thrown when trying to access sun.misc package in Applet context.
                reflectionProvider = new PureJavaReflectionProvider();
            }
        }
        return reflectionProvider;
    }

    private boolean canUseSun14ReflectionProvider() {
    	    return (isSun() || isApple() || isHPUX() || isIBM() || isBlackdown()) && is14() && loadClass("sun.misc.Unsafe") != null;
    }

    public static synchronized boolean reverseFieldDefinition() {
        return reverseFieldOrder;
    }

}
