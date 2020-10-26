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

import java.io.PrintWriter;
import java.io.Writer;

/**
 * Reports results of Harness in text form designed for human reading.
 *
 * @author Joe Walnes
 * @see com.thoughtworks.xstream.tools.benchmark.Harness
 * @see Reporter
 * @deprecated As of 1.4.9 use JMH instead
 */
@Deprecated
public class TextReporter implements Reporter {

    private final PrintWriter out;
    private Metric currentMetric;

    public TextReporter(PrintWriter out) {
        this.out = out;
    }

    public TextReporter(Writer out) {
        this(new PrintWriter(out));
    }

    /**
     * Reports to System.out.
     */
    public TextReporter() {
        this(new PrintWriter(System.out, true));
    }

    public void startBenchmark() {
    }

    public void startMetric(Metric metric) {
        currentMetric = metric;
        out.println("======================================================================");
        out.println(metric);
        out.println("======================================================================");
    }

    public void startTarget(Target target) {
        out.println("* " + target + "");
    }

    public void metricRecorded(Product product, double result) {
        out.println("  - " + pad(product.toString()) + " " + result + " " + currentMetric.unit());
    }

    public void metricFailed(Product product, Exception e) {
        out.println("  - " + pad(product.toString()) + " FAILED (" + e + ")");
    }

    public void endTarget(Target target) {
    }

    public void endMetric(Metric metric) {
        out.println();
        currentMetric = null;
    }

    public void endBenchmark() {
        out.flush();
    }

    private String pad(String value) {
        StringBuffer result = new StringBuffer();
        result.append(value);
        while (result.length() < 50) {
            result.append('.');
        }
        return result.toString();
    }
}
