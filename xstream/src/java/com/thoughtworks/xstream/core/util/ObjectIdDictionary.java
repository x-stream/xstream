/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 09. May 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.core.util;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Store IDs against given object references.
 * <p>
 * Behaves similar to java.util.IdentityHashMap, but in JDK1.3 as well. Additionally the implementation
 * keeps track of orphaned IDs by using a WeakReference to store the reference object.
 * </p>
 */
public class ObjectIdDictionary {

    private final Map map = new HashMap();
    private int invalidCounter;

    private static interface Wrapper {
        int hashCode();
        boolean equals(Object obj);
        String toString();
        Object get();
    }
    
    private static class IdWrapper implements Wrapper {

        private final Object obj;

        public IdWrapper(Object obj) {
            this.obj = obj;
        }

        public int hashCode() {
            return System.identityHashCode(obj);
        }

        public boolean equals(Object other) {
            return obj == ((Wrapper)other).get();
        }

        public String toString() {
            return obj.toString();
        }
        
        public Object get() {
            return obj;
        }
    }

    private class WeakIdWrapper implements Wrapper {

        private final int hashCode;
        private final WeakReference ref;

        public WeakIdWrapper(Object obj) {
            hashCode = System.identityHashCode(obj);
            ref = new WeakReference(obj);
        }

        public int hashCode() {
            return hashCode;
        }

        public boolean equals(Object other) {
            return get() == ((Wrapper)other).get();
        }

        public String toString() {
            Object obj = get();
            return obj == null ? "(null)" : obj.toString();
        }
        
        public Object get() {
            Object obj = ref.get();
            if (obj == null) {
                // it was a lot faster and more efficient simply to count the number of
                // evidences instead of keeping the Wrapper somewhere in a remove list
                ++ObjectIdDictionary.this.invalidCounter;
            }
            return obj;
        }
    }

    public void associateId(Object obj, Object id) {
        map.put(new WeakIdWrapper(obj), id);
        cleanup();
    }

    public Object lookupId(Object obj) {
        Object id = map.get(new IdWrapper(obj));
        cleanup();
        return id;
    }

    public boolean containsId(Object item) {
        boolean b = map.containsKey(new IdWrapper(item));
        cleanup();
        return b;
    }

    public void removeId(Object item) {
        map.remove(new IdWrapper(item));
        cleanup();
    }
    
    public int size() {
        return map.size();
    }

    private void cleanup() {
        if (invalidCounter > 100) {
            // much more efficient to remove any orphaned wrappers at once
            for (final Iterator iterator = map.keySet().iterator(); iterator.hasNext();) {
                final WeakIdWrapper key = (WeakIdWrapper)iterator.next();
                if (key.get() == null) {
                    iterator.remove();
                }
            }
            invalidCounter = 0;
        }
    }
}
