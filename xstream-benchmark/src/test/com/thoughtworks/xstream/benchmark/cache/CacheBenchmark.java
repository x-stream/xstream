/*
 * Copyright (C) 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 01. January 2008 by Joerg Schaible
 */
package com.thoughtworks.xstream.benchmark.cache;


import com.thoughtworks.xstream.benchmark.cache.products.Cache122;
import com.thoughtworks.xstream.benchmark.cache.products.AliasedAttributeCache;
import com.thoughtworks.xstream.benchmark.cache.products.NoCache;
import com.thoughtworks.xstream.benchmark.cache.products.RealClassCache;
import com.thoughtworks.xstream.benchmark.cache.targets.BasicTarget;
import com.thoughtworks.xstream.benchmark.cache.targets.ExtendedTarget;
import com.thoughtworks.xstream.benchmark.cache.targets.ReflectionTarget;
import com.thoughtworks.xstream.benchmark.cache.targets.SerializableTarget;
import com.thoughtworks.xstream.tools.benchmark.Harness;
import com.thoughtworks.xstream.tools.benchmark.metrics.DeserializationSpeedMetric;
import com.thoughtworks.xstream.tools.benchmark.metrics.SerializationSpeedMetric;
import com.thoughtworks.xstream.tools.benchmark.reporters.TextReporter;

import java.io.PrintWriter;


/**
 * Main application to run harness for Profile benchmark.
 * 
 * @author J&ouml;rg Schaible
 */
public class CacheBenchmark {
    public static void main(String[] args) {
        Harness harness = new Harness();
        /*
        harness.addMetric(new SerializationSpeedMetric(1) {
            public String toString() {
                return "Initial run serialization";
            }
        });
        harness.addMetric(new DeserializationSpeedMetric(1, false) {
            public String toString() {
                return "Initial run deserialization";
            }
        });
        */
        harness.addMetric(new SerializationSpeedMetric(10000));
        harness.addMetric(new DeserializationSpeedMetric(10000, false));
        harness.addProduct(new NoCache());
        harness.addProduct(new Cache122());
        harness.addProduct(new RealClassCache());
        harness.addProduct(new AliasedAttributeCache());
        harness.addProduct(new NoCache());
        harness.addTarget(new BasicTarget());
        harness.addTarget(new ExtendedTarget());
        harness.addTarget(new ReflectionTarget());
        harness.addTarget(new SerializableTarget());
        harness.run(new TextReporter(new PrintWriter(System.out, true)));
        System.out.println("Done.");
    }
}
