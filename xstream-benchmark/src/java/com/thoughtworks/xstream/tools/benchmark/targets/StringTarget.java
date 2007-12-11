/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 15. July 2006 by Joe Walnes
 */
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
