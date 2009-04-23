/*
 * Copyright (C) 2007, 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 26. June 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.tools.benchmark.reflection;

import com.thoughtworks.xstream.tools.benchmark.Harness;
import com.thoughtworks.xstream.tools.benchmark.metrics.DeserializationSpeedMetric;
import com.thoughtworks.xstream.tools.benchmark.metrics.SerializationSpeedMetric;
import com.thoughtworks.xstream.tools.benchmark.reflection.products.XStreamClassAliases;
import com.thoughtworks.xstream.tools.benchmark.reflection.products.XStreamFieldAliases;
import com.thoughtworks.xstream.tools.benchmark.reflection.products.XStreamLocalAttributeAliases;
import com.thoughtworks.xstream.tools.benchmark.reflection.products.XStreamPlain;
import com.thoughtworks.xstream.tools.benchmark.reporters.TextReporter;
import com.thoughtworks.xstream.tools.model.targets.FieldReflection;
import com.thoughtworks.xstream.tools.model.targets.HierarchyLevelReflection;
import com.thoughtworks.xstream.tools.model.targets.InnerClassesReflection;
import com.thoughtworks.xstream.tools.model.targets.StaticInnerClassesReflection;


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
