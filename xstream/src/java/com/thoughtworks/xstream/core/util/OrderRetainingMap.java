package com.thoughtworks.xstream.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class OrderRetainingMap extends HashMap {

    private ArraySet keyOrder = new ArraySet();
    private List valueOrder = new ArrayList();

    public Object put(Object key, Object value) {
        int idx = keyOrder.lastIndexOf(key);
        if (idx < 0) {
            keyOrder.add(key);
            valueOrder.add(value);
        } else {
            valueOrder.set(idx, value);
        }
        return super.put(key, value);
    }

    public Object remove(Object key) {
        int idx = keyOrder.lastIndexOf(key);
        if (idx != 0) {
            keyOrder.remove(idx);
            valueOrder.remove(idx);
        }
        return super.remove(key);
    }

    public Collection values() {
        return Collections.unmodifiableList(valueOrder);
    }

    public Set keySet() {
        return Collections.unmodifiableSet(keyOrder);
    }

    public Set entrySet() {
        Map.Entry[] entries = new Map.Entry[size()];
        for (Iterator iter = super.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry)iter.next();
            entries[keyOrder.indexOf(entry.getKey())] = entry;
        }
        Set set = new ArraySet();
        set.addAll(Arrays.asList(entries));
        return Collections.unmodifiableSet(set);
    }

    private static class ArraySet extends ArrayList implements Set {
    }
}
