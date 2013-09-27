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

import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.core.util.DependencyInjectionFactory;
import com.thoughtworks.xstream.core.util.PresortedMap;
import com.thoughtworks.xstream.core.util.PresortedSet;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessControlException;
import java.text.AttributedString;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class JVM implements Caching {

    private ReflectionProvider reflectionProvider;
    
    private static final boolean isAWTAvailable;
    private static final boolean isSwingAvailable;
    private static final boolean isSQLAvailable;
    private static final boolean canAllocateWithUnsafe;
    private static final boolean optimizedTreeSetAddAll;
    private static final boolean optimizedTreeMapPutAll;
    private static final boolean canParseUTCDateFormat;

    private static final String vendor = System.getProperty("java.vm.vendor");
    private static final float majorJavaVersion = getMajorJavaVersion();
    private static final float DEFAULT_JAVA_VERSION = 1.4f;
    private static final boolean reverseFieldOrder = false;
    private static final Class reflectionProviderType;

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
            test = true;
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
        
        isAWTAvailable = loadClassForName("java.awt.Color", false) != null;
        isSwingAvailable = loadClassForName("javax.swing.LookAndFeel", false) != null;
        isSQLAvailable = loadClassForName("java.sql.Date") != null;
        
        Class type = PureJavaReflectionProvider.class;
        if (canUseSun14ReflectionProvider()) {
            Class cls = loadClassForName("com.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider");
            if (cls != null) {
                try {
                    DependencyInjectionFactory.newInstance(cls, null);
                    type = cls;
                } catch (ObjectAccessException e) {
                }
            }
        }
        reflectionProviderType = type;
    }
    
    /**
     * @deprecated As of 1.4.5 use the static methods of JVM.
     */
    public JVM() {
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

    /**
     * Load a XStream class for the given name.
     * 
     * <p>This method is not meant to use loading arbitrary classes. It is used by XStream bootstrap
     * until it is able to use the user provided or the default {@link ClassLoader}.</p>
     * 
     * @since 1.4.5
     */
    public static Class loadClassForName(String name) {
        return loadClassForName(name, true);
    }

    /**
     * @deprecated As of 1.4.5 use {@link #loadClassForName(String)}
     */
    public Class loadClass(String name) {
        return loadClassForName(name, true);
    }

    /**
     * Load a XStream class for the given name.
     * 
     * <p>This method is not meant to use loading arbitrary classes. It is used by XStream bootstrap
     * until it is able to use the user provided or the default {@link ClassLoader}.</p>
     * 
     * @since 1.4.5
     */
    public static Class loadClassForName(String name, boolean initialize) {
        try {
            Class clazz = Class.forName(name, initialize, JVM.class.getClassLoader());
            return clazz;
        } catch (LinkageError e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * @since 1.4.4
     * @deprecated As of 1.4.5 use {@link #loadClassForName(String, boolean)}
     */
    public Class loadClass(String name, boolean initialize) {
        return loadClassForName(name, initialize);
    }
    
    /**
     * Create the best matching ReflectionProvider.
     * 
     * @return a new instance
     * @since 1.4.5
     */
    public static ReflectionProvider newReflectionProvider() {
        return (ReflectionProvider)DependencyInjectionFactory.newInstance(reflectionProviderType, null);
    }
    
    /**
     * Create the best matching ReflectionProvider.
     *
     * @param dictionary the FieldDictionary to use by the ReflectionProvider
     * @return a new instance
     * @since 1.4.5
     */
    public static ReflectionProvider newReflectionProvider(FieldDictionary dictionary) {
        return (ReflectionProvider)DependencyInjectionFactory.newInstance(reflectionProviderType, new Object[]{ dictionary });
    }
    
    /**
     * Get the XMLInputFactory implementation used normally by the current Java runtime as
     * standard.
     * <p>
     * In contrast to XMLInputFactory.newFactory() this method will ignore any implementations
     * provided with the system property <em>javax.xml.stream.XMLInputFactory</em>,
     * implementations configured in <em>lib/stax.properties</em> or registered with the Service
     * API.
     * </p>
     * 
     * @return the XMLInputFactory implementation or null
     * @throws ClassNotFoundException if the standard class cannot be found
     * @since 1.4.5
     */
    public static Class getStaxInputFactory() throws ClassNotFoundException {
        if (is16()) {
            if (isIBM()) {
                return Class.forName("com.ibm.xml.xlxp.api.stax.XMLInputFactoryImpl");
            } else {
                return Class.forName("com.sun.xml.internal.stream.XMLInputFactoryImpl");
            }
        }
        return null;
    }
    
    /**
     * Get the XMLOutputFactory implementation used normally by the current Java runtime as
     * standard.
     * <p>
     * In contrast to XMLOutputFactory.newFactory() this method will ignore any implementations
     * provided with the system property <em>javax.xml.stream.XMLOutputFactory</em>,
     * implementations configured in <em>lib/stax.properties</em> or registered with the Service
     * API.
     * </p>
     * 
     * @return the XMLOutputFactory implementation or null
     * @throws ClassNotFoundException if the standard class cannot be found
     * @since 1.4.5
     */
    public static Class getStaxOutputFactory() throws ClassNotFoundException {
        if (is16()) {
            if (isIBM()) {
                return Class.forName("com.ibm.xml.xlxp.api.stax.XMLOutputFactoryImpl");
            } else {
                return Class.forName("com.sun.xml.internal.stream.XMLOutputFactoryImpl");
            }
        }
        return null;
    }
    
    /**
     * @deprecated As of 1.4.5 use {@link #newReflectionProvider()}
     */
    public synchronized ReflectionProvider bestReflectionProvider() {
        if (reflectionProvider == null) {
            try {
                String className = null;
                if (canUseSun14ReflectionProvider()) {
                    className = "com.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider";
                }
                if (className != null) {
                    Class cls = loadClassForName(className);
                    if (cls != null) {
                        reflectionProvider = (ReflectionProvider) cls.newInstance();
                    }
                }
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            } catch (AccessControlException e) {
                // thrown when trying to access sun.misc package in Applet context
            }
            if (reflectionProvider == null) {
                reflectionProvider = new PureJavaReflectionProvider();
            }
        }
        return reflectionProvider;
    }

    private static boolean canUseSun14ReflectionProvider() {
        return canAllocateWithUnsafe && is14();
    }

    /**
     * @deprecated As of 1.4.5
     */
    public static boolean reverseFieldDefinition() {
        return reverseFieldOrder;
    }

    /**
     * Checks if AWT is available.
     * @since 1.4.5
     */
    public static boolean isAWTAvailable() {
        return isAWTAvailable;
    }

    /**
     * Checks if the jvm supports awt.
     * @deprecated As of 1.4.5 use {@link #isAWTAvailable()}
     */
    public boolean supportsAWT() {
        return this.isAWTAvailable;
    }

    /**
     * Checks if Swing is available.
     * @since 1.4.5
     */
    public static boolean isSwingAvailable() {
        return isSwingAvailable;
    }

    /**
     * Checks if the jvm supports swing.
     * @deprecated As of 1.4.5 use {@link #isSwingAvailable()}
     */
    public boolean supportsSwing() {
        return this.isSwingAvailable;
    }

    /**
     * Checks if SQL is available.
     * @since 1.4.5
     */
    public static boolean isSQLAvailable() {
        return isSQLAvailable;
    }

    /**
     * Checks if the jvm supports sql.
     * @deprecated As of 1.4.5 use {@link #isSQLAvailable()}
     */
    public boolean supportsSQL() {
        return this.isSQLAvailable;
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

    /**
     * @deprecated As of 1.4.5 no functionality
     */
    public void flushCache() {
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

        String staxInputFactory = null;
        try {
            staxInputFactory = getStaxInputFactory().getName();
        } catch (ClassNotFoundException e) {
            staxInputFactory = e.getMessage();
        } catch (NullPointerException e) {
        }
        
        String staxOutputFactory = null;
        try {
            staxOutputFactory = getStaxOutputFactory().getName();
        } catch (ClassNotFoundException e) {
            staxOutputFactory = e.getMessage();
        } catch (NullPointerException e) {
        }
        
        System.out.println("XStream JVM diagnostics");
        System.out.println("java.specification.version: " + System.getProperty("java.specification.version"));
        System.out.println("java.specification.vendor: " + System.getProperty("java.specification.vendor"));
        System.out.println("java.specification.name: " + System.getProperty("java.specification.name"));
        System.out.println("java.vm.vendor: " + vendor);
        System.out.println("java.vendor: " + System.getProperty("java.vendor"));
        System.out.println("java.vm.name: " + System.getProperty("java.vm.name"));
        System.out.println("Version: " + majorJavaVersion);
        System.out.println("XStream support for enhanced Mode: " + canUseSun14ReflectionProvider());
        System.out.println("Supports AWT: " + isAWTAvailable());
        System.out.println("Supports Swing: " + isSwingAvailable());
        System.out.println("Supports SQL: " + isSQLAvailable());
        System.out.println("Standard StAX XMLInputFactory: " + staxInputFactory);
        System.out.println("Standard StAX XMLOutputFactory: " + staxOutputFactory);
        System.out.println("Optimized TreeSet.addAll: " + hasOptimizedTreeSetAddAll());
        System.out.println("Optimized TreeMap.putAll: " + hasOptimizedTreeMapPutAll());
        System.out.println("Can parse UTC date format: " + canParseUTCDateFormat());
        System.out.println("Reverse field order detected (only if JVM class itself has been compiled): " + reverse);
    }
}
