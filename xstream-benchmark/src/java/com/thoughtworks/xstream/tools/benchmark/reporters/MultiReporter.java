/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
 * @deprecated As of 1.4.9 use JMH instead
 */
@Deprecated
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
