package com.thoughtworks.xstream.tools.benchmark.metrics;

import com.thoughtworks.xstream.tools.benchmark.Metric;
import com.thoughtworks.xstream.tools.benchmark.Product;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

/**
 * Determines how long it takes to deserialize an object (in ms).
 *
 * @author Joe Walnes
 * @see com.thoughtworks.xstream.tools.benchmark.Harness
 * @see Metric
 */
public class DeserializationSpeedMetric implements Metric {

    private int iterations;

    public DeserializationSpeedMetric(int iterations) {
        this.iterations = iterations;
    }

    public Object run(Product product, Object object) throws Exception {

        // Serialize once (because we need something to deserialize).
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        product.serialize(object, output);
        byte[] data = output.toByteArray();

        // Deserialize once, to warm up.
        product.deserialize(new ByteArrayInputStream(data));

        // Now lots of times
        long start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            product.deserialize(new ByteArrayInputStream(data));
        }
        long end = System.currentTimeMillis();

        return new Long(end - start);
    }

    public String unit() {
        return "ms";
    }

    public String toString() {
        return "Speed of deserializing " + iterations + " times";
    }
}
