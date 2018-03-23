/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 30. May 2004 by Joe Walnes
 */
package com.thoughtworks.acceptance.objects;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class StatusEnum implements Serializable, Comparable<StatusEnum> {
    private static final long serialVersionUID = 200405L;
    private static int nextOrdinal = 0;
    private final int ordinal = nextOrdinal++;

    public static final StatusEnum STARTED = new StatusEnum("STARTED");

    public static final StatusEnum FINISHED = new StatusEnum("FINISHED");

    private static final StatusEnum[] PRIVATE_VALUES = {STARTED, FINISHED};

    public static final List<StatusEnum> VALUES = Collections.unmodifiableList(Arrays.asList(PRIVATE_VALUES));

    private String name; // for debug only

    private StatusEnum(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(final StatusEnum o) {
        return ordinal - o.ordinal;
    }

    private Object readResolve() {
        return PRIVATE_VALUES[ordinal]; // Canonicalize
    }
}
