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

    private static class IdWrapper {

        private final Object obj;

        public IdWrapper(Object obj) {
            this.obj = obj;
        }

        public int hashCode() {
            return System.identityHashCode(obj);
        }

        public boolean equals(Object other) {
            return obj == ((IdWrapper)other).obj;
        }

        public String toString() {
            return obj.toString();
        }
    }

    public void associateId(Object obj, Object id) {
        map.put(new IdWrapper(obj), id);
    }

    public Object lookupId(Object obj) {
        return map.get(new IdWrapper(obj));
    }

    public boolean containsId(Object item) {
        return map.containsKey(new IdWrapper(item));
    }

    public void removeId(Object item) {
        map.remove(new IdWrapper(item));
    }

}
