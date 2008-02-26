/*
 * Copyright (C) 2007, 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 14. September 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.tools.benchmark.reporters;

import com.thoughtworks.xstream.tools.benchmark.Metric;
import com.thoughtworks.xstream.tools.benchmark.Product;
import com.thoughtworks.xstream.tools.benchmark.Reporter;
import com.thoughtworks.xstream.tools.benchmark.Target;

/**
 * A reporter multiplexing the results to other Reporters.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.3
 */
public class MultiReporter implements Reporter {
    
    private final Reporter[] reporter;

    public MultiReporter(Reporter[] reporter) {
        this.reporter = reporter;
    }

    public void endBenchmark() {
        for (int i = 0; i < reporter.length; i++) {
            reporter[i].endBenchmark();
        }
    }

    public void endMetric(Metric metric) {
        for (int i = 0; i < reporter.length; i++) {
            reporter[i].endMetric(metric);
        }
    }

    public void endTarget(Target target) {
        for (int i = 0; i < reporter.length; i++) {
            reporter[i].endTarget(target);
        }
    }

    public void metricFailed(Product product, Exception e) {
        for (int i = 0; i < reporter.length; i++) {
            reporter[i].metricFailed(product, e);
        }
    }

    public void metricRecorded(Product product, double result) {
        for (int i = 0; i < reporter.length; i++) {
            reporter[i].metricRecorded(product, result);
        }
    }

    public void startBenchmark() {
        for (int i = 0; i < reporter.length; i++) {
            reporter[i].startBenchmark();
        }
    }

    public void startMetric(Metric metric) {
        for (int i = 0; i < reporter.length; i++) {
            reporter[i].startMetric(metric);
        }
    }

    public void startTarget(Target target) {
        for (int i = 0; i < reporter.length; i++) {
            reporter[i].startTarget(target);
        }
    }

}
