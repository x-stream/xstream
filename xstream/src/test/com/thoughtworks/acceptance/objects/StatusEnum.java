package com.thoughtworks.acceptance.objects;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StatusEnum implements Serializable, Comparable {

    private static int nextOrdinal = 0;
    private int ordinal = nextOrdinal++;

    public static final StatusEnum STARTED = new StatusEnum("STARTED");

    public static final StatusEnum FINISHED = new StatusEnum("FINISHED");

    private static final StatusEnum[] PRIVATE_VALUES = {STARTED, FINISHED};

    public static final List VALUES = Collections.unmodifiableList(Arrays.asList(PRIVATE_VALUES));

    private String name; // for debug only

    private StatusEnum() {
    }

    private StatusEnum(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    public int compareTo(Object o) {
        return ordinal - ((StatusEnum) o).ordinal;
    }

    private Object readResolve() {
        return PRIVATE_VALUES[ordinal]; //Canonicalize
    }
}
