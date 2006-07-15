package com.thoughtworks.xstream.tools.benchmark.metrics;

import com.thoughtworks.xstream.tools.benchmark.Metric;
import com.thoughtworks.xstream.tools.benchmark.Product;

import java.io.ByteArrayOutputStream;

/**
 * Determines the size of the serialized form of an object (in bytes).
 *
 * @author Joe Walnes
 * @see com.thoughtworks.xstream.tools.benchmark.Harness
 * @see Metric
 */
public class SizeMetric implements Metric {

    public Object run(Product product, Object object) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        product.serialize(object, buffer);
        return new Integer(buffer.size());
    }

    public String toString() {
        return "Size of serialized data";
    }

    public String unit() {
        return "bytes";
    }
}
