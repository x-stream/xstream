/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2010, 2011, 2012, 2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020, 2021 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 09. May 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.core;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.AttributedString;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.core.util.Base64JavaUtilCodec;
import com.thoughtworks.xstream.core.util.CustomObjectOutputStream;
import com.thoughtworks.xstream.core.util.DependencyInjectionFactory;
import com.thoughtworks.xstream.core.util.PresortedMap;
import com.thoughtworks.xstream.core.util.PresortedSet;


public class JVM implements Caching {

    private ReflectionProvider reflectionProvider;

    private static final boolean isAWTAvailable;
    private static final boolean isSwingAvailable;
    private static final boolean isSQLAvailable;
    private static final boolean isUnnamedModule;
    private static final boolean canAllocateWithUnsafe;
    private static final boolean canWriteWithUnsafe;
    private static final boolean optimizedTreeSetAddAll;
    private static final boolean optimizedTreeMapPutAll;
    private static final boolean canParseUTCDateFormat;
    private static final boolean canParseISO8601TimeZoneInDateFormat;
    private static final boolean canCreateDerivedObjectOutputStream;

    private static final String vendor = System.getProperty("java.vm.vendor");
    private static final float majorJavaVersion = getMajorJavaVersion();
    private static final float DEFAULT_JAVA_VERSION = 1.8f;
    private static final boolean reverseFieldOrder = false;
    private static final Class<? extends ReflectionProvider> reflectionProviderType;
    @Deprecated
    private static final StringCodec base64Codec = new Base64JavaUtilCodec();

    static class Test {
        @SuppressWarnings("unused")
        private Object o;
        @SuppressWarnings("unused")
        private char c;
        @SuppressWarnings("unused")
        private byte b;
        @SuppressWarnings("unused")
        private short s;
        @SuppressWarnings("unused")
        private int i;
        @SuppressWarnings("unused")
        private long l;
        @SuppressWarnings("unused")
        private float f;
        @SuppressWarnings("unused")
        private double d;
        @SuppressWarnings("unused")
        private boolean bool;

        Test() {
            throw new UnsupportedOperationException();
        }
    }

    static {
        final Exception exception = new RuntimeException();
        exception.fillInStackTrace();
        final StackTraceElement[] stackTrace = exception.getStackTrace();
        isUnnamedModule = !stackTrace[0].toString().contains("xstream@"); // Java 9: getModule() == null

        boolean test = true;
        Object unsafe = null;
        try {
            final Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            final Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            unsafe = unsafeField.get(null);
            final Method allocateInstance = unsafeClass.getDeclaredMethod("allocateInstance", new Class[]{Class.class});
            allocateInstance.setAccessible(true);
            test = allocateInstance.invoke(unsafe, new Object[]{Test.class}) != null;
        } catch (final Exception | Error e) {
            test = false;
        }
        canAllocateWithUnsafe = test;
        test = false;
        Class<? extends ReflectionProvider> type = PureJavaReflectionProvider.class;
        if (canUseSunUnsafeReflectionProvider()) {
            Class<? extends ReflectionProvider> cls = loadClassForName(
                "com.thoughtworks.xstream.converters.reflection.SunUnsafeReflectionProvider");
            if (cls != null) {
                try {
                    final ReflectionProvider provider = DependencyInjectionFactory.newInstance(cls);
                    final Test t = (Test)provider.newInstance(Test.class);
                    try {
                        provider.writeField(t, "o", "object", Test.class);
                        provider.writeField(t, "c", Character.valueOf('c'), Test.class);
                        provider.writeField(t, "b", Byte.valueOf((byte)1), Test.class);
                        provider.writeField(t, "s", Short.valueOf((short)1), Test.class);
                        provider.writeField(t, "i", Integer.valueOf(1), Test.class);
                        provider.writeField(t, "l", Long.valueOf(1), Test.class);
                        provider.writeField(t, "f", Float.valueOf(1), Test.class);
                        provider.writeField(t, "d", Double.valueOf(1), Test.class);
                        provider.writeField(t, "bool", Boolean.TRUE, Test.class);
                        test = true;
                    } catch (final IncompatibleClassChangeError | ObjectAccessException e) {
                        cls = null;
                    }
                    if (cls == null) {
                        cls = loadClassForName(
                            "com.thoughtworks.xstream.converters.reflection.SunLimitedUnsafeReflectionProvider");
                    }
                    type = cls;
                } catch (final ObjectAccessException e) {
                }
            }
        }
        reflectionProviderType = type;
        canWriteWithUnsafe = test;
        final Comparator<Object> comparator = new Comparator<Object>() {
            @Override
            public int compare(final Object o1, final Object o2) {
                throw new RuntimeException();
            }
        };
        final SortedMap<Object, Object> map = new PresortedMap<>(comparator);
        map.put("one", null);
        map.put("two", null);
        try {
            new TreeMap<>(comparator).putAll(map);
            test = true;
        } catch (final RuntimeException e) {
            test = false;
        }
        optimizedTreeMapPutAll = test;
        final SortedSet<Object> set = new PresortedSet<>(comparator);
        set.addAll(map.keySet());
        try {
            new TreeSet<>(comparator).addAll(set);
            test = true;
        } catch (final RuntimeException e) {
            test = false;
        }
        optimizedTreeSetAddAll = test;
        try {
            new SimpleDateFormat("z").parse("UTC");
            test = true;
        } catch (final ParseException | RuntimeException e) {
            test = false;
        }
        canParseUTCDateFormat = test;
        try {
            new SimpleDateFormat("X").parse("Z");
            test = true;
        } catch (final ParseException | RuntimeException e) {
            test = false;
        }
        canParseISO8601TimeZoneInDateFormat = test;
        try {
            @SuppressWarnings("resource")
            final CustomObjectOutputStream stream = new CustomObjectOutputStream(null, null);
            test = stream != null;
        } catch (final RuntimeException | IOException e) {
            test = false;
        }
        canCreateDerivedObjectOutputStream = test;

        isAWTAvailable = loadClassForName("java.awt.Color", false) != null;
        isSwingAvailable = loadClassForName("javax.swing.LookAndFeel", false) != null;
        isSQLAvailable = loadClassForName("java.sql.Date") != null;
    }

    /**
     * @deprecated As of 1.4.5 use the static methods of JVM.
     */
    @Deprecated
    public JVM() {
    }

    /**
     * Parses the java version system property to determine the major java version, i.e. 1.x
     *
     * @return A float of the form 1.x
     */
    private static final float getMajorJavaVersion() {
        try {
            return isAndroid() ? 8f : Float.parseFloat(System.getProperty("java.specification.version"));
        } catch (final NumberFormatException e) {
            // Some JVMs may not conform to the x.y.z java.version format
            return DEFAULT_JAVA_VERSION;
        }
    }

    /**
     * @deprecated As of 1.4.4, minimal JDK version is 1.8 already
     */
    @Deprecated
    public static boolean is14() {
        return isVersion(4);
    }

    /**
     * @deprecated As of 1.4.4, minimal JDK version is 1.8 already
     */
    @Deprecated
    public static boolean is15() {
        return isVersion(5);
    }

    /**
     * @deprecated As of 1.4.4, minimal JDK version is 1.8 already
     */
    @Deprecated
    public static boolean is16() {
        return isVersion(6);
    }

    /**
     * @since 1.4
     * @deprecated As of 1.4.10, minimal JDK version is 1.8 already
     */
    @Deprecated
    public static boolean is17() {
        return isVersion(7);
    }

    /**
     * @since 1.4
     * @deprecated As of 1.4.11 use {@link #isVersion(int)}.
     */
    @Deprecated
    public static boolean is18() {
        return isVersion(8);
    }

    /**
     * @since 1.4.8
     * @deprecated As of 1.4.10 use {@link #isVersion(int)}.
     */
    @Deprecated
    public static boolean is19() {
        return majorJavaVersion >= 1.9f;
    }

    /**
     * @since 1.4.10
     * @deprecated As of 1.4.11 use {@link #isVersion(int)}
     */
    @Deprecated
    public static boolean is9() {
        return isVersion(9);
    }

    /**
     * Checks current runtime against provided major Java version.
     *
     * @param version the requested major Java version
     * @return true if current runtime is at least the provided major version
     * @since 1.4.11
     */
    public static boolean isVersion(final int version) {
        if (version < 1) {
            throw new IllegalArgumentException("Java version range starts with at least 1.");
        }
        final float v = majorJavaVersion < 9 ? 1f + version * 0.1f : version;
        return majorJavaVersion >= v;
    }

    private static boolean isIBM() {
        return vendor.contains("IBM");
    }

    /**
     * @since 1.4
     */
    private static boolean isAndroid() {
        return vendor.contains("Android"); // and version 19 (4.4 KitKat) for Java 7
    }

    /**
     * Load a XStream class for the given name.
     * <p>
     * This method is not meant to use loading arbitrary classes. It is used by XStream bootstrap until it is able to
     * use the user provided or the default {@link ClassLoader}.
     * </p>
     *
     * @since 1.4.5
     */
    public static <T> Class<? extends T> loadClassForName(final String name) {
        return loadClassForName(name, true);
    }

    /**
     * @deprecated As of 1.4.5 use {@link #loadClassForName(String)}
     */
    @Deprecated
    public <T> Class<? extends T> loadClass(final String name) {
        return loadClassForName(name, true);
    }

    /**
     * Load a XStream class for the given name.
     * <p>
     * This method is not meant to use loading arbitrary classes. It is used by XStream bootstrap until it is able to
     * use the user provided or the default {@link ClassLoader}.
     * </p>
     *
     * @since 1.4.5
     */
    public static <T> Class<? extends T> loadClassForName(final String name, final boolean initialize) {
        try {
            @SuppressWarnings("unchecked")
            final Class<? extends T> clazz = (Class<? extends T>)Class.forName(name, initialize, JVM.class
                .getClassLoader());
            return clazz;
        } catch (final LinkageError | ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * @since 1.4.4
     * @deprecated As of 1.4.5 use {@link #loadClassForName(String, boolean)}
     */
    @Deprecated
    public <T> Class<? extends T> loadClass(final String name, final boolean initialize) {
        return loadClassForName(name, initialize);
    }

    /**
     * Create the best matching ReflectionProvider.
     *
     * @return a new instance
     * @since 1.4.5
     */
    public static ReflectionProvider newReflectionProvider() {
        return DependencyInjectionFactory.newInstance(reflectionProviderType);
    }

    /**
     * Create the best matching ReflectionProvider.
     *
     * @param dictionary the FieldDictionary to use by the ReflectionProvider
     * @return a new instance
     * @since 1.4.5
     */
    public static ReflectionProvider newReflectionProvider(final FieldDictionary dictionary) {
        return DependencyInjectionFactory.newInstance(reflectionProviderType, dictionary);
    }

    /**
     * Get the XMLInputFactory implementation used normally by the current Java runtime as standard.
     * <p>
     * In contrast to XMLInputFactory.newFactory() this method will ignore any implementations provided with the system
     * property <em>javax.xml.stream.XMLInputFactory</em>, implementations configured in <em>lib/stax.properties</em> or
     * registered with the Service API.
     * </p>
     *
     * @return the XMLInputFactory implementation or null
     * @throws ClassNotFoundException if the standard class cannot be found
     * @since 1.4.5
     */
    @SuppressWarnings("unchecked")
    public static Class<? extends XMLInputFactory> getStaxInputFactory() throws ClassNotFoundException {
        if (isIBM()) {
            return (Class<? extends XMLInputFactory>)Class.forName("com.ibm.xml.xlxp.api.stax.XMLInputFactoryImpl");
        } else {
            return (Class<? extends XMLInputFactory>)Class.forName("com.sun.xml.internal.stream.XMLInputFactoryImpl");
        }
    }

    /**
     * Get the XMLOutputFactory implementation used normally by the current Java runtime as standard.
     * <p>
     * In contrast to XMLOutputFactory.newFactory() this method will ignore any implementations provided with the system
     * property <em>javax.xml.stream.XMLOutputFactory</em>, implementations configured in <em>lib/stax.properties</em>
     * or registered with the Service API.
     * </p>
     *
     * @return the XMLOutputFactory implementation or null
     * @throws ClassNotFoundException if the standard class cannot be found
     * @since 1.4.5
     */
    @SuppressWarnings("unchecked")
    public static Class<? extends XMLOutputFactory> getStaxOutputFactory() throws ClassNotFoundException {
        if (isIBM()) {
            return (Class<? extends XMLOutputFactory>)Class.forName("com.ibm.xml.xlxp.api.stax.XMLOutputFactoryImpl");
        } else {
            return (Class<? extends XMLOutputFactory>)Class.forName("com.sun.xml.internal.stream.XMLOutputFactoryImpl");
        }
    }

    /**
     * Get an available Base64 implementation. Prefers java.util.Base64 over DataTypeConverter from JAXB over XStream's
     * own implementation.
     * <p>
     * Since XStream 1.5 requires Java 8 as minimum it can always use the Base84 implementation of the Java runtime.
     *
     * @return a Base64 codec implementation
     * @since 1.4.11
     * @deprecated As of upcoming, no longer required
     */
    @Deprecated
    public static StringCodec getBase64Codec() {
        return base64Codec;
    }

    /**
     * @deprecated As of 1.4.5 use {@link #newReflectionProvider()}
     */
    @Deprecated
    public synchronized ReflectionProvider bestReflectionProvider() {
        if (reflectionProvider == null) {
            reflectionProvider = newReflectionProvider();
        }
        return reflectionProvider;
    }

    private static boolean canUseSunUnsafeReflectionProvider() {
        return canAllocateWithUnsafe;
    }

    private static boolean canUseSunLimitedUnsafeReflectionProvider() {
        return canWriteWithUnsafe;
    }

    /**
     * @deprecated As of 1.4.5
     */
    @Deprecated
    public static boolean reverseFieldDefinition() {
        return reverseFieldOrder;
    }

    /**
     * Checks if AWT is available.
     *
     * @since 1.4.5
     */
    public static boolean isAWTAvailable() {
        return isAWTAvailable;
    }

    /**
     * Checks if the JVM supports AWT.
     *
     * @deprecated As of 1.4.5 use {@link #isAWTAvailable()}
     */
    @Deprecated
    public boolean supportsAWT() {
        return isAWTAvailable;
    }

    /**
     * Checks if Swing is available.
     *
     * @since 1.4.5
     */
    public static boolean isSwingAvailable() {
        return isSwingAvailable;
    }

    /**
     * Checks if the JVM supports Swing.
     *
     * @deprecated As of 1.4.5 use {@link #isSwingAvailable()}
     */
    @Deprecated
    public boolean supportsSwing() {
        return isSwingAvailable;
    }

    /**
     * Checks if SQL is available.
     *
     * @since 1.4.5
     */
    public static boolean isSQLAvailable() {
        return isSQLAvailable;
    }

    /**
     * Checks for running in the unnamed module for Java 9 or greater.
     *
     * @return true for Java 8 or later or when XStream is running as part of the unnamed module in Java 9 or higher
     * @since upcoming
     */
    public static boolean isUnnamedModule() {
        return isUnnamedModule;
    }

    /**
     * Checks if the JVM supports SQL.
     *
     * @deprecated As of 1.4.5 use {@link #isSQLAvailable()}
     */
    @Deprecated
    public boolean supportsSQL() {
        return isSQLAvailable;
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
     * @since 1.4.8
     */
    public static boolean canParseISO8601TimeZoneInDateFormat() {
        return canParseISO8601TimeZoneInDateFormat;
    }

    /**
     * @since 1.4.6
     */
    public static boolean canCreateDerivedObjectOutputStream() {
        return canCreateDerivedObjectOutputStream;
    }

    /**
     * @deprecated As of 1.4.5 no functionality
     */
    @Deprecated
    @Override
    public void flushCache() {
    }

    public static void main(final String... args) {
        boolean reverseJDK = false;
        Field[] fields = AttributedString.class.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getName().equals("text")) {
                reverseJDK = i > 3;
                break;
            }
        }

        boolean reverseLocal = false;
        fields = Test.class.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getName().equals("o")) {
                reverseLocal = i > 3;
                break;
            }
        }

        String staxInputFactory = null;
        try {
            staxInputFactory = getStaxInputFactory().getName();
        } catch (final ClassNotFoundException e) {
            staxInputFactory = e.getMessage();
        } catch (final NullPointerException e) {
        }

        String staxOutputFactory = null;
        try {
            staxOutputFactory = getStaxOutputFactory().getName();
        } catch (final ClassNotFoundException e) {
            staxOutputFactory = e.getMessage();
        } catch (final NullPointerException e) {
        }

        System.out.println("XStream JVM diagnostics");
        System.out.println("java.specification.version: " + System.getProperty("java.specification.version"));
        System.out.println("java.specification.vendor: " + System.getProperty("java.specification.vendor"));
        System.out.println("java.specification.name: " + System.getProperty("java.specification.name"));
        System.out.println("java.vm.vendor: " + vendor);
        System.out.println("java.vendor: " + System.getProperty("java.vendor"));
        System.out.println("java.vm.name: " + System.getProperty("java.vm.name"));
        System.out.println("Version: " + majorJavaVersion);
        System.out.println("XStream in unnamed module: " + isUnnamedModule());
        System.out.println("XStream support for enhanced Mode: " + canUseSunUnsafeReflectionProvider());
        System.out.println("XStream support for reduced Mode: " + canUseSunLimitedUnsafeReflectionProvider());
        System.out.println("Supports AWT: " + isAWTAvailable());
        System.out.println("Supports Swing: " + isSwingAvailable());
        System.out.println("Supports SQL: " + isSQLAvailable());
        System.out.println("Java Beans EventHandler present: " + (loadClassForName("java.beans.EventHandler") != null));
        System.out.println("Standard StAX XMLInputFactory: " + staxInputFactory);
        System.out.println("Standard StAX XMLOutputFactory: " + staxOutputFactory);
        System.out.println("Optimized TreeSet.addAll: " + hasOptimizedTreeSetAddAll());
        System.out.println("Optimized TreeMap.putAll: " + hasOptimizedTreeMapPutAll());
        System.out.println("Can parse UTC date format: " + canParseUTCDateFormat());
        System.out.println("Can create derive ObjectOutputStream: " + canCreateDerivedObjectOutputStream());
        System.out.println("Reverse field order detected for JDK: " + reverseJDK);
        System.out.println("Reverse field order detected (only if JVM class itself has been compiled): "
            + reverseLocal);
    }
}
