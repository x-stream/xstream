/*
 * Copyright (C) 2017, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 29. July 2017 by Joerg Schaible
 */
package com.thoughtworks.xstream.benchmark.jmh;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.DatatypeConverter;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

import com.thoughtworks.xstream.core.StringCodec;
import com.thoughtworks.xstream.core.util.Base64Encoder;


/**
 * Benchmark for different Base64 encoder and decoder implementations. Each implementation can be used by the
 * EncodedByteArrayConverter.
 *
 * @author J&ouml;rg Schaible
 * @since 1.4.11
 */
@BenchmarkMode(Mode.AverageTime)
@Fork(value = 1)
@Measurement(iterations = 16)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Threads(4)
@Warmup(iterations = 5)
public class Base64Benchmark {

    /**
     * Enumeration for the operation of the base 64 coder.
     *
     * @since 1.4.11
     */
    public static enum Operation {
        /**
         * Encoding operation.
         */
        encode,
        /**
         * Decoding operation.
         */
        decode
    }

    /**
     * Enumeration for the different base 64 coder.
     *
     * @since 1.4.11
     */
    public static enum Codec implements StringCodec {
        /**
         * XStream's own codec.
         */
        xstreamInternal {
            final private Base64Encoder codec = new Base64Encoder(false);

            @Override
            public byte[] decode(final String base64) {
                return codec.decode(base64);
            }

            @Override
            public String encode(final byte[] data) {
                return codec.encode(data);
            }
        },
        /**
         * Codec of JAXB, part of the Java runtime since 1.6.
         */
        dataTypeConverter {

            @Override
            public byte[] decode(final String base64) {
                return DatatypeConverter.parseBase64Binary(base64);
            }

            @Override
            public String encode(final byte[] data) {
                return DatatypeConverter.printBase64Binary(data);
            }

        },
        /**
         * Official codec of the Java runtime since 1.8.
         */
        javaUtil {
            final private java.util.Base64.Decoder decoder = java.util.Base64.getDecoder();
            final private java.util.Base64.Encoder encoder = java.util.Base64.getEncoder();

            @Override
            public byte[] decode(final String base64) {
                return decoder.decode(base64);
            }

            @Override
            public String encode(final byte[] data) {
                return encoder.encodeToString(data);
            }
        },
        /**
         * Codec of Apache Commons Codec.
         */
        commonsCodec {

            @Override
            public byte[] decode(final String base64) {
                return org.apache.commons.codec.binary.Base64.decodeBase64(base64);
            }

            @Override
            public String encode(final byte[] data) {
                return org.apache.commons.codec.binary.Base64.encodeBase64String(data);
            }
        },
        /**
         * Codec of MiGBase64, repackaged by brsanthu.
         */
        migBase {

            @Override
            public byte[] decode(final String base64) {
                return com.migcomponents.migbase64.Base64.decode(base64);
            }

            @Override
            public String encode(final byte[] data) {
                return com.migcomponents.migbase64.Base64.encodeToString(data, false);
            }

        };
        
        // to please Eclipse with JDK 5 target
        public abstract byte[] decode(String encoded);
        public abstract String encode(byte[] data);
    }

    /**
     * Enumeration for the different data sets.
     *
     * @since 1.4.11
     */
    public static enum Data {
        /**
         * Small data (16 bytes).
         */
        small(16),
        /**
         * Medium data (4KB)
         */
        medium(4096),
        /**
         * Big data (1MB)
         */
        big(1024 * 1024);
        private Data(final int length) {
            data = getRandomBytes(length);
            base64 = Codec.xstreamInternal.encode(data);
        }

        private final String base64;
        private final byte[] data;

        /**
         * Get the encoded data as string.
         *
         * @return the encoded string
         * @since 1.4.11
         */
        public String getBase64() {
            return base64;
        }

        /**
         * Get the data to encode
         *
         * @return the data
         * @since 1.4.11
         */
        public byte[] getData() {
            return data;
        }

        static {
            final int lengths[] = {0, 1, 2, 3, 8, 65, 256, 4095, 1000000};
            final byte data[][] = new byte[lengths.length][];
            final String base64[] = new String[lengths.length];
            for (int i = 0; i < lengths.length; ++i) {
                final int length = lengths[i];
                final byte[] orig = getRandomBytes(length);
                for (final Codec codec : EnumSet.allOf(Codec.class)) {
                    if (base64[i] == null) {
                        base64[i] = codec.encode(orig);
                        data[i] = codec.decode(base64[i]);
                        assert Arrays.equals(data[i], orig);
                    } else {
                        assert base64[i].equals(codec.encode(orig)) : "Base64 differs for "
                            + codec
                            + ": <"
                            + base64[i]
                            + "> vs. <"
                            + codec.encode(orig)
                            + ">";
                    }
                }
            }
        }

        private static byte[] getRandomBytes(final int length) {
            final char[] ch = new char[length];
            for (int j = 0; j < length; ++j) {
                ch[j] = Character.valueOf((char)(Math.round(Math.random() * 254) + 1));
            }
            final byte[] orig = new String(ch).getBytes(StandardCharsets.UTF_8);
            return orig;
        }
    }

    @Param
    private Codec codec;
    @Param
    private Operation operation;
    @Param
    private Data data;

    /**
     * Encode and decode data.
     *
     * @since 1.4.11
     */
    @Benchmark
    public void run() {
        switch (operation) {
        case encode:
            codec.encode(data.getData());
            break;
        case decode:
            codec.decode(data.getBase64());
            break;
        }
    }
}
