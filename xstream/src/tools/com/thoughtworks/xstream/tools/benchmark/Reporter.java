package com.thoughtworks.xstream.tools.benchmark;

/**
 * A listener to what the {@link Harness} is doing that should report the results.
 *
 * The sequence of methods is:
 * <pre>
 * startBenchmark,
 * (
 *   startMetric,
 *   (
 *     startTarget,
 *     ( metricRecorded | metricFailed ),
 *     endTarget
 *   ) * ,
 *   endMetric
 * ) * ,
 * endBenchmark
 * </pre>
 *
 * @author Joe Walnes
 * @see Harness
 */
public interface Reporter {

    /**
     * Benchmark has started. This will always be called ONCE (and only once) BEFORE everything else.
     */
    void startBenchmark();

    void startMetric(Metric metric);

    void startTarget(Target target);

    void metricRecorded(Product product, Double result);

    void metricFailed(Product product, Exception e);

    void endTarget(Target target);

    void endMetric(Metric metric);

    /**
     * Benchmark has ended. This will always be called ONCE (and only once) AFTER everything else.
     */
    void endBenchmark();

}
