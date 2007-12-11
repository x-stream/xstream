/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 22. July 2006 by Joe Walnes
 */
package com.thoughtworks.xstream.tools.benchmark.reporters;

import com.thoughtworks.xstream.tools.benchmark.Reporter;
import com.thoughtworks.xstream.tools.benchmark.Metric;
import com.thoughtworks.xstream.tools.benchmark.Target;
import com.thoughtworks.xstream.tools.benchmark.Product;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Date;

public class HtmlReporter implements Reporter {

    private final PrettyPrintWriter out;
    private final String title;

    private Metric currentMetric;
    private double largestMetricForTarget;
    private List resultsForTarget;

    public HtmlReporter(File htmlFile, String title) throws IOException {
        this.title = title;
        out = new PrettyPrintWriter(new FileWriter(htmlFile));
    }

    public void startBenchmark() {
        out.startNode("html");

        out.startNode("head");
        writeTag("title", title);
        writeTag("style", css(), "type", "text/css");
        out.endNode();

        out.startNode("body");

        writeTag("h1", title);
    }

    private String css() {
        StringBuffer css = new StringBuffer("\n");
        css.append("body, h1, h2, h3, td { font-family: arial; }\n");
        css.append("h1 { text-align: center; }\n");
        css.append("table, h3 { margin-left: 40px; }\n");
        css.append("table, td, th { border: 1px solid #999; border-collapse: collapse; font-size: smaller; }\n");
        css.append(".success { color: #090; }\n");
        css.append(".fail { color: #900; }\n");
        return css.toString();
    }

    public void startMetric(Metric metric) {
        writeTag("h2", metric.toString());
        currentMetric = metric;
    }

    public void startTarget(Target target) {
        writeTag("h3", target.toString());
        out.flush(); // Flush now, so progress can be seen with slow benchmarks.
        largestMetricForTarget = 0;
        resultsForTarget = new ArrayList();
    }

    public void metricRecorded(Product product, double result) {
        // Keep a look out for the largest result.
        if (result > largestMetricForTarget) {
            largestMetricForTarget = result;
        }
        resultsForTarget.add(new MetricResult(product, result));
    }

    public void metricFailed(Product product, Exception e) {
        resultsForTarget.add(new MetricResult(product, e));
    }

    public void endTarget(Target target) {
        out.startNode("table");
        out.startNode("tr");

        writeTag("th", "Product");
        writeTag("th", currentMetric.unit());

        out.endNode();
        for (Iterator iterator = resultsForTarget.iterator(); iterator.hasNext();) {
            MetricResult metricResult = (MetricResult) iterator.next();
            out.startNode("tr");

            writeTag("td", metricResult.product.toString());

            if (metricResult.exception == null) {
                writeTag("td", String.valueOf(metricResult.result), "class", "success");

                long percentage = Math.round(Math.abs(metricResult.result / largestMetricForTarget) * 100.0);
                out.startNode("td");
                out.addAttribute("style", "width: 400px;");
                writeTag("div", "", "style", "height: 100%; width: " + percentage + "%; background-color: blue;");
                out.endNode();
            } else {
                writeTag("td", "FAIL", "class", "fail");
                writeTag("td", metricResult.exception.toString());
            }

            out.endNode();
        }
        out.endNode();
        out.flush(); // Flush now, so progress can be seen with slow benchmarks.
    }

    private void writeTag(String tag, String value) {
        out.startNode(tag);
        out.setValue(value);
        out.endNode();
    }

    private void writeTag(String tag, String value, String attributeName, String attributeValue) {
        out.startNode(tag);
        out.addAttribute(attributeName, attributeValue);
        out.setValue(value);
        out.endNode();
    }

    public void endMetric(Metric metric) {
    }

    public void endBenchmark() {
        writeTag("p", new Date().toString());
        out.endNode();
        out.endNode();
        out.close();
    }

    private static class MetricResult {
        private final Product product;
        private final double result;
        private final Exception exception;

        public MetricResult(Product product, double result) {
            this.result = result;
            this.product = product;
            this.exception = null;
        }

        public MetricResult(Product product, Exception exception) {
            this.product = product;
            this.result = 0;
            this.exception = exception;
        }
    }
}
