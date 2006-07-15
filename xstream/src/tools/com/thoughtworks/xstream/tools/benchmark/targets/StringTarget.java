package com.thoughtworks.xstream.tools.benchmark.targets;

import com.thoughtworks.xstream.tools.benchmark.Target;

/**
 * A small java.lang.String target.
 *
 * @author Joe Walnes
 * @see com.thoughtworks.xstream.tools.benchmark.Harness
 * @see Target
 */
public class StringTarget implements Target {

    private final String string = "Hello World!";

    public String toString() {
        return "Simple string";
    }

    public Object target() {
        return string;
    }

    public boolean isEqual(Object other) {
        return string.equals(other);
    }

}
