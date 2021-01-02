/*
 * Copyright (C) 2015, 2017, 2021 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 8. November 2015 by Joerg Schaible
 */
package com.thoughtworks.xstream.benchmark.jmh;

import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.BenchmarkParams;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.thoughtworks.xstream.core.util.WeakCache;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import com.thoughtworks.xstream.io.xml.MXParserDriver;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.Xpp3Driver;
import com.thoughtworks.xstream.security.ArrayTypePermission;
import com.thoughtworks.xstream.security.NoTypePermission;


/**
 * Benchmark for different StringConverter implementations.
 *
 * @author J&ouml;rg Schaible
 * @since 1.4.9
 */
@BenchmarkMode(Mode.AverageTime)
@Fork(value = 1)
@Measurement(iterations = 16)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Threads(4)
@Warmup(iterations = 5)
public class StringConverterBenchmark {

    private XStream xstream;
    private String xml;

    /**
     * No memory usage for cache, but any string is a separate instance after deserialization. Memory consumption of the
     * deserialized array is nearly 3 times compared to a converter that caches and reuses the strings.
     *
     * @since 1.4.9
     */
    public static final class NonCachingStringConverter extends AbstractSingleValueConverter {

        @Override
        public boolean canConvert(@SuppressWarnings("rawtypes") final Class type) {
            return type == String.class;
        }

        @Override
        public Object fromString(final String str) {
            return str;
        }
    }

    /**
     * Cache based on String.intern(). Uses PermGenSpace for Java 7 and below.
     *
     * @since 1.4.9
     */
    public static final class InternStringConverter extends AbstractSingleValueConverter {

        @Override
        public boolean canConvert(@SuppressWarnings("rawtypes") final Class type) {
            return type == String.class;
        }

        @Override
        public Object fromString(final String str) {
            return str.intern();
        }
    }

    /**
     * Cache based on a synchronized WeakHashMap with weak keys. Ensures that the deserialized strings vanish when the
     * deserialized object is GC'ed.
     *
     * @since 1.4.9
     */
    public class SynchronizedWeakCacheStringConverter extends AbstractSingleValueConverter {

        private final Map<String, String> cache;
        private final int lengthLimit;

        private SynchronizedWeakCacheStringConverter(final Map<String, String> map, final int lengthLimit) {
            cache = map;
            this.lengthLimit = lengthLimit;
        }

        /**
         * Constructs a SynchronizedWeakCacheStringConverter.
         *
         * @param lengthLimit length limit for cached strings
         * @since 1.4.9
         */
        @SuppressWarnings("unchecked")
        public SynchronizedWeakCacheStringConverter(final int lengthLimit) {
            this(Collections.synchronizedMap(new WeakCache()), lengthLimit);
        }

        @Override
        public boolean canConvert(@SuppressWarnings("rawtypes") final Class type) {
            return type.equals(String.class);
        }

        @Override
        public Object fromString(final String str) {
            if (cache != null && str != null && (lengthLimit < 0 || str.length() <= lengthLimit)) {
                String s = cache.get(str);

                if (s == null) {
                    // fill cache
                    cache.put(str, str);

                    s = str;
                }

                return s;
            } else {
                return str;
            }
        }
    }

    /**
     * Cache based on a ConcurrentMap. Cache is never flushed.
     *
     * @since 1.4.9
     */
    public class ConcurrentHashMapStringConverter extends AbstractSingleValueConverter {

        private final ConcurrentMap<String, String> cache;
        private final int lengthLimit;

        private ConcurrentHashMapStringConverter(final ConcurrentMap<String, String> map, final int lengthLimit) {
            cache = map;
            this.lengthLimit = lengthLimit;
        }

        /**
         * Constructs a ConcurrentHashMapStringConverter.
         *
         * @param lengthLimit length limit for cached strings
         * @since 1.4.9
         */
        public ConcurrentHashMapStringConverter(final int lengthLimit) {
            this(new ConcurrentHashMap<String, String>(), lengthLimit);
        }

        @Override
        public boolean canConvert(@SuppressWarnings("rawtypes") final Class type) {
            return type.equals(String.class);
        }

        @Override
        public Object fromString(final String str) {
            if (cache != null && str != null && (lengthLimit < 0 || str.length() <= lengthLimit)) {
                final String s = cache.putIfAbsent(str, str);
                return s == null ? str : s;
            } else {
                return str;
            }
        }
    }

    /**
     * Initialize the XML string to deserialize.
     *
     * @since 1.4.9
     */
    @Setup
    public void init() {
        final String array[] = new String[300];
        for (int i = 0; i < 100;) {
            array[i] = String.valueOf(++i);
        }
        for (int i = 100; i < 200;) {
            array[i] = "Binary value " + i + ": " + Integer.toString(++i, 2);
        }
        for (int i = 200; i < 300;) {
            array[i++] = UUID.randomUUID().toString().replace('-', ':');
        }

        final StringWriter stringWriter = new StringWriter();
        final PrettyPrintWriter writer = new CompactWriter(stringWriter);
        writer.startNode("string-array");
        for (int i = 0; i < 10000; ++i) {
            writer.startNode("string");
            final String s;
            if ((i & 1) == 1) {
                s = array[(i >> 1) % 100];
            } else if ((i & 2) == 2) {
                s = array[100 + (i >> 2) % 100];
            } else if ((i & 4) == 4) {
                s = array[200 + (i >> 3) % 100];
            } else {
                s = "Random UUID: " + UUID.randomUUID().toString();
            }
            writer.setValue(s);
            writer.endNode();
        }
        writer.endNode();
        writer.close();
        xml = stringWriter.toString();
    }

    /**
     * Setup the data to deserialize.
     *
     * @param params the parameters of the benchmark
     * @since 1.4.9
     */
    @Setup(Level.Trial)
    public void setUp(final BenchmarkParams params) {
        final String benchmark = params.getBenchmark();
        final SingleValueConverter converter;
        final String name = benchmark.substring(StringConverterBenchmark.class.getName().length() + 1);
        if ("nonCaching".equals(name)) {
            converter = new NonCachingStringConverter();
        } else if ("intern".equals(name)) {
            converter = new InternStringConverter();
        } else if ("unlimitedSynchronizedWeakCache".equals(name)) {
            converter = new SynchronizedWeakCacheStringConverter(Integer.MAX_VALUE);
        } else if ("limitedSynchronizedWeakCache".equals(name)) {
            converter = new SynchronizedWeakCacheStringConverter(UUID.randomUUID().toString().length() + 2);
        } else if ("unlimitedConcurrentMap".equals(name)) {
            converter = new SynchronizedWeakCacheStringConverter(Integer.MAX_VALUE);
        } else if ("limitedConcurrentMap".equals(name)) {
            converter = new SynchronizedWeakCacheStringConverter(UUID.randomUUID().toString().length() + 2);
        } else {
            throw new IllegalStateException("Unsupported benchmark type: " + benchmark);
        }
        xstream = new XStream(new MXParserDriver());
        xstream.addPermission(NoTypePermission.NONE);
        xstream.addPermission(ArrayTypePermission.ARRAYS);
        xstream.allowTypes(new Class[] {String.class});
        xstream.registerConverter(converter);
    }

    /**
     * No cache for deserialized strings, each string is an own instance.
     *
     * @since 1.4.9
     */
    @Benchmark
    public void nonCaching() {
        run();
    }

    /**
     * Any string is stored also in the String's internal memory space.
     *
     * @since 1.4.9
     */
    @Benchmark
    public void intern() {
        run();
    }

    /**
     * Any string is cached in a weak entry.
     *
     * @since 1.4.9
     */
    @Benchmark
    public void unlimitedSynchronizedWeakCache() {
        run();
    }

    /**
     * Strings of 38 characters or less are cached in a weak entry.
     *
     * @since 1.4.9
     */
    @Benchmark
    public void limitedSynchronizedWeakCache() {
        run();
    }

    /**
     * Any string is cached in a concurrent map.
     *
     * @since 1.4.9
     */
    @Benchmark
    public void unlimitedConcurrentMap() {
        run();
    }

    /**
     * Strings of 38 characters or less are cached in a concurrent map.
     *
     * @since 1.4.9
     */
    @Benchmark
    public void limitedConcurrentMap() {
        run();
    }

    private void run() {
        final String[] array = (String[])xstream.fromXML(xml);
        assert array.length == 10000 : "array length is " + array.length;
        assert array[1].equals("1") : "2nd element was: " + array[1];
        assert array[9999].equals("100") : "last element was: " + array[9999];
    }
}
