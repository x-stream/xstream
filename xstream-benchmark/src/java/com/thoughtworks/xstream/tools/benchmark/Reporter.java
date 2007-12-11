/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 15. July 2006 by Joe Walnes
 */
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

    void metricRecorded(Product product, double result);

    void metricFailed(Product product, Exception e);

    void endTarget(Target target);

    void endMetric(Metric metric);

    /**
     * Benchmark has ended. This will always be called ONCE (and only once) AFTER everything else.
     */
    void endBenchmark();

}
