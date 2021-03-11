/*
 * Copyright (C) 2015, 2017, 2021 XStream Committers.
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
import java.io.Writer;
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
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.xml.BEAStaxDriver;
import com.thoughtworks.xstream.io.xml.Dom4JDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.JDom2Driver;
import com.thoughtworks.xstream.io.xml.JDomDriver;
import com.thoughtworks.xstream.io.xml.KXml2Driver;
import com.thoughtworks.xstream.io.xml.MXParserDriver;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.StandardStaxDriver;
import com.thoughtworks.xstream.io.xml.WstxDriver;
import com.thoughtworks.xstream.io.xml.XomDriver;
import com.thoughtworks.xstream.io.xml.Xpp3Driver;
import com.thoughtworks.xstream.security.ArrayTypePermission;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;


/**
 * Benchmark for the different {@link HierarchicalStreamDriver} implementations.
 *
 * @author J&ouml;rg Schaible
 * @since 1.4.9
 */
@BenchmarkMode(Mode.AverageTime)
@Fork(value = 1)
@Measurement(iterations = 15)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Threads(1)
@Warmup(iterations = 5)
public class ParserBenchmark {

    /**
     * Driver factory. Enum values used as parameter for the parser benchmark methods.
     *
     * @author J&ouml;rg Schaible
     * @since 1.4.9
     */
    public enum DriverFactory {
        /**
         * Factory for the {@link MXParserDriver}.
         *
         * @since 1.4.16
         */
        MXParser(new MXParserDriver()), //
        /**
         * Factory for the {@link Xpp3Driver}.
         *
         * @since 1.4.9
         */
        Xpp3(new Xpp3Driver()), //
        /**
         * Factory for the {@link KXml2Driver}.
         *
         * @since 1.4.9
         */
        kXML2(new KXml2Driver()), //
        /**
         * Factory for the {@link StandardStaxDriver}.
         *
         * @since 1.4.9
         */
        JDKStax(new StandardStaxDriver()), //
        /**
         * Factory for the {@link WstxDriver}.
         *
         * @since 1.4.9
         */
        Woodstox(new WstxDriver()), //
        /**
         * Factory for the {@link BEAStaxDriver}.
         *
         * @since 1.4.9
         */
        BEAStax(new BEAStaxDriver()), //
        /**
         * Factory for the {@link DomDriver}.
         *
         * @since 1.4.9
         */
        DOM(new DomDriver()), //
        /**
         * Factory for the {@link Dom4JDriver}.
         *
         * @since 1.4.9
         */
        DOM4J(new Dom4JDriver() { // XML writer of DOM4J fails
            @Override
            public HierarchicalStreamWriter createWriter(final Writer out) {
                return new PrettyPrintWriter(out, getNameCoder());
            }
        }), //
        /**
         * Factory for the {@link JDomDriver}.
         *
         * @since 1.4.9
         */
        JDom(new JDomDriver()), //
        /**
         * Factory for the {@link JDom2Driver}.
         *
         * @since 1.4.9
         */
        JDom2(new JDom2Driver()), //
        /**
         * Factory for the {@link XomDriver}.
         *
         * @since 1.4.9
         */
        Xom(new XomDriver()), //
        /**
         * Factory for the {@link BinaryStreamDriver}.
         *
         * @since 1.4.9
         */
        Binary(new BinaryStreamDriver()), //
        /**
         * Factory for the {@link JettisonMappedXmlDriver}.
         *
         * @since 1.4.9
         */
        Jettison(new JettisonMappedXmlDriver());

        private final HierarchicalStreamDriver driver;

        private DriverFactory(final HierarchicalStreamDriver driver) {
            this.driver = driver;
        }

        /**
         * Request the driver of the instantiated factory.
         *
         * @return the driver
         * @since 1.4.9
         */
        public HierarchicalStreamDriver getDriver() {
            return driver;
        }
    }

    /**
     * Data factory. Enum values used as data generator and checker for the individual parser benchmark methods. Method
     * names define the data factory to use for the benchmark.
     *
     * @author J&ouml;rg Schaible
     * @since 1.4.9
     */
    public enum DataFactory {
        /**
         * A single element with a text of 100.000 characters.
         *
         * @author J&ouml;rg Schaible
         * @since 1.4.9
         */
        BigText {
            private int length;
            private String start;
            private String end;

            @Override
            public void writeData(final HierarchicalStreamWriter writer) {
                int length = 100000;
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
                assert length == s.length() : BigText + " fails length";
                assert start.equals(s.substring(0, 100)) : BigText + " fails start";
                assert end.equals(s.substring(length - 100)) : BigText + " fails end";
            }
        },
        /**
         * Nested list in list structure, 500 elements deep.
         *
         * @author J&ouml;rg Schaible
         * @since 1.4.9
         */
        NestedElements {
            private static final int DEPTH = 1000;
            private List<Integer> list;

            @Override
            public void writeData(final HierarchicalStreamWriter writer) {
                for (int i = 0; i < DEPTH; ++i) {
                    writer.startNode("list");
                }
                list = new ArrayList<Integer>(Arrays.asList(42, 7, 3, -17));
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
                    assert list.size() == 1 : NestedElements + " fails list size";
                    list = List.class.cast(list.get(0));
                }
                assert this.list.equals(list) : NestedElements + " fails inner list";
            }
        },
        /**
         * An array with 1.000 elements.
         *
         * @author J&ouml;rg Schaible
         * @since 1.4.9
         */
        ManyChildren {
            private static final int LENGTH = 1000;

            @Override
            public void writeData(final HierarchicalStreamWriter writer) {
                int length = LENGTH;
                writer.startNode("int-array");
                while (length-- > 0) {
                    writer.startNode("int");
                    writer.setValue(String.valueOf(length));
                    writer.endNode();
                }
                writer.endNode();
            }

            @Override
            public void checkData(final Object o) {
                final int[] array = int[].class.cast(o);
                assert LENGTH == array.length : ManyChildren + " fails length";
                assert LENGTH - 1 == array[0] : ManyChildren + " fails start";
                assert 0 == array[LENGTH - 1] : ManyChildren + " fails end";
            }
        };
        /**
         * Write the data of the factory into the writer of the hierarchical stream.
         *
         * @param writer the writer of the data
         * @since 1.4.9
         */
        public abstract void writeData(HierarchicalStreamWriter writer);

        /**
         * Check the deserialized object.
         *
         * @param o the object to check
         * @since 1.4.9
         */
        public abstract void checkData(Object o);
    }

    @Param
    private DriverFactory driverFactory;
    private DataFactory dataFactory;
    private byte[] data;
    private XStream xstream;
    private HierarchicalStreamDriver driver;

    /**
     * Initialize the XStream instance and instantiate the driver for the benchmark.
     *
     * @since 1.4.9
     */
    @Setup
    public void init() {
        xstream = new XStream();
        xstream.addPermission(NoTypePermission.NONE);
        xstream.addPermission(ArrayTypePermission.ARRAYS);
        xstream.addPermission(PrimitiveTypePermission.PRIMITIVES);
        xstream.allowTypes(new Class[]{List.class, String.class});
        xstream.setMode(XStream.NO_REFERENCES);
        driver = driverFactory.getDriver();
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
        dataFactory = DataFactory.valueOf(benchmark.substring(benchmark.lastIndexOf('.') + 6));

        final ByteArrayOutputStream baos = new ByteArrayOutputStream(1024 * 1024);
        final HierarchicalStreamWriter writer = driver.createWriter(baos);
        dataFactory.writeData(writer);
        writer.close();
        data = baos.toByteArray();
    }

    /**
     * Parse an element with a big text as value.
     *
     * @since 1.4.9
     */
    @Benchmark
    public void parseBigText() {
        final Object o = xstream.unmarshal(driver.createReader(new ByteArrayInputStream(data)));
        dataFactory.checkData(o);
    }

    /**
     * Parse a deeply nested structure.
     *
     * @since 1.4.9
     */
    @Benchmark
    public void parseNestedElements() {
        final Object o = xstream.unmarshal(driver.createReader(new ByteArrayInputStream(data)));
        dataFactory.checkData(o);
    }

    /**
     * Parse an element with a lot of simple children.
     *
     * @since 1.4.9
     */
    @Benchmark
    public void parseManyChildren() {
        final Object o = xstream.unmarshal(driver.createReader(new ByteArrayInputStream(data)));
        dataFactory.checkData(o);
    }
}
