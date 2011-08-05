/*
 * Copyright (C) 2007, 2008, 2009, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 14. September 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.tools.benchmark.metrics;

import com.thoughtworks.xstream.tools.benchmark.Metric;
import com.thoughtworks.xstream.tools.benchmark.Product;
import com.thoughtworks.xstream.tools.benchmark.Target;

import java.io.ByteArrayOutputStream;

/**
 * Determines the amount of a special characters.
 *
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public class CharacterCountMetric implements Metric {
    
    private final char ch;

    public CharacterCountMetric(char ch) {
        this.ch = ch;
    }

    public double run(Product product, Target target) throws Exception {
        return run(product, target.target());
    }

    /**
     *@deprecated since 1.3
     */
    public double run(Product product, Object object) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        product.serialize(object, buffer);
        String s = buffer.toString();
        int counter = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ch) {
                ++counter;
            }
        }
        return counter;
    }

    public String toString() {
        return "Character count for '" + ch + "'";
    }

    public String unit() {
        return "characters";
    }

    public boolean biggerIsBetter() {
        return false;
    }
}
