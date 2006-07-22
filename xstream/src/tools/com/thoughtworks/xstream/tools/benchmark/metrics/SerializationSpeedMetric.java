package com.thoughtworks.xstream.tools.benchmark.metrics;

import com.thoughtworks.xstream.tools.benchmark.Metric;
import com.thoughtworks.xstream.tools.benchmark.Product;

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
