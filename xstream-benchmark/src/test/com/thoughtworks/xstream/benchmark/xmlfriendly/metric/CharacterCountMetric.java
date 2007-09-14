package com.thoughtworks.xstream.benchmark.xmlfriendly.metric;

import com.thoughtworks.xstream.tools.benchmark.Metric;
import com.thoughtworks.xstream.tools.benchmark.Product;
import com.thoughtworks.xstream.tools.benchmark.Target;

import java.io.ByteArrayOutputStream;

/**
 * Determines the amount of a special characters.
 *
 * @author J&ouml;rg Schaible
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
     *@deprecated since upcoming
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
