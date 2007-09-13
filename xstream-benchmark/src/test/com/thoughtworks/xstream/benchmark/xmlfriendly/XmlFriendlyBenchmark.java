/*
 * Copyright (C) 2007 XStream team.
 */
package com.thoughtworks.xstream.benchmark.xmlfriendly;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.benchmark.reflection.targets.FieldReflection;
import com.thoughtworks.xstream.benchmark.reflection.targets.HierarchyLevelReflection;
import com.thoughtworks.xstream.benchmark.reflection.targets.InnerClassesReflection;
import com.thoughtworks.xstream.benchmark.reflection.targets.StaticInnerClassesReflection;
import com.thoughtworks.xstream.benchmark.xmlfriendly.product.CombinedLookupAppender;
import com.thoughtworks.xstream.benchmark.xmlfriendly.product.CombinedLookupReplacer;
import com.thoughtworks.xstream.benchmark.xmlfriendly.product.IterativeAppender;
import com.thoughtworks.xstream.benchmark.xmlfriendly.product.IterativeReplacer;
import com.thoughtworks.xstream.benchmark.xmlfriendly.product.NoReplacer;
import com.thoughtworks.xstream.benchmark.xmlfriendly.product.SeparateLookupReplacer;
import com.thoughtworks.xstream.benchmark.xmlfriendly.product.XStream122Replacer;
import com.thoughtworks.xstream.benchmark.xmlfriendly.target.UnderscoredFieldReflection;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.tools.benchmark.Harness;
import com.thoughtworks.xstream.tools.benchmark.metrics.DeserializationSpeedMetric;
import com.thoughtworks.xstream.tools.benchmark.metrics.SerializationSpeedMetric;
import com.thoughtworks.xstream.tools.benchmark.metrics.SizeMetric;
import com.thoughtworks.xstream.tools.benchmark.reporters.TextReporter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

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
                    .newInstance(new Object[]{dollar, underscore, Integer.valueOf(0)});
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
            throw new IllegalStateException("Cannot initialize XmlFriendlyReplacer", ex);
        }
    }

    XmlFriendlyBenchmark() {
        addTestSuite(XStream122Replacer.XmlFriendlyReplacer.class);
        addTestSuite(CombinedLookupAppender.XmlFriendlyReplacer.class);
        addTestSuite(CombinedLookupReplacer.XmlFriendlyReplacer.class);
        addTestSuite(IterativeAppender.XmlFriendlyReplacer.class);
        addTestSuite(IterativeReplacer.XmlFriendlyReplacer.class);
        addTestSuite(SeparateLookupReplacer.XmlFriendlyReplacer.class);
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
        Harness harness = new Harness();
        harness.addMetric(new SizeMetric());
        harness.addMetric(new SerializationSpeedMetric(10));
        harness.addMetric(new DeserializationSpeedMetric(10, true));
        harness.addProduct(new NoReplacer());
        harness.addProduct(new XStream122Replacer());
        harness.addProduct(new CombinedLookupAppender());
        harness.addProduct(new CombinedLookupReplacer());
        harness.addProduct(new IterativeAppender());
        harness.addProduct(new IterativeReplacer());
        harness.addProduct(new SeparateLookupReplacer());
        harness.addTarget(new FieldReflection());
        harness.addTarget(new HierarchyLevelReflection());
        harness.addTarget(new UnderscoredFieldReflection());
        harness.addTarget(new InnerClassesReflection());
        harness.addTarget(new StaticInnerClassesReflection());
        harness.run(new TextReporter());
    }
}
