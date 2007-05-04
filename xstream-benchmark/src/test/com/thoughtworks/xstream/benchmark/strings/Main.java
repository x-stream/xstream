/*
 * Copyright (C) 2007 XStream team.
 */
package com.thoughtworks.xstream.benchmark.strings;

import com.thoughtworks.xstream.tools.benchmark.Harness;
import com.thoughtworks.xstream.tools.benchmark.metrics.DeserializationSpeedMetric;
import com.thoughtworks.xstream.tools.benchmark.reporters.TextReporter;


/**
 * Main application to run harness for StringConverter benchmark.
 * 
 * @author J&ouml;rg Schaible
 */
public class Main {
    public static void main(String[] args) {
        Harness harness = new Harness();
        harness.addMetric(new DeserializationSpeedMetric(10));
        harness.addProduct(new StringInternConverter());
        harness.addProduct(new StringWithWeakHashMapConverter());
        harness.addProduct(new StringWithSynchronizedWeakHashMapConverter());
        harness.addTarget(new BigStringTarget());
        harness.addTarget(new StringArrayTarget(1024, 1024));
        harness.addTarget(new StringArrayTarget(64 * 1024, 8));
        harness.run(new TextReporter());
    }
}
