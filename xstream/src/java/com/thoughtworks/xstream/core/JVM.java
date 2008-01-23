/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008 XStream Committers.
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

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.security.AccessControlException;
import java.text.AttributedString;
import java.util.HashMap;
import java.util.Map;

public class JVM {

    private ReflectionProvider reflectionProvider;
    private transient Map loaderCache = new HashMap();
    
    private final boolean supportsAWT = loadClass("java.awt.Color") != null;
    private final boolean supportsSwing = loadClass("javax.swing.LookAndFeel") != null;
    private final boolean supportsSQL = loadClass("java.sql.Date") != null; 

    private static final String vendor = System.getProperty("java.vm.vendor");
    private static final float majorJavaVersion = getMajorJavaVersion();
    private static final boolean reverseFieldOrder = isHarmony() || (isIBM() && !is15());

    static final float DEFAULT_JAVA_VERSION = 1.3f;

    /**
     * Parses the java version system property to determine the major java version,
     * i.e. 1.x
     *
     * @return A float of the form 1.x
     */
    private static final float getMajorJavaVersion() {
        try {
            return Float.parseFloat(System.getProperty("java.specification.version"));
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

    public static boolean is16() {
        return majorJavaVersion >= 1.6f;
    }

    private static boolean isSun() {
        return vendor.indexOf("Sun") != -1;
    }

    private static boolean isApple() {
        return vendor.indexOf("Apple") != -1;
    }

    private static boolean isHPUX() {
        return vendor.indexOf("Hewlett-Packard Company") != -1;
    }

    private static boolean isIBM() {
    	return vendor.indexOf("IBM") != -1;
    }

    private static boolean isBlackdown() {
        return vendor.indexOf("Blackdown") != -1;
    }

    private static boolean isHarmony() {
        return vendor.indexOf("Apache Software Foundation") != -1;
    }

    /*
     * Support for sun.misc.Unsafe and sun.reflect.ReflectionFactory is present
     * in JRockit versions R25.1.0 and later, both 1.4.2 and 5.0 (and in future
     * 6.0 builds).
     */
    private static boolean isBEAWithUnsafeSupport() {
        // This property should be "BEA Systems, Inc."
        if (vendor.indexOf("BEA") != -1) {

            /*
             * Recent 1.4.2 and 5.0 versions of JRockit have a java.vm.version
             * string starting with the "R" JVM version number, i.e.
             * "R26.2.0-38-57237-1.5.0_06-20060209..."
             */
            String vmVersion = System.getProperty("java.vm.version");
            if (vmVersion.startsWith("R")) {
                /*
                 * We *could* also check that it's R26 or later, but that is
                 * implicitly true
                 */
                return true;
            }

            /*
             * For older JRockit versions we can check java.vm.info. JRockit
             * 1.4.2 R24 -> "Native Threads, GC strategy: parallel" and JRockit
             * 5.0 R25 -> "R25.2.0-28".
             */
            String vmInfo = System.getProperty("java.vm.info");
            if (vmInfo != null) {
                // R25.1 or R25.2 supports Unsafe, other versions do not
                return (vmInfo.startsWith("R25.1") || vmInfo
                        .startsWith("R25.2"));
            }
        }
        // If non-BEA, or possibly some very old JRockit version
        return false;
    }
    
    private static boolean isHitachi() {
        return vendor.indexOf("Hitachi") != -1;
    }
    
    private static boolean isSAP() {
        return vendor.indexOf("SAP AG") != -1;
    }

    public Class loadClass(String name) {
        try {
            WeakReference reference = (WeakReference) loaderCache.get(name);
            if (reference != null) {
                Class cached = (Class) reference.get();
                if (cached != null) {
                    return cached;
                }
            }
            
            Class clazz = Class.forName(name, false, getClass().getClassLoader());
            loaderCache.put(name, new WeakReference(clazz));
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
                } else if (canUseHarmonyReflectionProvider()) {
                    String cls = "com.thoughtworks.xstream.converters.reflection.HarmonyReflectionProvider";
                    reflectionProvider = (ReflectionProvider) loadClass(cls).newInstance();
                } 
                if (reflectionProvider == null) {
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
        return (isSun() || isApple() || isHPUX() || isIBM() || isBlackdown() || isBEAWithUnsafeSupport() || isHitachi() || isSAP()) && is14() && loadClass("sun.misc.Unsafe") != null;
    }

    private boolean canUseHarmonyReflectionProvider() {
        return isHarmony();
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

    private Object readResolve() {
        loaderCache = new HashMap();
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
        System.out.println("java.vm.vendor: " + vendor);
        System.out.println("Version: " + majorJavaVersion);
        System.out.println("XStream support for enhanced Mode: " + (jvm.canUseSun14ReflectionProvider() || jvm.canUseHarmonyReflectionProvider()));
        System.out.println("Supports AWT: " + jvm.supportsAWT());
        System.out.println("Supports SQL: " + jvm.supportsSQL());
        System.out.println("Reverse field order detected (may have failed): " + reverse);
    }
}
