package com.thoughtworks.xstream.benchmark.strings.targets;

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
