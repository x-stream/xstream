/*
 * Copyright (C) 2007, 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 26. June 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.tools.benchmark.strings.targets;

import com.thoughtworks.xstream.tools.benchmark.Target;

import java.util.Arrays;


/**
 * A target with an array of java.lang.String objects.
 * 
 * @author J&ouml;rg Schaible
 * @see com.thoughtworks.xstream.tools.benchmark.Harness
 * @see Target
 */
public class StringArray implements Target {

    private final String[] strings;
    private final int unique;

    public StringArray(int elements, int length, int unique) {
        this.unique = unique;
        char[] zero = new char[length];
        Arrays.fill(zero, '0');

        strings = new String[elements];
        for (int i = 0; i < strings.length; i++) {
            String hex = Integer.toHexString(i % unique);
            strings[i] = new String(zero, 0, zero.length - hex.length()) + hex;
        }
    }

    public String toString() {
        return "String array with "
            + strings.length
            + " elements of "
            + strings[0].length()
            + " chars and "
            + unique
            + " unique entries";
    }

    public Object target() {
        return strings;
    }

    public boolean isEqual(Object other) {
        return Arrays.equals(strings, (Object[])other);
    }
}
