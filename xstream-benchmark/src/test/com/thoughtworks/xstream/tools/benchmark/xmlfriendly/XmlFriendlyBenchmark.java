/*
 * Copyright (C) 2007, 2008, 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 13. September 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.tools.benchmark.xmlfriendly;

import com.thoughtworks.xstream.InitializationException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.tools.benchmark.Harness;
import com.thoughtworks.xstream.tools.benchmark.Reporter;
import com.thoughtworks.xstream.tools.benchmark.metrics.CharacterCountMetric;
import com.thoughtworks.xstream.tools.benchmark.metrics.DeserializationSpeedMetric;
import com.thoughtworks.xstream.tools.benchmark.metrics.SerializationSpeedMetric;
import com.thoughtworks.xstream.tools.benchmark.metrics.SizeMetric;
import com.thoughtworks.xstream.tools.benchmark.reporters.HtmlReporter;
import com.thoughtworks.xstream.tools.benchmark.reporters.MultiReporter;
import com.thoughtworks.xstream.tools.benchmark.reporters.TextReporter;
import com.thoughtworks.xstream.tools.benchmark.xmlfriendly.products.CachingIterativeAppenderWithShortcut;
import com.thoughtworks.xstream.tools.benchmark.xmlfriendly.products.CombinedLookupAppender;
import com.thoughtworks.xstream.tools.benchmark.xmlfriendly.products.CombinedLookupReplacer;
import com.thoughtworks.xstream.tools.benchmark.xmlfriendly.products.IterativeAppender;
import com.thoughtworks.xstream.tools.benchmark.xmlfriendly.products.IterativeAppenderWithShortcut;
import com.thoughtworks.xstream.tools.benchmark.xmlfriendly.products.IterativeReplacer;
import com.thoughtworks.xstream.tools.benchmark.xmlfriendly.products.NoReplacer;
import com.thoughtworks.xstream.tools.benchmark.xmlfriendly.products.SeparateLookupReplacer;
import com.thoughtworks.xstream.tools.benchmark.xmlfriendly.targets.Field$Reflection;
import com.thoughtworks.xstream.tools.benchmark.xmlfriendly.targets.Field_Reflection;
import com.thoughtworks.xstream.tools.model.targets.FieldReflection;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


/**
 * Main application to run harness for Reflection benchmark.
 * 
 * @author J&ouml;rg Schaible
 */
public class XmlFriendlyBenchmark extends TestSuite {

    public static class __ {
        public static class UnfriendlyClass {
            String __a__$$a__;
            String b__b__;
            String __c__c;

            public boolean equals(Object obj) {
                UnfriendlyClass other = (UnfriendlyClass)obj;
                return __a__$$a__.equals(other.__a__$$a__)
                    && b__b__.equals(other.b__b__)
                    && __c__c.equals(other.__c__c);
            }

        }
    }

    private static Class currentType;

    public static class ReplacerTest extends TestCase {

        private final Class type;

        public ReplacerTest(String name) {
            super(name);
            type = currentType;
        }

        public String getName() {
            return type.getName() + ": " + super.getName();
        }

        public void testReplacerWithDefaultReplacements() {
            String xml = ""
                + "<com.thoughtworks.xstream.benchmark.xmlfriendly.XmlFriendlyBenchmark_-_____-UnfriendlyClass>\n"
                + "  <____a_____-_-a____>a</____a_____-_-a____>\n"
                + "  <b____b____>b</b____b____>\n"
                + "  <____c____c>c</____c____c>\n"
                + "</com.thoughtworks.xstream.benchmark.xmlfriendly.XmlFriendlyBenchmark_-_____-UnfriendlyClass>";
            performTest("_-", "__", getReference(), xml);
        }

        public void testReplacerWithDollarReplacementOnly() {
            String xml = ""
                + "<com.thoughtworks.xstream.benchmark.xmlfriendly.XmlFriendlyBenchmark_-___-UnfriendlyClass>\n"
                + "  <__a___-_-a__>a</__a___-_-a__>\n"
                + "  <b__b__>b</b__b__>\n"
                + "  <__c__c>c</__c__c>\n"
                + "</com.thoughtworks.xstream.benchmark.xmlfriendly.XmlFriendlyBenchmark_-___-UnfriendlyClass>";
            performTest("_-", "_", getReference(), xml);
        }

        private void performTest(String dollar, String underscore, __.UnfriendlyClass object,
            String xml) {
            XStream xstream = createXStreamWithReplacer(dollar, underscore);
            assertEquals(xml, xstream.toXML(object));
            assertEquals(object, xstream.fromXML(xml));
        }

        private __.UnfriendlyClass getReference() {
            __.UnfriendlyClass ref = new __.UnfriendlyClass();
            ref.__a__$$a__ = "a";
            ref.b__b__ = "b";
            ref.__c__c = "c";
            return ref;
        }

        private XStream createXStreamWithReplacer(String dollar, String underscore) {
            Exception ex;
            try {
                Constructor constructor = type.getConstructor(new Class[]{
                    String.class, String.class, int.class});
                XmlFriendlyReplacer replacer = (XmlFriendlyReplacer)constructor
                    .newInstance(new Object[]{dollar, underscore, new Integer(0)});
                return new XStream(new XppDriver(replacer));
            } catch (NoSuchMethodException e) {
                ex = e;
            } catch (InstantiationException e) {
                ex = e;
            } catch (IllegalAccessException e) {
                ex = e;
            } catch (InvocationTargetException e) {
                ex = e;
            }
            throw new InitializationException("Cannot initialize XmlFriendlyReplacer", ex);
        }
    }

    XmlFriendlyBenchmark() {
        addTestSuite(CombinedLookupReplacer.XmlFriendlyReplacer.class);
        addTestSuite(CombinedLookupAppender.XmlFriendlyReplacer.class);
        addTestSuite(SeparateLookupReplacer.XmlFriendlyReplacer.class);
        addTestSuite(IterativeReplacer.XmlFriendlyReplacer.class);
        addTestSuite(IterativeAppender.XmlFriendlyReplacer.class);
        addTestSuite(IterativeAppenderWithShortcut.XmlFriendlyReplacer.class);
        addTestSuite(CachingIterativeAppenderWithShortcut.XmlFriendlyReplacer.class);
    }

    public void addTestSuite(Class replacerClass) {
        currentType = replacerClass;
        super.addTestSuite(ReplacerTest.class);
    }

    public static Test suite() {
        // Ensure the different implementations work
        return new XmlFriendlyBenchmark();
    }

    public static void main(String[] args) {
        new File("target/benchmarks").mkdirs();

        Reporter[] reporters;
        try {
            String basename = "target/benchmarks/xmlfriendly-"
                + System.getProperty("user.name");
            reporters = new Reporter[]{
                new TextReporter(), new TextReporter(new FileWriter(basename + ".txt")),
                new HtmlReporter(new File(basename + ".html"), "XmlFriendlyReplacer Benchmark")};
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Harness stats = new Harness();
        stats.addMetric(new SizeMetric());
        stats.addMetric(new CharacterCountMetric('$'));
        stats.addMetric(new CharacterCountMetric('_'));
        stats.addProduct(new NoReplacer());
        stats.addTarget(new FieldReflection());
        stats.addTarget(new Field_Reflection());
        stats.addTarget(new Field$Reflection());
        stats.run(new MultiReporter(reporters) {

            public void endBenchmark() {
                // do nothing
            }

        });

        Harness harness = new Harness();
        harness.addMetric(new SerializationSpeedMetric(100));
        harness.addMetric(new DeserializationSpeedMetric(100, false));
        harness.addProduct(new CombinedLookupReplacer(0));
        // harness.addProduct(new CombinedLookupReplacer(32));
        harness.addProduct(new CombinedLookupAppender(0));
        // harness.addProduct(new CombinedLookupAppender(32));
        harness.addProduct(new SeparateLookupReplacer(0));
        // harness.addProduct(new SeparateLookupReplacer(32));
        harness.addProduct(new IterativeReplacer(0));
        // harness.addProduct(new IterativeReplacer(32));
        harness.addProduct(new IterativeAppender(0));
        // harness.addProduct(new IterativeAppender(32));
        harness.addProduct(new IterativeAppenderWithShortcut());
        harness.addProduct(new CachingIterativeAppenderWithShortcut());
        harness.addTarget(new FieldReflection());
        harness.addTarget(new Field_Reflection());
        harness.addTarget(new Field$Reflection());
        harness.run(new MultiReporter(reporters) {

            public void startBenchmark() {
                // do nothing
            }

        });
    }
}
