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
 * A metric is what's actually recorded. This provides a strategy
 * for what to do with an object for a given product and should
 * return a measurable result. For example it could serialize an
 * object against a product and return how long it took to complete
 * the operation.
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @see Harness
 * @deprecated As of 1.4.9 use JMH instead
 */
@Deprecated
public interface Metric {

    /**
     * Run the test and produce a metric.
     *
     * @param product Product to use in test.
     * @param object A object to use against the product.
     * @return The resulting metric (e.g. 12.22).
     * @throws Exception If this metric could not be obtained. This will
     *                   be reported back to the {@link Reporter}.
     * @deprecated since 1.3
     */
    double run(Product product, Object object) throws Exception;
    
    /**
     * Run the test and produce a metric.
     *
     * @param product Product to use in test.
     * @param target A target to use against the product.
     * @return The resulting metric (e.g. 12.22).
     * @throws Exception If this metric could not be obtained. This will
     *                   be reported back to the {@link Reporter}.
     * @since 1.3
     */
    double run(Product product, Target target) throws Exception;

    /**
     * The unit the metric is recorded in (for reporting purposes).
     * e.g. "ms" or "bytes".
     */
    String unit();

    /**
     * Whether a big result is better for this metric.
     */
    boolean biggerIsBetter();

}
