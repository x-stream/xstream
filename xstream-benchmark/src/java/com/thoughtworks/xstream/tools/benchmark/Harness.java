/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2013 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 15. July 2006 by Joe Walnes
 */
package com.thoughtworks.xstream.tools.benchmark;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A simple harness for running benchmarks over object serialization products.
 *
 * <p>
 * There are three dimensions that can be added:
 * </p>
 * <ul>
 * <li>{@link Product} (e.g. DOM, SAX, XPP...)</li>
 * <li>{@link Metric} (e.g. time taken, memory usage, output size...)</li>
 * <li>{@link Target} (e.g. a small object, large object, list of objects...)</li>
 * </ul>
 * <p>
 * The Harness will then across every permutation of these
 * (in order of product, metric, target), and write the results to a {@link Reporter}.
 * </p>
 *
 * <h3>Example usage</h3>
 * <pre>
 * Harness harness = new Harness();
 *
 * // Compare speed of serialization/deserialization metrics...
 * harness.addMetric(new SerializationSpeedMetric());
 * harness.addMetric(new DeserializationSpeedMetric());
 *
 * // Using a simple String and a JTree instance...
 * harness.addTarget(new StringTarget());
 * harness.addTarget(new JTreeTarget());
 *
 * // Across XStream with different XML drivers.
 * harness.addProduct(new XStreamDom());
 * harness.addProduct(new XStreamXpp());
 * harness.addProduct(new XStreamSax());
 *
 * // Now do it, and report the results as text to the console.
 * harness.run(new TextReporter());
 * </pre>
 *
 * @author Joe Walnes
 */
public class Harness {

    private List products = new ArrayList();
    private List targets = new ArrayList();
    private List metrics = new ArrayList();

    public synchronized void addProduct(Product product) {
        products.add(product);
    }

    public synchronized void addTarget(Target target) {
        targets.add(target);
    }

    public synchronized void addMetric(Metric metric) {
        metrics.add(metric);
    }

    public synchronized void run(Reporter reporter) {
        // Nested loop galore.
        reporter.startBenchmark();
        for (Iterator metricsIt = metrics.iterator(); metricsIt.hasNext();) {
            Metric metric = (Metric) metricsIt.next();
            reporter.startMetric(metric);
            for (Iterator targetIt = targets.iterator(); targetIt.hasNext();) {
                Target target = (Target) targetIt.next();
                reporter.startTarget(target);
                for (Iterator productsIt = products.iterator(); productsIt.hasNext();) {
                    Product product = (Product) productsIt.next();
                    run(reporter, metric, target, product);
                }
                reporter.endTarget(target);
            }
            reporter.endMetric(metric);
        }
        reporter.endBenchmark();
    }

    private void run(Reporter reporter, Metric metric, Target target, Product product) {
        try {
            double result = metric.run(product, target);
            reporter.metricRecorded(product, result);
        } catch (Exception e) {
            reporter.metricFailed(product, e);
        }
    }

}
