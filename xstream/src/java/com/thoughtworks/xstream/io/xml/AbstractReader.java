package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

import java.util.Iterator;

public abstract class AbstractReader implements HierarchicalStreamReader {

    public Iterator getAttributeNames() {
        final int count = getAttributeCount();
        return new Iterator() {

            private int current;

            public boolean hasNext() {
                return current < count;
            }

            public Object next() {
                return getAttributeName(current++);
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }

        };
    }

}
