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

import org.apache.commons.io.IOUtils;

import java.io.IOException;


/**
 * A small java.lang.String target.
 * 
 * @author J&ouml;rg Schaible
 * @see com.thoughtworks.xstream.tools.benchmark.Harness
 * @see Target
 */
public class BigString implements Target {

    private final String string;

    public BigString() {
        try {
            string = IOUtils.toString(getClass().getResourceAsStream("eclipse-build-log.txt"));
        } catch (IOException e) {
            throw new RuntimeException("Cannot create big String target", e);
        }
    }

    public String toString() {
        return "Big string";
    }

    public Object target() {
        return string;
    }

    public boolean isEqual(Object other) {
        return string.equals(other);
    }
}
