package com.thoughtworks.xstream.core.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Store IDs against given object references.
 * <p/>
 * Behaves the same way as java.util.IdentityHashMap, but in JDK1.3 as well.
 */
public class ObjectIdDictionary {

    private Map map = new HashMap();

    public void associateId(Object obj, String id) {
        map.put(id(obj), id);
    }

    public String lookupId(Object obj) {
        return (String) map.get(id(obj));
    }

    private Object id(Object obj) {
        return new Integer(System.identityHashCode(obj));
    }

}
