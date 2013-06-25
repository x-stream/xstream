/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2010, 2011, 2012, 2013 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 09. May 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.core.util.PresortedMap;
import com.thoughtworks.xstream.core.util.PresortedSet;
import com.thoughtworks.xstream.core.util.WeakCache;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessControlException;
import java.text.AttributedString;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class JVM implements Caching {

    private ReflectionProvider reflectionProvider;
    private transient Map loaderCache = new WeakCache(new HashMap());
    
    private final boolean supportsAWT = loadClass("java.awt.Color", false) != null;
    private final boolean supportsSwing = loadClass("javax.swing.LookAndFeel", false) != null;
    private final boolean supportsSQL = loadClass("java.sql.Date") != null;
    private final boolean supportsSunStAX = loadClass("com.sun.xml.internal.stream.XMLInputFactoryImpl") != null;
    
    private static final boolean canAllocateWithUnsafe;
    private static final boolean optimizedTreeSetAddAll;
    private static final boolean optimizedTreeMapPutAll;
    private static final boolean canParseUTCDateFormat;

    private static final String vendor = System.getProperty("java.vm.vendor");
    private static final float majorJavaVersion = getMajorJavaVersion();
    private static final boolean reverseFieldOrder = false;

    private static final float DEFAULT_JAVA_VERSION = 1.4f;

    static class Broken {
        Broken() {
           throw new UnsupportedOperationException(); 
        }
    }
    
    static {
        boolean test = true;
        try {
            Class unsafeClass = Class.forName("sun.misc.Unsafe");
            Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            Object unsafe = unsafeField.get(null);
            Method allocateInstance = unsafeClass.getDeclaredMethod("allocateInstance", new Class[]{Class.class});
            allocateInstance.setAccessible(true);
            test = allocateInstance.invoke(unsafe, new Object[]{Broken.class}) != null;
        } catch (Exception e) {
            test = false;
        } catch (Error e) {
            test = false;
        }
        canAllocateWithUnsafe = test;
        Comparator comparator = new Comparator() {
            public int compare(Object o1, Object o2) {
                throw new RuntimeException();
            }
        };
        SortedMap map = new PresortedMap(comparator);
        map.put("one", null);
        map.put("two", null);
        try {
            new TreeMap(comparator).putAll(map);
        } catch (RuntimeException e) {
            test = false;
        }
        optimizedTreeMapPutAll = test;
        SortedSet set = new PresortedSet(comparator);
        set.addAll(map.keySet());
        try {
            new TreeSet(comparator).addAll(set);
            test = true;
        } catch (RuntimeException e) {
            test = false;
        }
        optimizedTreeSetAddAll = test;
        try {
            new SimpleDateFormat("z").parse("UTC");
            test = true;
        } catch (ParseException e) {
            test = false;
        }
        canParseUTCDateFormat = test;
    }
    
    /**
     * Parses the java version system property to determine the major java version,
     * i.e. 1.x
     *
     * @return A float of the form 1.x
     */
    private static final float getMajorJavaVersion() {
        try {
            return isAndroid() ? 1.5f : Float.parseFloat(System.getProperty("java.specification.version"));
        } catch ( NumberFormatException e ){
            // Some JVMs may not conform to the x.y.z java.version format
            return DEFAULT_JAVA_VERSION;
        }
    }

    /**
     * @deprecated As of 1.4.4, minimal JDK version is 1.4 already
     */
    public static boolean is14() {
        return majorJavaVersion >= 1.4f;
    }

    /**
     * @deprecated As of 1.4.4, minimal JDK version will be 1.6 for next major release
     */
    public static boolean is15() {
        return majorJavaVersion >= 1.5f;
    }

    /**
     * @deprecated As of 1.4.4, minimal JDK version will be 1.6 for next major release
     */
    public static boolean is16() {
        return majorJavaVersion >= 1.6f;
    }

    /**
     * @since 1.4
     */
    public static boolean is17() {
        return majorJavaVersion >= 1.7f;
    }

    /**
     * @since 1.4
     */
    public static boolean is18() {
        return majorJavaVersion >= 1.8f;
    }

    private static boolean isIBM() {
    	return vendor.indexOf("IBM") != -1;
    }

    /**
     * @since 1.4
     */
    private static boolean isAndroid() {
        return vendor.indexOf("Android") != -1;
    }

    public Class loadClass(String name) {
        return loadClass(name, true);
    }

    /**
     * @since 1.4.4
     */
    public Class loadClass(String name, boolean initialize) {
        Class cached = (Class) loaderCache.get(name);
        if (cached != null) {
            return cached;
        }
        try {
            Class clazz = Class.forName(name, initialize, getClass().getClassLoader());
            loaderCache.put(name, clazz);
            return clazz;
        } catch (LinkageError e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public synchronized ReflectionProvider bestReflectionProvider() {
        if (reflectionProvider == null) {
            try {
                String className = null;
                if (canUseSun14ReflectionProvider()) {
                    className = "com.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider";
                }
                if (className != null) {
                    Class cls = loadClass(className);
                    if (cls != null) {
                        reflectionProvider = (ReflectionProvider) cls.newInstance();
                    }
                }
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            } catch (AccessControlException e) {
                // thrown when trying to access sun.misc package in Applet context.
            }
            if (reflectionProvider == null) {
                reflectionProvider = new PureJavaReflectionProvider();
            }
        }
        return reflectionProvider;
    }

    private boolean canUseSun14ReflectionProvider() {
        return canAllocateWithUnsafe && is14();
    }

    public static boolean reverseFieldDefinition() {
        return reverseFieldOrder;
    }

    /**
     * Checks if the jvm supports awt.
     */
    public boolean supportsAWT() {
        return this.supportsAWT;
    }

    /**
     * Checks if the jvm supports swing.
     */
    public boolean supportsSwing() {
        return this.supportsSwing;
    }

    /**
     * Checks if the jvm supports sql.
     */
    public boolean supportsSQL() {
        return this.supportsSQL;
    }

    /**
     * Checks if the jvm supports StAX implementation by Sun.
     * 
     * @since upcoming
     */
    public boolean supportsSunStAX() {
        return this.supportsSunStAX;
    }
    
    /**
     * Checks if TreeSet.addAll is optimized for SortedSet argument.
     * 
     * @since 1.4
     */
    public static boolean hasOptimizedTreeSetAddAll() {
        return optimizedTreeSetAddAll;
    }
    
    /**
     * Checks if TreeMap.putAll is optimized for SortedMap argument.
     * 
     * @since 1.4
     */
    public static boolean hasOptimizedTreeMapPutAll() {
        return optimizedTreeMapPutAll;
    }

    public static boolean canParseUTCDateFormat() {
        return canParseUTCDateFormat;
    }

    public void flushCache() {
        loaderCache.clear();
    }
    
    private Object readResolve() {
        loaderCache = new WeakCache(new HashMap());
        return this;
    }
    
    public static void main(String[] args) {
        boolean reverse = false;
        Field[] fields = AttributedString.class.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getName().equals("text")) {
                reverse = i > 3;
                break;
            }
        }
        if (reverse) {
            fields = JVM.class.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].getName().equals("reflectionProvider")) {
                    reverse = i > 2;
                    break;
                }
            }
        }

        JVM jvm = new JVM();
        System.out.println("XStream JVM diagnostics");
        System.out.println("java.specification.version: " + System.getProperty("java.specification.version"));
        System.out.println("java.specification.vendor: " + System.getProperty("java.specification.vendor"));
        System.out.println("java.specification.name: " + System.getProperty("java.specification.name"));
        System.out.println("java.vm.vendor: " + vendor);
        System.out.println("java.vendor: " + System.getProperty("java.vendor"));
        System.out.println("java.vm.name: " + System.getProperty("java.vm.name"));
        System.out.println("Version: " + majorJavaVersion);
        System.out.println("XStream support for enhanced Mode: " + jvm.canUseSun14ReflectionProvider());
        System.out.println("Supports AWT: " + jvm.supportsAWT());
        System.out.println("Supports Swing: " + jvm.supportsSwing());
        System.out.println("Supports SQL: " + jvm.supportsSunStAX());
        System.out.println("Optimized TreeSet.addAll: " + hasOptimizedTreeSetAddAll());
        System.out.println("Optimized TreeMap.putAll: " + hasOptimizedTreeMapPutAll());
        System.out.println("Can parse UTC date format: " + canParseUTCDateFormat());
        System.out.println("Reverse field order detected (only if JVM class itself has been compiled): " + reverse);
    }
}
