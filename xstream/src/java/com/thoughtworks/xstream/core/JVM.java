package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;

import java.util.HashMap;
import java.util.Map;

public class JVM {

    private Map classCache = new HashMap();
    private static ReflectionProvider reflectionProvider;

    public static boolean is14() {
        float majorJavaVersion = Float.parseFloat(System.getProperty("java.version").substring(0, 3));
        return majorJavaVersion >= 1.4f;
    }

    public Class loadClass(String name) {
        if (classCache.containsKey(name)) {
            return (Class) classCache.get(name);
        } else {
            try {
                Class cls = Class.forName(name, false, getClass().getClassLoader());
                classCache.put(name, cls);
                return cls;
            } catch (ClassNotFoundException e) {
                return null;
            }
        }
    }

    public synchronized ReflectionProvider bestReflectionProvider() {
        if (reflectionProvider == null) {
            try {
                if (loadClass("sun.misc.Unsafe") != null) {
                    String cls = "com.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider";
                    reflectionProvider = (ReflectionProvider) loadClass(cls).newInstance();
                } else {
                    reflectionProvider = new PureJavaReflectionProvider();
                }
            } catch (InstantiationException e) {
                reflectionProvider = new PureJavaReflectionProvider();
            } catch (IllegalAccessException e) {
                reflectionProvider = new PureJavaReflectionProvider();
            }
        }
        return reflectionProvider;
    }

}
