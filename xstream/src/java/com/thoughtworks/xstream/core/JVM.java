package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;

import java.security.AccessControlException;

public class JVM {

    private ReflectionProvider reflectionProvider;

    private static final float majorJavaVersion;

    static {
       majorJavaVersion = Float.parseFloat(System.getProperty("java.version").substring(0, 3));
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

    public Class loadClass(String name) {
        try {
            return Class.forName(name, false, getClass().getClassLoader());
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
		return (isSun() || isApple() || isHPUX()) && is14() && loadClass("sun.misc.Unsafe") != null;
	}

}
