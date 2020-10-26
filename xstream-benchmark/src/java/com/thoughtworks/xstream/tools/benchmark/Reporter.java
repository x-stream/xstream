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
 * @deprecated As of 1.4.9 use JMH instead
 */
@Deprecated
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
