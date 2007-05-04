package com.thoughtworks.xstream.benchmark.strings;

import com.thoughtworks.xstream.tools.benchmark.Target;

import java.util.Arrays;


/**
 * A small java.lang.String target.
 * 
 * @author J&ouml;rg Schaible
 * @see com.thoughtworks.xstream.tools.benchmark.Harness
 * @see Target
 */
public class StringArrayTarget implements Target {

    private final String[] strings;

    public StringArrayTarget(int elements, int length) {
        char[] zero = new char[length];
        Arrays.fill(zero, '0');

        strings = new String[elements];
        for (int i = 0; i < strings.length; i++) {
            String hex = Integer.toHexString(i);
            strings[i] = new String(zero, 0, zero.length - hex.length()) + hex;
        }
    }

    public String toString() {
        return "String array with " + strings.length + " elements of " + strings[0].length() + " chars";
    }

    public Object target() {
        return strings;
    }

    public boolean isEqual(Object other) {
        return strings.equals(other);
    }
}
