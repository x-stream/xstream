package com.thoughtworks.xstream.core;

public class SequenceGenerator implements ReferenceByIdMarshaller.IDGenerator {

    private int counter;

    public SequenceGenerator(int startsAt) {
        this.counter = startsAt;
    }

    public String next() {
        return String.valueOf(counter++);
    }

}
