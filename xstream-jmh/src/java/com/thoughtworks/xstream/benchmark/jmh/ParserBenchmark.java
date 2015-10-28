/*
 * Copyright (C) 2015 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 25. October 2015 by Joerg Schaible
 */
package com.thoughtworks.xstream.benchmark.jmh;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.BenchmarkParams;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.binary.BinaryStreamDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.KXml2Driver;
import com.thoughtworks.xstream.io.xml.Xpp3Driver;


/**
 * Benchmark for the different {@link HierarchicalStreamDriver} implementations.
 *
 * @author J&ouml;rg Schaible
 */
@BenchmarkMode(Mode.AverageTime)
@Fork(value = 1)
@Measurement(iterations = 10)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Threads(1)
@Warmup(iterations = 3)
public class ParserBenchmark {

    public enum DriverFactory {
        DOM(new DomDriver()), //
        Xpp3(new Xpp3Driver()), //
        kXML2(new KXml2Driver()), //
        Binary(new BinaryStreamDriver());

        private final HierarchicalStreamDriver driver;

        private DriverFactory(final HierarchicalStreamDriver driver) {
            this.driver = driver;
        }

        public HierarchicalStreamDriver getDriver() {
            return driver;
        }
    }

    public enum DataFactory {
        String100k {
            private int length;
            private String start;
            private String end;

            @Override
            public void writeData(final HierarchicalStreamWriter writer) {
                int length = 1024 * 100;
                final StringBuilder builder = new StringBuilder(length);
                int i = 0;
                while (length > 0) {
                    final int codePoint = i % Character.MAX_CODE_POINT;
                    if (Character.isLetterOrDigit(codePoint)) {
                        builder.appendCodePoint(codePoint);
                        --length;
                    }
                    ++i;
                }
                final String s = builder.toString();
                this.length = s.length();
                start = s.substring(0, 100);
                end = s.substring(this.length - 100);

                writer.startNode("string");
                writer.setValue(s);
                writer.endNode();
            }

            @Override
            public void checkData(final Object o) {
                final String s = String.class.cast(o);
                assert length == s.length() : String100k + " fails length";
                assert start.equals(s.substring(0, 100)) : String100k + " fails start";
                assert start.equals(s.substring(length - 100)) : String100k + " fails end";
            }
        },
        NestedList {
            private static final int DEPTH = 500;
            private List<Integer> list;

            @Override
            public void writeData(final HierarchicalStreamWriter writer) {
                for (int i = 0; i < DEPTH; ++i) {
                    writer.startNode("list");
                }
                list = new ArrayList<>(Arrays.asList(42, 7, 3, -17));
                for (final Integer i : list) {
                    writer.startNode("int");
                    writer.setValue(i.toString());
                    writer.endNode();
                }
                for (int i = 0; i < DEPTH; ++i) {
                    writer.endNode();
                }
            }

            @Override
            public void checkData(final Object o) {
                List<?> list = List.class.cast(o);
                int depth = DEPTH;
                while (depth-- > 1) {
                    assert list.size() == 1 : NestedList + " fails list size";
                    list = List.class.cast(list.get(0));
                }
                assert this.list.equals(list) : NestedList + " fails inner list";
            }
        };
        public abstract void writeData(HierarchicalStreamWriter writer);

        public abstract void checkData(Object o);
    }

    @Param({"Xpp3", "kXML2", "DOM", "Binary"})
    private DriverFactory driverFactory;
    private DataFactory dataFactory;
    private byte[] data;
    private XStream xstream;
    private HierarchicalStreamDriver driver;

    @Setup
    public void init() {
        xstream = new XStream();
        xstream.setMode(XStream.NO_REFERENCES);
        driver = driverFactory.getDriver();
    }

    @Setup(Level.Trial)
    public void setUp(final BenchmarkParams params) {
        final String benchmark = params.getBenchmark();
        dataFactory = DataFactory.valueOf(benchmark.substring(benchmark.lastIndexOf('.') + 6));
        
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(1024 * 1024);
        final HierarchicalStreamWriter writer = driver.createWriter(baos);
        dataFactory.writeData(writer);
        writer.close();
        data = baos.toByteArray();
    }

    @Benchmark
    public void parseNestedList() {
        final Object o = xstream.unmarshal(driver.createReader(new ByteArrayInputStream(data)));
        dataFactory.checkData(o);
    }

    @Benchmark
    public void parseString100k() {
        final Object o = xstream.unmarshal(driver.createReader(new ByteArrayInputStream(data)));
        dataFactory.checkData(o);
    }
}
