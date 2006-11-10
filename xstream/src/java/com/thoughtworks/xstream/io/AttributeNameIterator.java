package com.thoughtworks.xstream.io;

import java.util.Iterator;

/**
 * Provide an iterator over the attribute names of the current node of a reader.
 *
 * @author Joe Walnes
 */
public class AttributeNameIterator implements Iterator {

    private int current;
    private final int count;
    private final HierarchicalStreamReader reader;

    public AttributeNameIterator(HierarchicalStreamReader reader) {
        this.reader = reader;
        count = reader.getAttributeCount();
    }

    public boolean hasNext() {
        return current < count;
    }

    public Object next() {
        return reader.getAttributeName(current++);
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

}
