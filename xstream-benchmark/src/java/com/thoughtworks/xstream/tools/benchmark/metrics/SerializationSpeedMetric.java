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

package com.thoughtworks.xstream.tools.benchmark.metrics;

import com.thoughtworks.xstream.tools.benchmark.Metric;
import com.thoughtworks.xstream.tools.benchmark.Product;
import com.thoughtworks.xstream.tools.benchmark.Target;

import java.io.ByteArrayOutputStream;

/**
 * Determines how long it takes to serialize an object (in ms).
 *
 * @author Joe Walnes
 * @see com.thoughtworks.xstream.tools.benchmark.Harness
 * @see Metric
 * @deprecated As of 1.4.9 use JMH instead
 */
@Deprecated
public class SerializationSpeedMetric implements Metric {

    private int iterations;

    public SerializationSpeedMetric(int iterations) {
        this.iterations = iterations;
    }

    public double run(Product product, Target target) throws Exception {
        return run(product, target.target());
    }
    
    /**
     *@deprecated since 1.3
     */
    public double run(Product product, Object object) throws Exception {
        // Do it once to warm up.
        product.serialize(object, new ByteArrayOutputStream());

        // Now lots of times
        long start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            product.serialize(object, new ByteArrayOutputStream());
        }
        long end = System.currentTimeMillis();

        return (end - start);
    }

    public String unit() {
        return "ms";
    }

    public boolean biggerIsBetter() {
        return false;
    }

    public String toString() {
        return "Serialization speed (" + iterations + " iteration" + (iterations == 1 ? "" : "s") + ")";
    }
}
