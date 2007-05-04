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
 */
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
        this(new PrintWriter(System.out));
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
