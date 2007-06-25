/*
 * Copyright (C) 2007 XStream team.
 */
package com.thoughtworks.xstream.benchmark.strings;

import com.thoughtworks.xstream.benchmark.strings.products.StringInternConverter;
import com.thoughtworks.xstream.benchmark.strings.products.StringNonCachingConverter;
import com.thoughtworks.xstream.benchmark.strings.products.StringWithSynchronizedWeakHashMapConverter;
import com.thoughtworks.xstream.benchmark.strings.products.StringWithWeakHashMapConverter;
import com.thoughtworks.xstream.benchmark.strings.targets.BigString;
import com.thoughtworks.xstream.benchmark.strings.targets.StringArray;
import com.thoughtworks.xstream.tools.benchmark.Harness;
import com.thoughtworks.xstream.tools.benchmark.metrics.DeserializationSpeedMetric;
import com.thoughtworks.xstream.tools.benchmark.reporters.TextReporter;


/**
 * Main application to run harness for StringConverter benchmark.
 * 
 * @author J&ouml;rg Schaible
 */
public class StringsBenchmark {
    public static void main(String[] args) {
        Harness harness = new Harness();
        harness.addMetric(new DeserializationSpeedMetric(10, true));
        harness.addProduct(new StringNonCachingConverter());
        harness.addProduct(new StringInternConverter());
        harness.addProduct(new StringWithWeakHashMapConverter());
        harness.addProduct(new StringWithSynchronizedWeakHashMapConverter());
        harness.addTarget(new BigString());
        harness.addTarget(new StringArray(1024, 1024, 128));
        harness.addTarget(new StringArray(64 * 1024, 8, 32));
        harness.run(new TextReporter());
    }
}
