/*
 * Copyright (C) 2007 XStream team.
 */
package com.thoughtworks.xstream.benchmark.reflection;

import com.thoughtworks.xstream.benchmark.reflection.products.XStreamClassAliases;
import com.thoughtworks.xstream.benchmark.reflection.products.XStreamFieldAliases;
import com.thoughtworks.xstream.benchmark.reflection.products.XStreamLocalAttributeAliases;
import com.thoughtworks.xstream.benchmark.reflection.products.XStreamPlain;
import com.thoughtworks.xstream.benchmark.reflection.targets.FieldReflection;
import com.thoughtworks.xstream.benchmark.reflection.targets.HierarchyLevelReflection;
import com.thoughtworks.xstream.benchmark.reflection.targets.InnerClassesReflection;
import com.thoughtworks.xstream.benchmark.reflection.targets.StaticInnerClassesReflection;
import com.thoughtworks.xstream.tools.benchmark.Harness;
import com.thoughtworks.xstream.tools.benchmark.metrics.DeserializationSpeedMetric;
import com.thoughtworks.xstream.tools.benchmark.metrics.SerializationSpeedMetric;
import com.thoughtworks.xstream.tools.benchmark.reporters.TextReporter;


/**
 * Main application to run harness for Reflection benchmark.
 * 
 * @author J&ouml;rg Schaible
 */
public class ReflectionBenchmark {
    public static void main(String[] args) {
        Harness harness = new Harness();
        harness.addMetric(new SerializationSpeedMetric(10));
        harness.addMetric(new DeserializationSpeedMetric(10, true));
        harness.addProduct(new XStreamPlain());
        harness.addProduct(new XStreamClassAliases());
        harness.addProduct(new XStreamFieldAliases());
        harness.addProduct(new XStreamLocalAttributeAliases());
        harness.addTarget(new FieldReflection());
        harness.addTarget(new HierarchyLevelReflection());
        harness.addTarget(new InnerClassesReflection());
        harness.addTarget(new StaticInnerClassesReflection());
        harness.run(new TextReporter());
    }
}
