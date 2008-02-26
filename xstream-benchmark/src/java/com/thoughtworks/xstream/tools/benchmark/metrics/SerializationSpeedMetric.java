/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 15. July 2006 by Joe Walnes
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
 */
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
