package com.thoughtworks.xstream.converters.reference;

import java.util.Collection;
import java.util.HashSet;

public class CircularityTracker {

    private Collection refs = new HashSet();

    public void track(Object o) {
        Integer objectId = new Integer(System.identityHashCode(o));

        if (refs.contains(objectId)) {
            throw new CircularityException(String.valueOf(o));
        }

        refs.add(objectId);
    }

}
