/*
 * Copyright (C) 2007, 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 04. May 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.tools.benchmark.strings;

import com.thoughtworks.xstream.tools.benchmark.Harness;
import com.thoughtworks.xstream.tools.benchmark.metrics.DeserializationSpeedMetric;
import com.thoughtworks.xstream.tools.benchmark.reporters.TextReporter;
import com.thoughtworks.xstream.tools.benchmark.strings.products.StringInternConverter;
import com.thoughtworks.xstream.tools.benchmark.strings.products.StringNonCachingConverter;
import com.thoughtworks.xstream.tools.benchmark.strings.products.StringWithSynchronizedWeakHashMapConverter;
import com.thoughtworks.xstream.tools.benchmark.strings.products.StringWithWeakHashMapConverter;
import com.thoughtworks.xstream.tools.benchmark.strings.targets.BigString;
import com.thoughtworks.xstream.tools.benchmark.strings.targets.StringArray;


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
