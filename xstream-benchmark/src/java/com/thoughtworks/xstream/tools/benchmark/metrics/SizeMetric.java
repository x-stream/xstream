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
 * Determines the size of the serialized form of an object (in bytes).
 *
 * @author Joe Walnes
 * @see com.thoughtworks.xstream.tools.benchmark.Harness
 * @see Metric
 */
public class SizeMetric implements Metric {

    public double run(Product product, Target target) throws Exception {
        return run(product, target.target());
    }

    /**
     *@deprecated since 1.3
     */
    public double run(Product product, Object object) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        product.serialize(object, buffer);
        return buffer.size();
    }

    public String toString() {
        return "Size of serialized data";
    }

    public String unit() {
        return "bytes";
    }

    public boolean biggerIsBetter() {
        return false;
    }
}
