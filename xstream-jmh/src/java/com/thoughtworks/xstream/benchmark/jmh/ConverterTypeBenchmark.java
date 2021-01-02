/*
 * Copyright (C) 2015, 2017, 2021 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 20 November 2015 by Joerg Schaible
 */
package com.thoughtworks.xstream.benchmark.jmh;

import java.math.BigInteger;
import java.util.UUID;
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
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.javabean.JavaBeanConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.MXParserDriver;
import com.thoughtworks.xstream.security.ArrayTypePermission;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;


/**
 * Benchmark for the different converter types.
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
public class ConverterTypeBenchmark {

    private XStream xstream;
    private Model array[];
    private String xml;

    @SuppressWarnings("javadoc")
    public static class Model {
        private char ch;
        private int i;
        private String s;
        private double d;
        private float f;
        private BigInteger bi;
        private UUID uuid;

        public Model() {
            ch = 0;
            i = 0;
            d = 0.0;
            f = 0.0f;
        }

        public Model(final int i) {
            ch = (char)(i % 256);
            this.i = i;
            s = Integer.toString(i, 2);
            d = Math.PI * i;
            f = (float)(Math.E * i);
            bi = new BigInteger(s, 2);
            uuid = UUID.randomUUID();
        }

        public char getCh() {
            return ch;
        }

        public void setCh(final char ch) {
            this.ch = ch;
        }

        public int getI() {
            return i;
        }

        public void setI(final int i) {
            this.i = i;
        }

        public String getS() {
            return s;
        }

        public void setS(final String s) {
            this.s = s;
        }

        public Double getD() {
            return d;
        }

        public void setD(final Double d) {
            this.d = d;
        }

        public Float getF() {
            return f;
        }

        public void setF(final Float f) {
            this.f = f;
        }

        public BigInteger getBi() {
            return bi;
        }

        public void setBi(final BigInteger bi) {
            this.bi = bi;
        }

        public UUID getUuid() {
            return uuid;
        }

        public void setUuid(final UUID uuid) {
            this.uuid = uuid;
        }
    }

    /**
     * Converter for a Model.
     *
     * @since 1.4.9
     */
    public static final class ModelConverter implements Converter {

        public boolean canConvert(@SuppressWarnings("rawtypes") final Class type) {
            return type == Model.class;
        }

        public void marshal(final Object source, final HierarchicalStreamWriter writer,
                final MarshallingContext context) {
            final Model type = Model.class.cast(source);
            writeElement(writer, "ch", String.valueOf(type.getCh()));
            writeElement(writer, "i", String.valueOf(type.getI()));
            writeElement(writer, "s", type.getS());
            writeElement(writer, "d", String.valueOf(type.getD()));
            writeElement(writer, "f", String.valueOf(type.getF()));
            writeElement(writer, "bi", type.getBi().toString());
            writeElement(writer, "uuid", type.getUuid().toString());
        }

        private void writeElement(final HierarchicalStreamWriter writer, final String name, final String value) {
            writer.startNode(name);
            writer.setValue(value);
            writer.endNode();
        }

        public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
            final Model type = new Model();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                final String value = reader.getValue();
                final String name = reader.getNodeName();
                if (name.equals("ch")) {
                    if (value.length() != 1) {
                        throw new ConversionException("Not a single character");
                    }
                    type.setCh(value.charAt(0));
                } else if (name.equals("i")) {
                    type.setI(Integer.parseInt(value));
                } else if (name.equals("s")) {
                    type.setS(value);
                } else if (name.equals("d")) {
                    type.setD(Double.parseDouble(value));
                } else if (name.equals("f")) {
                    type.setF(Float.parseFloat(value));
                } else if (name.equals("bi")) {
                    type.setBi(new BigInteger(value));
                } else if (name.equals("uuid")) {
                    type.setUuid(UUID.fromString(value));
                } else {
                    throw new ConversionException("Unkown element");
                }
                reader.moveUp();
            }

            return type;
        }

    }

    /**
     * Initialize the XML string to deserialize.
     *
     * @since 1.4.9
     */
    @Setup
    public void init() {
        array = new Model[1000];
        for (int i = 0; i < array.length; ++i) {
            array[i] = new Model(i);
        }
    }

    /**
     * Setup the data to deserialize.
     *
     * @param params the parameters of the benchmark
     * @since 1.4.9
     */
    @Setup(Level.Trial)
    public void setUp(final BenchmarkParams params) {
        xstream = new XStream(new MXParserDriver());
        xstream.addPermission(NoTypePermission.NONE);
        xstream.addPermission(ArrayTypePermission.ARRAYS);
        xstream.addPermission(PrimitiveTypePermission.PRIMITIVES);
        xstream.allowTypes(new Class[]{Model.class, String.class, BigInteger.class, UUID.class});
        final String benchmark = params.getBenchmark();
        final String name = benchmark.substring(ConverterTypeBenchmark.class.getName().length() + 1);
        if (name.equals("reflection")) {
            xstream.registerConverter(new ReflectionConverter(xstream.getMapper(), xstream.getReflectionProvider(),
                Model.class));
        } else if (name.equals("javaBean")) {
            xstream.registerConverter(new JavaBeanConverter(xstream.getMapper(), Model.class));
        } else if (name.equals("custom")) {
            xstream.registerConverter(new ModelConverter());
        } else {
            throw new IllegalStateException("Unsupported benchmark type: " + benchmark);
        }
        xml = xstream.toXML(array);
        // System.out.println(xstream.toXML(array[0]));
    }

    /**
     * Use ReflectionConverter.
     *
     * @since 1.4.9
     */
    @Benchmark
    public void reflection() {
        run();
    }

    /**
     * Use JavaBeanConverter.
     *
     * @since 1.4.9
     */
    @Benchmark
    public void javaBean() {
        run();
    }

    /**
     * Use custom converter.
     *
     * @since 1.4.9
     */
    @Benchmark
    public void custom() {
        run();
    }

    private void run() {
        final Object o = xstream.fromXML(xml);
        assert xstream.toXML(o).equals(xml) : "XML differs";
    }
}
