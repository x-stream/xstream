package com.thoughtworks.xstream.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class OrderRetainingMap extends HashMap {

    private Set keyOrder = new ArraySet();
    private List valueOrder = new ArrayList();
    
    public Object put(Object key, Object value) {
        keyOrder.add(key);
        valueOrder.add(value);
        return super.put(key, value);
    }

    public Collection values() {
        return Collections.unmodifiableList(valueOrder);
    }

    public Set keySet() {
        return Collections.unmodifiableSet(keyOrder);
    }

    public Set entrySet() {
        throw new UnsupportedOperationException();
    }

    private static class ArraySet extends ArrayList implements Set {
    }

}
